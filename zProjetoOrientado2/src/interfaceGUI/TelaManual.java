package interfaceGUI;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.ListModel;

import utils.Utilidades;
import javax.swing.Box;

public class TelaManual {

	private JFrame telaManual;
	private JList rightList;
	private JList leftList;

	private static Vector LIST;
	
	
	private JButton botaoRemover;
	private JButton botaoAdicionar;

	private static final int MAX_PAPEIS = 10;
	private JLabel label;
	private JButton btnPronto;

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// EventQueue.invokeLater(new Runnable() {
	// public void run() {
	// try {
	// TelaManual window = new TelaManual(null);
	// window.telaManual.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }

	/**
	 * Create the application.
	 * 
	 * @param nomesPapeis
	 */
	public TelaManual(ArrayList<String> nomesPapeis) {
		initialize(nomesPapeis);
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @param codigoPapeis
	 */
	private void initialize(ArrayList<String> nomesPapeis) {
		telaManual = new JFrame();
		telaManual
				.setTitle("Intelligent System for Portfolio Selection - Version 0.0.1 Beta");
		telaManual.setBounds(100, 100, 635, 398);
		telaManual.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(12, 52, 224, 245);
		telaManual.getContentPane().add(panel);
		panel.setLayout(new GridLayout());

		DefaultListModel model = new DefaultListModel();
		model.ensureCapacity(100);

		for (String papeis : nomesPapeis) {
			model.addElement(papeis);
		}

		rightList = new JList(model);
		rightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		rightList.setBounds(0, 0, 201, 253);
		JScrollPane scrollPane = new JScrollPane(rightList);
		scrollPane.setBounds(202, 0, 22, 253);

		panel.add(scrollPane);

//		rightList.ensureIndexIsVisible(50);

		JLabel lblSeleoManual = new JLabel("Pap\u00E9is selecionados");
		lblSeleoManual.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSeleoManual.setBounds(375, 12, 224, 27);
		telaManual.getContentPane().add(lblSeleoManual);

		botaoAdicionar = new JButton("Adicionar >>");
		botaoAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// rightList.remove( );
				try {


					Vector listDataRight = Utilidades
							.convertJListToVector(rightList);
					Vector listDataLeft = Utilidades.convertJListToVector(leftList);

					listDataLeft.add(rightList.getSelectedValue());
					listDataRight.remove(rightList.getSelectedIndex());

					rightList.setListData(listDataRight);
					leftList.setListData(listDataLeft);

					System.out.println(listDataLeft.size());

					if (listDataLeft.size() == MAX_PAPEIS) {
						botaoAdicionar.setEnabled(false);
					}
					btnPronto.setEnabled(true);
					botaoRemover.setEnabled(true);
				} catch (ArrayIndexOutOfBoundsException e) {
//					botaoRemover.setEnabled(false);
					JOptionPane.showMessageDialog(null,
							"Selecione o papel a ser adicionado.");
				}

				// Object[] values = rightList.getSelectedValues();
				// String[] ativosEscolhidos = new String[values.length];
				//
				// int i = 0;
				// for(Object value : values){
				// String v = (String) value;
				//
				// ativosEscolhidos[0] = v.split(" - ")[0];
				// System.out.println(ativosEscolhidos[0]);
				// i++;
				// }

			}
		});
		botaoAdicionar.setBounds(248, 100, 110, 25);
		telaManual.getContentPane().add(botaoAdicionar);

		botaoRemover = new JButton("Remover <<");
		botaoRemover.setEnabled(false);
		botaoRemover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					
					
					Vector listDataRight = Utilidades
							.convertJListToVector(rightList);
					Vector listDataLeft = Utilidades
							.convertJListToVector(leftList);

					listDataRight.add(leftList.getSelectedValue());
					listDataLeft.remove(leftList.getSelectedIndex());

					Collections.sort(listDataRight);
					rightList.setListData(listDataRight);
					leftList.setListData(listDataLeft);

					if (listDataLeft.size() == 0) {
						btnPronto.setEnabled(false);
						botaoRemover.setEnabled(false);
					}
					botaoAdicionar.setEnabled(true);
					System.out.println(listDataLeft.size());
				} catch (ArrayIndexOutOfBoundsException e) {
					JOptionPane.showMessageDialog(null,
							"Selecione o papel a ser removido.");
				}
			}
		});
		botaoRemover.setBounds(248, 226, 110, 25);
		telaManual.getContentPane().add(botaoRemover);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(375, 52, 224, 245);
		telaManual.getContentPane().add(panel_1);
		panel_1.setLayout(new GridLayout());

		DefaultListModel model2 = new DefaultListModel();
		model.ensureCapacity(100);

		leftList = new JList(model2);
		leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane_1 = new JScrollPane(leftList);
		panel_1.add(scrollPane_1);
		
		label = new JLabel("Lista de Pap\u00E9is");
		label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		label.setBounds(12, 12, 224, 27);
		telaManual.getContentPane().add(label);
		
		btnPronto = new JButton("Pronto");
		btnPronto.setEnabled(false);
		btnPronto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LIST = Utilidades.convertJListToVector(leftList);
				telaManual.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				telaManual.dispose();
			}
		});
		btnPronto.setBounds(489, 315, 110, 25);
		telaManual.getContentPane().add(btnPronto);

	}

	public void setVisibleFrame(boolean b) {
		telaManual.setVisible(b);

	}

	public static Vector getLIST() {
		return LIST;
	}
	
	
}
