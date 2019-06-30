/**
 * 
 */
package pregel;

import java.util.List;

/**
 * @author standingby
 *
 */
public interface Combiner<M> {

    public M combine(List<M> msgList);

}
