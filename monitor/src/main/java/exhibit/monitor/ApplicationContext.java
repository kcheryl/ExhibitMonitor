package exhibit.monitor;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
	protected static Map<FileDetails, Map<String, String>> xmlMap;
	protected static Map<String, String> inputFileMap;

	static {
		xmlMap = new HashMap<>();
		inputFileMap = new HashMap<>();
	}

	private ApplicationContext() {
	}
}
