/**
 * 
 */
package pagerank;

import java.util.List;
import pregel.Combiner;

/**
 * @author standingby
 *
 */
public class PrCombiner implements Combiner<Double> {

    /*
     * (non-Javadoc)
     * 
     * @see pregel.Combiner#combine(java.util.List)
     */
    @Override
    public Double combine(List<Double> msgList) {
        double sum = 0;
        for (Double double1 : msgList) {
            sum += double1;
        }
        return sum;
    }

}
