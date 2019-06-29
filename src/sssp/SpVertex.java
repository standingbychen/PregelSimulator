/**
 * 
 */
package sssp;

import java.util.List;
import java.util.Map;
import pregel.Master;
import pregel.Vertex;

/**
 * @author standingby
 *
 */
public class SpVertex extends Vertex<Integer, Integer, SpMessage> {
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
    public void compute(List<SpMessage> msgs) {
        int min = source.equals(vertexId) ? 0 : Integer.MAX_VALUE;
        SpMessage result = null;
        for (SpMessage spMessage : msgs) {
            if (min > spMessage.cost) {
                min = spMessage.cost;
                result = spMessage;
            }
        }
        if (min < vertexValue) {
            vertexValue = min;
            if (result != null) {
                this.path = result.path + " > " + this.vertexId;
            }
            for (Map.Entry<String, Integer> entry : targets.entrySet()) {
                sendMessageTo(entry.getKey(), new SpMessage(this.path, min + entry.getValue()));
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
        Master<Integer, Integer, SpMessage> master = new Master<>(10);
        // master.importGraph("web-Google.txt", utilImpl);
        SpUtils utilImpl = new SpUtils();
        master.setCombiner(SpCombiner.class);
        master.load("src/partition", utilImpl);
        while (!master.allInactive()) {
            master.run();
        }
        System.out.println("\nMission Complete.");
        master.resultOutput("src/sssp/result.txt");
        master.shutdown();
    }
}


