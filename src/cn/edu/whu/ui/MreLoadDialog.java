package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cn.edu.whu.MainData;
import cn.edu.whu.CircView;
import cn.edu.whu.util.DbUtil;

public class MreLoadDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private File[] files;
	Connection conn;

	public MreLoadDialog(Connection conn) {
		super(CircView.frame);
		this.conn = conn;
		initUi();
		setTitle("Load MRE files");
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
		cbSpecies.setPreferredSize(new Dimension(150, 28));
		getContentPane().add(cbSpecies);
		// Add ItemListener to Speicies
		cbSpecies.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
				}
			}
		});
		for (String speciesName : MainData.getSpeciesFile().keySet()) {
			cbSpecies.addItem(speciesName);
		}
		// Button
		JButton btOpen = new JButton("Open MRE files");
		getContentPane().add(btOpen);
		btOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MreLoadDialog.this.setVisible(false);
				OpenFileChooser openFile = new OpenFileChooser("Open MRE Files");
				openFile.setMultiSelectionEnabled(true);
				openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFile.setFileHidingEnabled(true);
				int returnValue = openFile.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					String species = cbSpecies.getSelectedItem().toString();
					files = openFile.getSelectedFiles();
					DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(CircView.frame, "Loading MRE Data ...");
					save2Db(species, files);
					CircView.updateRbpMreStatus();
					dataLoadingDialog.setVisible(false);
				}
				MreLoadDialog.this.dispose();
			}
		});
	}

	private void save2Db(String species, File[] files) {
		if (null == conn) {
			try {
				conn = DbUtil.connectDb();
			} catch (ClassNotFoundException | SQLException e) {
				CircView.log.warn(e.getMessage());
				JOptionPane.showConfirmDialog(CircView.frame, "Can't Connect to the Database!");
				return;
			}
		}
		try {
			String tableName = "mre_" + DbUtil.species2TableName(species);
			if (!DbUtil.existTable(conn, tableName)) {
				DbUtil.createMreTable(conn, tableName);
			}
			for (int i = 0; i < files.length; i++) {
				DbUtil.file2Db(conn, files[i].getAbsolutePath(), tableName);
			}
		} catch (ClassNotFoundException | SQLException e) {
			CircView.log.warn(e.getMessage());
			JOptionPane.showMessageDialog(CircView.frame, "File FORMAT ERROR!");
		}
		CircView.log.info("MRE Data for " + species + " Loaded");
	}
}
