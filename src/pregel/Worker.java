package pregel;

import java.util.HashSet;
import java.util.Set;

public class Worker<V, E, M> extends Thread {
    private Set<Vertex<V, E, M>> vertices = new HashSet<>();

    /**
     * 
     */
    protected Worker() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 串行运行各节点的compute函数
     */
    @Override
    public void run() {
        for (Vertex<V, E, M> vertex : vertices) {
            vertex.compute();
        }
    }

    public void addVertex(Vertex<V, E, M> vertex) {
        vertices.add(vertex);
    }


}
