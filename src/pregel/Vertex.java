package pregel;

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
 * @param <V> VatexValue
 * @param <E> EdgeValue
 * @param <M> MessageValue
 */
public abstract class Vertex<V, E, M> {
	public final String vertexId;
	private V vertexValue;

	/**
	 * 标记顶点活跃状态
	 * <p>
	 * true（默认值）：参与计算
	 * <p>
	 * false：不参与计算，接受消息后转为true
	 */
	private boolean active = true;

	private Map<Vertex<V, E, M>, E> targets = new HashMap<>();

	private List<M> lastMessage = new LinkedList<>();
	private List<M> curMessage = new LinkedList<>();

	public Vertex(String vertexId) {
		super();
		this.vertexId = vertexId;
	}

	public Vertex(String vertexId, V vertexValue) {
		super();
		this.vertexId = vertexId;
		this.vertexValue = vertexValue;
	}

	abstract public void compute();

	abstract public void getMessage();

	public void addTarget(Vertex<V, E, M> target, E edgeValue) {
		targets.put(target, edgeValue);
	}

	public void voteToHalt() {
		active = false;
	}

	public V getValue() {
		return vertexValue;
	}

	public boolean isActive() {
		return active;
	}

	public String outputFormater() {
		return toString();
	}
}
