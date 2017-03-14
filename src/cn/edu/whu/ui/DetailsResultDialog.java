package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.edu.whu.CircView;

public class DetailsResultDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public DetailsResultDialog(String contents) {
		super(CircView.frame);
		setTitle("Details");
		setResizable(true);
		setSize(600, 400);
		centerScreen();

		JTextArea detailsTextArea = new JTextArea();
		detailsTextArea.setSize(new Dimension(this.getContentPane().getWidth(), this.getContentPane().getWidth()));
		detailsTextArea.setEditable(false);
		detailsTextArea.setText(contents);
		JScrollPane sp = new JScrollPane(detailsTextArea);
		sp.setPreferredSize(new Dimension(this.getContentPane().getWidth(), this.getContentPane().getWidth()));
		this.getContentPane().add(sp);
		this.setVisible(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	private void centerScreen() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
		super.setVisible(true);
		requestFocus();
	}
}
