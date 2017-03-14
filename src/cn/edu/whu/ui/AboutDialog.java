package cn.edu.whu.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cn.edu.whu.CircView;
import cn.edu.whu.util.Constant;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public AboutDialog() {
		super(CircView.frame);
		initUi();
		setTitle("About CircView");
		// setResizable(false);
		setSize(250, 150);
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
		Box b = Box.createVerticalBox();
		b.add(Box.createGlue());
		b.add(new JLabel(Constant.ABOUT));
		b.add(new JLabel(Constant.VERSION));
		b.add(new JLabel(Constant.AUTHOR));
		b.add(Box.createGlue());
		getContentPane().add(b, "Center");

		JPanel p = new JPanel();
		JButton btClose = new JButton("Close");
		p.add(btClose);
		getContentPane().add(p, "South");

		btClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.dispose();
			}
		});
	}
}
