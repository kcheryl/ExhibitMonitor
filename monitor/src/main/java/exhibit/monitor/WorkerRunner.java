package exhibit.monitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerRunner implements Runnable {
	private String fileName;
	private static final String FILE_DIR = "D:/ExhibitMonitorData/Process/";
	private static final String COMMA = ",";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private Logger logger;

	public WorkerRunner(String fileName) {
		this.fileName = fileName;
		logger = Logger.getLogger("Exception");
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
				line = br.readLine(); // discard first line (column headings)

				int count = 1;
				while ((line = br.readLine()) != null) {
					ApplicationContext.validRecords.add(new Record(fileName, date, count, line));
					count++;
				}
				br.close();

				// notify DB thread
				synchronized (ApplicationContext.validRecords) {
					System.out.println("[Worker] Notifying valid database thread.. " + fileName);
					ApplicationContext.validRecords.notify();
				}

			} catch (Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				// e.printStackTrace();
			}
		} else {
			BufferedReader br;
			try {
				String dateStr = ApplicationContext.inputFileMap.get(fileName);
				Date date = DATE_FORMAT.parse(dateStr);

				String line = "";
				br = new BufferedReader(new FileReader(FILE_DIR + fileName));
				line = br.readLine(); // discard first line (column headings)

				int count = 1;
				while ((line = br.readLine()) != null) {
					ApplicationContext.invalidRecords.add(new Record(fileName, date, count, line));
					count++;
				}
				br.close();

				// notify DB thread
				synchronized (ApplicationContext.invalidRecords) {
					System.out.println("[Worker] Notifying invalid database thread.. " + fileName);
					ApplicationContext.invalidRecords.notify();
				}

			} catch (Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				// e.printStackTrace();
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

			if (csvData.length != fieldMap.size()) {
				return false;
			}

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
				while ((line = br.readLine()) != null) {
					String[] fieldArr = line.split(COMMA);
					if (fieldArr.length != csvData.length) {
						return false;
					}
					int fieldCount = 0;
					Iterator<Entry<String, String>> iteratorContent = fieldMap.entrySet().iterator();

					while (iteratorContent.hasNext()) {
						Entry<String, String> element = iteratorContent.next();
						try {
							if (element.getValue().equalsIgnoreCase("integer")) {
								int number = Integer.parseInt(fieldArr[fieldCount]);

							}
							if (element.getValue().equalsIgnoreCase("double")) {
								double number = Double.parseDouble(fieldArr[fieldCount]);
							}
							if (element.getValue().equalsIgnoreCase("date")) {
								Date date = DATE_FORMAT.parse(fieldArr[fieldCount]);
								DATE_FORMAT.format(date);
							}
						} catch (Exception e) {
							logger.log(Level.FINEST, e.getMessage(), e);
							// e.printStackTrace();
							return false;
						} finally {
							fieldCount++;
						}
					}
				}
			}
			return true;

		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			// e.printStackTrace();
		}
		return false;
	}

}
