package com.taylor.globalcache;

import com.taylor.utilities.CommunicationDevice;

public class Controller extends CommunicationDevice {
	
	public static final String			DEFAULT_IP				= "192.168.1.65";
	public static final int				DEFAULT_PORT			= 4998;
	public static final int				SOCKET_RECV_WAIT		= 500;	// wait for response before reading
	public static final int				SOCKET_RECV_TIMEOUT		= 5000;	// wait for response before timout
	public static final int				SOCKET_SEND_WAIT		= 500;	// wait between sends
	public static final int				DEFAULT_IR_PORT			= 1;

	private String name;
	private int irPort;
	
	@Override
	public String getDefaultIp() {
		return DEFAULT_IP;
	}
	
	@Override
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}
	
	@Override
	public int getSocketReceiveWait() {
		return SOCKET_RECV_WAIT;
	}
	
	@Override
	public int getSocketReceiveTimeout() {
		return SOCKET_RECV_TIMEOUT;
	}
	
	@Override
	public int getSocketSendWait() {
		return SOCKET_SEND_WAIT;
	}
	
	@Override
	public boolean getDebug() {
		return ITach.DEBUG;
	}
	
	@Override
	public boolean getInfo() {
		return ITach.INFO;
	}
	
	public String getName() {
		return name;
	}
	
	public Controller(String name, String ipAddress, int port, int irPort, int repeatDelay){
		super(ipAddress, port, repeatDelay);
		this.name = name;
		this.irPort = irPort;
	}
	
	public Response sendCommand(Command command) {
		return sendCommand(command, 1, 1);
	}
	
	public Response sendCommand(Command command, int repeat) {
		if (repeat <= 1){
			return sendCommand(command);
		}
		else {
			return sendCommand(command, repeat, getRepeatDelay());
		}
	}
	
	public Response sendCommand(Command command, int repeat, int delay) {
		
		// get the command that we need to send
		String commandString = command.getCommand(this.irPort);
		
		// iTach rerequires a CR at the end of the command
		String responseMessage = super.sendCommand(commandString + "\r", repeat, delay, command.getName());
		
		// iTach appends a [CR] to the response - remove it
		responseMessage = responseMessage.trim();
		
		Response response = new Response(responseMessage);
		return response;
		
	}

}
