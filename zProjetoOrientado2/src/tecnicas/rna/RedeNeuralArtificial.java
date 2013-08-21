package tecnicas.rna;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.error.ErrorCalculationMode;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import classes.Papel;
import classes.Pregao;
import parser.Parser;
import parser.exceptions.ParserEmptyException;
import parser.exceptions.ParserIndexOutOfBoundsException;
import tecnicas.mvs.libsvm.core.svm;
import tecnicas.mvs.libsvm.core.svm_model;
import tecnicas.mvs.libsvm.core.svm_node;
import testes.junk.QPImportanteMelhor;

import utils.Utilidades;

public class RedeNeuralArtificial {

	private BasicNetwork network;

	public ArrayList testar(double[][] inputSet, double[][] outputSet) {

		MLDataSet set = new BasicMLDataSet(inputSet, outputSet);

		// testar a rede neural
		// System.out.println("Resultados da Rede Neural:");

		double erro = 0.0;
		double erroMape = 0.0;

		for (MLDataPair pair : set) {

			final MLData output = network.compute(pair.getInput());

			double out = output.getData(0);
			double target = pair.getIdeal().getData(0);

			erro += ((out - target) * (out - target));

			// if (target == 0) {
			// target = 0.01;
			// }
			// erroMape += Math.sqrt(Math.pow((target - out) / target, 2));
			erroMape += Math.abs((target - out) / target);

			// System.out.println(pair.getInput().getData(0) + ","
			// + pair.getInput().getData(1) + ", actual="
			// + output.getData(0) + ",ideal="
			// + pair.getIdeal().getData(0));

		}

		double mse = erro / set.size();
		double rmse = Math.sqrt(mse);
		double mape = erroMape / set.size();

		// System.out.println("Mean squared error (MSE) = " + mse);
		// System.out.println("Root-Mean squared error (RMSE) = " + rmse);
		// System.out.println("Mean Absolute Percent Error (MAPE) = " + mape);

		ArrayList infos = new ArrayList();
		infos.add(mse);
		infos.add(rmse);
		infos.add(mape);
		return infos;
	}

	public ArrayList testarZZ(double[][] inputSet, double[][] outputSet) {

		MLDataSet set = new BasicMLDataSet(inputSet, outputSet);

		// testar a rede neural
		// System.out.println("Resultados da Rede Neural:");

		double erro = 0.0;
		double erroMape = 0.0;
		//
		// if (i <= setIN.length * 0.8) {
		// item = setIN[i];
		// } else {
		// for(int j = 0; j < item.length - 1; ++j){
		// item[j] = item[j+1];
		// }
		// item[item.length - 1] = output;
		// }

		double[] item = null;
		MLData output = null;
		double out = 0;
		int i = 0;

		for (MLDataPair pair : set) {
			if (i == 0) {
				item = inputSet[i];
			} else {
				for (int j = 0; j < item.length - 1; ++j) {
					item[j] = item[j + 1];
				}
				item[item.length - 1] = out;
				pair.setInputArray(item);
			}

			output = network.compute(pair.getInput());
			out = output.getData(0);

			double target = pair.getIdeal().getData(0);

			erro += ((out - target) * (out - target));

			// if (target == 0) {
			// target = 0.01;
			// }
			// erroMape += Math.sqrt(Math.pow((target - out) / target, 2));
			erroMape += Math.abs((target - out) / target);

			// System.out.println(pair.getInput().getData(0) + ","
			// + pair.getInput().getData(1) + ", actual="
			// + output.getData(0) + ",ideal="
			// + pair.getIdeal().getData(0));

			i++;

		}

		double mse = erro / set.size();
		double rmse = Math.sqrt(mse);
		double mape = erroMape / set.size();

		// System.out.println("Mean squared error (MSE) = " + mse);
		// System.out.println("Root-Mean squared error (RMSE) = " + rmse);
		// System.out.println("Mean Absolute Percent Error (MAPE) = " + mape);

		ArrayList infos = new ArrayList();
		infos.add(mse);
		infos.add(rmse);
		infos.add(mape);
		return infos;
	}

