package classes;

public class Pregao {

	private int dia;
	private int mes;
	private int ano;

	private String data;
	private double preco;
	private double retorno;

	public Pregao(String data, double preco) {
		super();

		this.data = data;
		this.preco = preco;
		this.retorno = 0.00001d; // retorno padrao, próximo de 0

		if (!data.equals("")) {
			this.ano = Integer.valueOf(data.substring(0, 4));
			this.mes = Integer.valueOf(data.substring(4, 6));
			this.dia = Integer.valueOf(data.substring(6));
		}
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public double getRetorno() {
		return retorno;
	}

	public void setRetorno(double retorno) {
		this.retorno = retorno;
	}

	public int getDia() {
		return dia;
	}

	public int getMes() {
		return mes;
	}

	public int getAno() {
		return ano;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(data);
		sb.append(";");
		sb.append(preco);
		sb.append(";");
		sb.append(retorno);

		return sb.toString();

	}

	public boolean equals(Pregao pregao) {
		return (this.data.equals(pregao.data) && this.preco == pregao.preco && this.retorno == pregao.retorno);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
