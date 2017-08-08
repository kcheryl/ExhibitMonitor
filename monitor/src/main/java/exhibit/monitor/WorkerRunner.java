package exhibit.monitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	private boolean isValidRecord;
	private boolean isInvalidRecord;

	public WorkerRunner(String fileName) {
		this.fileName = fileName;
		logger = Logger.getLogger("Exception");
	}

	@Override
	public void run() {
		if (!isValidFile()) {
			try (BufferedReader br = new BufferedReader(new FileReader(FILE_DIR + fileName))) {
				String dateStr = ApplicationContext.inputFileMap.get(fileName);
				Date date = DATE_FORMAT.parse(dateStr);

				String line = "";
				line = br.readLine(); // discard first line (column headings)

				int count = 1;
				while ((line = br.readLine()) != null) {
					ApplicationContext.invalidRecords.add(new Record(fileName, date, count, line));
					count++;
					isValidRecord = false;
					isInvalidRecord = true;
				}

			} catch (Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				// e.printStackTrace();
			}
		} else {
			checkValidRow();
		}

		if (isValidRecord) {
			synchronized (ApplicationContext.validRecords) {
				System.out.println("[Worker] Notifying valid database thread.. " + fileName);
				ApplicationContext.validRecords.notify();
			}
		}

		if (isInvalidRecord) {
			synchronized (ApplicationContext.invalidRecords) {
				System.out.println("[Worker] Notifying invalid database thread.. " + fileName);
				ApplicationContext.invalidRecords.notify();
			}
		}
	}

	private boolean isValidFile() {
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_DIR + fileName))) {
			// read first line of csv (headings)
			line = br.readLine();
			String[] csvData = line.split(COMMA);
			FileDetails details = new FileDetails(fileName, "input");
			Map<String, String> fieldMap = ApplicationContext.xmlMap.get(details);

			// if no. of columns is different in csv file and xml (headings)
			if (csvData.length != fieldMap.size()) {
				return false;
			}

			// go through inner map (fieldname, type) of xmlMap
			// check if the fieldname (xmlMap) is same as heading (csv file)
			Iterator<Entry<String, String>> iterator = fieldMap.entrySet().iterator();
			int count = 0;
			while (iterator.hasNext()) {
				Entry<String, String> element = iterator.next();
				if (!csvData[count].equalsIgnoreCase(element.getKey())) {
					return false;
				}
				count++;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.log(Level.FINEST, e.getMessage(), e);
			return false;
		}
		return true;
	}

	private void checkValidRow() {
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_DIR + fileName))) {
			// get date from inputFileMap with filename
			String dateStr = ApplicationContext.inputFileMap.get(fileName);
			Date recordDate = DATE_FORMAT.parse(dateStr);

			// read first line of csv (headings) and ignore
			line = br.readLine();

			// go through every line in csv file
			int rowCount = 1;
			while ((line = br.readLine()) != null) {
				String[] fieldArr = line.split(COMMA);
				FileDetails details = new FileDetails(fileName, "input");
				Map<String, String> fieldMap = ApplicationContext.xmlMap.get(details);

				// if no. of columns is the same in csv row and xml
				if (fieldArr.length == fieldMap.size()) {

					// go through inner map (fieldname, type) of xmlMap
					// check if curr row content in csv is the right type in
					// xmlMap
					Iterator<Entry<String, String>> iteratorContent = fieldMap.entrySet().iterator();
					int fieldCount = 0;
					boolean isInvalid = false;
					while (iteratorContent.hasNext()) {
						Entry<String, String> element = iteratorContent.next();
						try {
							if (element.getValue().equalsIgnoreCase("integer")) {
								@SuppressWarnings("unused")
								int num = Integer.parseInt(fieldArr[fieldCount]);

							}
							if (element.getValue().equalsIgnoreCase("double")) {
								@SuppressWarnings("unused")
								double num = Double.parseDouble(fieldArr[fieldCount]);
							}
							if (element.getValue().equalsIgnoreCase("date")) {
								String[] parts = fieldArr[fieldCount].split("/");
								Calendar cal = Calendar.getInstance();
								cal.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]),
										Integer.parseInt(parts[0]));
							}

						} catch (Exception e) {
							// e.printStackTrace();
							logger.log(Level.FINEST, e.getMessage(), e);
							isInvalid = true;
							ApplicationContext.invalidRecords.add(new Record(fileName, recordDate, rowCount, line));
							isInvalidRecord = true;
						} finally {
							fieldCount++;
						}
					}
					if (!isInvalid) {
						ApplicationContext.validRecords.add(new Record(fileName, recordDate, rowCount, line));
						isValidRecord = true;
					}
				}

				// if no. of columns is the different in csv row and xml
				else {
					ApplicationContext.invalidRecords.add(new Record(fileName, recordDate, rowCount, line));
					isInvalidRecord = true;
				}
				rowCount++;

			}
		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			// e.printStackTrace();
		}
	}
}
