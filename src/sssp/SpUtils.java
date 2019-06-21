/**
 * 
 */
package sssp;

import pregel.IUtils;
import pregel.Triplet;

/**
 * @author standingby
 *
 */
public class SpUtils implements IUtils<Integer, Integer, Integer> {

    /*
     * (non-Javadoc)
     * 
     * @see pregel.IUtils#parseGraphFileLine(java.lang.String)
     */
    @Override
    public Triplet<Integer, Integer, Integer> parseGraphFileLine(String line) {
        String[] strings = line.split("\\t");
        SpVertex source = new SpVertex(strings[0]);
        SpVertex target;
        if (strings.length >= 2) {
            target = new SpVertex(strings[1]);
        } else {
            // Ã»ÓÐ³ö±ß
            target = null;
        }
        return new Triplet<Integer, Integer, Integer>(source, target, 1);
    }


}
