package cn.edu.whu.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import cn.edu.whu.CircRna;
import cn.edu.whu.CircView;
import cn.edu.whu.Gene;
import cn.edu.whu.GeneTranscript;
import cn.edu.whu.MainData;
import cn.edu.whu.util.Constant;
import cn.edu.whu.util.EvenOddRenderer;

public class ComparisonFrame extends JFrame {
	private JComboBox<String> cbSpecies;
	private JComboBox<String> cbCompare;
	private JComboBox<String> cbTolerate;
	private boolean sampleOrTool;
	private JList<String> jlCompareItem;
	private JList<String> jlUnSelected;
	private JList<String> jlSelected;
	private Vector<String> compareItem;
	private Vector<String> unSelected;
	private Vector<String> selected;
	private JScrollPane scrollCompareItem;
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
		sampleOrTool = true;
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
		JLabel lbEmpty = new JLabel(" ");
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		JPanel jpWest = new JPanel();
		jpWest.setLayout(new BoxLayout(jpWest, BoxLayout.Y_AXIS));
		cbSpecies = new JComboBox<String>();
		cbSpecies.setPreferredSize(new Dimension(200, 28));
		cbCompare = new JComboBox<String>();
		cbCompare.setPreferredSize(new Dimension(200, 28));
		JPanel jpTmp = new JPanel();
		JLabel lbTolerate = new JLabel("Compare Overlap");
		cbTolerate = new JComboBox<String>();
		cbTolerate.setPreferredSize(new Dimension(100, 28));
		// cbTolerate.setEditable(true);
		JLabel lbBp = new JLabel("bp");
		jpTmp.add(lbTolerate);
		jpTmp.add(cbTolerate);
		jpTmp.add(lbBp);
		jpWest.add(cbSpecies);
		jpWest.add(cbCompare);
		jpWest.add(jpTmp);
		northPanel.add(jpWest, BorderLayout.WEST);

		JPanel jpCenter = new JPanel(new BorderLayout());
		final JLabel lbCompareItem = new JLabel("Algorithm List");
		final JLabel lbUnSelected = new JLabel("Sample List");
		final JLabel lbSelected = new JLabel("Sample selected");
		jlCompareItem = new JList<String>();
		jlUnSelected = new JList<String>();
		jlSelected = new JList<String>();
		compareItem = new Vector<String>();
		unSelected = new Vector<String>();
		selected = new Vector<String>();
		jlCompareItem.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlUnSelected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlSelected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollCompareItem = new JScrollPane(jlCompareItem);
		scrollUnSelected = new JScrollPane(jlUnSelected);
		scrollSelected = new JScrollPane(jlSelected);
		scrollCompareItem.setPreferredSize(new Dimension((int) screenSize.getWidth() / 4, 100));
		scrollUnSelected.setPreferredSize(new Dimension((int) screenSize.getWidth() / 4, 100));
		scrollSelected.setPreferredSize(new Dimension((int) screenSize.getWidth() / 4, 100));
		JPanel jpLeft = new JPanel();
		JPanel jpCent = new JPanel();
		JPanel jpRight = new JPanel();
		jpLeft.setLayout(new BoxLayout(jpLeft, BoxLayout.Y_AXIS));
		jpCent.setLayout(new BoxLayout(jpCent, BoxLayout.Y_AXIS));
		jpRight.setLayout(new BoxLayout(jpRight, BoxLayout.Y_AXIS));
		jpLeft.add(lbCompareItem);
		jpLeft.add(scrollCompareItem);
		jpCent.add(lbUnSelected);
		jpCent.add(scrollUnSelected);
		jpRight.add(lbSelected);
		jpRight.add(scrollSelected);
		jpCenter.add(jpLeft, BorderLayout.WEST);
		jpCenter.add(jpCent, BorderLayout.CENTER);
		jpCenter.add(jpRight, BorderLayout.EAST);
		northPanel.add(jpCenter, BorderLayout.CENTER);

