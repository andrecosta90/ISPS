package classes;

import java.util.ArrayList;
import java.util.LinkedList;

import utils.Utilidades;

public class Papel {
	
	private String codigoPapel;
	private String nomeEmpresa;
	private ArrayList<Pregao> pregoes;
	
	public Papel(String codigoPapel, String nomeEmpresa){
		this.codigoPapel = codigoPapel;
		this.nomeEmpresa = nomeEmpresa;
		this.pregoes = new ArrayList<Pregao>();
	}
	
	public Papel(String codigoPapel, String nomeEmpresa, ArrayList<Pregao> pregoes){
		this.codigoPapel = codigoPapel;
		this.nomeEmpresa = nomeEmpresa;
		this.pregoes = pregoes;
	}
	
	private double calculaRetorno(double precoAtual, double precoAnterior){
		return (precoAtual - precoAnterior) / precoAnterior;
	}
	
	public void adicionarPregao(Pregao pregao){
		int size = this.pregoes.size();
		if (size > 0){
			double precoAnterior = this.pregoes.get(size-1).getPreco();
			double precoAtual = pregao.getPreco();
			
			double retorno = calculaRetorno(precoAtual, precoAnterior);
			pregao.setRetorno(retorno);
		}
		this.pregoes.add(pregao);
	}

	
	public String getCodigoPapel() {
		return codigoPapel;
	}

	public void setCodigoPapel(String codigoPapel) {
		this.codigoPapel = codigoPapel;
	}

	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}
	
	public double getMenor() {
		double menor = Double.MAX_VALUE;
		
		for (Pregao pregao : pregoes){
			
			double preco = pregao.getPreco();
			
			
			if (preco < menor){
				menor = preco;
			}
			
		}
		
		return menor;
	}
	
	public double getMaior() {
		double maior = Double.MIN_VALUE;
		
		
		for (Pregao pregao : pregoes){
			
			double preco = pregao.getPreco();
			
			if (preco > maior){
				maior = preco;
			}
			
		}
		
		return maior;
	}

	
	
	
	public ArrayList<Pregao> getPregoes() {
		return (ArrayList<Pregao>) pregoes.clone();
	}

	public int getQuantidadePregoes() {
		return this.pregoes.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(codigoPapel);
		sb.append(";");
		sb.append(nomeEmpresa);
		sb.append(";");
		sb.append(pregoes);
		
		return sb.toString();
	}
	
	public Pregao getPregao(int index){
		return this.pregoes.get(index);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

	}

	public double[] converteParaVet(LinkedList<Pregao> pregoes) {
		double item[] = new double[pregoes.size()];

		for (int i = 0; i < item.length; ++i) {
			Pregao pregao = pregoes.get(i);
			item[i] = pregao.getPreco();
		}

		return item;
	}
	
	public double[] converteParaVet() {
		double item[] = new double[pregoes.size()];

		for (int i = 0; i < item.length; ++i) {
			Pregao pregao = pregoes.get(i);
			item[i] = pregao.getPreco();
		}

		return item;
	}

//	private double[] converteParaVetNormalizado(double min, double max) {
//		double item[] = new double[pregoes.size()];
//
//		for (int i = 0; i < item.length; ++i) {
//			Pregao pregao = pregoes.get(i);
//			item[i] = Utilidades.normalizar(min, max, pregao.getPreco());
//		}
//
//		return item;
//	}
	
	public double[] converteParaVetNormalizado(
			LinkedList<Pregao> pregoes, double min, double max) {
		double item[] = new double[pregoes.size()];

		for (int i = 0; i < item.length; ++i) {
			Pregao pregao = pregoes.get(i);
			item[i] = Utilidades.normalizar(min, max, pregao.getPreco());
		}

		return item;
	}

}
