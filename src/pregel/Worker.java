package pregel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author standingby
 *
 * @param <V>
 * @param <E>
 * @param <M>
 */
public class Worker<V, E, M> implements Runnable {
    public final int id;
    private final Master<V, E, M> master;

    /** 记录当前Worker内所维护的结点 */
    private Map<String, Vertex<V, E, M>> vertices = new HashMap<>();
    /** 缓存目标结点所在worker的引用 */
    private Map<String, Worker<V, E, M>> vertexIn = new HashMap<>();
    /** 待发送消息队列，存储当前消息 */
    private Map<String, List<M>> messagesTobeSent = new HashMap<>();

    protected Combiner<M> combiner;
    protected Aggregator<Vertex<V, E, M>, M> aggregator;
    protected M aggValue;

    protected double timespan;
    protected int traffic = 0;

    protected Worker(int id, Master<V, E, M> master) {
        this.id = id;
        this.master = master;
    }

    /**
     * 串行运行各节点的compute函数
     */
    @Override
    public void run() {
        double start = System.currentTimeMillis();
        traffic = 0;

        // Compute
        for (Vertex<V, E, M> vertex : vertices.values()) {
            List<M> msgs = vertex.resetMessages();
            if (vertex.isActive()) {
                vertex.compute(msgs);
                vertex.incSuperStep();
            }
        }

        // Aggregate （可选）
        if (aggregator != null) {
            List<M> values = new LinkedList<>();
            for (Vertex<V, E, M> vertex : vertices.values()) {
                values.add(aggregator.report(vertex));
            }
            aggValue = aggregator.aggregate(values);
        }

//        sendMessages();
        // 计数减一，表示完成任务
        master.countDownLatch.countDown();
        timespan = (System.currentTimeMillis() - start) / 1000;


    }


    /**
     * 判断结点Vertex，
     * @return true：所有节点均InActive
     */
    protected boolean allInactive() {
        for (Vertex<V, E, M> vertex : vertices.values()) {
            if (vertex.isActive()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 由Vertex调用，向发送队列中添加待发送消息
     * @param targetId
     * @param msg
     */
    protected void addSentMessage(String targetId, M msg) {
        traffic++;
        if (messagesTobeSent.containsKey(targetId)) {
            messagesTobeSent.get(targetId).add(msg);
        } else {
            messagesTobeSent.put(targetId, new LinkedList<>(Arrays.asList(msg)));
        }

        // 冷启动，缓存worker信息
        if (!vertexIn.containsKey(targetId)) {
            vertexIn.put(targetId, master.getVertexInWorker(targetId));
        }
    }

    protected void sendMessages() {
        if (combiner != null) {
            // 指定 Combiner
            sendMessagesWithCombiner();
        } else {
            // 默认发送模式
            for (Map.Entry<String, List<M>> entry : messagesTobeSent.entrySet()) {
                vertexIn.get(entry.getKey()).addNewMessages(entry.getKey(), entry.getValue());
            }
        }
        // 清空消息队列
        messagesTobeSent.clear();
    }

    /**
     * 若指定Combiner，则利用combine函数处理消息队列
     * @param combiner
     */
    private void sendMessagesWithCombiner() {
        for (Map.Entry<String, List<M>> entry : messagesTobeSent.entrySet()) {
            vertexIn.get(entry.getKey()).addNewMessage(entry.getKey(),
                    combiner.combine(entry.getValue()));
        }
    }

    /**
     * 由其他worker调用，向指定target vertex 新增消息
     * @param vertexId 消息接收target
     * @param msgs 传递消息
     */
    private synchronized void addNewMessages(String vertexId, List<M> msgs) {
        vertices.get(vertexId).addNewMessages(msgs);
    }

    private synchronized void addNewMessage(String vertexId, M msg) {
        vertices.get(vertexId).addNewMessage(msg);
    }



    protected void addVertex(Vertex<V, E, M> vertex) {
        vertices.put(vertex.vertexId, vertex);
        vertex.setWorker(this);
    }

    protected int getVerticesNum() {
        return master.verticesNum;
    }

    /**
     * 使用Combiner并指定combine实现
     * @param combiner
     */
    public void setCombiner(Class<? extends Combiner<M>> combiner) {
        try {
            this.combiner = (Combiner<M>) combiner.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用 Aggregator 并指定 report 和 aggregate 的实现
     * @param aggregator the aggregator to set
     */
    public void setAggregator(Aggregator<Vertex<V, E, M>, M> aggregator) {
        try {
            this.aggregator = (Aggregator<Vertex<V, E, M>, M>) aggregator.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 统计并打印顶点数和边数
     */
    protected void reportVertexAndEdge() {
        int edgeSum = 0;
        for (Vertex<V, E, M> vertex : vertices.values()) {
            edgeSum += vertex.targets.size();
        }
        System.out.printf("Worker %d load %d vertices concerning %d edges ... \n", id,
                vertices.size(), edgeSum);
    }

    protected Set<String> loadPartition(File file, IUtils<V, E, M> utils) {
        Set<String> result = new HashSet<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if (line.startsWith("#") || line.length() == 0) {
                    continue;
                }
                Triplet<V, E, M> triplet = utils.parseGraphFileLine(line);
                if (triplet == null) {
                    continue;
                }
                if (!result.contains(triplet.source.vertexId)) {
                    // 尚未添加 source
                    result.add(triplet.source.vertexId);
                    vertices.put(triplet.source.vertexId, triplet.source);
                    triplet.source.setWorker(this);
                }
                if (triplet.target != null) {
                    // 有出边
                    vertices.get(triplet.source.vertexId).targets.put(triplet.target.vertexId,
                            triplet.edgeValue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void savePartition(String outputPath) {
        try {
            BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
            for (Vertex<V, E, M> vertex : vertices.values()) {
                writer.write(vertex.toString());
            }
            writer.close();
            System.out.printf("Worker %d saved %d vertices on %s...\n", id, vertices.size(),
                    outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void resultOutput(BufferedWriter writer) throws IOException {
        for (Vertex<V, E, M> vertex : vertices.values()) {
            writer.write(vertex.resultFormater());
        }
    }

}
