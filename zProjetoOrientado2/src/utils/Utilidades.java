package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
//import classes.Pregao;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import classes.Papel;
import classes.Pregao;

public class Utilidades {

	public static String inserir(String string, String item, int pos) {
		StringBuilder stringBuilder = new StringBuilder(string);
		stringBuilder.insert(pos, item);
		return stringBuilder.toString();
	}

	public static void main(String[] args) {
		// System.out.println(desnormalizar(13.3, 24.5, 0.9857142857142857d));
		ArrayList<Double> array = new ArrayList<Double>();

		array.add(3d);
		array.add(2d);
		array.add(1d);
		array.add(9d);
		array.add(6d);
		array.add(4d);
		array.add(5d);
		array.add(8d);

		System.out.println(calculaMedia(array));
		System.out.println(calculaDesvioPadrao(array));

	}

	public static double normalizar(double min, double max, double preco) {
		min -= 0.01;
		max += 0.01;
		preco = (preco - min) / (max - min);
		return preco;
	}

	public static double desnormalizar(double min, double max, double preco) {
		preco = preco * (max - min) + min;
		return preco;
	}

	// public static ArrayList normalizarDados(ArrayList pregoes) {
	//
	// return null;
	// }

	public static double[] ones(int length) {
		double ones[] = new double[length];

		for (int i = 0; i < length; ++i) {
			ones[i] = 1;
		}

		return ones;

	}

	public static double[] zeros(int length) {
		double zeros[] = new double[length];

		for (int i = 0; i < length; ++i) {
			zeros[i] = 0;
		}

		return zeros;

	}

	public static double[] calcRisk(double[][] r, double[] rmean) {
		double risk[] = new double[rmean.length];

		for (int i = 0; i < r.length; ++i) {

			double sum = 0;
			for (int j = 0; j < r[i].length; ++j) {
				sum += Math.pow((r[i][j] - rmean[i]), 2);
			}
			risk[i] = sum / r.length;
		}

		return risk;
	}

	public static double[][] calcCovariance(double[][] r, double[] rmean) {
		double gamma[][] = new double[rmean.length][rmean.length];

		// System.out.println(r.length);
		// System.out.println(r[0].length);
		for (int i = 0; i < r.length; ++i) {
			for (int j = 0; j < r.length; ++j) {
				gamma[i][j] = calcCovariance(r[i], r[j], rmean[i], rmean[j]);
			}
		}
		return gamma;
	}

	public static double calcCovariance(double[] ri, double[] rj, double rm_i,
			double rm_j) {

		double sum = 0;
		for (int t = 0; t < ri.length; ++t) {
			sum += (ri[t] - rm_i) * (rj[t] - rm_j);
		}
		return sum / ri.length;

	}

	public static double[] calcMean(double[][] r) {
		double Rmean[] = new double[r.length];
		for (int i = 0; i < r.length; ++i) {
			double sum = 0;
			for (int j = 0; j < r[i].length; ++j) {
				sum += r[i][j];
			}
			Rmean[i] = sum / r.length;
		}
		return Rmean;
	}

	public static double[] trocarSinal(double[] r) {
		for (int i = 0; i < r.length; ++i) {
			r[i] *= (-1);
		}
		return r;
	}

	public static double[] normalizar(double min, double max, double[] item) {
		double itemNorm[] = new double[item.length];
		for (int i = 0; i < item.length; ++i) {
			itemNorm[i] = normalizar(min, max, item[i]);
		}
		return itemNorm;
	}

