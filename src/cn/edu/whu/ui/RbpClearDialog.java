package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import cn.edu.whu.MainData;
import cn.edu.whu.CircView;
import cn.edu.whu.util.DbUtil;

public class RbpClearDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	Connection conn;

	public RbpClearDialog(Connection conn) {
		super(CircView.frame);
		this.conn = conn;
		initUi();
		setTitle("Clear RBP Data");
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
		for (String speciesName : MainData.getSpeciesNames()) {
			cbSpecies.addItem(speciesName);
		}
		// Button
		JButton btOpen = new JButton("Clear RBP Data");
		getContentPane().add(btOpen);
		btOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RbpClearDialog.this.setVisible(false);
				int dialogButton = JOptionPane.showConfirmDialog(CircView.frame,
						"All RBP Data for " + cbSpecies.getSelectedItem() + " WILL BE ERASE!");
				if (dialogButton == JOptionPane.YES_OPTION) {
					DataLoadingDialog clearDialog = new DataLoadingDialog(CircView.frame, "Erasing RBP ...");
					// Delete RBP data for selected Species
					String species = cbSpecies.getSelectedItem().toString();
					try {
						String tableName = "rbp_" + DbUtil.species2TableName(species);
						DbUtil.dropTable(conn, tableName);
					} catch (ClassNotFoundException | SQLException ex) {
						JOptionPane.showMessageDialog(CircView.frame, "RBP Data Clear Failed!");
						CircView.log.warn(ex.getMessage());
					}
					CircView.log.info("RBP Data for " + species + " Deleted");
					CircView.updateRbpMreStatus();
					clearDialog.setVisible(false);	
				}
				RbpClearDialog.this.dispose();
			}
		});
	}
}
