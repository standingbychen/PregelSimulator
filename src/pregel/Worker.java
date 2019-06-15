package pregel;

import java.util.HashSet;
import java.util.Set;

public class Worker<V, E, M> extends Thread {
	private final Master<V, E, M> master;
	private Set<Vertex<V, E, M>> vertices = new HashSet<>();

	/**
	 * 
	 */
	protected Worker(Master<V, E, M> master) {
		this.master = master;
	}

	/**
	 * 串行运行各节点的compute函数
	 */
	@Override
	public void run() {
		for (Vertex<V, E, M> vertex : vertices) {
			if (vertex.isActive()) {
				vertex.compute();
			}
		}
		// 计数减一，表示完成任务
		master.countDownLatch.countDown();
	}

	public boolean allInactive() {
		for (Vertex<V, E, M> vertex : vertices) {
			if (vertex.isActive()) {
				return false;
			}
		}
		return true;
	}

	public void addVertex(Vertex<V, E, M> vertex) {
		vertices.add(vertex);
	}

}
