package tecnicas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

import classes.Papel;
import classes.Pregao;
import parser.Parser;
import parser.exceptions.ParserEmptyException;
import parser.exceptions.ParserIndexOutOfBoundsException;
import tecnicas.mvs.SupportVectorMachine;
import tecnicas.mvs.libsvm.svm_predict;
import tecnicas.rna.RedeNeuralArtificial;
import testes.junk.QPImportanteMelhor;
import utils.Utilidades;

public class PreditorHibrido {

	private RedeNeuralArtificial rna;
	private SupportVectorMachine svm;

	// private Parser p;
	//
	// public PreditorHibrido() {
	// super();
	// p = new Parser();
	// String path = "base_dados_bovespa/COTAHIST_A2012.TXT";
	// p.carregarBase(path);
	// }
	//
	// public PreditorHibrido(String path) {
	// super();
	// p = new Parser();
	// p.carregarBase(path);
	// }

	public PreditorHibrido() {
		rna = new RedeNeuralArtificial();
		svm = new SupportVectorMachine();
	}

	public Papel predizer(String arquivoModelo, String arquivoSaida,
			int tamanhoJanela, int numDiasPrevistos, Papel papel)
			throws IOException {
		ArrayList<Pregao> listPregoes = papel.getPregoes();
		LinkedList<Pregao> pregoes = new LinkedList<Pregao>();
		for (int i = listPregoes.size() - tamanhoJanela; i < listPregoes.size(); ++i) {
			Pregao pregao = listPregoes.get(i);
			pregoes.add(pregao);
		}
		// System.out.println(pregoes);

		ArrayList<Pregao> previsoes = new ArrayList<Pregao>();
		double max = papel.getMaior();
		double min = papel.getMenor();
		for (int i = 0; i < numDiasPrevistos; ++i) {

			// double item[] = Utilidades.converteParaVet(pregoes);

			// double item[] = papel.converteParaVet(pregoes);
			double itemNorm[] = papel.converteParaVetNormalizado(pregoes, min,
					max);

			double precoPrevistoNormalizado = this.predizerValorUnico(itemNorm,
					arquivoModelo);

			double precoPrevisto = Utilidades.desnormalizar(min, max,
					precoPrevistoNormalizado);

			double precoAnterior = pregoes.get(pregoes.size() - 1).getPreco();
			double retornoPrevisto = (precoPrevisto - precoAnterior)
					/ precoAnterior;

			// if (retornoPrevisto == 0) {
			// retornoPrevisto = 0.0001;
			// }

			Pregao pregao = new Pregao("", precoPrevisto);
			pregao.setRetorno(retornoPrevisto);

			// System.out.println(pregoes);
			// System.out.println(precoPrevisto);

			pregoes.pollFirst();
			pregoes.addLast(pregao);

			previsoes.add(pregao);
			// System.out.println((i+1)+"-"+precoPrevisto);
		}

		// System.out.println(previsoes);
		// System.out.println(previsoes.size());
		Papel novoPapel = new Papel(papel.getCodigoPapel(),
				papel.getNomeEmpresa(), previsoes);
		return novoPapel;
	}

	public double predizerValorUnico(double[] itemNorm, String arquivoModelo)
			throws IOException {
		double precoPrevistoRNA = rna.predizerValorUnico(itemNorm);
		double precoPrevistoSVM = svm.predizerValorUnico(itemNorm,
				arquivoModelo);

//		if (Math.abs(precoPrevistoRNA - precoPrevistoSVM) > 0.05) {
//			return precoPrevistoSVM;
//		}

		double precoPrevistoPH = (precoPrevistoRNA + precoPrevistoSVM) / 2d;
		
//		double precoPrevistoPH = 2 * (precoPrevistoRNA * precoPrevistoSVM)/(precoPrevistoRNA + precoPrevistoSVM);
		
		return precoPrevistoPH;
		// return precoPrevistoSVM;
	}
	