	public double predizerValorUnico(double[] itemNorm) {
		double inputSet[][] = new double[][] { itemNorm };
		double outputSet[][] = new double[1][1];

		MLDataSet set = new BasicMLDataSet(inputSet, outputSet);
		MLDataPair pair = set.get(0);

		MLData output = network.compute(pair.getInput());
		return output.getData(0);
	}

	public ArrayList treinarComValidacaoCruzada(double[][] inputTreino,
			double[][] outputTreino, double[][] inputVal, double[][] outputVal,
			int camadaDeEntrada, int camadaEscondida, int camadaDeSaida,
			double txAprendizagem, double txMomentum, int numeroEpocas) {
		System.out.print("\nTreinando rede neural...");

		ArrayList infos;

		// final MLDataSet trainingSet = TrainingSetUtil.loadCSVTOMemory(
		// CSVFormat.ENGLISH, "treinoKNN", false, 6, 1);

		// System.out.println("Criando uma rede neural...");
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, camadaDeEntrada));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				camadaEscondida));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				camadaDeSaida));
		network.getStructure().finalizeStructure();
		network.reset();

		// criando conjunto de treinamento
		MLDataSet trainingSet = new BasicMLDataSet(inputTreino, outputTreino);

		// treinando a rede neural
		MLTrain train = new Backpropagation(network, trainingSet,
				txAprendizagem, txMomentum);
		// MLTrain train = new ResilientPropagation(network, trainingSet);

		ErrorCalculation.setMode(ErrorCalculationMode.MSE);

		int epoch = 1;

		double errorVal_mape = 0;
		double errorVal_rmse = 0;
		do {
			train.iteration();

			// infos = this.testarZZ(inputVal, outputVal);
			infos = this.testar(inputVal, outputVal);
			errorVal_rmse = (Double) infos.get(1);
			errorVal_mape = (Double) infos.get(2);

			// System.out.println("Epoch #" + epoch + " Error:" +
			// errorVal_rmse);

			epoch++;
		} while (epoch < numeroEpocas && errorVal_rmse > 0.03);
		// } while (epoch < 500 || (epoch < numeroEpocas && (errorVal_mape >
		// 0.16 || errorVal_rmse > 0.04)));
		// } while (epoch < 500 || (epoch < numeroEpocas && (errorVal_mape >
		// 0.07 || errorVal_rmse > 0.03)));
		// train.finishTraining();
		Encog.getInstance().shutdown();

		System.out.println(" OK! -- Épocas = #" + epoch);

		// System.out.println("Cross Validation Mean squared error (MSE) = "
		// + infos.get(0));
		System.out.println("Cross Validation Root-Mean squared error (RMSE) = "
				+ infos.get(1));
		System.out
				.println("Cross Validation Mean Absolute Percent Error (MAPE) = "
						+ infos.get(2));

		return infos;

	}

	public ArrayList treinar(double input[][], double output[][],
			int camadaDeEntrada, int camadaEscondida, int camadaDeSaida,
			double txAprendizagem, double txMomentum, int numeroEpocas) {

		ArrayList infos;

		// final MLDataSet trainingSet = TrainingSetUtil.loadCSVTOMemory(
		// CSVFormat.ENGLISH, "treinoKNN", false, 6, 1);

		System.out.print("Treinando rede neural...");
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, camadaDeEntrada));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				camadaEscondida));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				camadaDeSaida));
		network.getStructure().finalizeStructure();
		network.reset();

		// criando conjunto de treinamento
		MLDataSet trainingSet = new BasicMLDataSet(input, output);

		// treinando a rede neural
		// MLTrain train = new Backpropagation(network, trainingSet,
		// txAprendizagem, txMomentum);
		MLTrain train = new ResilientPropagation(network, trainingSet);

		ErrorCalculation.setMode(ErrorCalculationMode.MSE);

		int epoch = 1;

		do {
			train.iteration();

			infos = this.testar(input, output);
			// System.out.println("Epoch #" + epoch + " Error:" + infos);
			epoch++;
		} while (epoch < numeroEpocas);
		// train.finishTraining();
		Encog.getInstance().shutdown();
		System.out.println(" OK!");
		return infos;

		// return infos;
	}

	private static void testeUnico(Parser p, int tamanhoJanela,
			int numDiasPrevistos, int camadaEscondida, int camadaDeSaida,
			double txAprendizagem, double txMomentum, int numeroEpocas)
			throws Exception {

		String[] ativosEscolhidos = { "AEDU3", "ALSC3", "AFLT3", "PETR3" }; // 2)usuario
																			// escolhe
																			// os
																			// ativos

		ArrayList<Papel> papeis = new ArrayList<Papel>();
		RedeNeuralArtificial rna = new RedeNeuralArtificial();
		for (String codigoPapel : ativosEscolhidos) {
			try {
				ArrayList dados = p.buscarVetorTrainTest(codigoPapel, "RNA",
						tamanhoJanela, true);

				double[][] inputTreino = (double[][]) dados.get(0);
				double[][] outputTreino = (double[][]) dados.get(1);

				double[][] inputTeste = (double[][]) dados.get(2);
				double[][] outputTeste = (double[][]) dados.get(3);

				ArrayList infosTreino = rna.treinar(inputTreino, outputTreino,
						tamanhoJanela, camadaEscondida, camadaDeSaida,
						txAprendizagem, txMomentum, numeroEpocas);

				ArrayList infosTeste = rna.testar(inputTeste, outputTeste);
				double mse = (Double) infosTeste.get(0);
				double rmse = (Double) infosTeste.get(1);
				double mape = (Double) infosTeste.get(2);

				System.out.println("Mean squared error (MSE) = " + mse);
				System.out.println("Root-Mean squared error (RMSE) = " + rmse);
				System.out.println("Mean Absolute Percent Error (MAPE) = "
						+ mape);

				Papel papel = rna.predizer(tamanhoJanela, numDiasPrevistos,
						p.buscar(codigoPapel));

				papeis.add(papel);

			} catch (ParserIndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		double R[][] = p.buscarMatrizRetorno(papeis);

		System.out.println("ok");

		QPImportanteMelhor.testeQP(R);

		System.out.println();

	}

	public Papel predizer(int tamanhoJanela, int numDiasPrevistos, Papel papel) {
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

			double precoPrevistoNormalizado = this.predizerValorUnico(itemNorm); // prevendo
																					// utilizando
																					// SVM

			// precoPrevRNA = predictRNA(); //predizer utilizando RNA !!!!

			// precoPrevistoNormalizado = precoPrevSVM / precoPrevRNA;
			// //calcular a media artimetica dos dois

			double precoPrevisto = Utilidades.desnormalizar(min, max,
					precoPrevistoNormalizado);

			double precoAnterior = pregoes.get(pregoes.size() - 1).getPreco();
			double retornoPrevisto = (precoPrevisto - precoAnterior)
					/ precoAnterior;

			if (retornoPrevisto == 0) {
				retornoPrevisto = 0.01;
			}

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

	public static void testeGeral(Parser p, int tamanhoJanela,
			int camadaEscondida, int camadaDeSaida, double txAprendizagem,
			double txMomentum, int numeroEpocas) {
		// String[] ativosEscolhidos = {"AEDU3","ALSC3","AFLT3","PETR3"};
		// //2)usuario escolhe os ativos
		String[] ativosEscolhidos = { "AEDU3", "ALSC3", "AFLT3", "PETR3" }; // 2)usuario
		// escolhe
		// os ativos
		// String[] ativosEscolhidos = p.getCodigosPapeis();

		// ou serao escolhidos baseados no indice sharpe!

		// String codigosPapeis[] =
		// {"AEDU3","ALSC3","AFLT3","AGRO3","AGEN11","ALPA4","ABCB4","ABCB4F","PETR3"};

		ArrayList<Papel> papeis = new ArrayList<Papel>();
		RedeNeuralArtificial rna = new RedeNeuralArtificial();
		for (String codigoPapel : ativosEscolhidos) {
			try {
				// System.out.println(codigoPapel);

				// no index[0] está um vetor de entrada (input) e no index[1] um
				// vetor de saída (output), ambos no formato double.
				ArrayList dados = p.buscarVetorTrainValTest(codigoPapel, "RNA",
						tamanhoJanela, true);
				// System.out.println(dados);

				double[][] inputTreino = (double[][]) dados.get(0);
				double[][] outputTreino = (double[][]) dados.get(1);

				double[][] inputVal = (double[][]) dados.get(2);
				double[][] outputVal = (double[][]) dados.get(3);

				// o método treinar() retorna um ArrayList com as métricas: MSE,
				// RMSE e MAPE
				ArrayList infosTreino = rna.treinarComValidacaoCruzada(
						inputTreino, outputTreino, inputVal, outputVal,
						tamanhoJanela, camadaEscondida, camadaDeSaida,
						txAprendizagem, txMomentum, numeroEpocas);

				// ArrayList infosTreino = rna.treinar(inputTreino,
				// outputTreino,
				// tamanhoJanela, camadaEscondida, camadaDeSaida,
				// txAprendizagem, txMomentum, numeroEpocas);

				// double a[][] = (double[][]) dados.get(0);
				// System.out.println(a);
				double[][] inputTeste = (double[][]) dados.get(4);
				double[][] outputTeste = (double[][]) dados.get(5);
				ArrayList infosTeste = rna.testar(inputTeste, outputTeste);

				System.out.println("\nTestando rede neural...");
				System.out.println("Mean squared error (MSE) = "
						+ infosTeste.get(0));
				System.out.println("Root-Mean squared error (RMSE) = "
						+ infosTeste.get(1));
				System.out.println("Mean Absolute Percent Error (MAPE) = "
						+ infosTeste.get(2) + "\n");

			} catch (ParserIndexOutOfBoundsException e) {

				e.printStackTrace();
			} catch (ParserEmptyException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws Exception {

		Parser p = new Parser();
		String path = "base_dados_bovespa/COTAHIST_A2012.TXT";
		p.carregarBase(path);

		// #Parametros??
		int numDiasPrevistos = 10; // 1)usuario define -> numeros de dias a
									// serem previstos ::

		int tamanhoJanela = 10; // será a quantidade de neurônios na camada de
								// entrada

		// De acordo com HECHT - NIELSEN, a camada oculta deve ter por volta de
		// 2i+1 neurônios, onde i é o número de variáveis de entrada
		int camadaEscondida = (int) (2.5 * tamanhoJanela + 1);

		int camadaDeSaida = 1; // apenas um neurônio na camada de saída

		double txAprendizagem = 0.9;
		double txMomentum = 0.95;
		int numeroEpocas = 10000;

		// testeUnico(p, tamanhoJanela);
		// testeGeral(p, tamanhoJanela, camadaEscondida, camadaDeSaida,
		// txAprendizagem, txMomentum, numeroEpocas);

		testeUnico(p, tamanhoJanela, numDiasPrevistos, camadaEscondida,
				camadaDeSaida, txAprendizagem, txMomentum, numeroEpocas);

		// System.out.println("TERMINOU");

	}

	/**
	 * retornar um arrayList contendo no seu inteiros dois arraylist com (0)
	 * erros do conj. de treino e (1) erros do conj. de validação
	 */
	public ArrayList trainCV_geraGrafico(double[][] inputTreino,
			double[][] outputTreino, double[][] inputVal, double[][] outputVal,
			int camadaDeEntrada, int camadaEscondida, int camadaDeSaida,
			double txAprendizagem, double txMomentum, int numeroEpocas) {

		System.out.print("\nTreinando rede neural...");

		ArrayList erros = new ArrayList();
		;
		ArrayList<Double> errosTreinoRMSE = new ArrayList<Double>();
		ArrayList<Double> errosCVRMSE = new ArrayList<Double>();

		ArrayList<Double> errosTreinoMAPE = new ArrayList<Double>();
		ArrayList<Double> errosCVMAPE = new ArrayList<Double>();

		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, camadaDeEntrada));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				camadaEscondida));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				camadaDeSaida));
		network.getStructure().finalizeStructure();
		network.reset();

		// criando conjunto de treinamento
		MLDataSet trainingSet = new BasicMLDataSet(inputTreino, outputTreino);

		// treinando a rede neural
		MLTrain train = new Backpropagation(network, trainingSet,
				txAprendizagem, txMomentum);
		// MLTrain train = new ResilientPropagation(network, trainingSet);

		ErrorCalculation.setMode(ErrorCalculationMode.MSE);

		int epoch = 1;

		double errorVal_mape = 0;
		double errorVal_rmse = 0;
		ArrayList infosCV;
		do {
			train.iteration();

			// ArrayList infosTreino = this.testarZZ(inputTreino, outputTreino);
			ArrayList infosTreino = this.testar(inputTreino, outputTreino);

			double errorTreino_rmse = (Double) infosTreino.get(1);
			double errorTreino_mape = (Double) infosTreino.get(2);
			errosTreinoRMSE.add(errorTreino_rmse);
			errosTreinoMAPE.add(errorTreino_mape);

			infosCV = this.testar(inputVal, outputVal);
			// infosCV = this.testar(inputVal, outputVal);
			errorVal_rmse = (Double) infosCV.get(1);
			errorVal_mape = (Double) infosCV.get(2);
			errosCVRMSE.add(errorVal_rmse);
			errosCVMAPE.add(errorVal_mape);

			// System.out.println("Epoch #" + epoch + " Error:" + infos);
			epoch++;
			// } while (epoch < numeroEpocas && (errorVal_mape >= 0.3 ||
			// errorVal_rmse >= 0.08));
		} while (epoch < numeroEpocas && errorVal_rmse > 0.03);
		// train.finishTraining();
		Encog.getInstance().shutdown();

		System.out.println(" OK!");

		System.out.println("Cross Validation Mean squared error (MSE) = "
				+ infosCV.get(0));
		System.out.println("Cross Validation Root-Mean squared error (RMSE) = "
				+ infosCV.get(1));
		System.out
				.println("Cross Validation Mean Absolute Percent Error (MAPE) = "
						+ infosCV.get(2));

		erros.add(errosTreinoRMSE);
		erros.add(errosCVRMSE);

		return erros;

	}

	/**
	 * retorna um arraylist com uma série de saídas (0) desejadas e (1)
	 * previstas
	 */
	public ArrayList testar_geraGrafico(double[][] inputSet,
			double[][] outputSet) {
		MLDataSet set = new BasicMLDataSet(inputSet, outputSet);

		// testar a rede neural
		// System.out.println("Resultados da Rede Neural:");
		ArrayList logDesejado = new ArrayList();
		ArrayList logPrevisto = new ArrayList();

		for (MLDataPair pair : set) {

			final MLData output = network.compute(pair.getInput());

			double out = output.getData(0);
			logPrevisto.add(out);

			double target = pair.getIdeal().getData(0);
			logDesejado.add(target);

			// + pair.getIdeal().getData(0));

		}

		ArrayList logs = new ArrayList();
		logs.add(logDesejado);
		logs.add(logPrevisto);
		return logs;
	}

	public ArrayList treinarComValidacaoCruzadaFiltro(double[][] inputTreino,
			double[][] outputTreino, double[][] inputVal, double[][] outputVal,
			int camadaDeEntrada, int camadaEscondida, int camadaDeSaida,
			double txAprendizagem, double txMomentum, int numeroEpocas) {
		System.out.print("\nTreinando rede neural...");

		ArrayList infos;

		// final MLDataSet trainingSet = TrainingSetUtil.loadCSVTOMemory(
		// CSVFormat.ENGLISH, "treinoKNN", false, 6, 1);

		// System.out.println("Criando uma rede neural...");
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, camadaDeEntrada));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				camadaEscondida));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
				camadaDeSaida));
		network.getStructure().finalizeStructure();
		network.reset();

		// criando conjunto de treinamento
		MLDataSet trainingSet = new BasicMLDataSet(inputTreino, outputTreino);

		// treinando a rede neural
		MLTrain train = new Backpropagation(network, trainingSet,
				txAprendizagem, txMomentum);
		// MLTrain train = new ResilientPropagation(network, trainingSet);

		ErrorCalculation.setMode(ErrorCalculationMode.MSE);

		int epoch = 1;

		double minErroVal_rmse = Double.MAX_VALUE;

		HashMap<Struct,Double> minWeigths = this.getWeigths();
		// minWeigths = network.getWeight(fromLayer, fromNeuron, toNeuron);
		// System.out.println("layer count : "+network.getLayerCount());
		// System.out.println("camada 1 "+network.getLayerNeuronCount(0));
		// System.out.println("camada 2 "+network.getLayerNeuronCount(1));
		// System.out.println("camada 3 "+network.getLayerNeuronCount(2));

		Struct struct = new Struct();
		for (int fromLayer = 0; fromLayer < network.getLayerCount() - 1; fromLayer++) {
			for (int fromNeuron = 0; fromNeuron < network
					.getLayerNeuronCount(fromLayer); fromNeuron++) {
				for (int toNeuron = 0; toNeuron < network
						.getLayerNeuronCount(fromLayer + 1); toNeuron++) {
					// System.out.println("neuronio "+fromNeuron+" da camada "+fromLayer+" => neuronio "+toNeuron+" da camada "+(fromLayer+1));
					//System.out.println(network.getWeight(fromLayer,fromNeuron, toNeuron));
					
					struct.setFromLayer(fromLayer);
					struct.setFromNeuron(fromNeuron);
					struct.setToNeuron(toNeuron);
					
//					System.out.println(network.getWeight(fromLayer,fromNeuron, toNeuron) == minWeigths.get(struct));
					System.out.println( minWeigths.get(struct));
					
					System.out.println(minWeigths.keySet());
				}
			}
		}

		double errorVal_mape = 0;
		double errorVal_rmse = 0;
		do {
			train.iteration();

			// infos = this.testarZZ(inputVal, outputVal);
			infos = this.testar(inputVal, outputVal);

			errorVal_rmse = (Double) infos.get(1);
			if (minErroVal_rmse > errorVal_rmse) {
				minErroVal_rmse = errorVal_rmse;

			}

			errorVal_mape = (Double) infos.get(2);

			// System.out.println("Epoch #" + epoch + " Error:" +
			// errorVal_rmse);

			epoch++;
		} while (epoch < numeroEpocas);
		// } while (epoch < 500 || (epoch < numeroEpocas && (errorVal_mape >
		// 0.16 || errorVal_rmse > 0.04)));
		// } while (epoch < 500 || (epoch < numeroEpocas && (errorVal_mape >
		// 0.07 || errorVal_rmse > 0.03)));
		// train.finishTraining();
		Encog.getInstance().shutdown();

		System.out.println(" OK! -- Épocas = #" + epoch);

		// System.out.println("Cross Validation Mean squared error (MSE) = "
		// + infos.get(0));
		System.out.println("Cross Validation Root-Mean squared error (RMSE) = "
				+ infos.get(1));
		System.out
				.println("Cross Validation Mean Absolute Percent Error (MAPE) = "
						+ infos.get(2));

		return infos;
	}

	private HashMap<Struct,Double> getWeigths() {

		HashMap<Struct,Double> h = new HashMap<Struct,Double>();
		
		
		for (int fromLayer = 0; fromLayer < network.getLayerCount() - 1; fromLayer++) {
			for (int fromNeuron = 0; fromNeuron < network
					.getLayerNeuronCount(fromLayer); fromNeuron++) {
				for (int toNeuron = 0; toNeuron < network
						.getLayerNeuronCount(fromLayer + 1); toNeuron++) {

					Struct struct = new Struct(fromLayer,fromNeuron,toNeuron);
					h.put(struct, network.getWeight(fromLayer, fromNeuron, toNeuron));
					// System.out.println("neuronio "+fromNeuron+" da camada "+fromLayer+" => neuronio "+toNeuron+" da camada "+(fromLayer+1));
					// System.out.println(network.getWeight(fromLayer,
					// fromNeuron, toNeuron));
				}
			}
		}
		
		return h;
	}

}
