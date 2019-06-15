package pregel;

public class Triplet<V, E, M> {
	public final Vertex<V, E, M> source;
	public final Vertex<V, E, M> target;
	public final E edgeValue;

	public Triplet(Vertex<V, E, M> source, Vertex<V, E, M> target, E edgeValue) {
		super();
		this.source = source;
		this.target = target;
		this.edgeValue = edgeValue;
	}

	@Override
	public String toString() {
		return "Triplet [source=" + source + ", target=" + target + ", edgeValue=" + edgeValue + "]";
	}
	
	
}
