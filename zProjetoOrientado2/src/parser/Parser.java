package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import parser.exceptions.ParserEmptyException;
import parser.exceptions.ParserIndexOutOfBoundsException;
import parser.exceptions.ParserKeyException;
import parser.exceptions.ParserSupportException;

import classes.Papel;
import classes.Pregao;
import utils.Utilidades;
import emfproject.io.ReadFile;

public class Parser {

	private HashMap<String, Papel> h; // h[codigo] retorna o papel
	// private HashMap<String,String> hAux; //hAux[nome] retorna o codigo
	private ArrayList<String> nomesPapeis;

	public void carregarBase(String path) {
		ReadFile read = new ReadFile();
		read.open(path); // mudar o nome
							// do arquivo
							// depois!!

		h = new HashMap<String, Papel>();
		// hAux = new HashMap<String, String>();

		nomesPapeis = new ArrayList<String>();

		String linha = read.readLine();// pula a primeira linha
		linha = read.readLine();
		while (linha != null) {
			// System.out.println(linha);

			try {
				String codPapel = linha.substring(12, 24).trim(); // 13 a 24
				String precoPapelStr = linha.substring(109, 121);
				double precoPapel = Double.valueOf(Utilidades.inserir(
						precoPapelStr, ".", precoPapelStr.length() - 2));
				String dataPregao = linha.substring(2, 10); // 3 a 10 no arq
				String nomeEmpresa = linha.substring(27, 39).trim();

				if (!h.containsKey(codPapel)) {

					Papel papel = new Papel(codPapel, nomeEmpresa);
					h.put(codPapel, papel);

					// hAux.put(nomeEmpresa, codPapel);

					nomesPapeis.add(codPapel + " - " + nomeEmpresa);

				}

				Papel papel = h.get(codPapel);
				Pregao pregao = new Pregao(dataPregao, precoPapel);
				papel.adicionarPregao(pregao);

				// System.out.println(dataPregao + ", " + codPapel + ", "
				// + nomeEmpresa + ", " + precoPapel);

			} catch (NumberFormatException e) {
				// System.out.println("***** ENTRADA INVÁLIDA !!! *****");
			}

			linha = read.readLine();

		}

		// System.out.println(h.size());
		// System.out.println(h.get("ALSC3").getQuantidadePregoes());
		// System.out.println(h.get("ALSC3"));
		read.close();

		Collections.sort(nomesPapeis);
		System.out.println("Arquivo carregado com sucesso!");
	}

	public Papel buscar(String codigoPapel) {
		return h.get(codigoPapel);

	}

	// public String getCodigo(String nomePapel){
	// return hAux.get(nomePapel);
	//
	// }

	/**
	 * Retorna um vetor de strings onde o índice = 0 se refere ao conjunto de
	 * treinamento, índice = 1 ao conjunto de validação e índice = 2 ao conjunto
	 * de teste.
	 * 
	 * @param codigoPapel
	 * @param tecnica
	 *            a técnica a ser utilizada ("RNA" ou "MVS")
	 * @param tamanhoJanela
	 * @param normalizar
	 *            se deseja normalizar (true) ou não (false).
	 * @return retorna um vetor de strings com os conjuntos de treinamento,
	 *         validação e teste.
	 * @throws ParserIndexOutOfBoundsException
	 * @throws ParserEmptyException
	 */
	public String[] buscarFormatoTrainValTest(String codigoPapel,
			String tecnica, int tamanhoJanela, boolean normalizar)
			throws ParserIndexOutOfBoundsException, ParserEmptyException {
		String format[] = new String[3];

		String str = this.buscar(codigoPapel, tecnica, tamanhoJanela,
				normalizar);
		String a[] = str.split("\n");

		if (str.equals("")) {
			System.out
					.println("O código '"
							+ codigoPapel
							+ "' não foi adicionado! Não há amostras (históricos) suficientes para formar as bases de "
							+ "'Treino', 'Validação' e 'Teste' com uma a janela de tamanho ="
							+ tamanhoJanela);
			throw new ParserEmptyException(
					"Não há amostras (históricos) suficientes para formar as bases de 'Treino', 'Validação' e 'Teste'!");
		}

		// System.out.println("Total -> " + a.length);

		int limTrainFinal = (int) (a.length * 0.8) - 1;
		// System.out.println("Train -> 0 até " + limTrainFinal);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= limTrainFinal; ++i) {
			sb.append(a[i]);
			sb.append("\n");
		}
		format[0] = sb.toString(); // treinamento

