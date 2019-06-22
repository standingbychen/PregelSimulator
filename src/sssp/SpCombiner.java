/**
 * 
 */
package sssp;

import java.util.List;
import pregel.Combiner;

/**
 * @author standingby
 *
 */
public class SpCombiner implements Combiner<SpMessage> {

    /*
     * (non-Javadoc)
     * 
     * @see pregel.Combiner#combine(java.util.List)
     */
    @Override
    public synchronized SpMessage combine(List<SpMessage> msgList) {
        int min = msgList.get(0).cost;
        SpMessage result = msgList.get(0);
        for (SpMessage spMessage : msgList) {
            if (min > spMessage.cost) {
                min = spMessage.cost;
                result = spMessage;
            }
        }
        return result;
    }

}
