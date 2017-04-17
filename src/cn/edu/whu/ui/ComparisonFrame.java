package cn.edu.whu.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import cn.edu.whu.CircRna;
import cn.edu.whu.CircView;
import cn.edu.whu.Gene;
import cn.edu.whu.GeneTranscript;
import cn.edu.whu.MainData;
import cn.edu.whu.util.Constant;
import cn.edu.whu.util.EvenOddRenderer;

public class ComparisonFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JComboBox<String> cbSpecies;
	private JComboBox<String> cbTolerate;
	private JList<String> jlUnSelected;
	private JList<String> jlSelected;
	private Vector<String> unSelected;
	private Vector<String> selected;
	private JScrollPane scrollUnSelected;
	private JScrollPane scrollSelected;
	private DefaultTableModel model;
	private JTable tbResult;
	private Vector<String> colName;
	private Vector<Vector<String>> tableData;
	Dimension screenSize;

	public ComparisonFrame() {
		super();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(true);
		setTitle("Comparison Setting");

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 100, (int) screenSize.getWidth(), (int) screenSize.getHeight() - 100);
		centerScreen();
		initUi();
	}

	private void centerScreen() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
		super.setVisible(true);
		requestFocus();
	}

	private void initUi() {
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		JPanel jpWest = new JPanel();
		jpWest.setLayout(new BoxLayout(jpWest, BoxLayout.Y_AXIS));
		cbSpecies = new JComboBox<String>();
		cbSpecies.setPreferredSize(new Dimension(200, 28));
		JPanel jpTmp = new JPanel();
		JLabel lbTolerate = new JLabel("Compare Overlap");
		cbTolerate = new JComboBox<String>();
		cbTolerate.setPreferredSize(new Dimension(80, 28));
		// cbTolerate.setEditable(true);
		JLabel lbBp = new JLabel("bp");
		jpTmp.add(lbTolerate);
		jpTmp.add(cbTolerate);
		jpTmp.add(lbBp);
		jpWest.add(new JLabel(" "));
		jpWest.add(cbSpecies);
		jpWest.add(jpTmp);
		northPanel.add(jpWest, BorderLayout.WEST);

		JPanel jpCenter = new JPanel();
		jpCenter.setLayout(new BoxLayout(jpCenter, BoxLayout.X_AXIS));
		final JLabel lbUnSelected = new JLabel("Sample List");
		final JLabel lbSelected = new JLabel("Sample selected");
		jlUnSelected = new JList<String>();
		jlSelected = new JList<String>();
		unSelected = new Vector<String>();
		selected = new Vector<String>();
		jlUnSelected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlSelected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollUnSelected = new JScrollPane(jlUnSelected);
		scrollSelected = new JScrollPane(jlSelected);
		scrollUnSelected.setPreferredSize(new Dimension((int) jpCenter.getWidth() / 2, 100));
		scrollSelected.setPreferredSize(new Dimension((int) jpCenter.getWidth() / 2, 100));
		JPanel jpLeft = new JPanel();
		JPanel jpRight = new JPanel();
		jpLeft.setLayout(new BoxLayout(jpLeft, BoxLayout.Y_AXIS));
		jpRight.setLayout(new BoxLayout(jpRight, BoxLayout.Y_AXIS));
		jpLeft.add(lbUnSelected);
		jpLeft.add(scrollUnSelected);
		jpRight.add(lbSelected);
		jpRight.add(scrollSelected);
		jpCenter.add(jpLeft);
		jpCenter.add(jpRight);
		northPanel.add(jpCenter, BorderLayout.CENTER);

		final JButton btCompare = new JButton("Compare");
		JButton btReset = new JButton("Reset");
		JButton btSave = new JButton("Save as");
		JPanel jpEast = new JPanel();
		jpEast.setLayout(new BoxLayout(jpEast, BoxLayout.Y_AXIS));
		jpEast.add(new JLabel(" "));
		jpEast.add(btCompare);
		jpEast.add(btReset);
		jpEast.add(btSave);
		northPanel.add(jpEast, BorderLayout.EAST);
		getContentPane().add(northPanel, BorderLayout.NORTH);

		model = new DefaultTableModel();
		tbResult = new JTable(model);
		colName = new Vector<String>();
		tableData = new Vector<Vector<String>>();
		JScrollPane jsResult = new JScrollPane(tbResult);
		tbResult.setDefaultRenderer(Object.class, new EvenOddRenderer());
		tbResult.setAutoCreateRowSorter(true);
		tbResult.setAutoscrolls(true);
		tbResult.setEnabled(false);
		getContentPane().add(jsResult, BorderLayout.CENTER);

		colName.addElement("No.");
		colName.addElement("gene name");
		colName.addElement("circRNA ID");
		colName.addElement("chromosome");
		colName.addElement("donor site");
		colName.addElement("acceptor site");
		colName.addElement("junction reads");
		colName.addElement("strand");
		colName.addElement("tissue name");
		colName.addElement("tissue num");
		colName.addElement("sample name");
		colName.addElement("sample num");
		colName.addElement("tool name");
		colName.addElement("tool num");
		colName.addElement("circRNA type");
		colName.addElement("circRNA region");

		cbSpecies.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					unSelected.removeAllElements();
					selected.removeAllElements();
					if(null == cbSpecies.getSelectedItem()) {
						return;
					}
					String species = cbSpecies.getSelectedItem().toString();
					TreeMap<String, String> tools = new TreeMap<String, String>();
					for(Vector<String> rowData : MainData.getCircRnaFilesInfo()) {
						String sname = rowData.get(0);
						String fname = rowData.get(2);
						if(species.equalsIgnoreCase(sname)) {
							tools.put(fname, fname);
						}
					}
					for(String tool : tools.keySet()) {
						unSelected.add(tool);
					}
					
					jlUnSelected.setListData(unSelected);
					jlSelected.setListData(selected);
				}
			}
		});

		jlUnSelected.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				JList<String> theList = (JList<String>) e.getSource();
				int index = theList.locationToIndex(e.getPoint());
				if (index >= 0) {
					String slct = theList.getModel().getElementAt(index);
					for (String name : unSelected) {
						if (name.equalsIgnoreCase(slct)) {
							unSelected.remove(name);
							break;
						}
					}
					selected.addElement(slct);
					jlUnSelected.removeAll();
					jlSelected.removeAll();
					jlUnSelected.setListData(unSelected);
					jlSelected.setListData(selected);
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});

		jlSelected.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				JList<String> theList = (JList<String>) e.getSource();
				int index = theList.locationToIndex(e.getPoint());
				if (index >= 0) {
					String slct = theList.getModel().getElementAt(index);
					for (String name : selected) {
						if (name.equalsIgnoreCase(slct)) {
							selected.remove(name);
							break;
						}
					}
					unSelected.addElement(slct);
					jlUnSelected.removeAll();
					jlSelected.removeAll();
					jlUnSelected.setListData(unSelected);
					jlSelected.setListData(selected);
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});

		btCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String species = (String) cbSpecies.getSelectedItem();
				if (null == species || species.equals("")) {
					return;
				}
				fillTable();
			}
		});

		btReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unSelected.removeAllElements();
				selected.removeAllElements();
				jlUnSelected.setListData(unSelected);
				jlSelected.setListData(selected);
				tableData.removeAllElements();
				model.setDataVector(tableData, colName);
			}
		});

		btSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CsvSaveFileChooser saveFile = new CsvSaveFileChooser("Save as ...");
				saveFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				saveFile.setMultiSelectionEnabled(false);
				int returnValue = saveFile.showSaveDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File fileOut = saveFile.getSelectedFile();
					String type = "csv";
					String fileName = fileOut.getAbsolutePath() + "." + type;
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
						for (int i = 0; i < tbResult.getColumnCount(); i++) {
							out.write(tbResult.getColumnName(i) + "\t");
						}
						out.newLine();
						for (int i = 0; i < tbResult.getRowCount(); i++) {
							for (int j = 0; j < tbResult.getColumnCount(); j++) {
								out.write(tbResult.getValueAt(i, j).toString() + "\t");
							}
							out.newLine();
						}
						out.close();
						JOptionPane.showMessageDialog(null, "Export Data Successfully!");
					} catch (IOException e1) {
						CircView.log.warn(e1.getMessage());
					}
				}
			}
		});

		tbResult.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				int row = tbResult.rowAtPoint(e.getPoint());
				int col = tbResult.columnAtPoint(e.getPoint());
				if (row >= 0 && col >= 0) {
					Object value = tbResult.getValueAt(row, col);
					if (null != value && !value.equals("")) {
						tbResult.setToolTipText(value.toString());
					} else {
						tbResult.setToolTipText(null);
					}
				}
			}
		});

		for (String speciesName : MainData.getSpeciesData().keySet()) {
			cbSpecies.addItem(speciesName);
		}
		for (int i = 0; i <= Constant.BP_MATCH_TOLERATE; i++) {
			cbTolerate.addItem(i + "");
		}

	}

	private void fillTable() {
		tableData.removeAllElements();
		if (selected.size() > 0) {
			fillTableOr();
		}
		model.setDataVector(tableData, colName);
	}

	private void fillTableOr() {
		int tolerate = Integer.parseInt((String) cbTolerate.getSelectedItem());
		int num = 0;
		String species = (String) cbSpecies.getSelectedItem();
		if ((null == species) || (species.equals(""))) {
			return;
		}
		TreeMap<String, Gene> genes = MainData.getSpeciesData().get(species);
		for (String geneName : genes.keySet()) {
			Gene gene = genes.get(geneName);
			TreeMap<String, GeneTranscript> geneTrans = gene.getGeneTranscripts();
			for (String geneTransName : geneTrans.keySet()) {
				GeneTranscript gt = geneTrans.get(geneTransName);
				TreeMap<String, CircRna> circRnas = gt.getCircRnas();
				Vector<String> circIds = new Vector<String>();
				for (String circId : circRnas.keySet()) {
					circIds.addElement(circId);
				}
				TreeMap<String, Vector<String>> matched = new TreeMap<String, Vector<String>>();
				TreeMap<Integer, String> deleted = new TreeMap<Integer, String>();
				for (int i = 0; i < circIds.size() - 1; i++) {
					for (int j = i + 1; j < circIds.size(); j++) {
						if ((null != deleted.get(i)) || (null != deleted.get(j))) {
							continue;
						}
						CircRna majorCirc = circRnas.get(circIds.get(i));
						CircRna minorCirc = circRnas.get(circIds.get(j));
						if ((Math.abs(majorCirc.getStartPoint() - minorCirc.getStartPoint()) <= tolerate)
								&& (Math.abs(majorCirc.getEndPoint() - minorCirc.getEndPoint()) <= tolerate)) {
							if (null != matched.get(majorCirc.getCircRnaID())) {
								Vector<String> sameCircs = matched.get(majorCirc.getCircRnaID());
								sameCircs.addElement(minorCirc.getCircRnaID());
								matched.put(majorCirc.getCircRnaID(), sameCircs);
								deleted.put(j, minorCirc.getCircRnaID());
							} else {
								Vector<String> sameCircs = new Vector<String>();
								sameCircs.addElement(minorCirc.getCircRnaID());
								matched.put(majorCirc.getCircRnaID(), sameCircs);
								deleted.put(j, minorCirc.getCircRnaID());
							}
						}
					}
				}

				for (int i = 0; i < circIds.size(); i++) {
					if (null != deleted.get(i)) {
						continue;
					}
					String circId = circIds.get(i);
					// tissues name, smaples name, tools name from circRNA info
					TreeMap<String, Integer> files = new TreeMap<String, Integer>();
					TreeMap<String, Integer> tissues = new TreeMap<String, Integer>();
					TreeMap<String, Integer> samples = new TreeMap<String, Integer>();
					TreeMap<String, Integer> tools = new TreeMap<String, Integer>();
					for (String fileName : gt.getCircRnas().get(circId).getFiles().keySet()) {
						files.put(fileName, 1);
					}
					for (String sampleName : gt.getCircRnas().get(circId).getSamples().keySet()) {
						samples.put(sampleName, 1);
					}
					for (String toolName : gt.getCircRnas().get(circId).getCircTools().keySet()) {
						tools.put(toolName, 1);
					}
					Vector<String> sameCircs = matched.get(circId);
					if (null != sameCircs) {
						for (String sameCircId : sameCircs) {
							for (String fileName : gt.getCircRnas().get(sameCircId).getFiles().keySet()) {
								files.put(fileName, 1);
							}
							for (String sampleName : gt.getCircRnas().get(sameCircId).getSamples().keySet()) {
								samples.put(sampleName, 1);
							}
							for (String toolName : gt.getCircRnas().get(sameCircId).getCircTools().keySet()) {
								tools.put(toolName, 1);
							}
						}
					}
					for (String sampleName : samples.keySet()) {
						String[] tmp = sampleName.split("_");
						tissues.put(tmp[0], 1);
					}

					// tissues name, smaples name, tools name from file table
					TreeMap<String, Integer> fTissues = new TreeMap<String, Integer>();
					TreeMap<String, Integer> fSamples = new TreeMap<String, Integer>();
					TreeMap<String, Integer> fTools = new TreeMap<String, Integer>();
					for (String name : selected) {
						for (Vector<String> rowData : MainData.getCircRnaFilesInfo()) {
							String sname = rowData.get(0);
							String tname = rowData.get(1);
							String fname = rowData.get(2);
							if(species.equalsIgnoreCase(sname) && fname.equalsIgnoreCase(name)) {
								String[] tmp = fname.split("_");
								fTissues.put(tmp[0], 1);
								tmp = fname.split("\\.");
								fSamples.put(tmp[0], 1);
								fTools.put(tname, 1);
							}
						}
					}

					boolean hasFile = false;
					for (String name : files.keySet()) {
						for (String file : selected) {
							if (file.equalsIgnoreCase(name)) {
								hasFile = true;
								break;
							}
						}
						if (hasFile) {
							break;
						}
					}
					if (!hasFile) {
						continue;
					}

					num++;
					Vector<String> one = new Vector<String>();
					// Number
					one.addElement(num + "");
					// Gene Name
					one.addElement(gene.getGeneName());
					// CircRNA ID
					String id = circId;
					if (null != sameCircs) {
						for (int j = 0; j < sameCircs.size(); j++) {
							if (0 == j) {
								id += "(";
							}
							id += sameCircs.get(j) + ",";
							if ((sameCircs.size() - 1) == j) {
								id += ")";
							}
						}
					}
					one.addElement(id);
					one.addElement(gt.getCircRnas().get(circId).getChrom());
					// Start Point
					String startP = gt.getCircRnas().get(circId).getStartPoint() + "";
					if (null != sameCircs) {
						for (int j = 0; j < sameCircs.size(); j++) {
							if (0 == j) {
								startP += "(";
							}
							startP += gt.getCircRnas().get(sameCircs.get(j)).getStartPoint() + ",";
							if ((sameCircs.size() - 1) == j) {
								startP += ")";
							}
						}
					}
					one.addElement(startP);
					// End Point
					String endP = gt.getCircRnas().get(circId).getEndPoint() + "";
					if (null != sameCircs) {
						for (int j = 0; j < sameCircs.size(); j++) {
							if (0 == j) {
								endP += "(";
							}
							endP += gt.getCircRnas().get(sameCircs.get(j)).getEndPoint() + ",";
							if ((sameCircs.size() - 1) == j) {
								endP += ")";
							}
						}
					}
					one.addElement(endP);
					// Junction Reads
					String reads = gt.getCircRnas().get(circId).getJunctionReads() + "";
					if (null != sameCircs) {
						for (int j = 0; j < sameCircs.size(); j++) {
							if (0 == j) {
								reads += "(";
							}
							reads += gt.getCircRnas().get(sameCircs.get(j)).getJunctionReads() + ",";
							if ((sameCircs.size() - 1) == j) {
								reads += ")";
							}
						}
					}
					one.addElement(reads);
					// Strand
					one.addElement(gt.getCircRnas().get(circId).getStrand());
					// CircRNA Tissues
					String tissue = "";
					int tissueNum = 0;
					for (String tissueName : fTissues.keySet()) {
						if (null != tissues.get(tissueName)) {
							tissue += tissueName + ",";
							tissueNum++;
						}
					}
					one.addElement(tissue);
					one.addElement(tissueNum + "");
					// CircRNA Sample Name
					String sample = "";
					int sampleNum = 0;
					for (String sampleName : fSamples.keySet()) {
						if (null != samples.get(sampleName)) {
							sample += sampleName + ",";
							sampleNum++;
						}

					}
					one.addElement(sample);
					one.addElement(sampleNum + "");
					// CircRNA Tools or Algorithm
					String tool = "";
					int toolNum = 0;
					for (String toolName : fTools.keySet()) {
						if (null != tools.get(toolName)) {
							tool += toolName + ",";
							toolNum++;
						}
					}
					one.addElement(tool);
					one.addElement(toolNum + "");
					// CircRNA Type
					one.addElement(gt.getCircRnas().get(circId).getCircRnaType());
					// CircRNA Region
					one.addElement(gt.getCircRnas().get(circId).getRegion());
					tableData.addElement(one);
				}
				break;
			}
		}
	}

}
