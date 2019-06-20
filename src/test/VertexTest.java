package test;

import java.util.List;
import pregel.IUtils;
import pregel.Master;
import pregel.Triplet;
import pregel.Vertex;

public class VertexTest extends Vertex<Integer, Integer, Integer> {

    /**
     * @param vertexId
     */
    public VertexTest(String vertexId) {
        super(vertexId);
        // TODO Auto-generated constructor stub
    }


    @Override
    public void compute(List<Integer> msgs) {
        if (!msgs.isEmpty()) {
            voteToHalt();
        }
        for (String vertex : targets.keySet()) {
            sendMessageTo(vertex, 0);
        }
        if (getSuperStep() >= 15 && isActive()) {
            System.out.println(this.vertexId);
            voteToHalt();
        }
    }


    public static void main(String[] args) {
        Master<Integer, Integer, Integer> master = new Master<>(10);
        UtilTest utilImpl = new UtilTest();
        master.importGraph("web-Google.txt", utilImpl);
        master.save("src/partition");
    }

}


class UtilTest implements IUtils<Integer, Integer, Integer> {
    @Override
    public Triplet<Integer, Integer, Integer> parseGraphFileLine(String line) {
        String[] strings = line.split("\\t");
        VertexTest source = new VertexTest(strings[0]);
        VertexTest target = new VertexTest(strings[1]);
        return new Triplet<Integer, Integer, Integer>(source, target, 0);
    }
}
