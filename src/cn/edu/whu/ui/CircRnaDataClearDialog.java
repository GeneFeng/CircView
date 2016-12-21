package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import cn.edu.whu.MainData;
import cn.edu.whu.CircView;
import cn.edu.whu.util.Constant;

public class CircRnaDataClearDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	public CircRnaDataClearDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Clear CircRNA Data");
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
		final JButton btClear = new JButton("Clear");

		cbSpecies.setPreferredSize(new Dimension(150, 28));
		cbCircRnaTool.setPreferredSize(new Dimension(150, 28));

		getContentPane().add(cbSpecies);
		getContentPane().add(cbCircRnaTool);
		getContentPane().add(btClear);
		TreeMap<String, String> speciesNames = new TreeMap<String, String>();
		TreeMap<String, String> toolNames = new TreeMap<String, String>();
		for (String speciesTool : MainData.getCircRnaToolsData().keySet()) {
			String[] str = speciesTool.split(Constant.SEPERATER);
			speciesNames.put(str[0], str[0]);
			toolNames.put(str[1], str[1]);
		}
		for (String speciesName : speciesNames.keySet()) {
			cbSpecies.addItem(speciesName);
		}
		for (String toolName : toolNames.keySet()) {
			cbCircRnaTool.addItem(toolName);
		}

		cbSpecies.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String speciesName = cbSpecies.getSelectedItem().toString();
					for (String speciesTool : MainData.getCircRnaToolsData().keySet()) {
						String[] str = speciesTool.split(Constant.SEPERATER);
						if (speciesName.equals(str[0])) {
							cbCircRnaTool.addItem(str[1]);
						}
					}
				}
			}
		});

		// Button
		btClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String speciesName = cbSpecies.getSelectedItem().toString();
				String circRnaName = cbCircRnaTool.getSelectedItem().toString();
				MainData.getCircRnaToolsData().remove(speciesName + Constant.SEPERATER + circRnaName);
				CircView.updateCircRnaToolsCombo();
				CircView.updateGeneTransList();
				CircView.log.info(circRnaName + " Data for " + speciesName + " Deleted");
				CircRnaDataClearDialog.this.dispose();
				System.gc();
			}
		});
	}
}
