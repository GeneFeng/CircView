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
		cbSpecies.setPreferredSize(new Dimension(200, 28));
		cbCircRnaTool.setPreferredSize(new Dimension(200, 28));
		jpNorth.add(cbSpecies);
		jpNorth.add(cbCircRnaTool);
		jpNorth.add(btAdd);
		getContentPane().add(jpNorth, BorderLayout.NORTH);

		for (String speciesName : MainData.getSpeciesNames()) {
			cbSpecies.addItem(speciesName);
		}
		for (String circRnaToolName : MainData.getCircRnaToolNames()) {
			cbCircRnaTool.addItem(circRnaToolName);
		}

		// Center
		final Vector<String> colName = new Vector<String>();
		colName.addElement("Species");
		colName.addElement("CircRNA Tool");
		colName.addElement("File Name");
		colName.addElement("File Path");
		tableData = new Vector<Vector<String>>();
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
				if(null == cbSpecies.getSelectedItem()) {
					JOptionPane.showMessageDialog(CircView.frame, "Species Name is needed");
					return;
				}
				if(null == cbCircRnaTool.getSelectedItem()) {
					JOptionPane.showMessageDialog(CircView.frame, "Tool Name is needed");
					return;
				}
				String speciesName = cbSpecies.getSelectedItem().toString();
				String circRnaToolName = cbCircRnaTool.getSelectedItem().toString();
				String pattern = "\\s+";
				if (speciesName.matches(pattern)) {
					JOptionPane.showMessageDialog(CircView.frame, "Species Name is needed");
					return;
				}
				if (circRnaToolName.matches(pattern)) {
					JOptionPane.showMessageDialog(CircView.frame, "Tool Name is needed");
					return;
				}
				
				OpenFileChooser openFile = new OpenFileChooser("Open CircRNA Files");
				openFile.setMultiSelectionEnabled(true);
				openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFile.setFileHidingEnabled(true);
				int returnValue = openFile.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File[] files = null;
					files = openFile.getSelectedFiles();
					for (File file : files) {
						Vector<String> rowData = new Vector<String>();
						rowData.addElement(speciesName);
						rowData.addElement(circRnaToolName);
						rowData.addElement(file.getName());
						rowData.addElement(file.getPath());
						tableData.addElement(rowData);
						model.setDataVector(tableData, colName);
					}
				}
			}
		});

		btClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableData.removeAllElements();
				model.setDataVector(tableData, colName);
			}
		});

		btOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(CircView.frame, "Loading CircRNA Data ...");
				setVisible(false);
				TreeMap<String, String> uniqSpecies = new TreeMap<String, String>();
				for (Vector<String> rowData : tableData) {
					String speciesName = rowData.get(0);
					uniqSpecies.put(speciesName, speciesName);
				}
				boolean hasSpecies = true;
				for (String speciesName : uniqSpecies.keySet()) {
					if (null == MainData.getSpeciesData().get(speciesName)) {
						// Load Species Data
						TreeMap<String, Gene> genes = new TreeMap<String, Gene>();
						File speciesFile = new File(MainData.getSpeciesFile().get(speciesName));
						if (CircRnaTool.initSpeciesDataFromFile(speciesFile, genes)) {
							MainData.getSpeciesData().put(speciesName, genes);
							CircView.log.info(speciesName + " Data Loaded");
						} else {
							hasSpecies = false;
							JOptionPane.showMessageDialog(CircView.frame,
									"[" + speciesFile.getPath() + "] does NOT EXIST or file FORMAT ERROR!");
							CircView.log.error("[" + speciesFile.getPath() + "] does NOT EXIST or file FORMAT ERROR!");
							dataLoadingDialog.setVisible(false);
							CircRnaDataLoadDialog.this.dispose();
						}
					}
				}
				if (hasSpecies) {
					for (String speciesName : uniqSpecies.keySet()) {
						TreeMap<String, Gene> genes = MainData.getSpeciesData().get(speciesName);
						try {
							// Load CircRNA Data
							CircRnaTool.initCircRnaDataFromFiles(tableData, speciesName, genes);
							// Save CircRNA File Info
							for (Vector<String> rowData : tableData) {
								String sname = rowData.get(0);
								if (sname.equalsIgnoreCase(speciesName)) {
									MainData.getCircRnaFilesInfo().add(rowData);
								}
							}
							// Update UI
							CircView.updateSpeciesCombo();
							CircView.updateCircRnaToolsCombo();
							CircView.updateSamplesCombo();
							CircView.updateCbChrom();
							CircView.updateGeneTransList();
						} catch (FileReadException e1) {
							JOptionPane.showMessageDialog(CircView.frame, e1.getMessage());
							CircView.log.error(e1.getMessage());
							dataLoadingDialog.setVisible(false);
							CircRnaDataLoadDialog.this.dispose();
						}

					}
				}
				dataLoadingDialog.setVisible(false);
				CircRnaDataLoadDialog.this.dispose();
			}
		});
	}
}