		final JButton btCompare = new JButton("Compare");
		JButton btReset = new JButton("Reset");
		JButton btSave = new JButton("Save as");
		JPanel jpEast = new JPanel();
		jpEast.setLayout(new BoxLayout(jpEast, BoxLayout.Y_AXIS));
		jpEast.add(lbEmpty);
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
		colName.addElement("algorithm name");
		colName.addElement("algorithm num");
		colName.addElement("sample name");
		colName.addElement("sample num");
		colName.addElement("circRNA type");
		colName.addElement("circRNA region");

		cbSpecies.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					compareItem.removeAllElements();
					unSelected.removeAllElements();
					selected.removeAllElements();
					String species = (String) cbSpecies.getSelectedItem();
					if (null == species) {
						return;
					}
					if (sampleOrTool) {
						lbCompareItem.setText("Algorithm List");
						lbUnSelected.setText("Sample List");
						lbSelected.setText("Sample Compare");
						if (null != MainData.getFileToolTable().get(species)) {
							Vector<Vector<String>> table = MainData.getFileToolTable().get(species);
							TreeMap<String, String> tools = new TreeMap<String, String>();
							for (Vector<String> line : table) {
								tools.put(line.get(1), line.get(1));
							}
							for (String name : tools.keySet()) {
								compareItem.addElement(name);
							}
						}
					} else {
						lbCompareItem.setText("Sample List");
						lbUnSelected.setText("Algorithm List");
						lbSelected.setText("Algorithm Compare");
						if (null != MainData.getFileToolTable().get(species)) {
							Vector<Vector<String>> table = MainData.getFileToolTable().get(species);
							TreeMap<String, String> samples = new TreeMap<String, String>();
							for (Vector<String> line : table) {
								samples.put(line.get(2), line.get(2));
							}
							for (String name : samples.keySet()) {
								compareItem.addElement(name);
							}
						}
					}
					jlCompareItem.setListData(compareItem);
					jlUnSelected.setListData(unSelected);
					jlSelected.setListData(selected);
				}
			}
		});

		cbCompare.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String compareType = (String) cbCompare.getSelectedItem();
					if (null == compareType) {
						return;
					}
					if (compareType.toLowerCase().contains("sample")) {
						lbCompareItem.setText("Algorithm List");
						lbUnSelected.setText("Sample List");
						lbSelected.setText("Sample Compare");
						sampleOrTool = true;
					} else {
						lbCompareItem.setText("Sample List");
						lbUnSelected.setText("Algorithm List");
						lbSelected.setText("Algorithm Compare");
						sampleOrTool = false;
					}
					compareItem.removeAllElements();
					unSelected.removeAllElements();
					selected.removeAllElements();
					String species = (String) cbSpecies.getSelectedItem();
					if (null == species) {
						return;
					}
					if (sampleOrTool) {
						if (null != MainData.getFileToolTable().get(species)) {
							Vector<Vector<String>> table = MainData.getFileToolTable().get(species);
							TreeMap<String, String> tools = new TreeMap<String, String>();
							for (Vector<String> line : table) {
								tools.put(line.get(1), line.get(1));
							}
							for (String name : tools.keySet()) {
								compareItem.addElement(name);
							}
						}
					} else {
						if (null != MainData.getFileToolTable().get(species)) {
							Vector<Vector<String>> table = MainData.getFileToolTable().get(species);
							TreeMap<String, String> samples = new TreeMap<String, String>();
							for (Vector<String> line : table) {
								int tmpP = line.get(2).lastIndexOf(".");
								String name = line.get(2).substring(0, tmpP);
								samples.put(name, name);
							}
							for (String name : samples.keySet()) {
								compareItem.addElement(name);
							}
						}
					}
					jlCompareItem.setListData(compareItem);
					jlUnSelected.setListData(unSelected);
					jlSelected.setListData(selected);
				}
			}
		});

		jlCompareItem.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				JList<String> theList = (JList<String>) e.getSource();
				int index = theList.locationToIndex(e.getPoint());
				if (index >= 0) {
					String slct = theList.getModel().getElementAt(index);
					String species = (String) cbSpecies.getSelectedItem();
					if (null == species) {
						return;
					}
					Vector<Vector<String>> table = MainData.getFileToolTable().get(species);
					if (null == table) {
						return;
					}
					TreeMap<String, String> item = new TreeMap<String, String>();
					if (sampleOrTool) {
						for (Vector<String> line : table) {
							if (line.get(1).equals(slct)) {
								item.put(line.get(2), line.get(2));
							}
						}
					} else {
						for (Vector<String> line : table) {
							if (line.get(2).contains(slct)) {
								item.put(line.get(2), line.get(2));
							}
						}
					}
					unSelected.removeAllElements();
					selected.removeAllElements();
					jlUnSelected.removeAll();
					jlSelected.removeAll();
					for (String name : item.keySet()) {
						unSelected.addElement(name);
					}
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
						JOptionPane.showMessageDialog(null, "Data was exported successfully!");
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
		cbCompare.addItem("Compare by Samples");
		cbCompare.addItem("Compare by Algorithms");
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
					TreeMap<String, Integer> samples = new TreeMap<String, Integer>();
					TreeMap<String, Integer> tools = new TreeMap<String, Integer>();
					for (String sampleName : gt.getCircRnas().get(circId).getSamples().keySet()) {
						samples.put(sampleName, 1);
					}
					for (String toolName : gt.getCircRnas().get(circId).getCircTools().keySet()) {
						tools.put(toolName, 1);
					}
					Vector<String> sameCircs = matched.get(circId);
					if (null != sameCircs) {
						for (String sameCircId : sameCircs) {
							for (String sampleName : gt.getCircRnas().get(sameCircId).getSamples().keySet()) {
								samples.put(sampleName, 1);
							}
							for (String toolName : gt.getCircRnas().get(sameCircId).getCircTools().keySet()) {
								tools.put(toolName, 1);
							}
						}
					}
					Vector<Vector<String>> table = MainData.getFileToolTable().get(species);
					TreeMap<String, String> uniTools = new TreeMap<String, String>();
					for (String name : selected) {
						for (Vector<String> line : table) {
							if (line.get(2).equalsIgnoreCase(name)) {
								uniTools.put(line.get(1), line.get(1));
							}
						}
					}

					boolean hasTool = false;
					for (String name : samples.keySet()) {
						for (String sampleName : selected) {
							if (sampleName.equalsIgnoreCase(name)) {
								hasTool = true;
								break;
							}
						}
						if (hasTool) {
							break;
						}
					}
					if (!hasTool) {
						continue;
					}

					boolean hasSample = false;
					for (String name : tools.keySet()) {
						for (String toolName : uniTools.keySet()) {
							if (toolName.equalsIgnoreCase(name)) {
								hasSample = true;
								break;
							}
						}
						if (hasSample) {
							break;
						}
					}
					if (!hasSample) {
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
					// CircRNA Tools or Algorithm

					String algorithm = "";
					int toolNum = 0;
					for (String toolName : tools.keySet()) {
						if (null != uniTools.get(toolName)) {
							algorithm += toolName + ",";
							toolNum++;
						}
					}
					one.addElement(algorithm);
					one.addElement(toolNum + "");
					// CircRNA Sample Name
					TreeMap<String, String> uniSamples = new TreeMap<String, String>();
					for (String name : selected) {
						if (null != samples.get(name)) {
							int tmpP = name.lastIndexOf(".");
							uniSamples.put(name.substring(0, tmpP), name);
						}
					}
					String sampleName = "";
					for (String name : uniSamples.keySet()) {
						sampleName += name + ",";
					}
					one.addElement(sampleName);
					one.addElement(uniSamples.size() + "");
					one.addElement(gt.getCircRnas().get(circId).getCircRnaType());
					one.addElement(gt.getCircRnas().get(circId).getRegion());
					tableData.addElement(one);
				}
				break;
			}
		}
	}

}