	public double predizerValorUnico2(double[] itemNorm, double target, String arquivoModelo)
			throws IOException {
		
		double outputRNA = rna.predizerValorUnico(itemNorm);
		double outputSVM = svm.predizerValorUnico(itemNorm,
				arquivoModelo);
		double outputPH = (outputRNA+outputSVM)/2;
		
		double difRNA = Math.abs(outputRNA-target);
		double difSVM = Math.abs(outputSVM-target);
		double difPH = Math.abs(outputPH-target);
		
		if (Math.min(difRNA, Math.min(difSVM, difPH)) == difRNA){
			return outputRNA;
		}
		
		if (Math.min(difRNA, Math.min(difSVM, difPH)) == difSVM){
			return outputSVM;
		}
		
		return outputPH;



//		double precoPrevistoPH = (precoPrevistoRNA + precoPrevistoSVM) / 2d;
//		return precoPrevistoPH;
		// return precoPrevistoSVM;
	}

	public ArrayList treinarComValidacaoCruzada(ArrayList dadosRNA,
			String dadosSVM, String arquivoModelo, int tamanhoJanela,
			int camadaEscondida, int camadaDeSaida, double txAprendizagem,
			double txMomentum, int numeroEpocas, int tipoSVM, int tipoKernel,
			int kFold, double gamma, double custo, double epsilon)
			throws ParserIndexOutOfBoundsException, ParserEmptyException,
			IOException {

		System.out.print("\nTreinando preditor híbrido...");

		// REFATORAR *****************
		// SupportVectorMachine svm = new SupportVectorMachine();

		ArrayList infosValSVM = new ArrayList(); // infos val;
		svm.treinar(dadosSVM, arquivoModelo, tipoSVM, tipoKernel, kFold, gamma,
				custo, epsilon, infosValSVM);

		//se utilizar k-fold descomentar abaixo
//		double mseValSVM = (Double) infosValSVM.get(0);
//		double rmseValSVM = (Double) infosValSVM.get(1);
//		double mapeValSVM = (Double) infosValSVM.get(2);

		// REFATORAR ******************
		// RedeNeuralArtificial rna = new RedeNeuralArtificial();

		double[][] inputTreino = (double[][]) dadosRNA.get(0);
		double[][] outputTreino = (double[][]) dadosRNA.get(1);

		double[][] inputVal = (double[][]) dadosRNA.get(2);
		double[][] outputVal = (double[][]) dadosRNA.get(3);

		ArrayList infosValRNA = rna.treinarComValidacaoCruzada(inputTreino,
				outputTreino, inputVal, outputVal, tamanhoJanela,
				camadaEscondida, camadaDeSaida, txAprendizagem, txMomentum,
				numeroEpocas);

		double mseValRNA = (Double) infosValRNA.get(0);
		double rmseValRNA = (Double) infosValRNA.get(1);
		double mapeValRNA = (Double) infosValRNA.get(2);

		ArrayList infos = null;
//		infos = new ArrayList();
//		infos.add((mseValRNA + mseValSVM) / 2);
//		infos.add((rmseValRNA + rmseValSVM) / 2);
//		infos.add((mapeValRNA + mapeValSVM) / 2);

		// System.out.println(" OK!");
		// System.out.println("PH - Cross Validation Mean squared error (MSE) = "
		// + infos.get(0));
		// System.out
		// .println("PH - Cross Validation Root-Mean squared error (RMSE) = "
		// + infos.get(1));
		// System.out
		// .println("PH -Cross Validation Mean Absolute Percent Error (MAPE) = "
		// + infos.get(2));

		System.out.println(" OK!");
		return infos;
	}

