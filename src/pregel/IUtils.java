package pregel;

public interface IUtils<V, E, M> {

	public Triplet<V, E, M> parseGraphFileLine(String line);
	
}


