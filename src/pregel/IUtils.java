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

    /**
     * 定义输入文件一行内容的解析方式
     * @param line  一条输入记录
     * @return  三元组
     */
    public Triplet<V, E, M> parseGraphFileLine(String line);

}