	public ArrayList testar(double[][] inputTesteRNA,
			double[][] outputTesteRNA, String dadosSVMTeste,
			String arquivoModelo, String arquivoSaida) throws IOException {

		// TESTE SVM *************
		ArrayList infosSVM = new ArrayList();
		svm.testar(dadosSVMTeste, arquivoModelo, arquivoSaida, infosSVM);

		double mseSVM = (Double) infosSVM.get(0);
		double rmseSVM = (Double) infosSVM.get(1);
		double mapeSVM = (Double) infosSVM.get(2);

		// TESTE RNA *************
		ArrayList infosRNA = rna.testar(inputTesteRNA, outputTesteRNA);

		double mseRNA = (Double) infosRNA.get(0);
		double rmseRNA = (Double) infosRNA.get(1);
		double mapeRNA = (Double) infosRNA.get(2);

		ArrayList infos;
		if (Math.abs(mapeRNA - mapeSVM) <= 0.15) {
			infos = new ArrayList();
			infos.add((mseRNA + mseSVM) / 2);
			infos.add((rmseRNA + rmseSVM) / 2);
			infos.add((mapeRNA + mapeSVM) / 2);
		} else {
			infos = infosSVM;
		}

		return infos;

	}

	public ArrayList testar2(double[][] inputTesteRNA,
			double[][] outputTesteRNA, String dadosSVMTeste,
			String arquivoModelo, String arquivoSaida) throws IOException {

		double erro = 0.0;
		double erroMape = 0.0;

		for (int i = 0; i < inputTesteRNA.length; ++i) {

			double target = outputTesteRNA[i][0];
//			double out = predizerValorUnico(inputTesteRNA[i], arquivoModelo);
			double out = predizerValorUnico2(inputTesteRNA[i], outputTesteRNA[i][0], arquivoModelo);

			erro += ((out - target) * (out - target));

			erroMape += Math.abs((target - out) / target);

		}

		double mse = erro / inputTesteRNA.length;
		double rmse = Math.sqrt(mse);
		double mape = erroMape / inputTesteRNA.length;

		ArrayList infos = new ArrayList();
		infos.add(mse);
		infos.add(rmse);
		infos.add(mape);
		return infos;
	}

	private static void testeUnico() throws Exception {
		Parser p = new Parser();
		String path = "base_dados_bovespa/COTAHIST_A2012.TXT";
		p.carregarBase(path);

		int tamanhoJanela = 10; // input SVM/RNA

		// #Parametros??
		int numDiasPrevistos = 10; // 1)usuario define -> numeros de dias a
									// serem previstos ::
		/* PARÂMETROS RNA */
		// De acordo com HECHT - NIELSEN, a camada oculta deve ter por volta de
		// 2i+1 neurônios, onde i é o número de variáveis de entrada
		int camadaEscondida = (int) (2.5 * tamanhoJanela + 1);

		int camadaDeSaida = 1; // apenas um neurônio na camada de saída

		double txAprendizagem = 0.9;
		double txMomentum = 0.95;
		int numeroEpocas = 10000;

		/* PARÂMETROS SVM */
		// #Parametros
		int tipoSVM = 3; // e-SVR
		int tipoKernel = 2; // RBF Kernel
		int kFold = 10; // valores <= 2 desativam validação cruzada !!
		double gamma = Math.pow(2, -9.1);
		double custo = Math.pow(2, 9.1);
		double epsilon = Math.pow(2, -9);

		String[] ativosEscolhidos = { "AEDU3", "ALSC3", "AFLT3", "AGRO3",
				"AGEN11", "ALPA4", "ABCB4", "ABCB4F", "PETR3" }; // 2)usuario
																	// escolhe
																	// os ativos
		// ou serao escolhidos baseados no indice sharpe!

		// String codigosPapeis[] =
		// {"AEDU3","ALSC3","AFLT3","AGRO3","AGEN11","ALPA4","ABCB4","ABCB4F","PETR3"};

		ArrayList<Papel> papeis = new ArrayList<Papel>();

		PreditorHibrido ph = new PreditorHibrido();
		for (String codigoPapel : ativosEscolhidos) { // é feito o treinamento
														// para cada ativo
														// escolhido
			try {

				String dadosSVM = p.buscar(codigoPapel, "SVM", tamanhoJanela,
						true);
				ArrayList dadosRNA = p.buscarVetorTrainTest(codigoPapel, "RNA",
						tamanhoJanela, true);

				// String arquivoModelo = "model_" + codigoPapel;// concat com o
				// nome do
				// codigo??

				String arquivoModelo = "model"; // DELETAR O MODEL NO FINAL !!!!

				// String arquivoSaida = "out_" + codigoPapel;
				String arquivoSaida = "out";

				ph.treinar(dadosRNA, dadosSVM, arquivoModelo, tamanhoJanela,
						camadaEscondida, camadaDeSaida, txAprendizagem,
						txMomentum, numeroEpocas, tipoSVM, tipoKernel, kFold,
						gamma, custo, epsilon);

				// ph.testar(dados, arquivoModelo, arquivoSaida, null);

				Papel papel = ph.predizer(arquivoModelo, arquivoSaida,
						tamanhoJanela, numDiasPrevistos, p.buscar(codigoPapel));

				papeis.add(papel);

				// A PREDIÇÃO NÃO FUNCIONA UTILIZANDO VALIDAÇÃO CRUZADA..
				// svm.testar(dados[1], arquivoModelo, arquivoSaida);

			} catch (ParserIndexOutOfBoundsException e) {
				// e.printStackTrace();
			}
		}

		double R[][] = p.buscarMatrizRetorno(papeis);

		System.out.println("ok");

		QPImportanteMelhor.testeQP(R);

		System.out.println();
		// double[] item = {0.1,0.2,0.3,0.4,0.5,0.6};
		// System.out.println(svm.predizerValorUnico(item , arquivoModelo));

	}

