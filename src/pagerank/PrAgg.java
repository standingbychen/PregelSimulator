/**
 * 
 */
package pagerank;

import java.util.List;
import pregel.Aggregator;
import pregel.Vertex;

/**
 * @author standingby
 *
 */
public class PrAgg implements Aggregator<Vertex<Double, Void, Double>, Double> {

    /*
     * (non-Javadoc)
     * 
     * @see pregel.Aggregator#report(java.lang.Object)
     */
    @Override
    public Double report(Vertex<Double, Void, Double> vertex) {
        PrVertex ver = (PrVertex) vertex;
        return Math.abs(ver.getVertexValue() - ver.getOldValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see pregel.Aggregator#aggregate(java.util.List)
     */
    @Override
    public Double aggregate(List<Double> msgs) {
        double sum = 0;
        for (Double double1 : msgs) {
            sum += double1;
        }
        return sum;
    }


}
