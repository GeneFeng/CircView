package cn.edu.whu.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class CsvSaveFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	public CsvSaveFileChooser(String title) {
		super(title);
		addFileFilter();
		// Set default Dir
		this.setCurrentDirectory(new File(System.getProperty("user.dir")));
	}

	private void addFileFilter() {

		addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().endsWith("csv");
			}

			public String getDescription() {
				return "csv";
			}
		});
	}

}
