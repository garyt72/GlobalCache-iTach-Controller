package com.taylor.globalcache;

public class Response {

	//private static final boolean DEBUGGING = Util.DEBUGGING;
	@SuppressWarnings("unused")
	private static final boolean DEBUGGING = ITach.DEBUG;
	private String responseMessage;
	private String friendlyResponse;
	private boolean success;
	
	Response(String response) {
		this.responseMessage = response;
		parseResponse(response);
	}
	
	public String getResponse() {
		return responseMessage;
	}
	
	public String getFriendlyResponse() {
		return friendlyResponse;
	}
	
	public boolean success() {
		return success;
	}
	
	private void parseResponse (String response) {
		
		// if there's an error
		if (response.startsWith("ERR_")) {
			// if there was an error - we weren't successful
			success = false;
			
			String split[] = response.split("_.*,");
			
			// remove the device from the command
			String errMessage = split[0] + "_" + split[1];
			
			// switch to prefix specifc code
			switch (errMessage) {
				case "ERR_001":	
					friendlyResponse = "Invalid command. Command not found.";
					break;
				case "ERR_002":	
					friendlyResponse = "Invalid module address (does not exist).";
					break;
				case "ERR_003":	
					friendlyResponse = "Invalid connector address (does not exist).";
					break;
				case "ERR_004":	
					friendlyResponse = "Invalid ID value.";
					break;
				case "ERR_005":	
					friendlyResponse = "Invalid frequency value.";
					break;
				case "ERR_006":	
					friendlyResponse = "Invalid repeat value.";
					break;
				case "ERR_007":	
					friendlyResponse = "Invalid offset value.";
					break;
				case "ERR_008":	
					friendlyResponse = "Invalid pulse count.";
					break;
				case "ERR_009":	
					friendlyResponse = "Invalid pulse data.";
					break;
				case "ERR_010":	
					friendlyResponse = "Uneven amount of <on|off> statements.";
					break;
				case "ERR_011":	
					friendlyResponse = "No carriage return found.";
					break;
				case "ERR_012":	
					friendlyResponse = "Repeat count exceeded.";
					break;
				case "ERR_013":	
					friendlyResponse = "IR command sent to input connector.";
					break;
				case "ERR_014":	
					friendlyResponse = "Blaster command sent to non-blaster connector.";
					break;
				case "ERR_015":	
					friendlyResponse = "No carriage return before buffer full.";
					break;
				case "ERR_016":	
					friendlyResponse = "No carriage return.";
					break;
				case "ERR_017":	
					friendlyResponse = "Bad command syntax.";
					break;
				case "ERR_018":	
					friendlyResponse = "Sensor command sent to non-input connector.";
					break;
				case "ERR_019":	
					friendlyResponse = "Repeated IR transmission failure.";
					break;
				case "ERR_020":	
					friendlyResponse = "Above designated IR <on|off> pair limit.";
					break;
				case "ERR_021":	
					friendlyResponse = "Symbol odd boundary.";
					break;
				case "ERR_022":	
					friendlyResponse = "Undefined symbol.";
					break;
				case "ERR_023":	
					friendlyResponse = "Unknown option.";
					break;
				case "ERR_024":	
					friendlyResponse = "Invalid baud rate setting.";
					break;
				case "ERR_025":	
					friendlyResponse = "Invalid flow control setting.";
					break;
				case "ERR_026":	
					friendlyResponse = "Invalid parity setting.";
					break;
				case "ERR_027":	
					friendlyResponse = "Settings are locked.";
					break;
				default:	
					// if we haven't gone anywhere else, then send back what we got
					friendlyResponse = getResponse();
					break;
			}// case

		}// error
		else {
			// not an ERR message
			if (response.startsWith("completeir")) {
				success = true;
				friendlyResponse = "Success";
			} else {
				success = false;
				friendlyResponse = "Unknown Response (" + responseMessage + ")";
			}
			
		}
	}
	
}
