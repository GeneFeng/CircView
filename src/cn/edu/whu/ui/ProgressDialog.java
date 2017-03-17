package cn.edu.whu.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import cn.edu.whu.CircView;

public class ProgressDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private static JProgressBar progressBar;

	public ProgressDialog() {
		super(CircView.frame);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(600, 400);
		setResizable(false);
		setTitle("Load Data ...");
		centerScreen();
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		JPanel panel = new JPanel();
		panel.add(progressBar);
		this.getContentPane().add(panel);
		start();
	}

	public void start() {
		while (progressBar.getValue() < 100) {
			try {
				Thread.sleep(500);
				repaint();
			} catch (InterruptedException e) {
				CircView.log.info(e.getMessage());
			}
		}
	}

	private void centerScreen() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
		super.setVisible(true);
		requestFocus();
	}

	public static JProgressBar getProgressBar() {
		return progressBar;
	}
	
	public static void setProgressBar(JProgressBar progressBar) {
		ProgressDialog.progressBar = progressBar;
	}
}
