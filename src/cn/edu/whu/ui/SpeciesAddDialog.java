package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import cn.edu.whu.MainData;
import cn.edu.whu.CircView;

public class SpeciesAddDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private File speciesFile;

	public SpeciesAddDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Add Species");
		setResizable(false);
		setSize(600, 65);
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
		final JLabel lbName = new JLabel("Name:");
		final JTextField tfSpecies = new JTextField();
		final JLabel lbFile = new JLabel("");
		final JTextField tfFile = new JTextField();
		tfFile.setEditable(false);
		final JButton btChoose = new JButton("Choose File");
		final JButton btAdd = new JButton("Add");

		tfSpecies.setPreferredSize(new Dimension(100, 28));
		tfFile.setPreferredSize(new Dimension(150, 28));

		getContentPane().add(lbName);
		getContentPane().add(tfSpecies);
		getContentPane().add(lbFile);
		getContentPane().add(tfFile);
		getContentPane().add(btChoose);
		getContentPane().add(btAdd);

		// Button
		btChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OpenFileChooser openFile = new OpenFileChooser("Select Gene Annotation File");
				openFile.setMultiSelectionEnabled(false);
				openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFile.setFileHidingEnabled(true);
				int returnValue = openFile.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					tfFile.setText(openFile.getSelectedFile().getName());
					speciesFile = openFile.getSelectedFile();
				}
			}
		});
		btAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String speciesName = tfSpecies.getText();
				String pattern = "\\s+";
				if (speciesName.matches(pattern)) {
					JOptionPane.showMessageDialog(CircView.frame, "Species Name is needed");
					return;
				}
				if (null == speciesFile || speciesFile.getPath().matches(pattern)) {
					JOptionPane.showMessageDialog(CircView.frame,
							"Annotation file for [" + speciesName + "] is needed");
					return;
				}

				if (null != MainData.getSpeciesFile().get(speciesName)) {
					JOptionPane.showMessageDialog(CircView.frame, "Species [" + speciesName + "] already exists");
					return;
				} else {
					MainData.getSpeciesFile().put(speciesName, speciesFile.getPath());
					CircView.log.error(speciesName + " " + speciesFile.getPath() + " is added.");
					JOptionPane.showMessageDialog(CircView.frame,
							speciesName + " " + speciesFile.getName() + " is added.");
					SpeciesAddDialog.this.dispose();
				}
			}
		});
	}
}
