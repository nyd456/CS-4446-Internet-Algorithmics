import java.util.Vector; //Do not delete or modify this line

public class Diameter extends Algorithm {

	public String computeDiameter(String id) {
		try {
			/* Write your code to declare and initialize variables here */

			Message mssg = null; // message to send
			Message m; // message to receiver
			int height = 0;
			int diameter = 0;
			int message_received = 0;

			int numChildren = numChildren();

			// to store the length of the longest paths between each child to this node
			Vector<Integer> lengthOfPaths = new Vector<Integer>();

			if (numChildren == 0) { // If we are a leaf node
				if (getParent() == null) { // We are a leaf node and have no parent
					printDiameter(diameter);
					return "";
				} else {
					// leaf node sends message to parent with height=0 and diameter=0
					mssg = createMessage(getParent(), integerToString(height), integerToString(diameter));
				}
			}

			while (waitForNextRound()) {

				/* Write your code here to send, receive, and process messages */
				// Send message
				if (mssg != null) {
					send(mssg);
					String dStr = getDataItem(2, mssg); // get diameter from mssg
					if (dStr != null) {
						printDiameter(stringToInteger(dStr)); // display diameter in node
					}
					return "";
				}
				mssg = null;

				// Receive message
				m = receive();

				while (m != null) {
					message_received++;
					height = stringToInteger(getDataItem(1, m)) + 1;
					
					if (numChildren == 1) {
						diameter = height;
						if (getParent() != null) {
							// a message to its parent with 2 values: the height and diameter of its subtree.
							mssg = createMessage(getParent(), integerToString(height), integerToString(diameter));
						} else {
							printDiameter(diameter);
							return "";
						}
					} else if (numChildren > 1) {
						lengthOfPaths.add(height);
						if (message_received == numChildren) {
							// add top 2 children from diameter list (Vector)
							diameter = lengthOfPaths.get(lengthOfPaths.size() - 1) + lengthOfPaths.get(lengthOfPaths.size() - 2);
							if (getParent() != null) {
								mssg = createMessage(getParent(), integerToString(height), integerToString(diameter));
							} else {
								printDiameter(diameter);
								return "";
							}
						}
					}

					m = receive();
				}
			}
		} catch (SimulatorException e) {
			System.out.println("ERROR: " + e.toString());
		}

		return "";
	}

	/*
	 * =============================================================================
	 * == Do not modify any of the methods below
	 * =============================================================================
	 * ==
	 */
	private Vector<String> children1 = new Vector<String>(), children2 = new Vector<String>();

	public Object run() {
		String result = computeDiameter(getID());
		return result;
	}

	/* Show the diameter of the subtree roooted at a processor */
	private void printDiameter(int diam) {
		showMessage("diameter: " + integerToString(diam));
	}

	/*
	 * Receives as input a message containing several data items and the position of
	 * a data item, and it returns the corresponding data item. The first data item
	 * is at position 1, the second is ar posiiton 2, and so on.
	 */
	private String getDataItem(int numItem, Message msg) {
		String[] messages = unpack(msg.data());
		return messages[numItem - 1];
	}

	/*
	 * Creates a message <destination,source, dataItem1, dataItem2, ...> with data
	 * containing an arbitray number of data items.
	 */
	private Message createMessage(String destination, String... dataItems) {
		String msg = "";
		for (int i = 0; i < dataItems.length - 1; ++i)
			msg = msg + dataItems[i] + ",";
		msg = msg + dataItems[dataItems.length - 1];
		return makeMessage(destination, msg);
	}

}
