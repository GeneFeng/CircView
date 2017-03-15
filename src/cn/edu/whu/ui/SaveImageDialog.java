package cn.edu.whu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import cn.edu.whu.CircView;

public class SaveImageDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static CircRnaImagePanel circRnaImage;

	public SaveImageDialog(CircRnaImagePanel image) {
		super(CircView.frame);
		circRnaImage = image;
		initUi();
		setTitle("Save Image as File");
		setResizable(false);
		setSize(500, 65);
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
		final JLabel lbWidth = new JLabel("Width");
		final JTextField tfWidth = new JTextField();
		final JLabel lbHeight = new JLabel("Height");
		final JTextField tfHeight = new JTextField();
		JButton btSave = new JButton("Save as");

		tfWidth.setPreferredSize(new Dimension(150, 28));
		tfHeight.setPreferredSize(new Dimension(150, 28));
		getContentPane().add(lbWidth);
		getContentPane().add(tfWidth);
		getContentPane().add(lbHeight);
		getContentPane().add(tfHeight);
		getContentPane().add(btSave);

		tfWidth.setText("1000");
		tfHeight.setText("500");

		// Button
		btSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int w = Integer.parseInt(tfWidth.getText());
				int h = Integer.parseInt(tfHeight.getText());
				if (w * h > 20000 * 10000) {
					JOptionPane.showMessageDialog(CircView.frame, "Can NOT Create Such Large Image!");
				} else if (w > 0 && h > 0) {
					SaveImageDialog.this.setVisible(false);
					ImageSaveFileChooser saveFile = new ImageSaveFileChooser("Save Image as ...");
					saveFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
					saveFile.setMultiSelectionEnabled(false);
					int returnValue = saveFile.showSaveDialog(null);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						File fileOut = saveFile.getSelectedFile();
						String type = "png";
						String fileName = fileOut.getAbsolutePath() + "." + type;
						circRnaImage.saveImage(circRnaImage.getGt(), new File(fileName), type, w, h);
					}
					SaveImageDialog.this.dispose();
				}
			}
		});
	}
}
