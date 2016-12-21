package cn.edu.whu;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.whu.ui.AboutDialog;
import cn.edu.whu.ui.CircRnaDataClearDialog;
import cn.edu.whu.ui.CircRnaImagePanel;
import cn.edu.whu.ui.CircRnaDataLoadDialog;
import cn.edu.whu.ui.CircRnaToolAddDialog;
import cn.edu.whu.ui.CircRnaToolDelDialog;
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
	private static JComboBox<String> cbChrom;
	private static Vector<String> geneTransName;
	private static JList<String> geneTransList;
	private JComboBox<String> cbCircRnaSelect;
	private CircRnaImagePanel circRnaImage;

	private static JCheckBox cbRbp;
	private static JCheckBox cbMre;

	private static Connection conn;
	public static Logger log;

	private static TreeMap<String, Gene> genes;
	private boolean cbChromInit;

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
		JMenu mnRbp = new JMenu("RBP");
		JMenu mnMre = new JMenu("MRE");
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnFile);
		menuBar.add(mnSpecies);
		menuBar.add(mnCircRna);
		menuBar.add(mnRbp);
		menuBar.add(mnMre);
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
		JMenuItem mntmCircRnaClear = new JMenuItem("Clear");
		JMenuItem mntmCircRnaAdd = new JMenuItem("Add Tool");
		JMenuItem mntmCircRnaDel = new JMenuItem("Delete Tool");
		mnCircRna.add(mntmCircRnaLoad);
		mnCircRna.add(mntmCircRnaClear);
		mnCircRna.addSeparator();
		mnCircRna.add(mntmCircRnaAdd);
		mnCircRna.add(mntmCircRnaDel);
		// Add MenuItem to RBP Menu
		JMenuItem mntmRbpLoad = new JMenuItem("Load Data");
		JMenuItem mntmRbpClear = new JMenuItem("Clear");
		mnRbp.add(mntmRbpLoad);
		mnRbp.add(mntmRbpClear);
		// Add MenuItem to MRE Menu
		JMenuItem mntmMreLoad = new JMenuItem("Load Data");
		JMenuItem mntmMreClear = new JMenuItem("Clear");
		mnMre.add(mntmMreLoad);
		mnMre.add(mntmMreClear);
		// Add MenuItem to Help
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		//
		if (null == conn) {
			mntmRbpLoad.setEnabled(false);
			mntmRbpClear.setEnabled(false);
			mntmMreLoad.setEnabled(false);
			mntmMreClear.setEnabled(false);
			JOptionPane.showMessageDialog(CircView.frame, "Can NOT Connect to Database!");
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

		// Add ActionListener to CircRNA Clear MenuItem
		mntmCircRnaClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CircRnaDataClearDialog();
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
		// Add Chrom JComboBox to ToolBar
		cbChrom.setPreferredSize(new Dimension(100, 28));
		toolBar.add(cbChrom);

		// Init Species ComboBox
		updateSpeciesCombo();

		// Init CircRna ComboBox
		updateCircRnaToolsCombo();

		// Add ItemListener to Species
		cbSpecies.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String speciesName = cbSpecies.getSelectedItem().toString();
					int toolNum = 0;
					cbCircRnaTool.removeAllItems();
					for (String speciesTool : MainData.getCircRnaToolsData().keySet()) {
						String[] str = speciesTool.split(Constant.SEPERATER);
						if (speciesName.equals(str[0])) {
							cbCircRnaTool.addItem(str[1]);
							toolNum++;
						}
					}
					if (toolNum != 0) {
						String toolName = cbCircRnaTool.getSelectedItem().toString();
						genes = MainData.getCircRnaToolsData().get(speciesName + Constant.SEPERATER + toolName);
					} else {
						genes = null;
					}
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
					String speciesName = cbSpecies.getSelectedItem().toString();
					String toolName = cbCircRnaTool.getSelectedItem().toString();
					genes = MainData.getCircRnaToolsData().get(speciesName + Constant.SEPERATER + toolName);
					updateCbChrom();
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
					String chr = cbChrom.getSelectedItem().toString();
					geneTransName.removeAllElements();
					if (null != genes) {
						for (String geneName : genes.keySet()) {
							Gene gene = genes.get(geneName);
							for (String transName : gene.getGeneTranscripts().keySet()) {
								GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
								if (geneTrans.getChrom().equalsIgnoreCase(chr) || chr.equalsIgnoreCase("All")) {
									if (geneTrans.getCircRnas().size() > 0) {
										geneTransName.addElement(
												gene.getGeneName() + " [" + geneTrans.getTranscriptName() + "]");
									}
								}
							}
						}
					}
					geneTransList.setListData(geneTransName);
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
							if (geneTrans.getGeneName().contains(searchGeneName)
									|| searchGeneName.equalsIgnoreCase("")) {
								if (geneTrans.getCircRnas().size() > 0) {
									geneTransName.addElement(
											gene.getGeneName() + " [" + geneTrans.getTranscriptName() + "]");
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
									geneTransName.addElement(
											gene.getGeneName() + " [" + geneTrans.getTranscriptName() + "]");
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
									geneTransName.addElement(
											gene.getGeneName() + " [" + geneTrans.getTranscriptName() + "]");
								}
							}
						}
					}
					geneTransList.setListData(geneTransName);
				}
			}
		});

		// Add ActionListener to Search Button
		// btSearch.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// // TODO Auto-generated method stub
		// if ((tfGeneName.getText().length() == 0) &&
		// (tfLocStart.getText().length() == 0)
		// && (tfLocEnd.getText().length() == 0)) {
		// return;
		// } else if (tfGeneName.getText().length() != 0) {
		// String searchGeneName = tfGeneName.getText();
		// geneTransName.removeAllElements();
		// if (genes != null) {
		// for (String geneName : genes.keySet()) {
		// Gene gene = genes.get(geneName);
		// for (String transName : gene.getGeneTranscripts().keySet()) {
		// GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
		// if (geneTrans.getGeneName().equalsIgnoreCase(searchGeneName)) {
		// if (geneTrans.getCircRnas().size() > 0) {
		// geneTransName.addElement(
		// gene.getGeneName() + " [" + geneTrans.getTranscriptName() + "]");
		// }
		// }
		// }
		// }
		// geneTransList.setListData(geneTransName);
		// }
		// } else {
		// long start = 0;
		// long end = Long.MAX_VALUE;
		// if (tfLocStart.getText().matches("[0-9]+")) {
		// start = Long.parseLong(tfLocStart.getText());
		// }
		// if (tfLocEnd.getText().matches("[0-9]+")) {
		// end = Long.parseLong(tfLocEnd.getText());
		// }
		// geneTransName.removeAllElements();
		// if (genes != null) {
		// for (String geneName : genes.keySet()) {
		// Gene gene = genes.get(geneName);
		// for (String transName : gene.getGeneTranscripts().keySet()) {
		// GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
		// if ((start <= geneTrans.getTxStart()) && (geneTrans.getTxEnd() <=
		// end)) {
		// if (geneTrans.getCircRnas().size() > 0) {
		// geneTransName.addElement(
		// gene.getGeneName() + " [" + geneTrans.getTranscriptName() + "]");
		// }
		// }
		// }
		// }
		// geneTransList.setListData(geneTransName);
		// }
		// }
		// }
		// });

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
		JScrollPane geneTransScrollPane = new JScrollPane(geneTransList);
		geneTransScrollPane.setPreferredSize(new Dimension(250, mainPanel.getHeight()));
		mainPanel.add(geneTransScrollPane, BorderLayout.WEST);

		// Add ImagePanel to MainPanel
		imagePanel = new JPanel(new BorderLayout());
		mainPanel.add(imagePanel, BorderLayout.CENTER);

		// Add ImageToolBar to ImagePanel
		JPanel imageToolBar = new JPanel();
		imageToolBar.setLayout(new FlowLayout());
		imageToolBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		imagePanel.add(imageToolBar, BorderLayout.NORTH);

		// Add Buttons to ImageToolBar
		cbCircRnaSelect = new JComboBox<String>();
		JButton btZoomIn = new JButton("Zoom In");
		JButton btZoomOut = new JButton("Zoom Out");
		cbRbp = new JCheckBox("RBP", null, false);
		cbMre = new JCheckBox("MRE", null, false);
		JButton btDetails = new JButton("Details");
		JButton btSaveImage = new JButton("Save Image");
		cbCircRnaSelect.setPreferredSize(new Dimension(200, 28));
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
			public void keyPressed(KeyEvent key) {
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
		String speciesName = cbSpecies.getSelectedItem().toString();
		String toolName = cbCircRnaTool.getSelectedItem().toString();
		circRnaImage.setGt(geneTranscript,
				MainData.getCircRnaSampleFilesNum().get(speciesName + Constant.SEPERATER + toolName));
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

	public void updateSpecies(String species) {
		cbSpecies.setSelectedItem(species);
	}

	public void updateCircRnaTools(String circRnaTool) {
		cbCircRnaTool.setSelectedItem(circRnaTool);
	}

	public static void updateGeneTransList() {
		geneTransName.removeAllElements();
		if (genes == null) {

		} else {
			String chrom = cbChrom.getSelectedItem().toString();
			for (String geneName : genes.keySet()) {
				Gene gene = genes.get(geneName);
				for (String transName : gene.getGeneTranscripts().keySet()) {
					GeneTranscript geneTrans = gene.getGeneTranscripts().get(transName);
					if ((geneTrans.getCircRnas().size() > 0)
							&& ((chrom.equalsIgnoreCase("All")) || (geneTrans.getChrom().equalsIgnoreCase(chrom)))) {
						geneTransName.addElement(gene.getGeneName() + " [" + geneTrans.getTranscriptName() + "]");
					}
				}
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
	}

	public static void updateCircRnaToolsCombo() {
		String species = "";
		String toolName = "";
		if (null != cbSpecies.getSelectedItem()) {
			species = cbSpecies.getSelectedItem().toString();
		}
		if (null != cbCircRnaTool.getSelectedItem()) {
			toolName = cbCircRnaTool.getSelectedItem().toString();
		}
		TreeMap<String, String> toolsName = new TreeMap<String, String>();
		for (String speciesTool : MainData.getCircRnaToolsData().keySet()) {
			String[] tmp = speciesTool.split(Constant.SEPERATER);
			if (tmp[0].equalsIgnoreCase(species)) {
				toolsName.put(tmp[1], tmp[1]);
			}
		}
		cbCircRnaTool.removeAllItems();
		if (null != toolsName.get(toolName)) {
			cbCircRnaTool.addItem(toolName);
			toolsName.remove(toolName);
		}

		for (String tool : toolsName.keySet()) {
			cbCircRnaTool.addItem(tool);
		}

		if (null != cbCircRnaTool.getSelectedItem()) {
			toolName = cbCircRnaTool.getSelectedItem().toString();
		}

		genes = MainData.getCircRnaToolsData().get(species + Constant.SEPERATER + toolName);
		updateCbChrom();
		updateGeneTransList();
	}

	// public MainData getCircRnaVisual() {
	// return mainData;
	// }

	// public void setCircRnaVisual(MainData circRnaVisual) {
	// CircView.mainData = circRnaVisual;
	// }

	public TreeMap<String, Gene> getMainData() {
		return genes;
	}
}
