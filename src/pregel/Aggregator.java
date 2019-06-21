/**
 * 
 */
package pregel;

import java.util.List;

/**
 * @author standingby
 *
 */
public interface Aggregator<V, M> {

    /**
     * 获得顶点发送的内容
     * @param vertex
     * @return
     */
    public M report(V vertex);

    /**
     * 聚集计算
     * @param msgs
     * @return
     */
    public M aggregate(List<M> msgs);

}
