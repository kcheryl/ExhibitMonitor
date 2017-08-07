package exhibit.monitor;

import java.sql.*;
import java.util.Calendar;

public class ValidDBRunner implements Runnable {

	@Override
	public void run() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila?useSSL=false", "root",
					new Password().getPassword());
			String sql = "CREATE TABLE VALID_RECORDS " + "(id INTEGER not NULL AUTO_INCREMENT, "
					+ " file_name VARCHAR(255), " + " date VARCHAR(255), " + " record_number INTEGER, "
					+ " record VARCHAR(255), " + " PRIMARY KEY ( id ))";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.execute();

			synchronized (ApplicationContext.validRecords) {
				while (ApplicationContext.validRecords.isEmpty()) {
					ApplicationContext.validRecords.wait();
				}
				for (Record record : ApplicationContext.validRecords) {
					String query = "insert into valid_records (file_name, date, record_number, record) values (?, ?, ?, ?)";
					stmt = conn.prepareStatement(query);

					stmt.setString(1, record.getFileName());
					Calendar cal = Calendar.getInstance();
					cal.setTime(record.getDate());
					java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());
					stmt.setDate(2, sqlDate);
					stmt.setInt(3, record.getRecordNum());
					stmt.setString(4, record.getRecord());
					stmt.execute();
				}
				stmt.close();
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
