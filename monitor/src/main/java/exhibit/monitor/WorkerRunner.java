package exhibit.monitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class WorkerRunner implements Runnable {
	private String fileName;
	private static final String FILE_DIR = "D:/ExhibitMonitorData/Process/";
	private static final String COMMA = ",";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public WorkerRunner(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run() {
		if (isValid()) {
			BufferedReader br;
			try {
				String dateStr = ApplicationContext.inputFileMap.get(fileName);
				Date date = DATE_FORMAT.parse(dateStr);

				String line = "";
				br = new BufferedReader(new FileReader(FILE_DIR + fileName));
				br.readLine();

				int count = 1;
				while ((line = br.readLine()) != null) {
					ApplicationContext.validRecords.add(new Record(fileName, date, count, line));
					count++;
				}
				br.close();
				// notify DB thread
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			BufferedReader br;
			try {
				String dateStr = ApplicationContext.inputFileMap.get(fileName);
				Date date = DATE_FORMAT.parse(dateStr);

				String line = "";
				br = new BufferedReader(new FileReader(FILE_DIR + fileName));
				br.readLine();

				int count = 1;
				while ((line = br.readLine()) != null) {
					ApplicationContext.invalidRecords.add(new Record(fileName, date, count, line));
					count++;
				}
				br.close();
				// notify DB thread
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isValid() {
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_DIR + fileName))) {
			// read first line of csv
			line = br.readLine();
			String[] csvData = line.split(COMMA);
			FileDetails details = new FileDetails(fileName, "input");
			Map<String, String> fieldMap = ApplicationContext.xmlMap.get(details);

			if (csvData.length == fieldMap.size()) {
				Iterator<Entry<String, String>> iterator = fieldMap.entrySet().iterator();
				int i = 0;
				int count = 0;
				while (iterator.hasNext()) {
					Entry<String, String> element = iterator.next();
					if (csvData[i].equalsIgnoreCase(element.getKey())) {
						count++;
					}
					i++;
				}
				if (count == csvData.length) {
					System.out.println("[Worker] Valid record.. " + fileName);
					return true;
				}
			}
			System.out.println("[Worker] Invalid record.. " + fileName);
			return false;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
