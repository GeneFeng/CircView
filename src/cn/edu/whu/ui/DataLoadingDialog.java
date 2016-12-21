package cn.edu.whu.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class DataLoadingDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private static JProgressBar process;

	public DataLoadingDialog(Frame owner, String title) {
		super(owner, title);
		initUI();
		setTitle(title);
		setResizable(false);
		setSize(300, 1);
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

	private void initUI() {
		process = new JProgressBar(0, 100);
		process.setPreferredSize(new Dimension(200, 28));
		JPanel panel = new JPanel();
		panel.add(process);
		process.setBackground(Color.GREEN);
		process.setValue(50);
		process.setStringPainted(true);
		getContentPane().add(panel);
	}

	public static JProgressBar getProcess() {
		return process;
	}

	public static void setProcess(JProgressBar process) {
		DataLoadingDialog.process = process;
	}

}
