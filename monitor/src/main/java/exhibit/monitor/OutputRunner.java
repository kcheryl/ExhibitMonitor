package exhibit.monitor;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputRunner implements Runnable {
	private static final String FILE_DIR = "D:/ExhibitMonitorData/Output/";
	private String fileName;
	private List<String> dependencyList;
	private Logger logger;

	public OutputRunner(String fileName, List<String> dependencyList) {
		this.fileName = fileName;
		this.dependencyList = dependencyList;
		logger = Logger.getLogger("Exception");
	}

	@Override
	public void run() {
		Connection conn = null;

		// create output file
		System.out.println("[OutputRunner] Creating output file.. " + fileName);
		String fileDir = FILE_DIR + fileName;
		try (FileWriter fw = new FileWriter(fileDir)) {

			// write headings to the file (take from xml)
			System.out.println("[OutputRunner] Writing to file.. " + fileName);
			Iterator<Entry<String, String>> fieldSetIterator = ApplicationContext.xmlMap
					.get(new FileDetails(dependencyList.get(0), "input")).entrySet().iterator();
			int size = ApplicationContext.xmlMap.get(new FileDetails(dependencyList.get(0), "input")).entrySet().size();
			int count = 1;
			while (fieldSetIterator.hasNext()) {
				Entry<String, String> element = fieldSetIterator.next();
				if (count == size) {
					fw.append(element.getKey());
					fw.append("\r\n");
				} else {
					fw.append(element.getKey());
					fw.append(',');
				}
				count++;
			}

			// set up to interact with database
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila?useSSL=false", "root",
					new Password().getPassword());

			// construct sql command from dependencyList
			String query = "select * from valid_records where file_name = '" + dependencyList.get(0) + "'";
			for (int i = 1; i < dependencyList.size(); i++) {
				query += " or file_name = '" + dependencyList.get(i) + "'";

			}
			query += ";";

			// write database content to file
			queryDatabase(conn, fw, query);

		} catch (Exception e) {
			// e.printStackTrace();
			logger.log(Level.FINEST, e.getMessage(), e);
		} finally {
			close(conn);
			System.out.println("Run Completed!");
		}
	}

	private void queryDatabase(Connection conn, FileWriter fw, String query) {
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(2);
				if (dependencyList.contains(name)) {
					fw.append(rs.getString(5));
					fw.append("\r\n");
				}
			}
		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			// e.printStackTrace();
		}
	}

	private void close(Connection conn) {
		if (conn == null) {
			return;
		}
		try {
			conn.close();
		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			// e.printStackTrace();
		}
	}
}
