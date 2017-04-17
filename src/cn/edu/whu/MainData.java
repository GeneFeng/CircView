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
import cn.edu.whu.util.DbConfig;

public class MainData {
	private static TreeMap<String, String> speciesFile;					// SpeciesName - Species Data File Path
	private static TreeMap<String, TreeMap<String, Gene>> speciesData;	// SpeciesName - Loaded Species Data
	private static Vector<String> circRnaToolNames;						// Tool Names
	private static Vector<Vector<String>> circRnaFilesInfo;
	private static Properties properties;
	private static DbConfig dbConfig;

	public MainData() {
		speciesFile = new TreeMap<String, String>();
		speciesData = new TreeMap<String, TreeMap<String, Gene>>();
		circRnaToolNames = new Vector<String>();
		circRnaFilesInfo = new Vector<Vector<String>>();
		properties = new Properties();
		dbConfig = new DbConfig();
		configure();
	}

	private void configure() {
		File file = new File(Constant.CONFIG_FILE);
		if (file.exists()) {
			readDbConfig();
		} else {
			initDbConfig();
			writeDbConfig();
		}
		initSpeciesFile();
		initTools();
	}

	private void initSpeciesFile() {
		// Init Default Species Config
		for (String speciesName : Constant.SPECIES_FILE.keySet()) {
			speciesFile.put(speciesName, Constant.SPECIES_FILE.get(speciesName));
		}
	}

	private void initTools() {
		// Init Default CircRNA Tools Config
		for (String circRnaToolName : Constant.CIRCRNA_TOOLS) {
			circRnaToolNames.add(circRnaToolName);
		}
	}

	private void initDbConfig() {
		CircView.log.info("Init Default Config");
		// Init Default Database Config
		dbConfig.setDbServer(Constant.DEFAULT_DB_SERVER);
		dbConfig.setDbPort(Constant.DEFAULT_DB_PORT);
		dbConfig.setDbUser(Constant.DEFAULT_DB_USER);
		dbConfig.setDbPasswd(Constant.DEFAULT_DB_PASSWD);
		dbConfig.setDbName(Constant.DEFAULT_DB_NAME);
	}

	public static void readDbConfig() {
		// Read Configure File
		CircView.log.info(Constant.CONFIG_FILE + " is loaded");
		try {
			InputStream in = new FileInputStream(Constant.CONFIG_FILE);
			properties.load(in);
			dbConfig.setDbServer(properties.getProperty(Constant.CONFIG_DB_SERVER));
			dbConfig.setDbPort(properties.getProperty(Constant.CONFIG_DB_PORT));
			dbConfig.setDbUser(properties.getProperty(Constant.CONFIG_DB_USER));
			dbConfig.setDbPasswd(properties.getProperty(Constant.CONFIG_DB_PASSWD));
			dbConfig.setDbName(properties.getProperty(Constant.CONFIG_DB_NAME));
		} catch (IOException e) {
			CircView.log.info(e.getMessage());
		}
	}

	public static void writeDbConfig() {
		try {
			properties.setProperty(Constant.CONFIG_DB_SERVER, Constant.DEFAULT_DB_SERVER);
			properties.setProperty(Constant.CONFIG_DB_PORT, Constant.DEFAULT_DB_PORT);
			properties.setProperty(Constant.CONFIG_DB_USER, Constant.DEFAULT_DB_USER);
			properties.setProperty(Constant.CONFIG_DB_PASSWD, Constant.DEFAULT_DB_PASSWD);
			properties.setProperty(Constant.CONFIG_DB_NAME, Constant.DEFAULT_DB_NAME);
			// Write Config File
			OutputStream os = new FileOutputStream(Constant.CONFIG_FILE);
			properties.store(os, "Save Config File");
		} catch (IOException e) {
			CircView.log.error(e.getMessage());
		}
	}

	public static TreeMap<String, TreeMap<String, Gene>> getSpeciesData() {
		return speciesData;
	}

	public static void setSpeciesData(TreeMap<String, TreeMap<String, Gene>> speciesData) {
		MainData.speciesData = speciesData;
	}

	public static TreeMap<String, String> getSpeciesFile() {
		return speciesFile;
	}

	public static void setSpeciesFile(TreeMap<String, String> speciesFile) {
		MainData.speciesFile = speciesFile;
	}

	public static Vector<String> getCircRnaToolNames() {
		return circRnaToolNames;
	}

	public static void setCircRnaToolNames(Vector<String> circRnaToolNames) {
		MainData.circRnaToolNames = circRnaToolNames;
	}

	public static Vector<Vector<String>> getCircRnaFilesInfo() {
		return circRnaFilesInfo;
	}

	public static void setCircRnaFilesInfo(Vector<Vector<String>> circRnaFilesInfo) {
		MainData.circRnaFilesInfo = circRnaFilesInfo;
	}

	public static Properties getProperties() {
		return properties;
	}

	public static void setProperties(Properties properties) {
		MainData.properties = properties;
	}

	public static DbConfig getDbConfig() {
		return dbConfig;
	}

	public static void setDbConfig(DbConfig dbConfig) {
		MainData.dbConfig = dbConfig;
	}

}
