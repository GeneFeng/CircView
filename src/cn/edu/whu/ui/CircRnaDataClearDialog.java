package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import cn.edu.whu.MainData;
import cn.edu.whu.CircView;

public class CircRnaDataClearDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public CircRnaDataClearDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Clear CircRNA Data for:");
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
				if (null == cbSpecies.getSelectedItem()) {
					return;
				}
				String delSpecies = cbSpecies.getSelectedItem().toString();
				// Delete Species Data
				if (null != MainData.getSpeciesData().get(delSpecies)) {
					MainData.getSpeciesData().remove(delSpecies);
				}
				CircView.log.error("CircRNA Data for " + delSpecies + " is CLEARED");
				// Delete CircRNA File Info for this Species
				for (int i = MainData.getCircRnaFilesInfo().size() - 1; i >= 0; i--) {
					Vector<String> rowData = MainData.getCircRnaFilesInfo().get(i);
					String sname = rowData.get(0);
					if (delSpecies.equalsIgnoreCase(sname)) {
						MainData.getCircRnaFilesInfo().remove(i);
					}
				}
				CircView.log.error("CircRNA files for " + delSpecies + " is DELETED");

				CircView.updateSpeciesCombo();
				CircView.updateCircRnaToolsCombo();
				CircView.updateSamplesCombo();
				CircView.updateCbChrom();
				CircView.updateGeneTransList();
				CircRnaDataClearDialog.this.dispose();
			}

		});
	}
}
