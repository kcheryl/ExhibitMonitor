package exhibit.monitor;

import static org.junit.Assert.*;

import org.junit.Test;

public class MonitorTest {

	public void testXMLParser() {
		XMLParser parser = new XMLParser();
		parser.init();
		String result = parser.getMap().toString();
		String expected = "{FileDetails [a.csv, input, 09:30, 30]={firstName=text, lastName=text, serialNum=number, address=text, DOB=date, DOD=date, loanAmt=number}, "
				+ "FileDetails [b.csv, input, 11:00, 20]={firstName=text, lastName=text, serialNum=number, address=text, DOB=date, DOD=date, loanAmt=number}, "
				+ "FileDetails [x.csv, output, 14:00, 0]={b.csv=null, a.csv=null}}";
		assertEquals(expected, result);
	}

	@Test
	public void test() throws InterruptedException {
		Monitor.main(null);
		Thread.sleep(2000);
		System.out.println("[Junit] ValidRecords(Collection): ");
		for (Record element : ApplicationContext.validRecords) {
			System.out.println(element);
		}
		System.out.println("[Junit] InvalidRecords(Collection): ");
		for (Record element : ApplicationContext.invalidRecords) {
			System.out.println(element);
		}
	}
}