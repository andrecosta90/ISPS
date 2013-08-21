package tecnicas.mvs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
import tecnicas.mvs.libsvm.svm_predict;
import tecnicas.mvs.libsvm.svm_train;
import tecnicas.mvs.libsvm.core.svm;
import tecnicas.mvs.libsvm.core.svm_model;
import tecnicas.mvs.libsvm.core.svm_node;
import testes.junk.QPImportanteMelhor;
import utils.Utilidades;

public class SupportVectorMachine {

	public void testar(String baseTeste, String arquivoModelo,
			String arquivoSaida, ArrayList infos) throws IOException {
		String arquivoTeste = "test";

		String[] argsPredict = { arquivoTeste, arquivoModelo, arquivoSaida };
		svm_predict predict = new svm_predict();
		predict.executar(argsPredict, baseTeste, infos);
	}

	public void treinar(String baseTreino, String arquivoModelo, int tipoSVM,
			int tipoKernel, int kFold, double gamma, double custo,
			double epsilon, ArrayList infos) throws IOException {
		String arquivoTreinamento = "train";

		String svmType = String.valueOf(tipoSVM);
		String kernelType = String.valueOf(tipoKernel);
		String g = String.valueOf(gamma);
		String cost = String.valueOf(custo);
		String p = String.valueOf(epsilon);

		String argsTrain[] = { "-s", svmType, "-t", kernelType, "-g", g, "-c",
				cost, "-p", p, arquivoTreinamento, arquivoModelo };

		if (kFold > 2) {

			String k = String.valueOf(kFold);
			String[] argsTrainKFold = { "-s", svmType, "-t", kernelType, "-v",
					k, "-g", g, "-c", cost, "-p", p, arquivoTreinamento,
					arquivoModelo };

			argsTrain = argsTrainKFold;
		}

		svm_train train = new svm_train();
		train.executar(argsTrain, baseTreino, infos);
	}

	public double predizerValorUnico(double item[], String arquivoModelo)
			throws IOException {
		// predizer um único valor
		svm_node X[] = new svm_node[item.length];

		for (int i = 0; i < item.length; ++i) {
			svm_node x = new svm_node();
			x.index = (i + 1);
			x.value = item[i];

			X[i] = x;
		}
		svm_model model = svm.svm_load_model(arquivoModelo);

		return svm.svm_predict(model, X);
	}

