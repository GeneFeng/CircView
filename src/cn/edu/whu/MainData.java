package cn.edu.whu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

import cn.edu.whu.util.Constant;

public class MainData {
	private static TreeMap<String, TreeMap<String, Gene>> speciesData;
	private static Vector<String> speciesNames;
	private static Vector<String> circRnaToolNames;
	private static TreeMap<String, Vector<Vector<String>>> fileToolTable;
	private static Properties properties;

	public MainData() {
		speciesData = new TreeMap<String, TreeMap<String, Gene>>();
		speciesNames = new Vector<String>();
		circRnaToolNames = new Vector<String>();
		fileToolTable = new TreeMap<String, Vector<Vector<String>>>();
		properties = new Properties();
		configure();
	}

	private void configure() {
		File file = new File(Constant.CONFIG_FILE);
		if (file.exists()) {
			readConfig();
		} else {
			loadDefaultConfig();
			writeConfig();
		}
	}

	private void loadDefaultConfig() {
		CircView.log.info("Load Default Config");
		// Load Default Species Config
		String strSpeices = new String();
		for (String speciesName : Constant.SPECIES) {
			speciesNames.add(speciesName);
			strSpeices += speciesName + ";";
		}
		properties.setProperty(Constant.CONFIG_SPECIES, strSpeices);
		// Load Default CircRNA Tools Config
		String strTools = new String();
		for (String circRnaToolName : Constant.CIRCRNA_TOOLS) {
			circRnaToolNames.add(circRnaToolName);
			strTools += circRnaToolName + ";";
		}
		properties.setProperty(Constant.CONFIG_CIRCRNA_TOOLS, strTools);
		// Load Default Database Config
		properties.setProperty(Constant.CONFIG_DB_SERVER, Constant.DEFAULT_DB_SERVER);
		properties.setProperty(Constant.CONFIG_DB_PORT, Constant.DEFAULT_DB_PORT);
		properties.setProperty(Constant.CONFIG_DB_USER, Constant.DEFAULT_DB_USER);
		properties.setProperty(Constant.CONFIG_DB_PASSWD, Constant.DEFAULT_DB_PASSWD);
		properties.setProperty(Constant.CONFIG_DB_NAME, Constant.DEFAULT_DB_NAME);
	}

	public static void readConfig() {
		// Read Configure File
		CircView.log.info(Constant.CONFIG_FILE + " is loaded");
		try {
			InputStream in = new FileInputStream(Constant.CONFIG_FILE);
			properties.load(in);
			String names = properties.getProperty("species");
			String[] tmp = names.split(";");
			for (String name : tmp) {
				speciesNames.add(name);
			}
			names = properties.getProperty("circrnatools");
			tmp = names.split(";");
			for (String name : tmp) {
				circRnaToolNames.add(name);
			}
		} catch (IOException e) {
			CircView.log.info(e.getMessage());
		}
	}

	public static void writeConfig() {
		try {
			// Synchronize the Speices and CircRNA Tools data
			String strSpeices = new String();
			for (String speciesName : MainData.speciesNames) {
				strSpeices += speciesName + ";";
			}
			properties.setProperty(Constant.CONFIG_SPECIES, strSpeices);
			String strTools = new String();
			for (String circRnaToolName : MainData.getCircRnaToolNames()) {
				strTools += circRnaToolName + ";";
			}
			properties.setProperty(Constant.CONFIG_CIRCRNA_TOOLS, strTools);

			// Write Config File
			OutputStream os = new FileOutputStream(Constant.CONFIG_FILE);
			properties.store(os, "Save Config File");
		} catch (IOException e) {
			CircView.log.error(e.getMessage());
		}
	}

	// public static TreeMap<String, TreeMap<String, Gene>>
	// getCircRnaToolsData() {
	// return circRnaToolsData;
	// }
	//
	// public static void setCircRnaToolsData(TreeMap<String, TreeMap<String,
	// Gene>> circRnaTools) {
	// MainData.circRnaToolsData = circRnaTools;
	// }

	public static TreeMap<String, TreeMap<String, Gene>> getSpeciesData() {
		return speciesData;
	}

	public static void setSpeciesData(TreeMap<String, TreeMap<String, Gene>> speciesData) {
		MainData.speciesData = speciesData;
	}

	// public static TreeMap<String, Integer> getCircRnaSampleFilesNum() {
	// return circRnaSampleFilesNum;
	// }
	//
	// public static void setCircRnaSampleFilesNum(TreeMap<String, Integer>
	// circRnaSampleFilesNum) {
	// MainData.circRnaSampleFilesNum = circRnaSampleFilesNum;
	// }

	public static Vector<String> getSpeciesNames() {
		return speciesNames;
	}

	public static void setSpeciesNames(Vector<String> speciesNames) {
		MainData.speciesNames = speciesNames;
	}

	public static Vector<String> getCircRnaToolNames() {
		return circRnaToolNames;
	}

	public static void setCircRnaToolNames(Vector<String> circRnaToolNames) {
		MainData.circRnaToolNames = circRnaToolNames;
	}

//	public static TreeMap<String, Vector<String>> getLoadedToolName() {
//		return loadedToolName;
//	}
//
//	public static void setLoadedToolName(TreeMap<String, Vector<String>> loadedToolName) {
//		MainData.loadedToolName = loadedToolName;
//	}
//
//	public static TreeMap<String, Vector<String>> getSampleName() {
//		return sampleName;
//	}
//
//	public static void setSampleName(TreeMap<String, Vector<String>> sampleName) {
//		MainData.sampleName = sampleName;
//	}
//
//	public static TreeMap<String, Vector<String>> getFileName() {
//		return fileName;
//	}
//
//	public static void setFileName(TreeMap<String, Vector<String>> fileName) {
//		MainData.fileName = fileName;
//	}
	
	public static TreeMap<String, Vector<Vector<String>>> getFileToolTable() {
		return fileToolTable;
	}

	public static void setFileToolTable(TreeMap<String, Vector<Vector<String>>> fileToolTable) {
		MainData.fileToolTable = fileToolTable;
	}

	public static Properties getProperties() {
		return properties;
	}

	public static void setProperties(Properties properties) {
		MainData.properties = properties;
	}

	// private void initBaseData() {
	// // Init the Clean Species Template Data
	// for (String speciesName : Constant.SPECIES.keySet()) {
	// CircRnaTool speciesData = new CircRnaTool();
	// speciesData.initSpeciesDataFromFile(speciesName);
	// cleanSpeciesDatas.put(speciesName, speciesData);
	// }
	// }
	//
	// public void initSpeciesData() {
	// for (String speciesName : Constant.SPECIES.keySet()) {
	// CircRnaTool cleanData = cleanSpeciesDatas.get(speciesName);
	// for (String circRnaTool : Constant.CIRCRNA_TOOLS.keySet()) {
	// CircRnaTool oneData = cleanData.deepClone();
	// oneData.setSpeciesName(speciesName);
	// oneData.setCircRnaTool(circRnaTool);
	// circRnaToolsData.put(speciesName + circRnaTool, oneData);
	// }
	// }
	// }
	//
	// public void clearData(String speciesName, String circRnaTool) {
	// CircRnaTool mainData = cleanSpeciesDatas.get(speciesName).deepClone();
	// mainData.setSpeciesName(speciesName);
	// mainData.setCircRnaTool(circRnaTool);
	// circRnaToolsData.put(speciesName + circRnaTool, mainData);
	// }
	//
}
