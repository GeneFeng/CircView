package cn.edu.whu.util;

import java.io.File;
import java.util.TreeMap;
import java.util.Vector;

public class Constant {
	public final static String TOOL_CIRCEXPLORER = "CIRCexplorer";
	public final static String TOOL_CIRCRNAFINDER = "circRNA_finder";
	public final static String TOOL_CIRI = "CIRI";
	public final static String TOOL_FIND_CIRC = "find_circ";
	public final static String TOOL_MAPSPLICE = "Mapsplice";
	public final static String TOOL_UROBORUS = "UROBORUS";
	public final static Vector<String> CIRCRNA_TOOLS = new Vector<String>() {
		private static final long serialVersionUID = 1L;

		{
			add(TOOL_CIRCEXPLORER);
			add(TOOL_CIRCRNAFINDER);
			add(TOOL_CIRI);
			add(TOOL_FIND_CIRC);
			add(TOOL_MAPSPLICE);
			add(TOOL_UROBORUS);
		}
	};

	public final static String HUMAN_HG38 = "human (hg38)";
	public final static String HUMAN_HG19 = "human (hg19)";
	public final static String MOUSE_MM10 = "mouse (mm10)";
	public final static String MOUSE_MM9 = "mouse (mm9)";
	public final static String ZEBRAFISH = "zebrafish (zv9)";
	public final static String FLY = "fly (dm6)";
	public final static String C_ELEGANS = "c.elegans (ce10)";
	public final static Vector<String> SPECIES = new Vector<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(HUMAN_HG38);
			add(HUMAN_HG19);
			add(MOUSE_MM10);
			add(MOUSE_MM9);
			add(ZEBRAFISH);
			add(FLY);
			add(C_ELEGANS);
		}
	};

	public final static String HUMAN_HG38_FILE = "annotation" + File.separator + "Human_hg38.txt";
	public final static String HUMAN_HG19_FILE = "annotation" + File.separator + "Human_hg19.txt";
	public final static String MOUSE_MM10_FILE = "annotation" + File.separator + "Mouse_mm10.txt";
	public final static String MOUSE_MM9_FILE = "annotation" + File.separator + "Mouse_mm9.txt";
	public final static String ZEBRAFISH_FILE = "annotation" + File.separator + "Zebrafish_zv9.txt";
	public final static String FLY_FILE = "annotation" + File.separator + "Fly_dm6.txt";
	public final static String C_ELEGANS_FILE = "annotation" + File.separator + "C_elegans_ce10.txt";
	public final static TreeMap<String, String> SPECIES_FILE = new TreeMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(HUMAN_HG38, HUMAN_HG38_FILE);
			put(HUMAN_HG19, HUMAN_HG19_FILE);
			put(MOUSE_MM10, MOUSE_MM10_FILE);
			put(MOUSE_MM9, MOUSE_MM9_FILE);
			put(ZEBRAFISH, ZEBRAFISH_FILE);
			put(FLY, FLY_FILE);
			put(C_ELEGANS, C_ELEGANS_FILE);
		}
	};

	public final static String SEPERATER = "==>";
	public final static int ASSIGN_TOLERATION = 2;
	public final static int BP_MATCH_TOLERATE = 10;
	// RBP tables;
	public final static String RBP_TABLE_STRUCTS = "(chr varchar(10), start int, end int, rbp varchar(512), detail varchar(1024))";

	// MRE tables;
	public static final String MRE_TABLE_STRUCTS = "(chr varchar(10), start int, end int, mre varchar(512), detail varchar(1024))";

	// Default Database Configure
	public final static String DEFAULT_DB_SERVER = "127.0.0.1";
	public final static String DEFAULT_DB_PORT = "3306";
	public final static String DEFAULT_DB_USER = "root";
	public final static String DEFAULT_DB_PASSWD = "12345";
	public final static String DEFAULT_DB_NAME = "mre_rbp";

	// Data Configure
	public final static String CONFIG_FILE = "config.properties";
	public final static String CONFIG_DB_SERVER = "dbserver";
	public final static String CONFIG_DB_PORT = "dbport";
	public final static String CONFIG_DB_USER = "dbuser";
	public final static String CONFIG_DB_PASSWD = "dbpasswd";
	public final static String CONFIG_DB_NAME = "dbname";
	public final static String CONFIG_SPECIES = "species";
	public final static String CONFIG_CIRCRNA_TOOLS = "circrnatools";

	// Logfile Configure
	public final static String LOG_FILE = "log4j.properties";
	public final static String LOG4J_ROOTLOGGER = "log4j.rootLogger";
	public final static String LOG4J_APPENDER_CONSOLE = "log4j.appender.Console";
	public final static String LOG4J_APPENDER_CONSOLE_TARGET = "log4j.appender.Console.Target";
	public final static String LOG4J_APPENDER_CONSOLE_LAYOUT = "log4j.appender.Console.layout";
	public final static String LOG4J_APPENDER_CONSOLE_LAYOUT_CONV_PATT = "log4j.appender.Console.layout.ConversionPattern";
	public final static String LOG4J_APPENDER_FILE = "log4j.appender.File";
	public final static String LOG4J_APPENDER_FILE_FILE = "log4j.appender.File.File";
	public final static String LOG4J_APPENDER_FILE_MAXFILESIZE = "log4j.appender.File.MaxFileSize";
	public final static String LOG4J_APPENDER_FILE_THRESHOLD = "log4j.appender.File.Threshold";
	public final static String LOG4J_APPENDER_FILE_LAYOUT = "log4j.appender.File.layout";
	public final static String LOG4J_APPENDER_FILE_LAYOUT_CONV_PATT = "log4j.appender.File.layout.ConversionPattern";

	public final static String LOG4J_ROOTLOGGER_VALUE = "INFO,Console,File";
	public final static String LOG4J_APPENDER_CONSOLE_VALUE = "org.apache.log4j.ConsoleAppender";
	public final static String LOG4J_APPENDER_CONSOLE_TARGET_VALUE = "System.out";
	public final static String LOG4J_APPENDER_CONSOLE_LAYOUT_VALUE = "org.apache.log4j.PatternLayout";
	public final static String LOG4J_APPENDER_CONSOLE_LAYOUT_CONV_PATT_VALUE = "[%c] - %m%n";
	public final static String LOG4J_APPENDER_FILE_VALUE = "org.apache.log4j.RollingFileAppender";
	public final static String LOG4J_APPENDER_FILE_FILE_VALUE = "logs/circview.log";
	public final static String LOG4J_APPENDER_FILE_MAXFILESIZE_VALUE = "10MB";
	public final static String LOG4J_APPENDER_FILE_THRESHOLD_VALUE = "ALL";
	public final static String LOG4J_APPENDER_FILE_LAYOUT_VALUE = "org.apache.log4j.PatternLayout";
	public final static String LOG4J_APPENDER_FILE_LAYOUT_CONV_PATT_VALUE = "[%p] [%d{yyyy-MM-dd HH:mm:ss}][%c]%m%n";

	// About Content
	public static final String ABOUT = "Circular RNAs Viewer";
	public static final String VERSION = "version 1.0.0";
	public static final String AUTHOR = "Report bug to: gfeng@whu.edu.cn";
}
