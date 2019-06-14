package pregel;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author standingby
 *
 * @param <V> VatexValue
 * @param <E> EdgeValue
 * @param <M> MessageValue
 */
public abstract  class Vertex <V,E,M> {
    public final String vertexId;

    /**
	 * 标记顶点活跃状态<p>
	 * true：参与计算<p>
	 * false：不参与计算，接受消息后转为true
	 */
	private boolean active = true;
	
	private List<M> lastMessage = new LinkedList<>();
	private List<M> curMessage = new LinkedList<>();
	

	
	public Vertex(String vertexId) {
		super();
		this.vertexId = vertexId;
	}

	abstract public void compute();
	
	
	public boolean isActive() {
		return active;
	}
}
