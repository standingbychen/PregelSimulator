package test;

import pregel.IUtils;
import pregel.Master;
import pregel.Triplet;
import pregel.Vertex;

public class VertexTest extends Vertex<Integer, Integer, Integer> {

	public VertexTest(String vertexId) {
		super(vertexId);
	}

	@Override
	public void compute() {
//		System.out.println(this.vertexId);
		voteToHalt();
	}

	@Override
	public void getMessage() {
	}

	@Override
	public String toString() {
		return vertexId;
	}

	public static void main(String[] args) {
		Master<Integer, Integer, Integer> master = new Master<>(10);
		UtilTest utilImpl = new UtilTest();
		master.importGraph("web-Google.txt", utilImpl);
		master.launch();
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
