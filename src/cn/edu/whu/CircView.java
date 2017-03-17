package cn.edu.whu;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.whu.ui.AboutDialog;
import cn.edu.whu.ui.CircRnaImagePanel;
import cn.edu.whu.ui.CircRnaDataLoadDialog;
import cn.edu.whu.ui.CircRnaToolAddDialog;
import cn.edu.whu.ui.CircRnaToolDelDialog;
import cn.edu.whu.ui.ComparisonFrame;
import cn.edu.whu.ui.DetailsResultDialog;
import cn.edu.whu.ui.DataLoadingDialog;
import cn.edu.whu.ui.RbpLoadDialog;
import cn.edu.whu.ui.SaveImageDialog;
import cn.edu.whu.ui.SpeciesNameAddDialog;
import cn.edu.whu.ui.SpeciesNameDelDialog;
import cn.edu.whu.ui.SpeciesDataClearDialog;
import cn.edu.whu.ui.SpeciesDataLoadDialog;
import cn.edu.whu.ui.RbpClearDialog;
import cn.edu.whu.ui.MreClearDialog;
import cn.edu.whu.ui.MreLoadDialog;
import cn.edu.whu.util.Constant;
import cn.edu.whu.util.DbUtil;
import cn.edu.whu.util.RuntimeUtils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;

public class CircView {

	public static JFrame frame;
	private JPanel imagePanel;
	private static JComboBox<String> cbSpecies;
	private static JComboBox<String> cbCircRnaTool;
	private static JComboBox<String> cbSample;
	private static JComboBox<String> cbChrom;
	private static Vector<String> geneTransName;
	private static JList<String> geneTransList;
	private static JComboBox<String> cbCircRnaSelect;
	private CircRnaImagePanel circRnaImage;

	private static JCheckBox cbRbp;
	private static JCheckBox cbMre;

	private static Connection conn;
	public static Logger log;

	private static TreeMap<String, Gene> genes;
	private boolean cbChromInit;

	private static JTextArea tipsTA;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		initLogConfig(Constant.LOG_FILE);
		log = Logger.getLogger(CircView.class);
		PropertyConfigurator.configure(Constant.LOG_FILE);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					long mem = RuntimeUtils.getAvailableMemory();
					int MB = 1000000;
					if (mem < 400 * MB) {
						int mb = (int) (mem / MB);
						JOptionPane.showMessageDialog(null,
								"Warning: CircView is running with low available memory (" + mb + " mb)");
					}
					new CircView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void initLogConfig(String logFile) {
		File file = new File(Constant.LOG_FILE);
		if (!file.exists()) {
			Properties logPro = new Properties();
			try {
				logPro.setProperty(Constant.LOG4J_ROOTLOGGER, Constant.LOG4J_ROOTLOGGER_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_CONSOLE, Constant.LOG4J_APPENDER_CONSOLE_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_CONSOLE_TARGET,
						Constant.LOG4J_APPENDER_CONSOLE_TARGET_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_CONSOLE_LAYOUT,
						Constant.LOG4J_APPENDER_CONSOLE_LAYOUT_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_CONSOLE_LAYOUT_CONV_PATT,
						Constant.LOG4J_APPENDER_CONSOLE_LAYOUT_CONV_PATT_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_FILE, Constant.LOG4J_APPENDER_FILE_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_FILE_FILE, Constant.LOG4J_APPENDER_FILE_FILE_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_FILE_MAXFILESIZE,
						Constant.LOG4J_APPENDER_FILE_MAXFILESIZE_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_FILE_THRESHOLD,
						Constant.LOG4J_APPENDER_FILE_THRESHOLD_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_FILE_LAYOUT, Constant.LOG4J_APPENDER_FILE_LAYOUT_VALUE);
				logPro.setProperty(Constant.LOG4J_APPENDER_FILE_LAYOUT_CONV_PATT,
						Constant.LOG4J_APPENDER_FILE_LAYOUT_CONV_PATT_VALUE);

				// Write LogConfig File
				OutputStream os = new FileOutputStream(Constant.LOG_FILE);
				logPro.store(os, "Save LogConfig File");
			} catch (IOException e) {
				CircView.log.error(e.getMessage());
			}
		}
	}

