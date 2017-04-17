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

public class CircRnaToolDelDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	public CircRnaToolDelDialog() {
		super();
		initUi();
		setTitle("Del CircRNA Tools Name");
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
		final JComboBox<String> cbTools = new JComboBox<String>();
		final JButton btDel = new JButton("Delete");

		cbTools.setPreferredSize(new Dimension(150, 28));
		for (String toolName : MainData.getCircRnaToolNames()) {
			cbTools.addItem(toolName);
		}
		getContentPane().add(cbTools);
		getContentPane().add(btDel);

		// Button
		btDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (null != cbTools.getSelectedItem()) {
					String delTool = cbTools.getSelectedItem().toString();
					int index = cbTools.getSelectedIndex();
					cbTools.removeItemAt(index);
					MainData.getCircRnaToolNames().remove(delTool);
					MainData.writeDbConfig();
					CircView.log.info("CircRna Tool [" + delTool + "] Deleted.");
					CircRnaToolDelDialog.this.dispose();
				}
			}
		});
	}
}
