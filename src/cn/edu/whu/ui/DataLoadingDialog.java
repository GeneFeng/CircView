package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.JDialog;

public class DataLoadingDialog extends JDialog {
	private static final long serialVersionUID = 1L;

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
	}
}
