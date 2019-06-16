package pregel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Worker<V, E, M> extends Thread {
    public final int id;
    private final Master<V, E, M> master;
    private Map<String, Vertex<V, E, M>> vertices = new HashMap<>();

    private Map<Vertex<V, E, M>, List<M>> messagesTobeSent = new HashMap<>();


    protected Worker(int id, Master<V, E, M> master) {
        this.id = id;
        this.master = master;
    }

    /**
     * 串行运行各节点的compute函数
     */
    @Override
    public void run() {
        for (Vertex<V, E, M> vertex : vertices.values()) {
            if (vertex.isActive()) {
                vertex.compute();
            }
        }
        sendMessages();



        // 计数减一，表示完成任务
        master.countDownLatch.countDown();
    }

    public boolean allInactive() {
        for (Vertex<V, E, M> vertex : vertices.values()) {
            if (vertex.isActive()) {
                return false;
            }
        }
        return true;
    }

    protected void addSentMessage(String targetId, M msg) {
        
    }

    private void sendMessages() {
        if (master.combiner != null) {
            // 指定 Combiner
            try {
                Combiner<M> combiner = (Combiner<M>) master.combiner.getClass().newInstance();
                sendMessages(combiner);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            // 默认发送模式
            for (Map.Entry<Vertex<V, E, M>, List<M>> entry : messagesTobeSent.entrySet()) {
                Worker<V, E, M> worker = master.workers.get(entry.getKey().getWorkerId());
                for (M msg : entry.getValue()) {
                    worker.getMessage(entry.getKey().vertexId, msg);
                }
            }
        }
        messagesTobeSent.clear();
    }

    private void sendMessages(Combiner<M> combiner) {
        for (Map.Entry<Vertex<V, E, M>, List<M>> entry : messagesTobeSent.entrySet()) {
            master.workers.get(entry.getKey().getWorkerId()).getMessage(entry.getKey().vertexId,
                    combiner.combine(entry.getValue()));
        }
    }

    public synchronized void getMessage(String vertexId, M msg) {
        vertices.get(vertexId).getMessage(msg);
    }

    public void addVertex(Vertex<V, E, M> vertex) {
        vertices.put(vertex.vertexId, vertex);
        vertex.setWorker(this);
    }

}
