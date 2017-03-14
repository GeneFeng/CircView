package cn.edu.whu.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class ImageSaveFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	public ImageSaveFileChooser(String title) {
		super(title);
		addFileFilter();
		// Set default Dir
		this.setCurrentDirectory(new File(System.getProperty("user.dir")));
	}

	private void addFileFilter() {

		addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().endsWith("png");
			}

			public String getDescription() {
				return "png";
			}
		});
		// addChoosableFileFilter(new FileFilter() {
		// public boolean accept(File f) {
		// return f.getName().endsWith("jpg");
		// }
		//
		// public String getDescription() {
		// return "jpg";
		// }
		// });
	}

}
