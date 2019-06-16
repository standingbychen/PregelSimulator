package pregel;

/**
 * 定义文件中每行的解析方式
 * @author standingby
 *
 * @param <V>
 * @param <E>
 * @param <M>
 */
public interface IUtils<V, E, M> {

	public Triplet<V, E, M> parseGraphFileLine(String line);
	
}


