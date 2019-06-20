/**
 * 
 */
package pagerank;

import java.util.List;
import pregel.IUtils;
import pregel.Master;
import pregel.Triplet;
import pregel.Vertex;

/**
 * @author standingby
 *
 */
public class PrVertex extends Vertex<Double, Void, Double> {
    /** 阻尼系数 */
    public static final double DAMPFACTOR = 0.85;

    /**
     * @param vertexId
     */
    public PrVertex(String vertexId) {
        super(vertexId);
        vertexValue = 1.0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pregel.Vertex#compute(java.util.List)
     */
    @Override
    public void compute(List<Double> msgs) {
        if (getSuperStep() >= 1) {
            double sum = 0;
            for (Double msg : msgs) {
                sum += msg;
            }
            vertexValue = (1 - DAMPFACTOR) * getVerticesNum() + DAMPFACTOR * sum;
        }
        if (getSuperStep() < 30) {// 迭代30次以内
            int n = targets.size();
            // send msg to all neighbors
            for (String target : targets.keySet()) {
                sendMessageTo(target, vertexValue / n);
            }

        } else {
            // 停止迭代
            voteToHalt();
            System.out.println(resultFormater());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see pregel.Vertex#outputFormater()
     */
    @Override
    public String resultFormater() {
        return vertexId + "\t" + vertexValue + "\n";
    }

    public static void main(String[] args) {
        Master<Double, Void, Double> master = new Master<>(10);
        PrUtils utilImpl = new PrUtils();
        // master.importGraph("web-Google.txt", utilImpl);
        master.setCombiner(new PrCombiner());
        master.load("src/partition", utilImpl);
        master.launch();
        master.resultOutput("src/pagerank/result.txt");
    }
}