		int limValInicial = limTrainFinal + 1;
		int limValFinal = limValInicial + (int) (a.length * 0.1);
		// System.out.println("Val -> " + limValInicial + " até " +
		// limValFinal);
		sb = new StringBuilder();
		for (int i = limValInicial; i <= limValFinal; ++i) {
			sb.append(a[i]);
			sb.append("\n");
		}
		format[1] = sb.toString(); // validação
		if (format[1].equals("")) {
			System.out
					.println("O código '"
							+ codigoPapel
							+ "' não foi adicionado! Não há amostras (históricos) suficientes para formar as bases de 'VALIDAÇÃO' e 'TESTE'"
							+ " com uma a janela de tamanho =" + tamanhoJanela);
			throw new ParserEmptyException(
					"Não há amostras (históricos) suficientes para formar as bases de 'VALIDAÇÃO' e 'TESTE'!");
		}

		int limTestInicial = limValFinal + 1;
		int limTestFinal = a.length - 1;
		// System.out.println("Test -> " + limTestInicial + " até "+
		// (limTestFinal));
		sb = new StringBuilder();
		for (int i = limTestInicial; i <= limTestFinal; ++i) {
			sb.append(a[i]);
			sb.append("\n");
		}
		format[2] = sb.toString();
		if (format[2].equals("")) {
			System.out
					.println("O código '"
							+ codigoPapel
							+ "' não foi adicionado! Não há amostras (históricos) suficientes para formar as bases de 'TESTE' "
							+ "com uma a janela de tamanho =" + tamanhoJanela);
			throw new ParserEmptyException(
					"Não há amostras (históricos) suficientes para formar as bases de 'TESTE'!");
		}

