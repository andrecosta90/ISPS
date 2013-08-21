package interfaceGUI;

import interfacePadrao.components.FileChooserDemo2;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import parser.Parser;
import parser.exceptions.ParserIndexOutOfBoundsException;
import tecnicas.mvs.SupportVectorMachine;
import tecnicas.qp.ProblemaInviavelExcepetion;
import tecnicas.qp.ProgramacaoQuadratica;
import utils.Utilidades;

import emfproject.io.ReadFile;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JProgressBar;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTextField;

import org.apache.commons.lang.ArrayUtils;

import classes.Papel;

public class TelaPrincipal {

	private String path = "base_dados_bovespa/COTAHIST_A2012.TXT";
	private JFrame frmInvestfcil;
	private JFileChooser fileChooser;
	private Parser p;
	private JTextField textField1;
	private JTextField textField2;
	
	private JButton btnExecutar;
	
	
	private SupportVectorMachine svm;
	private ProgramacaoQuadratica qp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaPrincipal window = new TelaPrincipal();
					window.frmInvestfcil.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TelaPrincipal() {
		qp = new ProgramacaoQuadratica();
		svm = new SupportVectorMachine();
		
		p = new Parser();
		// path = file.toString(); //COMENTAR AQUI DEPOIS
		p.carregarBase(path);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmInvestfcil = new JFrame();
		frmInvestfcil
				.setTitle("Intelligent System for Portfolio Selection - Version 0.0.1 Beta");
		frmInvestfcil.setBounds(100, 100, 574, 415);
		frmInvestfcil.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmInvestfcil.getContentPane().setLayout(null);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 760, 26);
		frmInvestfcil.getContentPane().add(menuBar);

		JMenu mnArquivo = new JMenu("Arquivo");
		menuBar.add(mnArquivo);

		JMenuItem mntmAbrir = new JMenuItem("Abrir...");
		mntmAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (fileChooser == null) {
					fileChooser = new JFileChooser();
				}

