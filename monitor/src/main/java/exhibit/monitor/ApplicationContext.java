package exhibit.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ApplicationContext {
	protected static Map<FileDetails, Map<String, String>> xmlMap;
	protected static Map<String, String> inputFileMap;
	protected static List<Record> validRecords;
	protected static List<Record> invalidRecords;

	static {
		xmlMap = new HashMap<>();
		inputFileMap = new HashMap<>();
		validRecords = new Vector<>();
		invalidRecords = new Vector<>();
	}

	private ApplicationContext() {
	}
}
