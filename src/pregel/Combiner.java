/**
 * 
 */
package pregel;

import java.util.List;
import java.util.Map;

/**
 * @author standingby
 *
 */
public interface Combiner<M> {

    public M combine(List<M> msgList);

}
