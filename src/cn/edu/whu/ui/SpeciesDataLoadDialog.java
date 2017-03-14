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

public class SpeciesDataLoadDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private File file;

	public SpeciesDataLoadDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Load Species Data");
		setResizable(false);
		setSize(300, 65);
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
		JButton btOpen = new JButton("Open");

		cbSpecies.setPreferredSize(new Dimension(150, 28));
		getContentPane().add(cbSpecies);
		getContentPane().add(btOpen);

		for (String speciesName : MainData.getSpeciesNames()) {
			cbSpecies.addItem(speciesName);
		}

		// Button
		btOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String species = cbSpecies.getSelectedItem().toString();
				SpeciesDataLoadDialog.this.setVisible(false);
				for (String one : MainData.getSpeciesData().keySet()) {
					if (one.equals(species)) {
						JOptionPane.showMessageDialog(CircView.frame, "Data of " + species + " ALREADY EXISTS!");
						return;
					}
				}
				OpenFileChooser openFile = new OpenFileChooser("Open Species File");
				openFile.setMultiSelectionEnabled(false);
				openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFile.setFileHidingEnabled(true);
				int returnValue = openFile.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					file = openFile.getSelectedFile();
					DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(CircView.frame,
							"Loading Species Data ...");
					TreeMap<String, Gene> genes = new TreeMap<String, Gene>();
					if (CircRnaTool.initSpeciesDataFromFile(file, genes)) {
						MainData.getSpeciesData().put(species, genes);
						CircView.updateSpeciesCombo();
						CircView.log.info(species + " Data Loaded");
					} else {
						JOptionPane.showMessageDialog(CircView.frame,
								"Can not open [" + file.getName() + "] or file FORMAT ERROR!");
					}
					dataLoadingDialog.setVisible(false);
				}
				SpeciesDataLoadDialog.this.dispose();
			}
		});
	}
}
