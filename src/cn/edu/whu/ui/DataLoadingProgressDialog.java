package cn.edu.whu.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class DataLoadingProgressDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	public static JProgressBar process;

	public DataLoadingProgressDialog(Frame owner, String title) {
		super(owner, title);
		setTitle(title);
		setResizable(false);
		setSize(300, 300);
		this.setLayout(new FlowLayout());
		centerScreen();
		initUI();
	}

	private void centerScreen() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
		super.setVisible(true);
		requestFocus();
	}

	private void initUI() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		getContentPane().add(panel);
		process = new JProgressBar(0, 100);
		process.setStringPainted(true);
		process.setBackground(Color.GREEN);
		process.setPreferredSize(new Dimension(200, 40));
		process.setValue(0);
		panel.add(process);

		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int loadingValue = process.getValue();
				if (loadingValue < 100) {
					process.setValue(loadingValue);
				} else {
					timer.stop();
				}
			}
		});
		timer.start();
	}

	public static JProgressBar getProcess() {
		return process;
	}

	public static void setProcess(JProgressBar process) {
		DataLoadingProgressDialog.process = process;
	}

}