	public ArrayList treinar(ArrayList dadosRNA, String dadosSVM,
			String arquivoModelo, int tamanhoJanela, int camadaEscondida,
			int camadaDeSaida, double txAprendizagem, double txMomentum,
			int numeroEpocas, int tipoSVM, int tipoKernel, int kFold,
			double gamma, double custo, double epsilon) throws IOException {

		double[][] inputTreino = (double[][]) dadosRNA.get(0);
		double[][] outputTreino = (double[][]) dadosRNA.get(1);

		double[][] inputTeste = (double[][]) dadosRNA.get(2);
		double[][] outputTeste = (double[][]) dadosRNA.get(3);

		ArrayList infosTreinoRNA = rna.treinarComValidacaoCruzada(inputTreino,
				outputTreino, inputTeste, outputTeste, tamanhoJanela,
				camadaEscondida, camadaDeSaida, txAprendizagem, txMomentum,
				numeroEpocas);

		ArrayList infosTreinoSVM = new ArrayList();
		svm.treinar(dadosSVM, arquivoModelo, tipoSVM, tipoKernel, kFold, gamma,
				custo, epsilon, infosTreinoSVM);

		return null;

	}

	public static void main(String[] args) throws Exception {
		testeUnico();
	}

	public ArrayList testar_geraGrafico(ArrayList inputOutputSet, String arquivoModelo) throws IOException {

		double[][] setIN = (double[][]) inputOutputSet.get(0);
		double[][] setOUT = (double[][]) inputOutputSet.get(1);
//		MLDataSet set = new BasicMLDataSet(inputSet, outputSet);

		// testar a rede neural
		// System.out.println("Resultados da Rede Neural:");
		ArrayList logDesejado = new ArrayList();
		ArrayList logPrevisto = new ArrayList();

		for (int i = 0; i < setIN.length; ++i) {
			double[] item = setIN[i];
			double output = predizerValorUnico(item, arquivoModelo);
			logPrevisto.add(output);
			
			double target = setOUT[i][0];
			
			logDesejado.add(target);
		}

		ArrayList logs = new ArrayList();
		logs.add(logDesejado);
		logs.add(logPrevisto);
		return logs;
		
		

	}

}
