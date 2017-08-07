package exhibit.monitor;

import java.sql.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvalidDBRunner implements Runnable {
	private Logger logger;

	public InvalidDBRunner() {
		logger = Logger.getLogger("Exception");
	}

	@Override
	public void run() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila?useSSL=false", "root",
					new Password().getPassword());
			String sql = "CREATE TABLE INVALID_RECORDS " + "(id INTEGER not NULL AUTO_INCREMENT, "
					+ " file_name VARCHAR(255), " + " date VARCHAR(255), " + " record_number INTEGER, "
					+ " record VARCHAR(255), " + " PRIMARY KEY ( id ))";

			queryStatement(conn, stmt, sql);

			synchronized (ApplicationContext.invalidRecords) {
				while (ApplicationContext.invalidRecords.isEmpty()) {
					ApplicationContext.invalidRecords.wait();
				}

				for (Record record : ApplicationContext.invalidRecords) {
					String query = "insert into invalid_records (file_name, date, record_number, record) values (?, ?, ?, ?)";
					stmt = queryStatement(conn, stmt, record, query);
				}
			}
		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			// e.printStackTrace();
		} finally {
			close(conn);
		}

	}

	private PreparedStatement queryStatement(Connection conn, PreparedStatement stmt, Record record, String query) {
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, record.getFileName());
			Calendar cal = Calendar.getInstance();
			cal.setTime(record.getDate());
			java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());
			stmt.setDate(2, sqlDate);
			stmt.setInt(3, record.getRecordNum());
			stmt.setString(4, record.getRecord());
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return stmt;
	}

	private void queryStatement(Connection conn, PreparedStatement stmt, String sql) {
		try {
			stmt = conn.prepareStatement(sql);
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
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
