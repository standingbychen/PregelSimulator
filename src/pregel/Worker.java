package pregel;

import java.util.LinkedList;
import java.util.List;

public class Worker extends Thread{
	private List<Vertex> vertices = new LinkedList<>();
	
	/**
	 * 串行运行各节点的compute函数
	 */
	@Override
	public void run() {
		super.run();
	}
	
	
}
