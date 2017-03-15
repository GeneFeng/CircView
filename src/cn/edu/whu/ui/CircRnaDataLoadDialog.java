package cn.edu.whu.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import cn.edu.whu.CircRnaTool;
import cn.edu.whu.CircView;
import cn.edu.whu.Gene;
import cn.edu.whu.MainData;
import cn.edu.whu.exception.FileReadException;

public class CircRnaDataLoadDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JTable table;
	private Vector<Vector<String>> tableData;
	private Vector<String> filePath;
	// private JComboBox<String> cbCompare;
	private JComboBox<String> cbSpecies;
	private JComboBox<String> cbCircRnaTool;

	public CircRnaDataLoadDialog() {
		super(CircView.frame);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		initUi();
		setTitle("Load CircRNA files");
		setResizable(false);
		setSize(600, 400);
		centerScreen();
	}

	private void centerScreen() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
		super.setVisible(true);
		requestFocus();
	}

	private void initUi() {
		// North
		JPanel jpNorth = new JPanel(new FlowLayout());
		cbSpecies = new JComboBox<String>();
		cbCircRnaTool = new JComboBox<String>();
		JButton btAdd = new JButton("Add Files");
		cbSpecies.setPreferredSize(new Dimension(150, 28));
		cbCircRnaTool.setPreferredSize(new Dimension(150, 28));
		jpNorth.add(cbSpecies);
		jpNorth.add(cbCircRnaTool);
		jpNorth.add(btAdd);
		getContentPane().add(jpNorth, BorderLayout.NORTH);

		for (String speciesName : MainData.getSpeciesData().keySet()) {
			cbSpecies.addItem(speciesName);
		}
		for (String circRnaToolName : MainData.getCircRnaToolNames()) {
			cbCircRnaTool.addItem(circRnaToolName);
		}

		// Center
		final Vector<String> colName = new Vector<String>();
		colName.addElement("Species");
		colName.addElement("CircRNA Algorithm");
		colName.addElement("File Name");
		tableData = new Vector<Vector<String>>();
		filePath = new Vector<String>();
		final DefaultTableModel model = new DefaultTableModel();
		table = new JTable(model);
		model.setDataVector(tableData, colName);
		JScrollPane jsPane = new JScrollPane(table);
		getContentPane().add(jsPane, BorderLayout.CENTER);

		// South
		JPanel jpSouth = new JPanel(new FlowLayout());
		JButton btOpen = new JButton("Open");
		JButton btClear = new JButton("Clear");
		jpSouth.add(btOpen);
		jpSouth.add(btClear);
		getContentPane().add(jpSouth, BorderLayout.SOUTH);

		btAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (0 == cbSpecies.getItemCount()) {
					JOptionPane.showMessageDialog(CircView.frame, "Please Load Species DATA first!");
					return;
				}
				String species = cbSpecies.getSelectedItem().toString();
				if (null != MainData.getFileToolTable().get(species)) {
					JOptionPane.showMessageDialog(CircView.frame,
							species + " data is USED, please clear old " + species + " data and load a new one");
					return;
				}
				OpenFileChooser openFile = new OpenFileChooser("Open CircRNA Files");
				openFile.setMultiSelectionEnabled(true);
				openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFile.setFileHidingEnabled(true);
				int returnValue = openFile.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					String speciesName = cbSpecies.getSelectedItem().toString();
					String circRnaToolName = cbCircRnaTool.getSelectedItem().toString();
					File[] files = null;
					files = openFile.getSelectedFiles();
					for (File file : files) {
						Vector<String> rowData = new Vector<String>();
						rowData.addElement(speciesName);
						rowData.addElement(circRnaToolName);
						rowData.addElement(file.getName());
						filePath.addElement(file.getPath());
						tableData.addElement(rowData);
						model.setDataVector(tableData, colName);
						cbSpecies.setEnabled(false);
					}
				}
			}
		});

		btClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableData.removeAllElements();
				filePath.removeAllElements();
				model.setDataVector(tableData, colName);
				// cbCompare.setEnabled(true);
				cbSpecies.setEnabled(true);
				cbCircRnaTool.setEnabled(true);
			}
		});

		btOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String speciesName = cbSpecies.getSelectedItem().toString();
				TreeMap<String, Gene> genes = MainData.getSpeciesData().get(speciesName);

				DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(CircView.frame, "Loading CircRNA Data ...");
				setVisible(false);
				try {
					MainData.getFileToolTable().put(speciesName, tableData);
					CircRnaTool.initCircRnaDataFromFiles(tableData, filePath, genes);
					CircView.updateCircRnaToolsCombo();
					CircView.updateSamplesCombo();
					CircView.updateCbChrom();
					CircView.updateGeneTransList();
				} catch (FileReadException e1) {
					JOptionPane.showMessageDialog(CircView.frame, "File format error!");
					CircView.log.warn(e1.getMessage());
					dataLoadingDialog.setVisible(false);
				}
				dataLoadingDialog.setVisible(false);
				CircRnaDataLoadDialog.this.dispose();
			}
		});
	}
}
