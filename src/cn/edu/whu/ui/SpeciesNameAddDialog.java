package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import cn.edu.whu.MainData;
import cn.edu.whu.CircView;

public class SpeciesNameAddDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public SpeciesNameAddDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Add Species Name");
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
		final JTextField tfSpecies = new JTextField();
		final JLabel lbAdd = new JLabel("->");
		final JComboBox<String> cbSpecies = new JComboBox<String>();
		final JButton btAdd = new JButton("Add");

		cbSpecies.setPreferredSize(new Dimension(150, 28));
		for (String speciesName : MainData.getSpeciesNames()) {
			cbSpecies.addItem(speciesName);
		}
		tfSpecies.setPreferredSize(new Dimension(120, 28));

		getContentPane().add(tfSpecies);
		getContentPane().add(lbAdd);
		getContentPane().add(cbSpecies);
		getContentPane().add(btAdd);

		// Button
		btAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newSpecies = tfSpecies.getText();
				for (String oldSpecies : MainData.getSpeciesNames()) {
					if (newSpecies.equals(oldSpecies)) {
						// Species Name already exists
						JOptionPane.showMessageDialog(SpeciesNameAddDialog.this, "warning",
								"Species Name already exsits", JOptionPane.ERROR_MESSAGE);
					} else {
						MainData.getSpeciesNames().add(newSpecies);
						cbSpecies.addItem(newSpecies);
						tfSpecies.setText("");
						MainData.writeConfig();
						CircView.log.info("New Species [" + newSpecies+ "] Added." );
						break;
					}
				}
			}
		});
	}
}
