package com.taylor.globalcache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.taylor.utilities.AbstractConfig;

public class Config extends AbstractConfig {
	
	private static final String DEFAULT_FILENAME = "itach.xml";
	
	private Map<String, Controller> controllerMap;
	private List<String> controllerList;
	private Map<String, Command> commandMap;
	private List<String> commandList;
	public String defaultLocation;
	
	public Map<String, Controller> getControllerMap() {
		return controllerMap;
	}
	
	protected String getDefaultFilename() {
		return DEFAULT_FILENAME;
	}
	
	public List<String> getControllerList() {
		if (controllerList == null) {
			controllerList = new ArrayList<String>();
			for (Iterator<String> iter = controllerMap.keySet().iterator(); iter.hasNext();) {
				controllerList.add(iter.next());
			}
			Collections.sort(controllerList);
		}
		
		return controllerList;
	}
	
	public Map<String, Command> getIrCommandMap() {
		return commandMap;
	}

	public List<String> getCommandList() {
		if (commandList == null) {
			commandList = new ArrayList<String>();
			for (Iterator<String> iter = commandMap.keySet().iterator(); iter.hasNext();) {
				commandList.add(iter.next());
			}
			Collections.sort(commandList);
		}
		
		return commandList;
	}
	

	Config() throws Exception {
		super();
	}
	
	Config(String configFilePath) throws Exception {
		super(configFilePath);
	}

	protected void init(String configFilePath) throws Exception {
		super.init(configFilePath);
		parseConfigFile();
	}
	
	private void parseConfigFile() {
		
		// get the commands
		this.commandMap = new HashMap<String, Command>();
		for (Iterator<String> iter = super.commands.keySet().iterator(); iter.hasNext();) {
			String name = iter.next();
			String command = super.commands.get(name);
			commandMap.put(name, new Command(name, command));
		}
		
		// get the controllers
		try {
			controllerMap = new HashMap<String, Controller>();
			
			// get the controller nodes from the document
			NodeList nodes = getConfigDocument().getElementsByTagName("controller");
			for(int i = 0; i < nodes.getLength(); i++) {
				
				// get the values from the controller entry
				Element element = (Element) nodes.item(i);
				NamedNodeMap elements = element.getAttributes();


				String name = null;
				String ipAddress = null;
				Integer port = null;
				Integer irPort = null;
				Integer delay = null;
				
				// loop through them to allow for case-insensitive names
				for (int j=0; j < elements.getLength(); j++) {
					Node controllerNode =  elements.item(j);
					String controllerNodeName = controllerNode.getNodeName().toLowerCase();
					
					if (controllerNodeName.equals("ipaddress")) {
						ipAddress = controllerNode.getNodeValue();
					}
					else if (controllerNodeName.equals("name")) {
						name = controllerNode.getNodeValue();
					}
					else if (controllerNodeName.equals("port")) {
						port = Integer.parseInt(controllerNode.getNodeValue());
					}
					else if (controllerNodeName.equals("irport")) {
						irPort = Integer.parseInt(controllerNode.getNodeValue());
					}
					else if (controllerNodeName.equals("delay")) {
						delay = Integer.parseInt(controllerNode.getNodeValue());
					}

				}
				
				// build the controller & add it to the list
				Controller controller = new Controller(name, ipAddress, port, irPort, delay);
				controllerMap.put(name, controller);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
