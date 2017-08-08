package exhibit.monitor;

import static org.junit.Assert.*;

import org.junit.Test;

public class MonitorTest {
	@Test
	public void testXMLParser() {
		XMLParser parser = new XMLParser();
		parser.init();
		String result = parser.getMap().toString();
		String expected = "{FileDetails [a.csv, input, 20:00, 10]={SerialNum=integer, FirstName=text, LastName=text, DOB=date, DOD=date, LoanAmt=double, Address=text}, "
				+ "FileDetails [b.csv, input, 18:00, 20]={SerialNum=integer, FirstName=text, LastName=text, DOB=date, DOD=date, LoanAmt=double, Address=text}, "
				+ "FileDetails [x.csv, output, 15:05, null]={a.csv=null, b.csv=null}}";
		assertEquals(expected, result);
	}

	@Test
	public void test() throws InterruptedException {
		Monitor.main(null);
		Thread.sleep(2000);

		// System.out.println("[Junit] ValidRecords(Collection): ");
		// for (Record element : ApplicationContext.validRecords) {
		// System.out.println(element);
		// }
		// System.out.println("[Junit] InvalidRecords(Collection): ");
		// for (Record element : ApplicationContext.invalidRecords) {
		// System.out.println(element);
		// }
	}
}