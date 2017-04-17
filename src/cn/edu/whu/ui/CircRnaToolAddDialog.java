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

import com.mysql.jdbc.StringUtils;

import cn.edu.whu.MainData;
import cn.edu.whu.CircView;

public class CircRnaToolAddDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public CircRnaToolAddDialog() {
		super(CircView.frame);
		initUi();
		setTitle("Add CircRNA Tools Name");
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
		final JTextField tfTools = new JTextField();
		final JLabel lbAdd = new JLabel("->");
		final JComboBox<String> cbTools = new JComboBox<String>();
		final JButton btAdd = new JButton("Add");

		cbTools.setPreferredSize(new Dimension(150, 28));
		for (String toolName : MainData.getCircRnaToolNames()) {
			cbTools.addItem(toolName);
		}
		tfTools.setPreferredSize(new Dimension(120, 28));

		getContentPane().add(tfTools);
		getContentPane().add(lbAdd);
		getContentPane().add(cbTools);
		getContentPane().add(btAdd);

		// Button
		btAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newTool = tfTools.getText();
				if (StringUtils.isEmptyOrWhitespaceOnly(newTool)) {
					JOptionPane.showMessageDialog(CircRnaToolAddDialog.this, "CircRNA Tool Name is Needed", "warning",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				boolean existed = false;
				for (String oldTool : MainData.getCircRnaToolNames()) {
					if (newTool.equals(oldTool)) {
						existed = true;
					}
				}
				if (existed) {
					// Species Name already exists
					JOptionPane.showMessageDialog(CircRnaToolAddDialog.this, "CircRNA Tools already exsits", "warning",
							JOptionPane.ERROR_MESSAGE);
				} else {
					MainData.getCircRnaToolNames().add(newTool);
					cbTools.addItem(newTool);
					tfTools.setText("");
					MainData.writeDbConfig();
					CircView.log.info("New CircRnaTool [" + newTool + "] Added.");
				}
			}
		});
	}
}