	public static double[] converteParaVetor(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ArrayList converteParaVetor(String[] base, int tamanhoJanela) {
		ArrayList a = new ArrayList();

		int size = base.length;
		double input[][] = new double[size][tamanhoJanela];
		double output[][] = new double[size][1];
		for (int i = 0; i < size; ++i) {
			// System.out.println(base[i]);
			String aux[] = base[i].split(",");
			output[i][0] = Double.valueOf(aux[0]);

			for (int j = 0; j < tamanhoJanela; ++j) {
				input[i][j] = Double.valueOf(aux[j + 1]);
			}
		}
		a.add(input);
		a.add(output);

		return a;
	}

	public static double calculaMedia(ArrayList<Double> array) {
		double soma = 0.0;

		for (Double valor : array) {
			soma += valor;
		}

		return soma / (array.size() * 1d);

	}

	public static double calculaVariancia(ArrayList<Double> array) {
		double media = calculaMedia(array);

		double soma = 0.0;
		for (Double valor : array) {
			soma += Math.pow((valor - media), 2);
		}

		double size = array.size() * 1d;

		return (1 / (size - 1)) * soma;

	}

	public static double calculaDesvioPadrao(ArrayList<Double> array) {
		return Math.sqrt(calculaVariancia(array));

	}

	// private static double[] converteParaVet(LinkedList<Pregao> pregoes) {
	// double item[] = new double[pregoes.size()];
	//
	// for (int i = 0; i < item.length; ++i) {
	// Pregao pregao = pregoes.get(i);
	// item[i] = pregao.getPreco();
	// }
	//
	// return item;
	// }
	//
	// private static double[] converteParaVetNormalizado(
	// LinkedList<Pregao> pregoes, double min, double max) {
	// double item[] = new double[pregoes.size()];
	//
	// for (int i = 0; i < item.length; ++i) {
	// Pregao pregao = pregoes.get(i);
	// item[i] = normalizar(min, max, pregao.getPreco());
	// }
	//
	// return item;
	// }

	public static Vector convertJListToVector(JList jList) {
		Vector listData = new Vector();
		ListModel model = jList.getModel();
		for (int i = 0; i < model.getSize(); ++i) {
			listData.addElement(model.getElementAt(i));
		}
		return listData;
	}

//	public static Papel convertToPapelDesejado(double[][] inputTeste,
//			double[][] outputTeste, String codigoPapel, String nomeEmpresa) {
//
//		ArrayList<Pregao> pregoes = new ArrayList<Pregao>();
//
//		// double retornoPrevisto = (precoPrevisto - precoAnterior)/
//		// precoAnterior;
//
//		double anterior = 0.5d;
//		for (double atual : inputTeste[0]) {
//			double retorno = (atual - anterior) / anterior;
//
//			Pregao pregao = new Pregao("", atual);
//			pregao.setRetorno(retorno);
//
//			anterior = atual;
//
//			pregoes.add(pregao);
//		}
//
//		for (double[] item : outputTeste) {
//			double atual = item[0];
//			double retorno = (atual - anterior) / anterior;
//
//			Pregao pregao = new Pregao("", atual);
//			pregao.setRetorno(retorno);
//
//			anterior = atual;
//
//			pregoes.add(pregao);
//		}
//
//		Papel novoPapel = new Papel(codigoPapel, nomeEmpresa, pregoes);
//		return novoPapel;
//	}
	
	public static Papel convertToPapelDesejado(double[][] inputTeste,
			double[][] outputTeste, String codigoPapel, String nomeEmpresa) {
		
		ArrayList<Pregao> pregoes = new ArrayList<Pregao>();
		double anterior = 0.5d;
		for(int i = 0; i < inputTeste.length; ++i){
			
			double atual = outputTeste[i][0];
			
			double retorno = (atual - anterior) / anterior;
			
			Pregao pregao = new Pregao("", atual);
			pregao.setRetorno(retorno);

			anterior = atual;
			
			pregoes.add(pregao);
		}
		
		Papel novoPapel = new Papel(codigoPapel, nomeEmpresa, pregoes);
		return novoPapel;
	}

	public static JFreeChart createChart(XYDataset dataSet, String titulo,
			String eixoX, String eixoY) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(titulo, // title
				eixoX, // x-axis label
				eixoY, // y-axis label
				dataSet, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

//		chart.setBackgroundPaint(Color.white);
//
//		XYPlot plot = (XYPlot) chart.getPlot();
//		plot.setBackgroundPaint(Color.white);
//		plot.setDomainGridlinePaint(Color.white);
//		plot.setRangeGridlinePaint(Color.white);
//		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
//		plot.setDomainCrosshairVisible(true);
//		plot.setRangeCrosshairVisible(true);

		// plot.setRangeGridlinePaint(Color.BLACK);
		// plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		// Altero as cores das series de meus itens

//		XYItemRenderer r = plot.getRenderer();
//
//		r.setSeriesPaint(0, Color.BLACK);
//		r.setSeriesStroke(0, new BasicStroke(2));

//		r.setSeriesPaint(1, Color.BLUE);
		// r.setSeriesStroke(1, new BasicStroke(1));

//		r.setSeriesStroke(1,
//				new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
//						BasicStroke.JOIN_ROUND, 1.0f,
//						new float[] { 10.0f, 5.0f }, 5.0f));

		// DateAxis axis = (DateAxis) plot.getDomainAxis();
		// axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

		return chart;
	}

}
