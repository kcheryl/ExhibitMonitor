package exhibit.monitor;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
	public static Map<FileDetails, Map<String, String>> xmlMap;

	static {
		xmlMap = new HashMap<>();
	}

	private ApplicationContext() {
	}
}
