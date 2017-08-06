package exhibit.monitor;

public class Monitor {
	public static void main(String[] args) throws InterruptedException {
		XMLParser parser = new XMLParser();
		parser.init();

		Thread poller = new Thread(new PollerRunner());
		poller.start();

		Thread.sleep(5);
	}
}
