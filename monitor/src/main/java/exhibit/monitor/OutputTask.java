package exhibit.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class OutputTask extends TimerTask {
	private List<String> list;
	private String fileName;

	public OutputTask(String fileName, List<String> list) {
		this.fileName = fileName;
		this.list = list;
	}

	@Override
	public void run() {
		System.out.println("[OutputTask] Task executing.. " + fileName);
		// check if the dependency files have been received
		List<String> validList = new ArrayList<>();
		for (String dependencyFileName : list) {
			if (ApplicationContext.validRecords.contains(new Record(dependencyFileName))) {
				validList.add(dependencyFileName);
			}
		}

		// pass on the filename and list with received dependency files
		// to output thread
		if (!validList.isEmpty()) {
			System.out.println("[OutputTask] Starting output thread.. " + fileName);
			Thread output = new Thread(new OutputRunner(fileName, validList));
			output.start();
		}
	}

}
