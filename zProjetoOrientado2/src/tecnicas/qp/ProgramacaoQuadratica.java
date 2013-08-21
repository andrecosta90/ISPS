package tecnicas.qp;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;
import com.joptimizer.optimizers.OptimizationResponse;

import parser.Parser;
import utils.Utilidades;

public class ProgramacaoQuadratica {

	/**
	 * @param args
	 * @throws ProblemaInviavelExcepetion
	 * @throws Exception
	 */
	public static void main(String[] args) throws ProblemaInviavelExcepetion {

		Parser p = new Parser();
		p.carregarBase("base_dados_bovespa/COTAHIST_A2012.TXT");

		ProgramacaoQuadratica qp = new ProgramacaoQuadratica();

		// String codigosPapeis[] =
		// {"AEDU3","ALSC3","AFLT3","AGRO3","AGEN11","ALPA4","ABCB4","ABCB4F"};
		String codigosPapeis[] = {"AEDU3","ALSC3","AGRO3","AGEN11","ALPA4","ABCB4","ABCB4F"};
		// String codigosPapeis[] = p.getCodigosPapeis();

		double R[][] = p.buscarMatrizRetorno(codigosPapeis);
		System.out.println("ok");

		double retornoDesejado = 0.19;

//		ArrayList sol = qp.solucaoQP(R, retornoDesejado);
		ArrayList sol = qp.solucaoQPComRetornoMinimo(R, retornoDesejado);
		
		//ArrayList sol = qp.solucaoQPComRetornoMinimo(R, retornoDesejado);
		System.out.println("sol   : " + ArrayUtils.toString(sol.get(0)));
		System.out.println("value : " + sol.get(1));

	}

	/**
	 * Realiza o problema de programação quadrática com um retorno desejado.
	 * 
	 * @param R
	 * @return um ArrayList com as soluções (index = 0) e o valor da
	 *         função-objetivo com estes valores (index = 1)
	 * @throws ProblemaInviavelExcepetion
	 * @throws Exception
	 */
	public ArrayList solucaoQP(double[][] R, double retornoDesejado)
			throws ProblemaInviavelExcepetion {

		try {
			// media arit. das series de retornos de cada ativo no formato {
			// 20.0,
			// 45.0, 50.0 }, onde cada item é o retorno de um ativo..
			double Rmean[] = Utilidades.calcMean(R);
			// imprimir(Rmean);

			// double Rmean[] = new double[] { 20.0, 45.0, 50.0 };
			// System.out.println("Média --> " + Rmean[0] + "; " + Rmean[1] +
			// "; "
			// + Rmean[2]);

			// variancia das series dos retornos de cada ativo no formato {
			// 20.0,
			// 45.0, 50.0 }, onde cada item é o risco de um ativo..
			double v[] = Utilidades.calcRisk(R, Rmean);
			// System.out.println("Risco --> " + v[0] + "; " + v[1] + "; " +
			// v[2]);

			double gamma[][] = Utilidades.calcCovariance(R, Rmean);
			System.out.println(gamma.length + " X " + gamma[0].length);

			RealMatrix PMatrix = new Array2DRowRealMatrix(gamma);

			// Objective function (covariance matrix)
			PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(
					PMatrix.getData(), null, 0);

			// equalities (return and budget constraints)
			// double[][] A = new double[][] { { 0.018, 0.025, 0.01 }, { 1, 1, 1
			// }
			// };
			// double[] b = new double[] { 0.018, 1 };

			// Talvez mudar 'Rmean' para inequações ao invés de igualdade
			double vetOnes[] = Utilidades.ones(gamma.length);
			double[][] A = new double[][] { Rmean, vetOnes };
//			double[][] A = new double[][] {vetOnes };
			
//			for(int i  = 0; i <= Rmean.length; ++i){
//				Rmean[i] = Rmean[i] * (-1);
//			}

			// double retornoDesejado = 0.19; // dependendo deste valor pode
			// ocasionar
			// java.lang.Exception: Infeasible
			// problem
			double[] b = new double[] { retornoDesejado, 1 };
//			double[] b = new double[] { 1 };

			// inequalities (no-short constraints)
//			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[gamma.length + 1];
			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[gamma.length];

//			for (int i = 0; i < gamma.length-1; ++i) {
			for (int i = 0; i < gamma.length; ++i) {
			
				double vet[] = Utilidades.zeros(gamma.length);
				vet[i] = -1;
				inequalities[i] = new LinearMultivariateRealFunction(vet, 0);

			}
			
//			inequalities[gamma.length-1] = new LinearMultivariateRealFunction(Rmean, retornoDesejado);

			OptimizationRequest or = new OptimizationRequest();
			or.setF0(objectiveFunction);
			// or.setInitialPoint(new double[] { 0.25, 0.25, 0.25,0.25 });//
			// useful but not mandatory
			or.setFi(inequalities);
			or.setA(A);
			or.setB(b);

			// optimization
			JOptimizer opt = new JOptimizer();
			opt.setOptimizationRequest(or);
			int returnCode = opt.optimize();

			if (returnCode == OptimizationResponse.FAILED) {
				System.out.println("FAIL!");
				// fail();
			}

			OptimizationResponse response = opt.getOptimizationResponse();

			double[] sol = response.getSolution();

			ArrayList a = new ArrayList();
			a.add(sol);
			a.add(objectiveFunction.value(sol));

			return a;
		} catch (DimensionMismatchException e) {
			e.printStackTrace();
		} catch (NoDataException e) {
			e.printStackTrace();
		} catch (NullArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// System.out.println("Disparar (throw new) exceção de 'Problema Inviável' !");
			throw new ProblemaInviavelExcepetion(
					"Problema inviável! Impossível de Resolver! Tente outro parâmetro!");
		}
		return null;

	}

