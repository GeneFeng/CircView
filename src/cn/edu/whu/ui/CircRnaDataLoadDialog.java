package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cn.edu.whu.CircRnaTool;
import cn.edu.whu.MainData;
import cn.edu.whu.CircView;
import cn.edu.whu.Gene;
import cn.edu.whu.GeneTranscript;
import cn.edu.whu.exception.FileReadException;
import cn.edu.whu.util.CloneUtils;
import cn.edu.whu.util.Constant;

public class CircRnaDataLoadDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private File[] files;

	public CircRnaDataLoadDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Load CircRNA files");
		setResizable(false);
		setSize(400, 65);
		this.setLayout(new FlowLayout());
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
		// ComboBox
		final JComboBox<String> cbSpecies = new JComboBox<String>();
		final JComboBox<String> cbCircRnaTool = new JComboBox<String>();
		JButton btOpen = new JButton("Open");

		cbSpecies.setPreferredSize(new Dimension(150, 28));
		cbCircRnaTool.setPreferredSize(new Dimension(150, 28));
		getContentPane().add(cbSpecies);
		getContentPane().add(cbCircRnaTool);
		getContentPane().add(btOpen);

		for (String speciesName : MainData.getSpeciesData().keySet()) {
			cbSpecies.addItem(speciesName);
		}

		for (String circRnaToolName : MainData.getCircRnaToolNames()) {
			cbCircRnaTool.addItem(circRnaToolName);
		}

		// Button
		btOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String speciesName = cbSpecies.getSelectedItem().toString();
				String circRnaToolName = cbCircRnaTool.getSelectedItem().toString();
				TreeMap<String, Gene> genes = MainData.getSpeciesData().get(speciesName);
				// The data already exits
				String name = null;
				for (String speciesTools : MainData.getCircRnaToolsData().keySet()) {
					if (speciesTools.startsWith(speciesName)) {
						name = speciesTools;
						break;
					}
				}
				if (null != name) {
					String[] tmp = name.split(Constant.SEPERATER);
					int dialogButton = JOptionPane.showConfirmDialog(CircView.frame,
							"Data of " + tmp[1] + " for " + tmp[0] + " WILL BE ERASE!");
					if (dialogButton == JOptionPane.YES_OPTION) {
						MainData.getCircRnaToolsData().remove(name);
						MainData.getCircRnaSampleFilesNum().remove(name);
						clear(genes);
						loadData(speciesName, circRnaToolName, genes);
					}
				} else {
					loadData(speciesName, circRnaToolName, genes);
				}

			}
		});
	}

	protected void loadData(String speciesName, String circRnaToolName, TreeMap<String, Gene> genes) {
		CircRnaDataLoadDialog.this.setVisible(false);
		OpenFileChooser openFile = new OpenFileChooser("Open CircRNA Files");
		openFile.setMultiSelectionEnabled(true);
		openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
		openFile.setFileHidingEnabled(true);
		int returnValue = openFile.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			files = openFile.getSelectedFiles();
			DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(CircView.frame, "Loading CircRNA Data ...");
			System.gc();
			try {
				// TreeMap<String, Gene> genes =
				// CloneUtils.clone(MainData.getSpeciesData().get(speciesName));
				if (CircRnaTool.initCircRnaDataFromFiles(speciesName, circRnaToolName, files, genes)) {
					MainData.getCircRnaToolsData().put(speciesName + Constant.SEPERATER + circRnaToolName, genes);
					CircView.updateCircRnaToolsCombo();
					CircView.setSpeciesCombo(speciesName);
					CircView.setCircRnaToolsCombo(circRnaToolName);
					CircView.log.info(circRnaToolName + " Data Loaded for " + speciesName);
				}
				dataLoadingDialog.setVisible(false);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(CircView.frame, "Low Memory!");
				CircView.log.warn(e1.getMessage());
			}
		}
		CircRnaDataLoadDialog.this.dispose();
	}

	protected void clear(TreeMap<String, Gene> genes) {
		for (String geneName : genes.keySet()) {
			Gene gene = genes.get(geneName);
			for (String transName : gene.getGeneTranscripts().keySet()) {
				GeneTranscript trans = gene.getGeneTranscripts().get(transName);
				trans.getCircRnas().clear();
				trans.getCircRnasNum().clear();
			}
		}
	}
}
