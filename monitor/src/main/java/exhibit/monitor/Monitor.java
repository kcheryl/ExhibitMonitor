package exhibit.monitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Monitor {
	private static Logger logger;

	private Monitor() {
		logger = Logger.getLogger("Exception");
	}

	public static void main(String[] args) {
		XMLParser parser = new XMLParser();
		parser.init();

		Thread poller = new Thread(new PollerRunner());
		poller.start();

		Thread validDatabase = new Thread(new ValidDBRunner());
		validDatabase.start();
		Thread invalidDatabase = new Thread(new InvalidDBRunner());
		invalidDatabase.start();

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}

		// go through xmlMap(FileDetails, Map) and retrieve "output" type
		Iterator<FileDetails> iterator = ApplicationContext.xmlMap.keySet().iterator();
		while (iterator.hasNext()) {

			FileDetails element = iterator.next();
			String output = "output";
			if (element.getType().equalsIgnoreCase(output)) {

				// process time of output file
				String[] parts = element.getTime().split(":");
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
				cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
				cal.set(Calendar.SECOND, 0);

				// go through inner map (dependencyfile, null) of xmlMap
				// to retrieve and store dependency file in arraylist
				Iterator<Entry<String, String>> innerIterator = ApplicationContext.xmlMap.get(element).entrySet()
						.iterator();
				List<String> dependencyFiles = new ArrayList<>();
				while (innerIterator.hasNext()) {
					Entry<String, String> innerElement = innerIterator.next();
					dependencyFiles.add(innerElement.getKey());

				}

				// set timer task
				if (!dependencyFiles.isEmpty()) {
					Timer timer = new Timer();
					TimerTask task = new OutputTask(element.getName(), dependencyFiles);
					System.out.println("[Monitor] Scheduling task.. " + element.getName() + " [" + cal.getTime() + "]");
					timer.schedule(task, cal.getTime());
				}
			}

		}

	}
}
