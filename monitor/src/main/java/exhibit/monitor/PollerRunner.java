package exhibit.monitor;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PollerRunner implements Runnable {
	private static final String INPUT_FOLDER_DIR = "D:/ExhibitMonitorData/Input";
	private static final String PROCESS_FOLDER_DIR = "D:/ExhibitMonitorData/Process";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private Logger logger;

	public PollerRunner() {
		logger = Logger.getLogger("Exception");
	}

	@Override
	public void run() {
		final File inputFolder = new File(INPUT_FOLDER_DIR);
		while (true) {
			for (final File fileEntry : inputFolder.listFiles()) {
				if (fileEntry.isFile()) {
					if (isValidFile(fileEntry) && !isDuplicate(fileEntry) && isOnTime(fileEntry)) {
						System.out.println("[Poller] Moving file.. " + fileEntry.getName());
						moveFile(fileEntry.getName());

						System.out.println("[Poller] Start worker thread.. " + fileEntry.getName());
						Thread worker = new Thread(new WorkerRunner(fileEntry.getName()));
						worker.start();
					} else {
						System.out.println("[Poller] Deleting file.. " + fileEntry.getName());
						File inputFile = new File(INPUT_FOLDER_DIR + "/" + fileEntry.getName());
						inputFile.delete();
					}
				}
			}
		}
	}

	private boolean isValidFile(File file) {
		FileDetails details = new FileDetails(file.getName(), "input");
		return ApplicationContext.xmlMap.containsKey(details);
	}

	private boolean isDuplicate(File file) {
		if (ApplicationContext.inputFileMap.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			ApplicationContext.inputFileMap.put(file.getName(), DATE_FORMAT.format(cal.getTime()));
			return false;
		}

		Calendar cal = Calendar.getInstance();
		if (ApplicationContext.inputFileMap.containsKey(file.getName())) {
			return ApplicationContext.inputFileMap.get(file.getName()).equals(DATE_FORMAT.format(cal.getTime()));
		}
		ApplicationContext.inputFileMap.put(file.getName(), DATE_FORMAT.format(cal.getTime()));
		return false;
	}

	private boolean isOnTime(File file) {
		try {
			Iterator<Entry<FileDetails, Map<String, String>>> iterator = ApplicationContext.xmlMap.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<FileDetails, Map<String, String>> element = iterator.next();
				FileDetails details = element.getKey();
				if (details.getName().equals(file.getName()) && details.getType().equals("input")) {

					Calendar cal = Calendar.getInstance();
					String[] parts = details.getTime().split(":");
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
					cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]) + Integer.parseInt(details.getGracePeriod()));
					if (cal.getTimeInMillis() >= file.lastModified()) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			// e.printStackTrace();
		}
		return false;
	}

	private void moveFile(String fileName) {
		try {
			final File inputFile = new File(INPUT_FOLDER_DIR + "/" + fileName);
			inputFile.renameTo(new File(PROCESS_FOLDER_DIR + "/" + fileName));

		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
			// e.printStackTrace();
		}
	}
}
