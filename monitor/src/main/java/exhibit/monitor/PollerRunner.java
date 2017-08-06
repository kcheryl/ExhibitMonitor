package exhibit.monitor;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PollerRunner implements Runnable {
	private static final String inputFolderDir = "D:/ExhibitMonitorData/Input";
	private static final String processFolderDir = "D:/ExhibitMonitorData/Process";
	private static final DateFormat inputDF = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat lastModifiedDF = new SimpleDateFormat("HH:mm");

	@Override
	public void run() {
		final File inputFolder = new File(inputFolderDir);
		while (true) {
			for (final File fileEntry : inputFolder.listFiles()) {
				if (fileEntry.isFile()) {
					if (isValidFile(fileEntry) && !isDuplicate(fileEntry) && isOnTime(fileEntry)) {
						moveFile(fileEntry.getName());
						// Thread worker = new Thread(new WorkerRunner());
						// worker.start();
					} else {
						File inputFile = new File("D:/ExhibitMonitorData/Input/" + fileEntry.getName());
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
			ApplicationContext.inputFileMap.put(file.getName(), inputDF.format(cal.getTime()));
			return false;
		}

		Calendar cal = Calendar.getInstance();
		if (ApplicationContext.inputFileMap.containsKey(file.getName())) {
			return ApplicationContext.inputFileMap.get(file.getName()).equals(inputDF.format(cal.getTime()));
		}
		ApplicationContext.inputFileMap.put(file.getName(), inputDF.format(cal.getTime()));
		return false;
	}

	private boolean isOnTime(File file) {
		try {
			String lastModifiedTimeStr = lastModifiedDF.format(file.lastModified());
			long lastModifiedTime = new SimpleDateFormat("HH:mm").parse(lastModifiedTimeStr).getTime();

			Iterator<Entry<FileDetails, Map<String, String>>> iterator = ApplicationContext.xmlMap.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<FileDetails, Map<String, String>> element = iterator.next();
				FileDetails details = element.getKey();
				if (details.getName().equals(file.getName()) && details.getType().equals("input")) {
					Date xmlTime = new SimpleDateFormat("HH:mm").parse(details.getTime());
					if (xmlTime.getTime() - lastModifiedTime > 0) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void moveFile(String fileName) {
		try {
			final File inputFile = new File(inputFolderDir + "/" + fileName);
			inputFile.renameTo(new File(processFolderDir + "/" + inputFile.getName()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