				int returnVal = fileChooser.showDialog(null, "Abrir");

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					// carrega arquivo
					p = new Parser();
					path = file.toString(); // COMENTAR AQUI DEPOIS
					p.carregarBase(path);

				} else {
					System.out.println("Attachment cancelled by user.\n");
				}

				// Reset the file chooser for the next time it's shown.
				fileChooser.setSelectedFile(null);
			}
		});
		mnArquivo.add(mntmAbrir);

		JMenuItem mntmSair = new JMenuItem("Sair");
		mntmSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				frmInvestfcil.dispose();
				// System.exit(0);
			}
		});
		mnArquivo.add(mntmSair);

		JButton btnNewButton_1 = new JButton("Manual");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnExecutar.setEnabled(true);
				textField1.setEnabled(true);
				textField2.setEnabled(true);
				TelaManual w = new TelaManual(p.getNomesPapeis());
				w.setVisibleFrame(true);
			}
		});
		btnNewButton_1.setBounds(200, 84, 113, 43);
		frmInvestfcil.getContentPane().add(btnNewButton_1);

		JLabel lblW = new JLabel(
				"1) Clique no bot\u00E3o \"Manual\" para selecionar os pap\u00E9is que ir\u00E3o compor a carteira:");
		lblW.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblW.setBounds(10, 39, 501, 31);
		frmInvestfcil.getContentPane().add(lblW);

		JLabel lblInsiraO = new JLabel(
				"2) N\u00FAmero de dias a serem previstos:");
		lblInsiraO.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblInsiraO.setBounds(10, 180, 228, 31);
		frmInvestfcil.getContentPane().add(lblInsiraO);

		textField1 = new JTextField();
		textField1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textField1.setBounds(10, 208, 116, 22);
		frmInvestfcil.getContentPane().add(textField1);
		textField1.setColumns(10);
		textField1.setEnabled(false);

		JLabel lblRetornoDesejado = new JLabel("3) Retorno desejado (em %):");
		lblRetornoDesejado.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblRetornoDesejado.setBounds(10, 253, 182, 31);
		frmInvestfcil.getContentPane().add(lblRetornoDesejado);

		textField2 = new JTextField();
		textField2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textField2.setColumns(10);
		textField2.setBounds(10, 280, 116, 22);
		textField2.setEnabled(false);
		frmInvestfcil.getContentPane().add(textField2);

		btnExecutar = new JButton("Executar");
		btnExecutar.setEnabled(false);
		btnExecutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					int numDiasPrevistos = Integer.valueOf(textField1.getText());
					double retornoDesejado = Double.valueOf(textField2.getText()) / 100d;

					System.out.println("Dias -> " + numDiasPrevistos);
					System.out.println("Retorno desejado -> " + retornoDesejado);
					
					//CMIG3 , PETR3 , VALE3 , ITUB3 , ..AGRADARAM

					Vector list = TelaManual.getLIST();
					String[] ativosEscolhidos = new String[list.size()];

					for (int i = 0; i < ativosEscolhidos.length; ++i) {
						String str = (String) list.get(i);
						str = str.split(" - ")[0];

						ativosEscolhidos[i] = str;
					}

					for (String ativo : ativosEscolhidos) {
						System.out.print(ativo + " , ");
					}

					// **********************teste svm

					// #Parametros
					int tamanhoJanela = 5; // input SVM/RNA

					int tipoSVM = 3; // e-SVR
					int tipoKernel = 2; // RBF Kernel
					int kFold = 0; // valores <= 2 desativam validação cruzada
									// !!
					// double gamma = 0.02;
					// double custo = 1000;
					// double epsilon = 0.02;
					// double gamma = Math.pow(2, -9);
					// double custo = Math.pow(2, 5);
					// double epsilon = Math.pow(2, -5);
					double gamma = Math.pow(2, -9);
					double custo = Math.pow(2, 7);
					
					double epsilon = Math.pow(2, -9);

					ArrayList<Papel> papeis = new ArrayList<Papel>();

					for (String codigoPapel : ativosEscolhidos) { // é feito o
																	// treinamento
																	// para cada
																	// ativo
																	// escolhido

						String dados = p.buscar(codigoPapel, "SVM",
								tamanhoJanela, true);

						String arquivoModelo = "model"; // DELETAR O MODEL NO
														// FINAL !!!!

						String arquivoSaida = "out";

						svm.treinar(dados, arquivoModelo, tipoSVM, tipoKernel,
								kFold, gamma, custo, epsilon, null);

						svm.testar(dados, arquivoModelo, arquivoSaida, null);

						Papel papel = svm.predizer(arquivoModelo, arquivoSaida,
								tamanhoJanela, numDiasPrevistos, p.buscar(codigoPapel));

						papeis.add(papel);


					}
					
					double R[][] = p.buscarMatrizRetorno(papeis);
					System.out.println("** Buscou Matriz Retorno ***");
					
					ArrayList sol = qp.solucaoQPComRetornoMinimo(R, retornoDesejado);
					//ArrayList sol = qp.solucaoQPComRetornoMinimo(R, retornoDesejado);
					System.out.println("sol   : " + ArrayUtils.toString(sol.get(0)));
					System.out.println("value : " + sol.get(1));
					
					double array[] = (double[]) sol.get(0);
					double risco = (Double) sol.get(1);
					
					double count = 0;
					double[] rmean = Utilidades.calcMean(R);
					for(int i = 0; i < array.length; ++i){
						count += (rmean[i] * array[i]);
					}
					System.out.println("Retorno obtido = "+count);
					
					StringBuilder sb = new StringBuilder();
					sb.append("Como você deve investir seus recursos nos próximos ");
					sb.append(numDiasPrevistos);
//					sb.append(" dias.\n\n");
					sb.append(" dias, com risco de ");
					sb.append(String.format("%.4f", (risco * 100)));
					sb.append("%: \n\n");
					
					
					for(int i = 0; i < ativosEscolhidos.length; ++i){
						double value = Math.round(array[i] * 100);
						sb.append("- ");
						sb.append(value);
						sb.append("% em papéis da(o) ");
						String nomePapel = p.buscar(ativosEscolhidos[i]).getNomeEmpresa();
						sb.append(nomePapel);
						sb.append("\n");
					}
					
					JOptionPane.showMessageDialog(null, sb.toString());

					// **************************

				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null,
							"Caractere(s) inválido(s). Tente novamente.");
				} catch (ParserIndexOutOfBoundsException e) {
					// e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				} catch (ProblemaInviavelExcepetion e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null,
							"Não foi encontrada uma solução! Tente reduzir o valor do retorno mínimo!","Problema inviável",0,null);
				}
			}
		});
		btnExecutar.setBounds(200, 314, 113, 43);
		frmInvestfcil.getContentPane().add(btnExecutar);
	}
}