		return format;
	}

	/**
	 * Retorna um vetor de strings onde no index[0] está a base de Treino e no
	 * index[1] a base de Teste
	 * 
	 * @throws ParserIndexOutOfBoundsException
	 * @throws ParserEmptyException
	 */
	public String[] buscarFormatoTrainTest(String codigoPapel, String tecnica,
			int tamanhoJanela, boolean normalizar)
			throws ParserIndexOutOfBoundsException, ParserEmptyException {
		String format[] = new String[2];

		String str = this.buscar(codigoPapel, tecnica, tamanhoJanela,
				normalizar);

		if (str.equals("")) {
			System.out
					.println("O código '"
							+ codigoPapel
							+ "' não foi adicionado! Não há amostras (históricos) suficientes para formar as bases de 'Treino' e 'Teste' com uma a janela de tamanho ="
							+ tamanhoJanela);
			throw new ParserEmptyException(
					"Não há amostras (históricos) suficientes para formar as bases de 'Treino' e 'Teste'!");
		}

		String a[] = str.split("\n");

		// System.out.println("Total -> " + a.length);

		int limTrainFinal = (int) (a.length * 0.9);
		// System.out.println("Train -> 0 até " + limTrainFinal);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= limTrainFinal; ++i) {
			sb.append(a[i]);
			sb.append("\n");
		}
		format[0] = sb.toString(); // validação

		int limTestInicial = limTrainFinal + 1;
		int limTestFinal = a.length - 1;
		// System.out.println("Test -> " + limTestInicial + " até "+
		// (limTestFinal));
		sb = new StringBuilder();
		for (int i = limTestInicial; i <= limTestFinal; ++i) {
			sb.append(a[i]);
			sb.append("\n");
		}
		format[1] = sb.toString();

		if (format[1].equals("")) {
			System.out
					.println("O código '"
							+ codigoPapel
							+ "' não foi adicionado! Não há amostras (históricos) suficientes para formar as bases de 'TESTE' com uma a janela de tamanho ="
							+ tamanhoJanela);
			throw new ParserEmptyException(
					"Não há amostras (históricos) suficientes para formar as bases de 'TESTE'!");
		}

		return format;
	}

	/**
	 * Retorna um ArrayList onde: no index[0] está um vetor de entrada (input) e
	 * no index[1] um vetor de saída (output), ambos no formato double.
	 * 
	 * @param codigoPapel
	 * @param string
	 * @param tamanhoJanela
	 * @param b
	 * @return
	 * @throws ParserEmptyException
	 * @throws ParserIndexOutOfBoundsException
	 */
	public ArrayList buscarVetor(String codigoPapel, String tecnica,
			int tamanhoJanela, boolean normalizar)
			throws ParserIndexOutOfBoundsException {
		String dados = this.buscar(codigoPapel, tecnica, tamanhoJanela,
				normalizar);

		ArrayList base = new ArrayList();

		String baseTreino[] = dados.split("\n");
		ArrayList array = Utilidades.converteParaVetor(baseTreino,
				tamanhoJanela);
		double input[][] = (double[][]) array.get(0);
		double output[][] = (double[][]) array.get(1);
		base.add(input);
		base.add(output);

		// for(int i = 0; i < size; ++i){
		// input[i] = Utilidades.converteParaVetor(baseTreinoStr[i]);
		// System.out.println(baseTreinoStr[i]);
		// }

		// String baseTesteStr[] = dados[1].split("\n");

		return base;
	}

	public String buscar(String codigoPapel, String tecnica, int tamanhoJanela,
			boolean normalizar) throws ParserIndexOutOfBoundsException {
		Papel item = h.get(codigoPapel);
		if (item == null) {
			throw new ParserKeyException("Erro no Parser! A chave inserida '"
					+ (codigoPapel) + "' é inválida!");
		}

		// System.out.println(item.getPregoes());

		try {
			if (tecnica.equals("SVM")) {

				ArrayList<Pregao> pregoes = item.getPregoes();
				// double vet[] = item.getMenorMaior();
				double min = item.getMenor();
				double max = item.getMaior();

				// System.out.println("max -> "+max);
				// System.out.println("min -> "+min);

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < pregoes.size(); ++i) {
					Pregao pregao1 = (Pregao) pregoes
							.get((i + tamanhoJanela - 1));
					Pregao pregao2 = (Pregao) pregoes.get(pregoes.size() - 1);

					if (pregao1.equals(pregao2)) {
						// System.out.println("iguais!!");

						break;
					}

					Pregao pregao = (Pregao) pregoes.get(i + tamanhoJanela);

					double preco = pregao.getPreco();
					if (normalizar) {
						preco = Utilidades.normalizar(min, max, preco);
					}

					sb.append(preco);
					sb.append(" ");
					for (int j = 0; j < tamanhoJanela; ++j) {
						pregao = (Pregao) pregoes.get((i + j));
						preco = pregao.getPreco();
						if (normalizar) {
							preco = Utilidades.normalizar(min, max, preco);
						}

						sb.append((j + 1));
						sb.append(":");
						sb.append(preco);
						sb.append(" ");
					}
					sb.append("\n");
				}

				// return "RETORNAR FORMATO SUPPORT VECTOR MACHINES ";
				return sb.toString();

			} else if (tecnica.equals("RNA")) {

				ArrayList<Pregao> pregoes = item.getPregoes();
				// double vet[] = item.getMenorMaior();
				double min = item.getMenor();
				double max = item.getMaior();

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < pregoes.size(); ++i) {
					Pregao pregao1 = (Pregao) pregoes
							.get((i + tamanhoJanela - 1));
					Pregao pregao2 = (Pregao) pregoes.get(pregoes.size() - 1);

					if (pregao1.equals(pregao2)) {
						// System.out.println("iguais!!");
						break;
					}

					Pregao pregao = (Pregao) pregoes.get(i + tamanhoJanela);
					double preco = pregao.getPreco();
					if (normalizar) {
						preco = Utilidades.normalizar(min, max, preco);
					}

					sb.append(preco);
					sb.append(",");
					for (int j = 0; j < tamanhoJanela; ++j) {
						pregao = (Pregao) pregoes.get((i + j));

						preco = pregao.getPreco();
						if (normalizar) {
							preco = Utilidades.normalizar(min, max, preco);
						}

						sb.append(preco);
						if (j < (tamanhoJanela - 1)) {
							sb.append(",");
						}
					}
					sb.append("\n");

					// return "RETORNAR FORMATO REDES NEURAIS ARTIFICIAIS";

				}
				return sb.toString();

			}
		} catch (IndexOutOfBoundsException e) {
			System.out
					.println("O código '"
							+ codigoPapel
							+ "' não foi adicionado! Não há amostras (históricos) suficientes para formar as bases de Treino e Teste!");
			throw new ParserIndexOutOfBoundsException(
					"O código '"
							+ codigoPapel
							+ "' não foi adicionado! Não há amostras (históricos) suficientes para formar as bases de Treino e Teste!");
		}

		throw new ParserSupportException("Não há suporte ao formato '"
				+ tecnica + "'.");

	}

	public static void main(String[] args)
			throws ParserIndexOutOfBoundsException {

		// Carregar base...
		Parser p = new Parser();

		p.carregarBase("base_dados_bovespa/COTAHIST_A2012.TXT");
		// site para baixar a base
		// http://www.bmfbovespa.com.br/pt-br/cotacoes-historicas/FormSeriesHistoricasArq.asp

		String[] papeis = p.getCodigosPapeis();

		int cont = 0;
		double sum = 0.0;
		for (String k : papeis) {

			try {
				Papel papel = p.buscar(k);
				String[] a = p.buscarFormatoTrainTest("PETR4", "RNA", 5, true);
				int qtdPregoes = papel.getQuantidadePregoes();

				// System.out
				// .println(papel.getCodigoPapel() + " -- " + qtdPregoes);
				 cont++;
			} catch (ParserIndexOutOfBoundsException e) {
				// e.printStackTrace();
			} catch (ParserEmptyException e) {

				// e.printStackTrace();
			} finally {
//				cont++;
			}
		}

		System.out.println("CONT -> " + cont);
//		System.out.println(papeis.length);

	}

	public double[][] buscarMatrizRetorno(ArrayList<Papel> papeis) {
		int maior = Integer.MIN_VALUE;
		for (Papel item : papeis) {

			int value = item.getQuantidadePregoes();

			if (maior < value) {
				maior = value;
			}
		}

		int size = papeis.size();
		double[][] R = new double[size][maior];
		R = inicializarMatriz(R);

		for (int i = 0; i < size; ++i) {
			Papel item = papeis.get(i);
			ArrayList<Pregao> pregoes = item.getPregoes();
			for (int j = 0; j < pregoes.size(); ++j) {
				R[i][j] = pregoes.get(j).getRetorno();
			}
		}

		return R;
	}

	public double[][] buscarMatrizRetorno(String[] codigosPapeis) {

		int maior = Integer.MIN_VALUE;
		for (String codigoPapel : codigosPapeis) {
			Papel item = h.get(codigoPapel);
			int value = item.getQuantidadePregoes();

			if (maior < value) {
				maior = value;
			}
		}

		double[][] R = new double[codigosPapeis.length][maior];
		R = inicializarMatriz(R);

		for (int i = 0; i < codigosPapeis.length; ++i) {
			Papel item = h.get(codigosPapeis[i]);
			ArrayList<Pregao> pregoes = item.getPregoes();
			for (int j = 0; j < pregoes.size(); ++j) {
				R[i][j] = pregoes.get(j).getRetorno();
			}
		}

		return R;
	}

	private double[][] inicializarMatriz(double[][] r) {
		for (int i = 0; i < r.length; ++i) {
			for (int j = 0; j < r[i].length; ++j) {
				r[i][j] = 0.0;
			}
		}
		return r;
	}

	public String[] getCodigosPapeis() {
		Set<String> keys = h.keySet();

		String codigosPapeis[] = new String[keys.size()];

		int i = 0;
		for (String key : keys) {
			codigosPapeis[i] = key;
			++i;
		}

		// System.out.println(keys.size());
		return codigosPapeis;

	}

	/**
	 * Retorna um ArrayList onde: no index[0] está um vetor de entrada (input) e
	 * no index[1] um vetor de saída (output) referentes à base de Treino; e nos
	 * index[2] (input) e index[3] (output) estão a base de Teste. Todos no
	 * formato double.
	 * 
	 * @param codigoPapel
	 * @param string
	 * @param tamanhoJanela
	 * @param b
	 * @return
	 */
	public ArrayList buscarVetorTrainTest(String codigoPapel, String tecnica,
			int tamanhoJanela, boolean normalizar)
			throws ParserIndexOutOfBoundsException, ParserEmptyException {

		String[] dados = this.buscarFormatoTrainTest(codigoPapel, tecnica,
				tamanhoJanela, normalizar);

		ArrayList base = new ArrayList();

		String baseTreino[] = dados[0].split("\n");
		ArrayList array = Utilidades.converteParaVetor(baseTreino,
				tamanhoJanela);
		double input[][] = (double[][]) array.get(0);
		double output[][] = (double[][]) array.get(1);
		base.add(input);
		base.add(output);

		String baseTeste[] = dados[1].split("\n");
		array = Utilidades.converteParaVetor(baseTeste, tamanhoJanela);
		input = (double[][]) array.get(0);
		output = (double[][]) array.get(1);
		base.add(input);
		base.add(output);

		// for(int i = 0; i < size; ++i){
		// input[i] = Utilidades.converteParaVetor(baseTreinoStr[i]);
		// System.out.println(baseTreinoStr[i]);
		// }

		// String baseTesteStr[] = dados[1].split("\n");

		return base;

	}

	/**
	 * Retorna um ArrayList onde: no index[0] está um vetor de entrada (input) e
	 * no index[1] um vetor de saída (output) referentes à base de Treino; nos
	 * index[2] (input) e index[3] (output) estão a base de Validação; e nos
	 * index[4] (input) e index[5] (output) estão a base de Teste. Todos no
	 * formato double.
	 * 
	 * @param codigoPapel
	 * @param tecnica
	 * @param tamanhoJanela
	 * @param normalizar
	 * @return
	 * @throws ParserIndexOutOfBoundsException
	 * @throws ParserEmptyException
	 */
	public ArrayList buscarVetorTrainValTest(String codigoPapel,
			String tecnica, int tamanhoJanela, boolean normalizar)
			throws ParserIndexOutOfBoundsException, ParserEmptyException {

		String[] dados = this.buscarFormatoTrainValTest(codigoPapel, tecnica,
				tamanhoJanela, normalizar);

		ArrayList base = new ArrayList();

		String baseTreino[] = dados[0].split("\n");
		ArrayList array = Utilidades.converteParaVetor(baseTreino,
				tamanhoJanela);
		double input[][] = (double[][]) array.get(0);
		double output[][] = (double[][]) array.get(1);
		base.add(input);
		base.add(output);

		String baseVal[] = dados[1].split("\n");
		array = Utilidades.converteParaVetor(baseVal, tamanhoJanela);
		input = (double[][]) array.get(0);
		output = (double[][]) array.get(1);
		base.add(input);
		base.add(output);

		String baseTeste[] = dados[2].split("\n");
		array = Utilidades.converteParaVetor(baseTeste, tamanhoJanela);
		input = (double[][]) array.get(0);
		output = (double[][]) array.get(1);
		base.add(input);
		base.add(output);

		return base;
	}

	public ArrayList<String> getNomesPapeis() {
		return (ArrayList<String>) nomesPapeis.clone();
	}

	public ArrayList buscarVetor(String ativo, int tamanhoJanela, boolean b) throws ParserIndexOutOfBoundsException {
		
		return this.buscarVetor(ativo, "RNA", tamanhoJanela, b);
	}

}
