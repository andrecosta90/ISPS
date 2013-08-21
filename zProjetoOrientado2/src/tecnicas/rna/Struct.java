package tecnicas.rna;

public class Struct {

	private int fromLayer;
	private int fromNeuron;
	private int toNeuron;

	public Struct(int toLayer, int fromNeuron, int toNeuron) {
		this.fromLayer = toLayer;
		this.fromNeuron = fromNeuron;
		this.toNeuron = toNeuron;
	}

	public Struct() {
		// TODO Auto-generated constructor stub
		super();
	}

	public int getFromLayer() {
		return fromLayer;
	}

	public void setFromLayer(int toLayer) {
		this.fromLayer = toLayer;
	}

	public int getFromNeuron() {
		return fromNeuron;
	}

	public void setFromNeuron(int fromNeuron) {
		this.fromNeuron = fromNeuron;
	}

	public int getToNeuron() {
		return toNeuron;
	}

	public void setToNeuron(int toNeuron) {
		this.toNeuron = toNeuron;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public boolean equals(Struct struct) {

		return this.fromLayer == struct.fromLayer
				&& this.fromNeuron == struct.fromNeuron
				&& this.toNeuron == struct.toNeuron;
	}

}