	/**
	 * Create the application.
	 */
	public CircView() {
		// Init Config
		initData();
		// Connect to DB
		connectDB();
		// Initialize Interface
		initInterface();
		// Auto Resize
		frame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				display();
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	private void initData() {
		new MainData();
		geneTransName = new Vector<String>();
	}

	private void initInterface() {
		cbChromInit = true;
		initComponent();
		initFrame();
		initMenu();
		initToolBar();
		initMainPanel();
	}

	private void initComponent() {
		frame = new JFrame("CircRNA Viewer");
		cbSpecies = new JComboBox<String>();
		cbCircRnaTool = new JComboBox<String>();
		cbSample = new JComboBox<String>();
		cbChrom = new JComboBox<String>();
		geneTransList = new JList<String>();
	}

	private void connectDB() {
		try {
			conn = DbUtil.connectDb();
			DbUtil.createDb(conn);
		} catch (ClassNotFoundException | SQLException e) {
			log.warn(e.getMessage());
		}
		try {
			DbUtil.useDb(conn);
		} catch (ClassNotFoundException | SQLException e) {
			log.warn(e.getMessage());
		}
	}

	private void initFrame() {
		// TODO Auto-generated method stub
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight());
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initMenu() {
		// Add MenuBar to Frame
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		// Add Menu to MenuBar
		JMenu mnFile = new JMenu("File");
		JMenu mnSpecies = new JMenu("Species");
		JMenu mnCircRna = new JMenu("CircRNA");
		JMenu mnAnalysis = new JMenu("Analysis");
		JMenu mnMre = new JMenu("MRE");
		JMenu mnRbp = new JMenu("RBP");
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnFile);
		menuBar.add(mnSpecies);
		menuBar.add(mnCircRna);
		menuBar.add(mnAnalysis);
		menuBar.add(mnMre);
		menuBar.add(mnRbp);
		menuBar.add(mnHelp);
		// Add MenuItem to File Menu
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnFile.add(mntmQuit);
		// Add MenuItem to Species Menu
		JMenuItem mntmSpeciesLoad = new JMenuItem("Load Data");
		JMenuItem mntmSpeciesClear = new JMenuItem("Clear");
		JMenuItem mntmSpeciesAdd = new JMenuItem("Add Species");
		JMenuItem mntmSpeciesDel = new JMenuItem("Delete Species");
		mnSpecies.add(mntmSpeciesLoad);
		mnSpecies.add(mntmSpeciesClear);
		mnSpecies.addSeparator();
		mnSpecies.add(mntmSpeciesAdd);
		mnSpecies.add(mntmSpeciesDel);
		// Add MenuItem to CircRNA Menu
		JMenuItem mntmCircRnaLoad = new JMenuItem("Load Data");
		// JMenuItem mntmCircRnaClear = new JMenuItem("Clear");
		JMenuItem mntmCircRnaAdd = new JMenuItem("Add Tool");
		JMenuItem mntmCircRnaDel = new JMenuItem("Delete Tool");
		mnCircRna.add(mntmCircRnaLoad);
		// mnCircRna.add(mntmCircRnaClear);
		mnCircRna.addSeparator();
		mnCircRna.add(mntmCircRnaAdd);
		mnCircRna.add(mntmCircRnaDel);
		// Add MenuItem to Analysis Menu
		JMenuItem mntmCompare = new JMenuItem("Comparison");
		mnAnalysis.add(mntmCompare);
		// Add MenuItem to MRE Menu
		JMenuItem mntmMreLoad = new JMenuItem("Load Data");
		JMenuItem mntmMreClear = new JMenuItem("Clear");
		mnMre.add(mntmMreLoad);
		mnMre.add(mntmMreClear);
		// Add MenuItem to RBP Menu
		JMenuItem mntmRbpLoad = new JMenuItem("Load Data");
		JMenuItem mntmRbpClear = new JMenuItem("Clear");
		mnRbp.add(mntmRbpLoad);
		mnRbp.add(mntmRbpClear);
		// Add MenuItem to Help
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		//
		if (null == conn) {
			mntmRbpLoad.setEnabled(false);
			mntmRbpClear.setEnabled(false);
			mntmMreLoad.setEnabled(false);
			mntmMreClear.setEnabled(false);
			// JOptionPane.showMessageDialog(CircView.frame, "Can NOT Connect to
			// Database!");
		}

		// Add ActionListener to Quit MenuItem
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// Add ActionListener to Species Data Load MenuItem
		mntmSpeciesLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SpeciesDataLoadDialog();
			}
		});

		// Add ActionListener to CircRNA Clear MenuItem
		mntmSpeciesClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SpeciesDataClearDialog();
			}
		});

		// Add ActionListener to Species Name Add MenuItem
		mntmSpeciesAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SpeciesNameAddDialog();
			}
		});

		// Add ActionListener to Species Name Delete MenuItem
		mntmSpeciesDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SpeciesNameDelDialog();
			}
		});

		// Add ActionListener to CircRNA Load MenuItem
		mntmCircRnaLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CircRnaDataLoadDialog();
			}
		});

		// Add ActionListener to CircRNA Tool Name Add MenuItem
		mntmCircRnaAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CircRnaToolAddDialog();
			}
		});

		// Add ActionListener to CircRNA Tool Name Delete MenuItem
		mntmCircRnaDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CircRnaToolDelDialog();
			}
		});

		// Add ActionListener to CircRNA Tool Name Delete MenuItem
		mntmCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ComparisonFrame();
			}
		});

		// Add ActionListener to MRE Load Data MenuItem
		mntmMreLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MreLoadDialog(conn);
			}
		});
		// Add ActionListener to MRE Clear Data MenuItem
		mntmMreClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MreClearDialog(conn);
			}
		});

		// Add ActionListener to RBP Load Data MenuItem
		mntmRbpLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new RbpLoadDialog(conn);
			}
		});
		// Add ActionListener to RBP Clear Data MenuItem
		mntmRbpClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new RbpClearDialog(conn);
			}
		});

		// Add ActionListener to About MenuItem
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AboutDialog();
			}
		});
	}

	private void initToolBar() {
		// Add ToolBar to Frame
		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		// Add Species JComboBox to ToolBar
		cbSpecies.setPreferredSize(new Dimension(180, 28));
		toolBar.add(cbSpecies);
		// Add CircRnaTool JComboBox to ToolBar
		cbCircRnaTool.setPreferredSize(new Dimension(180, 28));
		toolBar.add(cbCircRnaTool);
		// Add Sample JComboBox to ToolBar
		cbSample.setPreferredSize(new Dimension(180, 28));
		toolBar.add(cbSample);
		// Add Chrom JComboBox to ToolBar
		cbChrom.setPreferredSize(new Dimension(100, 28));
		toolBar.add(cbChrom);

		// Init Species ComboBox
		updateSpeciesCombo();
		// Init CircRna ComboBox
		updateCircRnaToolsCombo();
		// Init Sample ComboBox
		updateSamplesCombo();
		// Init Chrom ComboBox
		updateCbChrom();
		// Init GeneTranscript List
		updateGeneTransList();

		// Add ItemListener to Species
		cbSpecies.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String species = (String) cbSpecies.getSelectedItem();
					genes = MainData.getSpeciesData().get(species);
					updateCircRnaToolsCombo();
					updateSamplesCombo();
					updateCbChrom();
					updateGeneTransList();
					updateRbpMreStatus();
				}
			}
		});

		// Add ItemListener to CircRnaTool
		cbCircRnaTool.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String speciesName = (String) cbSpecies.getSelectedItem();
					String toolName = (String) cbCircRnaTool.getSelectedItem();
					if (null == speciesName || null == toolName) {
						return;
					}
					cbSample.removeAllItems();
					cbSample.addItem("All");

					if (null == MainData.getFileToolTable().get(speciesName)) {
						return;
					}

					TreeMap<String, String> samples = new TreeMap<String, String>();
					Vector<Vector<String>> table = MainData.getFileToolTable().get(speciesName);
					for (Vector<String> line : table) {
						if (toolName.equalsIgnoreCase("All") || line.get(1).equalsIgnoreCase(toolName))
							samples.put(line.get(2), line.get(2));
					}
					for (String sampleName : samples.keySet()) {
						cbSample.addItem(sampleName);
					}
					updateGeneTransList();
				}
			}
		});
		// Add ItemListener to Sample
		cbSample.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					updateGeneTransList();
				}
			}
		});

		// Add ItemListener to Chrom
		cbChrom.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (cbChromInit) {
						cbChromInit = false;
						return;
					}
					updateGeneTransList();
				}
			}
		});
		cbChrom.addItem("All");

		// Search
		JLabel lbGeneName = new JLabel("Gene Name:", JLabel.RIGHT);
		final JTextField tfGeneName = new JTextField();
		tfGeneName.setPreferredSize(new Dimension(100, 20));
		JLabel lbLocation = new JLabel(" or Location:", JLabel.RIGHT);
		final JTextField tfLocStart = new JTextField();
		tfLocStart.setPreferredSize(new Dimension(100, 20));
		JLabel lbTo = new JLabel("-", JLabel.LEFT);
		final JTextField tfLocEnd = new JTextField();
		tfLocEnd.setPreferredSize(new Dimension(100, 20));
		// JButton btSearch = new JButton("Search");
		JButton btReset = new JButton("Reset");
		// Add to ToolBar
		toolBar.addSeparator();
		toolBar.add(lbGeneName);
		toolBar.add(tfGeneName);
		toolBar.add(lbLocation);
		toolBar.add(tfLocStart);
		toolBar.add(lbTo);
		toolBar.add(tfLocEnd);
		// toolBar.add(btSearch);
		toolBar.add(btReset);

		tfGeneName.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				searchByGeneName();
			}

			public void removeUpdate(DocumentEvent e) {
				searchByGeneName();
			}

			public void changedUpdate(DocumentEvent e) {
			}

			private void searchByGeneName() {
				String searchGeneName = tfGeneName.getText();
				geneTransName.removeAllElements();
				if (genes != null) {
					for (String geneName : genes.keySet()) {
						Gene gene = genes.get(geneName);
						for (String transName : gene.getGeneTranscripts().keySet()) {
							GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
							if (geneTrans.getGeneName().toLowerCase().contains(searchGeneName.toLowerCase())
									|| searchGeneName.equalsIgnoreCase("")) {
								if (geneTrans.getCircRnas().size() > 0) {
									geneTransName.addElement(gene.getGeneName() + " [" + geneTrans.getTranscriptName()
											+ "] " + "(" + geneTrans.getCircRnas().size() + ")");
								}
							}
						}
					}
					geneTransList.setListData(geneTransName);
				}
			}
		});

		tfLocStart.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				searchByLocation();
			}

			public void removeUpdate(DocumentEvent e) {
				searchByLocation();
			}

			public void changedUpdate(DocumentEvent e) {

			}

			private void searchByLocation() {
				long start = 0;
				long end = Long.MAX_VALUE;
				if (tfLocStart.getText().matches("[0-9]+")) {
					String str = tfLocStart.getText();
					start = Long.parseLong(str);
				}
				if (tfLocEnd.getText().matches("[0-9]+")) {
					String str = tfLocEnd.getText();
					end = Long.parseLong(str);
				}
				geneTransName.removeAllElements();
				if (genes != null) {
					for (String geneName : genes.keySet()) {
						Gene gene = genes.get(geneName);
						for (String transName : gene.getGeneTranscripts().keySet()) {
							GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
							if ((start <= geneTrans.getTxStart()) && (geneTrans.getTxEnd() <= end)) {
								if (geneTrans.getCircRnas().size() > 0) {
									geneTransName.addElement(gene.getGeneName() + " [" + geneTrans.getTranscriptName()
											+ "] " + "(" + geneTrans.getCircRnas().size() + ")");
								}
							}
						}
					}
					geneTransList.setListData(geneTransName);
				}
			}
		});

		tfLocEnd.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				searchByLocation();
			}

			public void removeUpdate(DocumentEvent e) {
				searchByLocation();
			}

			public void changedUpdate(DocumentEvent e) {

			}

			private void searchByLocation() {
				long start = 0;
				long end = Long.MAX_VALUE;
				if (tfLocStart.getText().matches("[0-9]+")) {
					String str = tfLocStart.getText();
					start = Long.parseLong(str);
				}
				if (tfLocEnd.getText().matches("[0-9]+")) {
					String str = tfLocEnd.getText();
					end = Long.parseLong(str);
				}
				geneTransName.removeAllElements();
				if (genes != null) {
					for (String geneName : genes.keySet()) {
						Gene gene = genes.get(geneName);
						for (String transName : gene.getGeneTranscripts().keySet()) {
							GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
							if ((start <= geneTrans.getTxStart()) && (geneTrans.getTxEnd() <= end)) {
								if (geneTrans.getCircRnas().size() > 0) {
									geneTransName.addElement(gene.getGeneName() + " [" + geneTrans.getTranscriptName()
											+ "] " + "(" + geneTrans.getCircRnas().size() + ")");
								}
							}
						}
					}
					geneTransList.setListData(geneTransName);
				}
			}
		});

		// Add ActionListener to Reset Button
		btReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateGeneTransList();
				tfGeneName.setText("");
				tfLocStart.setText("");
				tfLocEnd.setText("");
			}
		});
	}

	private void initMainPanel() {
		// Add MainPanel to Frame
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

		// Touch geneTransList to ScrollPane, Add ScrollPane to MainPanel
		JPanel geneTransPanel = new JPanel(new BorderLayout());
		final JComboBox<String> cbSort = new JComboBox<String>();
		cbSort.setPreferredSize(new Dimension(mainPanel.getWidth(), 28));
		cbSort.addItem("sort name by asc");
		cbSort.addItem("sort name by desc");
		cbSort.addItem("sort position by asc");
		cbSort.addItem("sort position by desc");
		cbSort.addItem("sort abundance by asc");
		cbSort.addItem("sort abundance by desc");
		geneTransPanel.add(cbSort, BorderLayout.NORTH);

		JScrollPane geneTransScrollPane = new JScrollPane(geneTransList);
		geneTransScrollPane.setPreferredSize(new Dimension(250, mainPanel.getHeight()));
		geneTransPanel.add(geneTransScrollPane, BorderLayout.CENTER);
		mainPanel.add(geneTransPanel, BorderLayout.WEST);

		// Add ImagePanel to MainPanel
		imagePanel = new JPanel(new BorderLayout());
		mainPanel.add(imagePanel, BorderLayout.CENTER);

		// Add ImageToolBar to ImagePanel
		JPanel imageToolBar = new JPanel();
		imageToolBar.setLayout(new FlowLayout());
		imageToolBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		imagePanel.add(imageToolBar, BorderLayout.NORTH);

		// Add Buttons to ImageToolBar
		JButton btHome = new JButton("Home");
		cbCircRnaSelect = new JComboBox<String>();
		JButton btZoomIn = new JButton("Zoom In");
		JButton btZoomOut = new JButton("Zoom Out");
		cbRbp = new JCheckBox("RBP", null, false);
		cbMre = new JCheckBox("MRE", null, false);
		JButton btDetails = new JButton("Details");
		JButton btSaveImage = new JButton("Save Image");
		cbCircRnaSelect.setPreferredSize(new Dimension(200, 28));
		imageToolBar.add(btHome);
		imageToolBar.add(cbCircRnaSelect);
		imageToolBar.add(btZoomIn);
		imageToolBar.add(btZoomOut);
		imageToolBar.add(cbRbp);
		imageToolBar.add(cbMre);
		imageToolBar.add(btDetails);
		imageToolBar.add(btSaveImage);

		if ((null == conn) || (null == cbSpecies.getSelectedItem()) || (null == circRnaImage.getGt())) {
			cbRbp.setEnabled(false);
			cbMre.setEnabled(false);
		}

		// Add Image to ImageScrollPane, Add ImageSchollPane to ImagePanel
		circRnaImage = new CircRnaImagePanel();
		final JScrollPane imageScrollPane = new JScrollPane(circRnaImage);
		imagePanel.add(imageScrollPane, BorderLayout.CENTER);

		// Mouse Tips
		tipsTA = new JTextArea();
		tipsTA.setLineWrap(true);
		tipsTA.setWrapStyleWord(true);
		circRnaImage.add(tipsTA);
		circRnaImage.setLayout(null);

		// Add ItemListener to Sort ComboBox
		cbSort.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String sortType = cbSort.getSelectedItem().toString();
					updateGeneTransList(sortType);

				}
			}
		});

		// Add ListSelectionListener to GeneTransList
		geneTransList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				cbRbp.setSelected(false);
				cbMre.setSelected(false);
				if (e.getValueIsAdjusting()) {
					circRnaImage.clearRbp();
					circRnaImage.clearMre();
					preDisplay();
					display();
					updateCircRnasCheckList();
					updateRbpMreStatus();
				}
			}
		});
		geneTransList.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent key) {
				cbRbp.setSelected(false);
				cbMre.setSelected(false);
				circRnaImage.clearRbp();
				circRnaImage.clearMre();
				preDisplay();
				display();
				updateCircRnasCheckList();
				updateRbpMreStatus();
			}
		});

		btHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cbCircRnaSelect.getItemCount() > 0
						&& !cbCircRnaSelect.getSelectedItem().toString().equalsIgnoreCase("All")) {
					cbCircRnaSelect.setSelectedIndex(0);
				}
			}
		});

		// Add ActionListener to CircRnaSelect
		cbCircRnaSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String circRnaId = "";
				if (null != cbCircRnaSelect.getSelectedItem()) {
					circRnaId = cbCircRnaSelect.getSelectedItem().toString();
				} else {
					circRnaId = "All";
				}
				if (circRnaId.equalsIgnoreCase("All")) {
					circRnaImage.selectAllCircRnas();
				} else {
					circRnaImage.selectOneCircRna(circRnaId);
				}
				display();
			}
		});

		// Add ActionListener to Zoom In Button
		btZoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				circRnaImage.zoomIn();
			}
		});

		// Add ActionListener to Zoom Out Button
		btZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				circRnaImage.zoomOut();
			}
		});

		// Add ActionListener to RBP CheckBox
		cbRbp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = cbRbp.isSelected();
				if (selected) {
					if (null != conn) {
						if ((null != cbSpecies.getSelectedItem()) && (null != circRnaImage.getGt())) {
							DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(frame, "Loading RBP Data ...");
							circRnaImage.queryRbpData(cbSpecies.getSelectedItem().toString(), circRnaImage.getGt());
							dataLoadingDialog.setVisible(false);
						}
					} else {
						JOptionPane.showMessageDialog(CircView.frame, "Can NOT connect to the Server!");
						cbRbp.setSelected(false);
					}
					display();
				} else {
					circRnaImage.clearRbp();
					display();
				}
			}
		});

		// Add ActionListener to MRE CheckBox
		cbMre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = cbMre.isSelected();
				if (selected) {
					if (null != conn) {
						if ((null != cbSpecies.getSelectedItem()) && (null != circRnaImage.getGt())) {
							DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(frame, "Loading MRE Data ...");
							circRnaImage.queryMreData(cbSpecies.getSelectedItem().toString(), circRnaImage.getGt());
							dataLoadingDialog.setVisible(false);
						}
					} else {
						JOptionPane.showMessageDialog(CircView.frame, "Can NOT connect to the Server!");
						cbMre.setSelected(false);
					}
					display();
				} else {
					circRnaImage.clearMre();
					display();
				}
			}
		});

		// Add ActionListener to View the Details
		btDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DetailsResultDialog(circRnaImage.getDetails(circRnaImage.getGt()));
			}
		});

		// Add ActionListener to Save Image Button
		btSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SaveImageDialog(circRnaImage);
			}
		});

		// Add MouseListener to
		circRnaImage.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (0 == cbCircRnaSelect.getItemCount()) {
					return;
				}
				if (!cbCircRnaSelect.getSelectedItem().equals("All")) {
					cbCircRnaSelect.setSelectedItem("All");
					circRnaImage.selectAllCircRnas();
					tipsTA.setVisible(false);
					display();
				} else {
					int circRnaIndex = -1;
					for (int i = 0; i < circRnaImage.getCircX().size(); i++) {
						if (hitCirc(circRnaImage.getCircX().get(i), circRnaImage.getCircY().get(i),
								circRnaImage.getCircR(), e.getX(), e.getY())) {
							circRnaIndex = i;
							break;
						}
					}
					if (circRnaIndex >= 0) {
						circRnaIndex++;
						cbCircRnaSelect.setSelectedIndex(circRnaIndex);
						circRnaImage.selectOneCircRna(cbCircRnaSelect.getItemAt(circRnaIndex));
						tipsTA.setVisible(false);
						display();
					}
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
		// Add MouseMotionListener
		circRnaImage.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
			}

			public void mouseMoved(MouseEvent e) {
				int tipsTop = 0;
				int tipsLeft = 0;
				if (0 == cbCircRnaSelect.getItemCount()) {
					return;
				}
				int circRnaIndex = -1;
				for (int i = 0; i < circRnaImage.getCircX().size(); i++) {
					if (hitCirc(circRnaImage.getCircX().get(i), circRnaImage.getCircY().get(i), circRnaImage.getCircR(),
							e.getX(), e.getY())) {
						tipsLeft = (int) Math.round(circRnaImage.getCircX().get(i) - circRnaImage.getCircR());
						tipsTop = (int) Math.round(circRnaImage.getCircY().get(i) + circRnaImage.getCircR() * 2);
						circRnaIndex = i;
						break;
					}
				}
				// Display the information of the circRnaIndex CircRna
				if (circRnaIndex >= 0) {
					String info = "";
					if (cbCircRnaSelect.getSelectedItem().equals("All")) {
						info = circRnaImage.getCircRnaInfo(cbCircRnaSelect.getItemAt(circRnaIndex + 1));
					} else {
						info = circRnaImage.getCircRnaInfo(cbCircRnaSelect.getSelectedItem().toString());
					}
					int fontSize = (int) Math.round(circRnaImage.getHeight() / 80.0) < (int) Math
							.round(imageScrollPane.getHeight() / 60.0)
									? (int) Math.round(circRnaImage.getHeight() / 80.0)
									: (int) Math.round(imageScrollPane.getHeight() / 60.0);
					tipsTA.setFont(new Font("TimeRomes", Font.PLAIN, fontSize));

					int tipsLength = imageScrollPane.getWidth() / 2;
					int tipsHeight = imageScrollPane.getHeight() / 4;
					if ((tipsLeft + tipsLength) > circRnaImage.getWidth()) {
						tipsLeft = circRnaImage.getWidth() - tipsLength;
					}
					tipsTA.setBounds(tipsLeft, tipsTop, tipsLength, tipsHeight);
					tipsTA.setRows(12);
					tipsTA.setColumns(150);
					tipsTA.setText(info);
					tipsTA.setVisible(true);
				}
				// Destory the tip dialog
				else {
					tipsTA.setVisible(false);
				}
			}
		});
	}

	private boolean hitCirc(double circX, double circY, double circR, int x, int y) {
		boolean ret = false;
		if ((circX - circR <= x) && (x <= circX + circR) && (circY - circR <= y) && (y <= circY + circR)) {
			ret = true;
		}
		return ret;
	}

	private void preDisplay() {
		String geneTransName = geneTransList.getSelectedValue();
		String[] tmp = geneTransName.split(" ");
		String gName = tmp[0];
		String trName = tmp[1].substring(1, tmp[1].length() - 1);
		GeneTranscript geneTranscript = null;
		OUTER: for (String geneName : genes.keySet()) {
			if (geneName.equalsIgnoreCase(gName)) {
				Gene gene = genes.get(geneName);
				for (String gtName : gene.getGeneTranscripts().keySet()) {
					if (gtName.equalsIgnoreCase(trName)) {
						geneTranscript = gene.getGeneTranscripts().get(gtName);
						break OUTER;
					}
				}
			}
		}
		String species = (String) cbSpecies.getSelectedItem();
		Vector<Vector<String>> table = MainData.getFileToolTable().get(species);
		if (null != table) {
			TreeMap<String, String> sampleName = new TreeMap<String, String>();
			TreeMap<String, String> toolName = new TreeMap<String, String>();
			for (Vector<String> line : table) {
				toolName.put(line.get(1), line.get(1));
				int tmpP = line.get(2).lastIndexOf(".");
				sampleName.put(line.get(2).substring(0, tmpP), line.get(2));
			}
			circRnaImage.setGt(geneTranscript, sampleName.size(), toolName.size());
		}
	}

	private void updateCircRnasCheckList() {
		cbCircRnaSelect.removeAllItems();
		cbCircRnaSelect.addItem("All");
		for (String circRnaId : circRnaImage.getGtBackup().getCircRnas().keySet()) {
			cbCircRnaSelect.addItem(circRnaId);
		}
	}

	public static void updateRbpMreStatus() {
		if (null != cbSpecies.getSelectedItem()) {
			String species = cbSpecies.getSelectedItem().toString();
			String rbpTableName = "rbp_" + DbUtil.species2TableName(species);
			String mreTableName = "mre_" + DbUtil.species2TableName(species);
			try {
				if (DbUtil.existTable(conn, rbpTableName)) {
					cbRbp.setEnabled(true);
				} else {
					cbRbp.setEnabled(false);
				}

				if (DbUtil.existTable(conn, mreTableName)) {
					cbMre.setEnabled(true);
				} else {
					cbMre.setEnabled(false);
				}
			} catch (ClassNotFoundException | SQLException e) {
				CircView.log.warn(e.getMessage());
			}
		} else {
			cbRbp.setEnabled(false);
			cbMre.setEnabled(false);
		}
	}

	private void display() {
		circRnaImage.createOneImage(imagePanel.getWidth(), imagePanel.getHeight(), conn);
		circRnaImage.repaint();
	}

	public static void updateGeneTransList() {
		geneTransName.removeAllElements();
		if (genes != null) {
			String toolName = (String) cbCircRnaTool.getSelectedItem();
			String sampleName = (String) cbSample.getSelectedItem();
			String chrom = (String) cbChrom.getSelectedItem();
			for (String geneName : genes.keySet()) {
				Gene gene = genes.get(geneName);
				for (String transName : gene.getGeneTranscripts().keySet()) {
					GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
					if ((geneTrans.getCircRnas().size() > 0)
							&& ((chrom.equalsIgnoreCase("All")) || (geneTrans.getChrom().equalsIgnoreCase(chrom)))) {
						if (toolName.equalsIgnoreCase("All") && sampleName.equalsIgnoreCase("All")) {
							geneTransName.addElement(gene.getGeneName() + " [" + geneTrans.getTranscriptName() + "] "
									+ "(" + geneTrans.getCircRnas().size() + ")");
						} else if (toolName.equalsIgnoreCase("All")) {
							boolean sign = false;
							for (String circRnaName : geneTrans.getCircRnas().keySet()) {
								CircRna circRna = geneTrans.getCircRnas().get(circRnaName);
								for (String sname : circRna.getFiles().keySet()) {
									if (sampleName.equalsIgnoreCase(sname)) {
										sign = true;
										break;
									}
								}
								if (sign) {
									break;
								}
							}
							if (sign) {
								geneTransName.addElement(gene.getGeneName() + " [" + geneTrans.getTranscriptName()
										+ "] " + "(" + geneTrans.getCircRnas().size() + ")");
							}
						} else if (sampleName.equalsIgnoreCase("All")) {
							boolean sign = false;
							for (String circRnaName : geneTrans.getCircRnas().keySet()) {
								CircRna circRna = geneTrans.getCircRnas().get(circRnaName);
								for (String tname : circRna.getCircTools().keySet()) {
									if (toolName.equalsIgnoreCase(tname)) {
										sign = true;
										break;
									}
								}
								if (sign) {
									break;
								}
							}
							if (sign) {
								geneTransName.addElement(gene.getGeneName() + " [" + geneTrans.getTranscriptName()
										+ "] " + "(" + geneTrans.getCircRnas().size() + ")");
							}
						} else {
							boolean sign = false;
							for (String circRnaName : geneTrans.getCircRnas().keySet()) {
								CircRna circRna = geneTrans.getCircRnas().get(circRnaName);
								for (String sname : circRna.getFiles().keySet()) {
									if (sampleName.equalsIgnoreCase(sname)) {
										sign = true;
										break;
									}
								}
								if (sign) {
									break;
								}
							}
							if (!sign) {
								continue;
							}
							sign = false;
							for (String circRnaName : geneTrans.getCircRnas().keySet()) {
								CircRna circRna = geneTrans.getCircRnas().get(circRnaName);
								for (String tname : circRna.getCircTools().keySet()) {
									if (toolName.equalsIgnoreCase(tname)) {
										sign = true;
										break;
									}
								}
								if (sign) {
									break;
								}
							}
							if (sign) {
								geneTransName.addElement(gene.getGeneName() + " [" + geneTrans.getTranscriptName()
										+ "] " + "(" + geneTrans.getCircRnas().size() + ")");
							}
						}
					}
				}
			}
		}
		geneTransList.setListData(geneTransName);
	}

	public static void updateGeneTransList(String sort) {

		// TreeMap Key sort
		Comparator<String> keyComparator = null;
		// TreeMap Value sort
		Comparator<Map.Entry<String, Long>> positionValueComparator = null;
		Comparator<Map.Entry<String, Integer>> abundanceValueComparator = null;

		if (sort.contains("name")) {
			if (sort.contains("asc")) {
				keyComparator = new Comparator<String>() {
					public int compare(String o1, String o2) {
						return o1.compareToIgnoreCase(o2);
					}
				};
			} else if (sort.contains("desc")) {
				keyComparator = new Comparator<String>() {
					public int compare(String o1, String o2) {
						return o2.compareToIgnoreCase(o1);
					}
				};
			}
		} else if (sort.contains("position")) {
			if (sort.contains("asc")) {
				positionValueComparator = new Comparator<Map.Entry<String, Long>>() {
					public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
						return (int) (o1.getValue() - o2.getValue());
					}
				};
			} else if (sort.contains("desc")) {
				positionValueComparator = new Comparator<Map.Entry<String, Long>>() {
					public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
						return (int) (o2.getValue() - o1.getValue());
					}
				};
			}
		} else if (sort.contains("abundance")) {
			if (sort.contains("asc")) {
				abundanceValueComparator = new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
						return (o1.getValue() - o2.getValue());
					}
				};
			} else if (sort.contains("desc")) {
				abundanceValueComparator = new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
						return (o2.getValue() - o1.getValue());
					}
				};
			}
		}
		TreeMap<String, Integer> geneTrans_juncReads;
		TreeMap<String, Long> geneTrans_position;
		if (sort.contains("name")) {
			geneTrans_juncReads = new TreeMap<String, Integer>(keyComparator);
			geneTrans_position = new TreeMap<String, Long>(keyComparator);

		} else {
			geneTrans_juncReads = new TreeMap<String, Integer>();
			geneTrans_position = new TreeMap<String, Long>();
		}

		for (String name : geneTransName) {
			String[] tmp = name.split(" \\[");
			String geneName = tmp[0];
			String[] tmp2 = tmp[1].split("\\]");
			String transName = tmp2[0];
			Gene gene = genes.get(geneName);
			if (null != gene) {
				GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
				geneTrans_juncReads.put(name, geneTrans.getTotalJunctionReads());
				geneTrans_position.put(name, geneTrans.getCdsStart());
			} else {
				System.out.println("Gene Transcript Name error " + name);
			}
		}

		geneTransName.removeAllElements();
		if (sort.contains("name")) {
			for (Map.Entry<String, Integer> entry : geneTrans_juncReads.entrySet()) {
				geneTransName.addElement(entry.getKey());
			}
		} else if (sort.contains("position")) {
			List<Map.Entry<String, Long>> positionList = new ArrayList<Map.Entry<String, Long>>(
					geneTrans_position.entrySet());
			Collections.sort(positionList, positionValueComparator);
			for (Map.Entry<String, Long> entry : positionList) {
				geneTransName.addElement(entry.getKey());
			}
		} else if (sort.contains("abundance")) {
			List<Map.Entry<String, Integer>> junctionReadsList = new ArrayList<Map.Entry<String, Integer>>(
					geneTrans_juncReads.entrySet());
			Collections.sort(junctionReadsList, abundanceValueComparator);
			for (Map.Entry<String, Integer> entry : junctionReadsList) {
				geneTransName.addElement(entry.getKey());
			}
		}

		geneTransList.setListData(geneTransName);
	}

	public static void updateCbChrom() {
		cbChrom.removeAllItems();
		cbChrom.addItem("All");
		if (genes == null) {
		} else {
			TreeMap<String, String> chrom = new TreeMap<String, String>();
			for (String geneName : genes.keySet()) {
				Gene gene = genes.get(geneName);
				for (String transcriptName : gene.getGeneTranscripts().keySet()) {
					GeneTranscript transcript = gene.getGeneTranscripts().get(transcriptName);
					if (transcript.getCircRnas().size() > 0) {
						chrom.put(transcript.getChrom(), transcript.getChrom());
					}
				}
			}
			for (String chr : chrom.keySet()) {
				cbChrom.addItem(chr);
			}
		}
	}

	public static void setSpeciesCombo(String species) {
		cbSpecies.setSelectedItem(species);
	}

	public static void setCircRnaToolsCombo(String circRnaTool) {
		cbCircRnaTool.setSelectedItem(circRnaTool);
	}

	public static void updateSpeciesCombo() {
		cbSpecies.removeAllItems();
		for (String speciesName : MainData.getSpeciesData().keySet()) {
			cbSpecies.addItem(speciesName);
		}
		String sname = (String) cbSpecies.getSelectedItem();
		if (null == sname) {
			genes = null;
		} else {
			genes = MainData.getSpeciesData().get(sname);
		}
	}

	public static void updateCircRnaToolsCombo() {
		String speciesName = "";
		if (null != cbSpecies.getSelectedItem()) {
			speciesName = cbSpecies.getSelectedItem().toString();
		}
		cbCircRnaTool.removeAllItems();
		cbCircRnaTool.addItem("All");

		if (null == MainData.getFileToolTable().get(speciesName)) {
			return;
		}

		TreeMap<String, String> tools = new TreeMap<String, String>();
		Vector<Vector<String>> table = MainData.getFileToolTable().get(speciesName);
		for (Vector<String> line : table) {
			tools.put(line.get(1), line.get(1));
		}
		for (String toolName : tools.keySet()) {
			cbCircRnaTool.addItem(toolName);
		}
	}

	public static void updateSamplesCombo() {
		String speciesName = "";
		if (null != cbSpecies.getSelectedItem()) {
			speciesName = cbSpecies.getSelectedItem().toString();
		}
		cbSample.removeAllItems();
		cbSample.addItem("All");

		if (null == MainData.getFileToolTable().get(speciesName)) {
			return;
		}

		TreeMap<String, String> samples = new TreeMap<String, String>();
		Vector<Vector<String>> table = MainData.getFileToolTable().get(speciesName);
		for (Vector<String> line : table) {
			samples.put(line.get(2), line.get(2));
		}
		for (String sampleName : samples.keySet()) {
			cbSample.addItem(sampleName);
		}
	}

	public TreeMap<String, Gene> getMainData() {
		return genes;
	}

	public static JComboBox<String> getCbCircRnaSelect() {
		return cbCircRnaSelect;
	}

	public static void setCbCircRnaSelect(JComboBox<String> cbCircRnaSelect) {
		CircView.cbCircRnaSelect = cbCircRnaSelect;
	}
}
