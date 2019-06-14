package element;

public abstract  class Vertex <VertexValue,EdgeValue,MessageValue> {
	/**
	 * 标记顶点活跃状态
	 * true：参与计算
	 * false：不参与计算，接受消息后转为true
	 */
	private boolean active = true;
	
	public final String vertexId;	

	
	public Vertex(String vertexId) {
		super();
		this.vertexId = vertexId;
	}

	abstract public void compute();
	
	
	public boolean isActive() {
		return active;
	}
}
