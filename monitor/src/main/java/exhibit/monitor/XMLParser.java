package exhibit.monitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser {
	private Logger logger;

	public XMLParser() {
		logger = Logger.getLogger("Exception");
	}

	// for testing
	public Map<FileDetails, Map<String, String>> getMap() {
		return new TreeMap<>(ApplicationContext.xmlMap);
	}

	public void init() {
		try {
			File inputFile = new File("configuration.xml");

			// get the factory
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			// parse using builder to get DOM representation of XML file
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList inputFileList = doc.getElementsByTagName("inputfile");
			for (int i = 0; i < inputFileList.getLength(); i++) {
				// get current element
				Node inputNode = inputFileList.item(i);
				if (inputNode.getNodeType() == Node.ELEMENT_NODE) {
					Element inputElement = (Element) inputNode;
					FileDetails details = new FileDetails(inputElement.getAttribute("name"), "input",
							inputElement.getAttribute("time"),
							Integer.parseInt(inputElement.getAttribute("grace_period")));

					NodeList structureList = inputElement.getElementsByTagName("structure");
					NodeList children = structureList.item(0).getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						if (children.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element childElement = (Element) children.item(j);

							// if details is already included in the map
							if (ApplicationContext.xmlMap.containsKey(details)) {
								Map<String, String> attrMap = ApplicationContext.xmlMap.get(details);
								attrMap.put(childElement.getAttribute("name"), childElement.getAttribute("type"));
							}
							// if details is new
							else {
								Map<String, String> attrMap = new HashMap<>();
								attrMap.put(childElement.getAttribute("name"), childElement.getAttribute("type"));
								ApplicationContext.xmlMap.put(details, attrMap);
							}
						}
					}
				}
			}

			NodeList outputFileList = doc.getElementsByTagName("outputfile");
			for (int i = 0; i < outputFileList.getLength(); i++) {
				// get current element
				Node outputNode = outputFileList.item(i);
				if (outputNode.getNodeType() == Node.ELEMENT_NODE) {
					Element outputElement = (Element) outputNode;
					FileDetails details = new FileDetails(outputElement.getAttribute("name"), "output",
							outputElement.getAttribute("time"));

					NodeList children = outputElement.getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						if (children.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element childElement = (Element) children.item(j);

							// if details is already included in the map
							if (ApplicationContext.xmlMap.containsKey(details)) {
								Map<String, String> attrMap = ApplicationContext.xmlMap.get(details);
								attrMap.put(childElement.getAttribute("file"), null);
							}
							// if details is new
							else {
								Map<String, String> attrMap = new HashMap<>();
								attrMap.put(childElement.getAttribute("file"), null);
								ApplicationContext.xmlMap.put(details, attrMap);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
}
