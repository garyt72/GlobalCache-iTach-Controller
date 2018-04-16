/*
 *  $URL: svn://svn.webarts.bc.ca/open/trunk/projects/WebARTS/ca/bc/webarts/tools/eiscp/Eiscp.java $
 *  $Author: tgutwin $
 *  $Revision: 624 $
 *  $Date: 2014-04-08 20:28:58 -0700 (Tue, 08 Apr 2014) $
 */
/*
 *
 *  Written by Tom Gutwin - WebARTS Design.
 *  Copyright (C) 2012-2014 WebARTS Design, North Vancouver Canada
 *  http://www.webarts.bc.ca
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without_ even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.taylor.globalcache;

import java.util.Iterator;

/**
 * A class that wraps the comunication to Onkyo/Integra devices using the
 * ethernet Integra Serial Control Protocal (eISCP). This class uses class
 * constants and commandMaps to help handling of the many iscp Commands. <br />
 * The Message packet looks like:<br />
 * <img src=
 * "http://tom.webarts.ca/_/rsrc/1320209141605/Blog/new-blog-items/javaeiscp-integraserialcontrolprotocol/eISCP-Packet.png"
 * border="1"/> <br />
 * See also <a href=
 * "http://tom.webarts.ca/Blog/new-blog-items/javaeiscp-integraserialcontrolprotocol"
 * > tom.webarts.ca</a> writeup.
 *
 * @author Tom Gutwin P.Eng
 */
public class ITach {
	
	public static boolean DEBUG = false;
	public static boolean INFO = false;
	public static Config config;

	/**
	 * Class main commandLine entry method.
	 * @throws Exception 
	 **/
	public static void main(String[] args) {
		
		int returnValue = processCommand(args);
		
		System.exit(returnValue);

	} // main

	public static int processCommand(String[] args) {

		// check for environment vaiable DEBUG set to true
		String env = System.getenv("DEBUG");
		if (env != null && env.toLowerCase().equals("true")) {
			System.out.println("Debugging enabled by ENV variable \"DEBUG\"");
			DEBUG = true;
		}

		// check for environment vaiable DEBUG set to true
		env = System.getenv("INFO");
		if (env != null && env.toLowerCase().equals("true")) {
			if (DEBUG) System.out.println("Info output enabled by ENV variable \"INFO\"");
			INFO = true;
		}

		// try to load configuration settings from file
		
		try {
			config = new Config();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return 3;
		}
				
		// config was loaded - check debug settings
		if (config.getDebug()) {
			System.out.println("Debug enabled by config file");
			DEBUG = true;
		}

		// config was loaded - check info settings
		if (config.getInfo()) {
			System.out.println("Info enabled by config file");
			INFO = true;
		}

		// we've loaded the config file - lets get deal with the arguments that were passed
		String controllerArg = new String();
		String commandArg = new String();
		
		if (args.length == 2) {
			// args are LOCATION COMMAND
			controllerArg = args[0].toUpperCase();
			commandArg = args[1].toUpperCase();
		}
		else {
			System.out.println(getUsage());
			System.exit(1);
			return 2;
		}		

		// get the controller
		Controller controller = config.getControllerMap().get(controllerArg);

		// if the controller wasn't found display a list of available controllers and exit
		if (controller == null) {
			StringBuffer output = new StringBuffer();
			output.append("Controller \"" + controllerArg + "\" not found\n");
			for (Iterator<String> iter = config.getControllerList().iterator(); iter.hasNext(); ){
				output.append("\t" + iter.next() + "\n");
			}
			
			System.out.println(output.toString());
			return 4;
		}
		
		// handle executing the command multiple times by appending * + count
		// example VOLUME_UP*5 executes VOLUME_UP 5 times
		int commandRepeat = 1;
		if (commandArg.contains("*")) {
			String[] split = commandArg.split("\\*");
			commandArg = split[0];
			try {
				commandRepeat = Integer.parseInt(split[1]);
			} catch (Exception e) {
				// do nothing, if we couldn't parse the repeat value it was already set to 1
			}
		}
		
		// get the command
		Command command = config.getIrCommandMap().get(commandArg);
		
		// if there was no command, display a list of available commands & exit
		if (command == null) {
			StringBuffer output = new StringBuffer();
			
			output.append("Command \"" + commandArg + "\" not found\n");
			boolean similarCommandFound = false;

			for (Iterator<String> iter = config.getCommandList().iterator(); iter.hasNext();) {
				String cmd = iter.next();
				
				if (cmd.contains(commandArg)) {
					output.append("\t" + cmd + "\n");
					similarCommandFound = true;
				}
			}
			
			if (similarCommandFound == false) {
				output.append(getCommandList());
			}
			
			System.out.println(output.toString());
			return 5;
		}

	
		// send the command and get the response
		Response response = controller.sendCommand(command, commandRepeat);
		
		if (DEBUG == true) {
			System.out.println("iTach response:     " + response.getResponse());
			System.out.println("Friendlly response: " + response.getFriendlyResponse());
		}
		else {
			System.out.println(response.getFriendlyResponse());
		}
				
		if (response.success()){
			return 0;
		}
		else {
			return 1;
		}

	}
	
	
	public static String getUsage() {
		StringBuffer output = new StringBuffer();

		output.append("Usage:\n\tiTach device command\n");
		output.append("where:\n");
		output.append("  valid devices:\n");
		output.append(getControllerList());
		output.append("  valid commands:\n");
		output.append(getCommandList());
		
		return output.toString();
	}
	
	public static String getControllerList() {
		StringBuffer output = new StringBuffer();
		for (Iterator<String> iter = config.getControllerList().iterator(); iter.hasNext();) {
			output.append("    " + iter.next() + "\n");
		}
		return output.toString();
	}
	
	public static String getCommandList() {
		StringBuffer output = new StringBuffer();

		for (Iterator<String> iter = config.getCommandList().iterator(); iter.hasNext();) {
			output.append("    " + iter.next() + "\n");
		}
		
		return output.toString();
	}


} // class