	/**
	 * Realiza o problema de programação quadrática com um retorno mínimo
	 * desejado
	 * 
	 * @param R
	 * @return um ArrayList com as soluções (index = 0) e o valor da
	 *         função-objetivo com estes valores (index = 1)
	 * @throws ProblemaInviavelExcepetion
	 * @throws Exception
	 */
	public ArrayList solucaoQPComRetornoMinimo(double[][] R,
			double retornoMinDesejado) throws ProblemaInviavelExcepetion {

		try {
			// media arit. das series de retornos de cada ativo no formato {
			// 20.0,
			// 45.0, 50.0 }, onde cada item é o retorno de um ativo..
			double Rmean[] = Utilidades.calcMean(R);
			// imprimir(Rmean);

			// double Rmean[] = new double[] { 20.0, 45.0, 50.0 };
			// System.out.println("Média --> " + Rmean[0] + "; " + Rmean[1] +
			// "; "
			// + Rmean[2]);

			// variancia das series dos retornos de cada ativo no formato {
			// 20.0,
			// 45.0, 50.0 }, onde cada item é o risco de um ativo..
			double v[] = Utilidades.calcRisk(R, Rmean);
			// System.out.println("Risco --> " + v[0] + "; " + v[1] + "; " +
			// v[2]);

			double gamma[][] = Utilidades.calcCovariance(R, Rmean);
			System.out.println(gamma.length + " X " + gamma[0].length);

			RealMatrix PMatrix = new Array2DRowRealMatrix(gamma);

			// Objective function (covariance matrix)
			PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(
					PMatrix.getData(), null, 0);

			// equalities (return and budget constraints)
			// double[][] A = new double[][] { { 0.018, 0.025, 0.01 }, { 1, 1, 1
			// }
			// };
			// double[] b = new double[] { 0.018, 1 };

			// Talvez mudar 'Rmean' para inequações ao invés de igualdade
			double vetOnes[] = Utilidades.ones(gamma.length);
			double[][] A = new double[][] { vetOnes };

			// double retornoDesejado = 0.19; // dependendo deste valor pode
			// ocasionar
			// java.lang.Exception: Infeasible
			// problem
			double[] b = new double[] { 1 };

			// inequalities (no-short constraints)
			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[gamma.length + 1];

			for (int i = 0; i < gamma.length; ++i) {
				double vet[] = Utilidades.zeros(gamma.length);
				vet[i] = -1;
				inequalities[i] = new LinearMultivariateRealFunction(vet, 0);

			}

			double vet[] = Utilidades.trocarSinal(Rmean);
			inequalities[gamma.length] = new LinearMultivariateRealFunction(
					vet, retornoMinDesejado);

			OptimizationRequest or = new OptimizationRequest();
			or.setF0(objectiveFunction);
			// or.setInitialPoint(new double[] { 0.25, 0.25, 0.25,0.25 });//
			// useful but not mandatory
			or.setFi(inequalities);
			or.setA(A);
			or.setB(b);

			// optimization
			JOptimizer opt = new JOptimizer();
			opt.setOptimizationRequest(or);
			int returnCode = opt.optimize();

			if (returnCode == OptimizationResponse.FAILED) {
				System.out.println("FAIL!");
				// fail();
			}

			OptimizationResponse response = opt.getOptimizationResponse();

			double[] sol = response.getSolution();

			ArrayList<Object> a = new ArrayList<Object>();
			a.add(sol);
			a.add(objectiveFunction.value(sol));

			return a;
		} catch (DimensionMismatchException e) {
			e.printStackTrace();
		} catch (NoDataException e) {
			e.printStackTrace();
		} catch (NullArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// System.out.println("Disparar (throw new) exceção de 'Problema Inviável' !");
			throw new ProblemaInviavelExcepetion(
					"Problema inviável! Impossível de Resolver! Tente outro parâmetro!");
		}
		return null;

	}

}
