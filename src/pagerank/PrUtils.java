/**
 * 
 */
package pagerank;

import java.util.Arrays;
import pregel.IUtils;
import pregel.Triplet;

/**
 * @author standingby
 *
 */
public class PrUtils implements IUtils<Double, Void, Double> {
    @Override
    public Triplet<Double, Void, Double> parseGraphFileLine(String line) {
        String[] strings = line.split("\\t");
        PrVertex source = new PrVertex(strings[0]);
        PrVertex target;
        if (strings.length >= 2) {
            target = new PrVertex(strings[1]);
        } else {
            // Ã»ÓÐ³ö±ß
            target = null;
        }
        return new Triplet<Double, Void, Double>(source, target, null);
    }

}

