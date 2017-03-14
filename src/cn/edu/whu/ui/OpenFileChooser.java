package cn.edu.whu.ui;

import java.io.File;

import javax.swing.JFileChooser;

public class OpenFileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;

	public OpenFileChooser(String title) {
		super(title);
		// addFileFilter();
		setAcceptAllFileFilterUsed(false);
		
		// Set default Dir
		this.setCurrentDirectory(new File(System.getProperty("user.dir")));
	}

	// private void addFileFilter() {
	//
	// for (final String circRnaTool : Constant.CIRCRNA_TOOLS.keySet()) {
	// for (final String species : Constant.SPECIES.keySet()) {
	// addChoosableFileFilter(new FileFilter() {
	// public boolean accept(File f) {
	// return f.getName().endsWith("");
	// }
	//
	// public String getDescription() {
	// return circRnaTool + " in " + species;
	// }
	// });
	// }
	// }
	// }
}
