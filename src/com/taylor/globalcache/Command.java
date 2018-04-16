package com.taylor.globalcache;

public class Command {

	@SuppressWarnings("unused")
	private static final boolean DEBUGGING = ITach.DEBUG;
	
	private String name;
	private String rawCommand;
	
	/**
	 * @return the "Friendly" command name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the sendir command
	 */
	public String getCommand(Integer port) {
		// start preparing the command we will send to the controller
		String command = this.rawCommand;

		command = command.replace("[PORT]", port.toString());

		return command;
	}
	
	
	/**
	 * @param name
	 * @param command
	 * @param number
	 */
	public Command(String name, String command) {
		super();
		this.name = name;
		this.rawCommand = command;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Command [CommandName=" + getName() + 
				", CommandString=" + getCommand(1) + "]";
	}

	
	
}
