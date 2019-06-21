/**
 * 
 */
package sssp;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import pagerank.PrCombiner;
import pagerank.PrUtils;
import pregel.IUtils;
import pregel.Master;
import pregel.Vertex;

/**
 * @author standingby
 *
 */
public class SpVertex extends Vertex<Integer, Integer, Integer> {
    private static String source = "0";
    private String path;

    /**
     * @param vertexId
     */
    public SpVertex(String vertexId) {
        super(vertexId);
        vertexValue = Integer.MAX_VALUE;
        path = source.equals(vertexId) ? "0" : "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see pregel.Vertex#compute(java.util.List)
     */
    @Override
    public void compute(List<Integer> msgs) {
        int min = source.equals(vertexId) ? 0 : Integer.MAX_VALUE;
        // SpMessage result = null;
        for (Integer spMessage : msgs) {
            // if (min < spMessage.cost) {
            // min = spMessage.cost;
            //// result = spMessage;
            // }
            min = Integer.min(spMessage, min);
        }
        if (min < vertexValue) {
            vertexValue = min;
            // if (result != null) {
            // this.path = result.path + " > " + this.vertexId;
            // }
            for (Map.Entry<String, Integer> entry : targets.entrySet()) {
//                sendMessageTo(entry.getKey(), new Integer(this.path, min + entry.getValue()));
                sendMessageTo(entry.getKey(), entry.getValue());
            }
        }
        voteToHalt();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pregel.Vertex#outputFormater()
     */
    @Override
    public String resultFormater() {
        if (vertexValue == Integer.MAX_VALUE) {
            return vertexId + "\tNone\n";
        }
        return vertexId + "\t" + vertexValue + "\t" + path + "\n";
    }


    public static void main(String[] args) {
        Master<Integer, Integer, Integer> master = new Master<>(10);
        // master.importGraph("web-Google.txt", utilImpl);
        SpUtils utilImpl = new SpUtils();
        // master.setCombiner(new SpCombiner());
        master.load("src/partition", utilImpl);
        while (!master.allInactive()) {
            master.run();
        }
        System.out.println("Mission Complete.");
        // master.resultOutput("src/sssp/result.txt");
        master.shutdown();
    }
}


