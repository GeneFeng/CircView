package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import cn.edu.whu.MainData;
import cn.edu.whu.CircView;

public class SpeciesDataClearDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public SpeciesDataClearDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Clear Species Data");
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
		final JButton btClear = new JButton("Clear");

		cbSpecies.setPreferredSize(new Dimension(160, 28));
		for (String speciesName : MainData.getSpeciesData().keySet()) {
			cbSpecies.addItem(speciesName);
		}

		getContentPane().add(cbSpecies);
		getContentPane().add(btClear);

		// Button
		btClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(0 == cbSpecies.getItemCount()) {
					return;
				}
				String delSpecies = cbSpecies.getSelectedItem().toString();
				if (null != MainData.getSpeciesData().get(delSpecies)) {
					// Delete Species Data
					MainData.getSpeciesData().remove(delSpecies);
					MainData.getFileToolTable().remove(delSpecies);
					CircView.updateSpeciesCombo();
					CircView.updateCircRnaToolsCombo();
					CircView.updateSamplesCombo();
					CircView.updateCbChrom();
					CircView.updateGeneTransList();
					CircView.log.info(delSpecies + " Data Deleted");
				} else {
					JOptionPane.showMessageDialog(CircView.frame, "There is no data for " + delSpecies);
				}
				SpeciesDataClearDialog.this.dispose();
			}

		});
	}
}