	private static void testeUnico() throws Exception {
		Parser p = new Parser();
		String path = "base_dados_bovespa/COTAHIST_A2012.TXT";
		p.carregarBase(path);

		// #Parametros
		int tipoSVM = 3; // e-SVR
		int tipoKernel = 2; // RBF Kernel
		int kFold = 10; // valores <= 2 desativam validação cruzada !!
		// double gamma = 0.02;
		// double custo = 1000;
		// double epsilon = 0.02;
		// double gamma = Math.pow(2, -9);
		// double custo = Math.pow(2, 5);
		// double epsilon = Math.pow(2, -5);
		double gamma = Math.pow(2, -9.1);
		double custo = Math.pow(2, 9.1);
		double epsilon = Math.pow(2, -9);

		int tamanhoJanela = 10; // input SVM/RNA

		int numDiasPrevistos = 10; // 1)usuario define -> numeros de dias a
									// serem previstos ::

		String[] ativosEscolhidos = { "AEDU3", "ALSC3", "AFLT3", "AGRO3",
				"AGEN11", "ALPA4", "ABCB4", "ABCB4F", "PETR3" }; // 2)usuario
																	// escolhe
																	// os ativos
		// ou serao escolhidos baseados no indice sharpe!

		// String codigosPapeis[] =
		// {"AEDU3","ALSC3","AFLT3","AGRO3","AGEN11","ALPA4","ABCB4","ABCB4F","PETR3"};

		ArrayList<Papel> papeis = new ArrayList<Papel>();

		SupportVectorMachine svm = new SupportVectorMachine();
		for (String codigoPapel : ativosEscolhidos) { // é feito o treinamento
														// para cada ativo
														// escolhido
			try {

				// String[] dados = p.buscarFormatoTrainTest(codigoPapel, "SVM",
				// 6, true);
				String dados = p
						.buscar(codigoPapel, "SVM", tamanhoJanela, true);

				// String arquivoModelo = "model_" + codigoPapel;// concat com o
				// nome do
				// codigo??

				String arquivoModelo = "model"; // DELETAR O MODEL NO FINAL !!!!

				// String arquivoSaida = "out_" + codigoPapel;
				String arquivoSaida = "out";

				svm.treinar(dados, arquivoModelo, tipoSVM, tipoKernel, kFold,
						gamma, custo, epsilon, null);

				svm.testar(dados, arquivoModelo, arquivoSaida, null);

				Papel papel = svm.predizer(arquivoModelo, arquivoSaida,
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
	
	public Papel predizer(double[][] inputTeste,
			String codigoPapel, String nomeEmpresa, String arquivoModelo) throws IOException {
		ArrayList<Pregao> pregoes = new ArrayList<Pregao>();

		double anterior = 0.5d;
		for(double[] item : inputTeste){
			double atual = this.predizerValorUnico(item, arquivoModelo);
			double retorno = (atual - anterior) / anterior;
			
			Pregao pregao = new Pregao("", atual);
			pregao.setRetorno(retorno);

			anterior = atual;
			
			pregoes.add(pregao);
		}
		
		Papel novoPapel = new Papel(codigoPapel, nomeEmpresa, pregoes);
		return novoPapel;
	}

	public Papel predizer(String arquivoModelo, String arquivoSaida,
			int tamanhoJanela, int numDiasPrevistos, Papel papel)
			throws IOException {

		// System.out.println(papel);
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
					arquivoModelo); // prevendo utilizando SVM

			// precoPrevRNA = predictRNA(); //predizer utilizando RNA !!!!

			// precoPrevistoNormalizado = precoPrevSVM / precoPrevRNA;
			// //calcular a media artimetica dos dois

			double precoPrevisto = Utilidades.desnormalizar(min, max,
					precoPrevistoNormalizado);

			double precoAnterior = pregoes.get(pregoes.size() - 1).getPreco();
			double retornoPrevisto = (precoPrevisto - precoAnterior)
					/ precoAnterior;
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

	public static void testeGeral() throws IOException {
		Parser p = new Parser();
		String path = "base_dados_bovespa/COTAHIST_A2012.TXT";
		p.carregarBase(path);

		// #Parametros
		int tipoSVM = 3; // e-SVR
		int tipoKernel = 2; // RBF Kernel
		int kFold = 10; // valores <= 2 desativam validação cruzada !!
		double gamma = Math.pow(2, -11);
		double custo = Math.pow(2, 5);
		double epsilon = Math.pow(2, -5);

		// String[] ativosEscolhidos = p.getCodigosPapeis();
		String[] ativosEscolhidos = { "AFLT3" };

		SupportVectorMachine svm = new SupportVectorMachine();
		for (String codigoPapel : ativosEscolhidos) {
			try {
				// String[] dados = p.buscarFormatoTrainTest(codigoPapel, "SVM",
				// 6, true);
				String[] dados = p.buscarFormatoTrainTest(codigoPapel, "SVM",
						10, true);

				String arquivoModelo = "model_" + codigoPapel;// concat com o
																// nome do
																// codigo??
				String arquivoSaida = "out";

				svm.treinar(dados[0], arquivoModelo, tipoSVM, tipoKernel,
						kFold, gamma, custo, epsilon, null);

				// A PREDIÇÃO NÃO FUNCIONA UTILIZANDO VALIDAÇÃO CRUZADA..
				svm.testar(dados[1], arquivoModelo, arquivoSaida, null);
			} catch (ParserIndexOutOfBoundsException e) {
				// e.printStackTrace();
			} catch (ParserEmptyException e) {
				// e.printStackTrace();

			}
		}

		// double[] item = {0.1,0.2,0.3,0.4,0.5,0.6};
		// System.out.println(svm.predizerValorUnico(item , arquivoModelo));
	}

	public static void main(String[] args) throws Exception {

		// testeGeral();
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
	
	public ArrayList testar_geraGraficoZZ(ArrayList inputOutputSet,
			String arquivoModelo) throws IOException {

		double[][] setIN = (double[][]) inputOutputSet.get(0);
		double[][] setOUT = (double[][]) inputOutputSet.get(1);
		// MLDataSet set = new BasicMLDataSet(inputSet, outputSet);

		// testar a rede neural
		// System.out.println("Resultados da Rede Neural:");
		ArrayList logDesejado = new ArrayList();
		ArrayList logPrevisto = new ArrayList();
		ArrayList logAdvinhado = new ArrayList(); // com a propria previsao da
													// mvs, e nao com a entrada
											// da base de dados
		double[] item = null;
		double output = 0;
		for (int i = 0; i < setIN.length; ++i) {
			if (i <= setIN.length * 0.8) {
				item = setIN[i];
			} else {
				for(int j = 0; j < item.length - 1; ++j){
					item[j] = item[j+1];
				}
				item[item.length - 1] = output;
			}
			output = predizerValorUnico(item, arquivoModelo);
			logPrevisto.add(output);

			double target = setOUT[i][0];

			logDesejado.add(target);
		}

		ArrayList logs = new ArrayList();
		logs.add(logDesejado);
		logs.add(logPrevisto);
		return logs;

	}





//	public ArrayList testar_geraGrafico(Papel papel, int tamanhoJanela, String arquivoModelo) {
//	
//	}

}
