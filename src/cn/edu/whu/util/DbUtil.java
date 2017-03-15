package cn.edu.whu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.whu.MainData;
import cn.edu.whu.CircView;

public class DbUtil {
	public static Connection connectDb() throws ClassNotFoundException, SQLException {
		String server = MainData.getProperties().getProperty(Constant.CONFIG_DB_SERVER);
		String port = MainData.getProperties().getProperty(Constant.CONFIG_DB_PORT);
		String user = MainData.getProperties().getProperty(Constant.CONFIG_DB_USER);
		String passwd = MainData.getProperties().getProperty(Constant.CONFIG_DB_PASSWD);

		String url = "jdbc:mysql://" + server + ":" + port + "?" + "user=" + user + "&password=" + passwd;
		CircView.log.info(url);
		Class.forName("com.mysql.jdbc.Driver");
		return (DriverManager.getConnection(url));
	}

	public static void createDb(Connection conn) throws SQLException, ClassNotFoundException {
		if (null == conn || conn.isClosed()) {
			conn = connectDb();
		}
		String sql = "create database if not exists " + MainData.getProperties().getProperty(Constant.CONFIG_DB_NAME);
		CircView.log.info(sql);
		Statement st = conn.createStatement();
		st.executeUpdate(sql);
	}

	public static void useDb(Connection conn) throws ClassNotFoundException, SQLException {
		if (null == conn || conn.isClosed()) {
			conn = connectDb();
		}
		String sql = "use " + MainData.getProperties().getProperty(Constant.CONFIG_DB_NAME);
		CircView.log.info(sql);
		Statement st = conn.createStatement();
		st.executeUpdate(sql);
	}

	public static void createRbpTable(Connection conn, String table) throws SQLException, ClassNotFoundException {
		if (null == conn || conn.isClosed()) {
			conn = connectDb();
		}
		String sql = "create table " + table + " " + Constant.RBP_TABLE_STRUCTS;
		CircView.log.info(sql);
		Statement st = conn.createStatement();
		st.executeUpdate(sql);
	}

	public static void createMreTable(Connection conn, String table) throws SQLException, ClassNotFoundException {
		if (null == conn || conn.isClosed()) {
			conn = connectDb();
		}
		String sql = "create table " + table + " " + Constant.MRE_TABLE_STRUCTS;
		CircView.log.info(sql);
		Statement st = conn.createStatement();
		st.executeUpdate(sql);
	}

	public static Vector<String> selectRbp(Connection conn, String table, String chrom, Vector<Long> starts,
			Vector<Long> ends) throws SQLException {
		if (0 == starts.size()) {
			CircView.log.info("There is no results from " + table);
			return null;
		}
		Vector<String> result = new Vector<String>();
		// Sql Statement
		String sql = "select chr, start, end, rbp, detail from " + table + " where ";
		for (int i = 0; i < starts.size(); i++) {
			sql += "chr='" + chrom + "' and " + starts.get(i) + "<=start and end<=" + ends.get(i) + " or ";
		}
		sql = sql.substring(0, sql.length() - 3);
		CircView.log.info(sql);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String chr = rs.getString(1);
			long start = rs.getInt(2);
			long end = rs.getInt(3);
			String rbp = rs.getString(4);
			String detail = rs.getString(5);
			result.addElement(chr + "\t" + start + "\t" + end + "\t" + rbp + "\t" + detail);
			CircView.log.info(chr + "\t" + start + "\t" + end + "\t" + rbp + "\t" + detail);
		}
		return result;
	}

	public static Vector<String> selectMre(Connection conn, String table, String chrom, Vector<Long> starts,
			Vector<Long> ends) throws SQLException {
		if (0 == starts.size()) {
			CircView.log.info("There is no results from " + table);
			return null;
		}
		Vector<String> result = new Vector<String>();
		// Sql Statement
		String sql = "select chr, start, end, mre, detail from " + table + " where ";
		for (int i = 0; i < starts.size(); i++) {
			sql += "chr='" + chrom + "' and " + starts.get(i) + "<=start and end<=" + ends.get(i) + " or ";
		}
		sql = sql.substring(0, sql.length() - 3);
		CircView.log.info(sql);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String chr = rs.getString(1);
			long start = rs.getInt(2);
			long end = rs.getInt(3);
			String rbp = rs.getString(4);
			String detail = rs.getString(5);
			result.addElement(chr + "\t" + start + "\t" + end + "\t" + rbp + "\t" + detail);
			CircView.log.info(chr + "\t" + start + "\t" + end + "\t" + rbp + "\t" + detail);
		}
		return result;
	}

	public static void file2Db(Connection conn, String fileName, String table) throws SQLException {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			CircView.log.info("Running under Windows Operating System.");
			fileName = fileName.replaceAll("\\\\", "\\\\\\\\");
		}
		Statement st = conn.createStatement();
		String sql = "load data local infile '" + fileName + "' into table " + table
				+ " fields terminated by '\t' lines terminated by '\n'";
		CircView.log.info(sql);
		st.execute(sql);

		// Statement st = conn.createStatement();
		// File file = new File(fileName);
		// if (file.isFile() && file.exists()) {
		// try {
		// InputStreamReader rd = new InputStreamReader(new
		// FileInputStream(file));
		// BufferedReader reader = new BufferedReader(rd);
		// String lineTxt = null;
		// while ((lineTxt = reader.readLine()) != null) {
		// String[] item = lineTxt.split("\t");
		// String chr = item[0];
		// long start = Long.parseLong(item[1]);
		// long end = Long.parseLong(item[2]);
		// String miRna = item[3];
		// String others = item[4];
		// String sql = "insert into " + table + " values ('" + chr + "'," +
		// start + "," + end + ",'" + miRna
		// + "','" + others + "')";
		// CircView.log.info(sql);
		// st.execute(sql);
		// }
		// } catch (IOException e) {
		// CircView.log.warn(e.getMessage());
		// }
		// } else {
		// CircView.log.info("Can not find the file");
		// }
	}

	public static void dropTable(Connection conn, String table) throws ClassNotFoundException, SQLException {
		if (null == conn || conn.isClosed()) {
			conn = connectDb();
		}
		String sql = "drop table " + table;
		CircView.log.info(sql);
		Statement st = conn.createStatement();
		st.execute(sql);
	}

	public static void closeDb(Connection conn) throws SQLException {
		CircView.log.info("Database Closed");
		conn.close();
	}

	public static String species2TableName(String species) {
		String regex = "[\\W]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(species);
		return matcher.replaceAll("_").trim();
	}

	public static boolean existTable(Connection conn, String tableName) throws ClassNotFoundException, SQLException {
		boolean ret = false;
		if (null == conn || conn.isClosed()) {
			conn = connectDb();
		}
		String sql = "show tables like '" + tableName + "'";
		// CircView.log.info(sql);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ret = true;
		}
		return ret;
	}
}
