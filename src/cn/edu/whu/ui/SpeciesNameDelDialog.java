package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import cn.edu.whu.MainData;
import cn.edu.whu.CircView;

public class SpeciesNameDelDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public SpeciesNameDelDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Delete Species Name");
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
		final JButton btDel = new JButton("Delete");

		cbSpecies.setPreferredSize(new Dimension(150, 28));
		for (String speciesName : MainData.getSpeciesNames()) {
			cbSpecies.addItem(speciesName);
		}

		getContentPane().add(cbSpecies);
		getContentPane().add(btDel);

		// Button
		btDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (null != cbSpecies.getSelectedItem()) {
					String delSpecies = cbSpecies.getSelectedItem().toString();
					int index = cbSpecies.getSelectedIndex();
					cbSpecies.removeItemAt(index);
					MainData.getSpeciesNames().remove(delSpecies);
					MainData.writeConfig();
					CircView.log.info("Species [" + delSpecies + "] Deleted.");
				}
			}
		});
	}
}
