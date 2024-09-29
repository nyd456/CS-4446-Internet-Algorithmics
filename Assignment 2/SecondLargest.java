import java.util.*;

public class SecondLargest extends Algorithm {

	/* This algorithm returns the second largest id in a ring network. */
	/* ================================= */
	public String findSecondLargest(String id) {
		/* ================================= */
		// Write your initialization code here
		String rightNeighbor = rightNeighbour();

		// the first data item stores the processor id
		// the second data item is a placeholder for the second largest id
		Message sendMsg = createMessage(rightNeighbor, id, "-1");
		Message receiveMsg; // receiving message

		try {
			while (waitForNextRound()) { // Processors wait here for the next round
				// Write your code to send messages here
				if (sendMsg != null) {
					send(sendMsg);
					// if <END> is found in the first data item, return the
					// second largest id that stored in the second data item
					String data = getDataItem(1, sendMsg);
					if (data != null && equal(data, "END")) {
						return getDataItem(2, sendMsg);
					}
				}
				sendMsg = null;
				// Write your code to receive messages and process them here
				// To receive a message use method receive(). If receive() returns
				// null means that no message was received in this round.
				receiveMsg = receive();
				if (receiveMsg != null) {
					String data1 = getDataItem(1, receiveMsg); // the current largest id or <END>
					String data2 = getDataItem(2, receiveMsg); // the current second largest id
					if (data1 != null && data1.length() > 0 && data2 != null && data2.length() > 0) {
						if (equal(data1, "END")) { 
							sendMsg = createMessage(rightNeighbor, "END", data2);
						} else {
							if (larger(id, data1)) { // this processor's id > data1 (current largest data)
								sendMsg = null;  // ignore the smaller processor ids
							}
							// id < data1 (current largest data) and id != data2 (current second largest id)
							else if (larger(data1, id) && !equal(data2, id)) {
								if (larger(data2, id)) { // if data2 > id then data2 is the current second largest id
									sendMsg = createMessage(rightNeighbor, data1, data2);
								} else { // if id > data2 then this processor's id is the current second largest id
									sendMsg = createMessage(rightNeighbor, data1, id);
								}
							} else if (equal(data2, id)) { // process receives the message with its own id
								sendMsg = createMessage(rightNeighbor, "END", data2);
							}
							// keep forwarding message if the processor with the largest id 
							// receives the message with its own id
							else if (equal(data1, id)) {
								sendMsg = createMessage(rightNeighbor, data1, data2);
							}
						}
					}
				}
			}
		} catch (SimulatorException e) {
			System.out.println("ERROR: " + e.getMessage());
			return "";
		}
		return "";
	}

	/*
	 * =============================================================================
	 * == Do not modify any of the methods below
	 * =============================================================================
	 * ==
	 */

	/* ====================== */
	public SecondLargest() {
		/* ====================== */
		/* This method is the same always */
		super();
	}

	/* ================ */
	public Object run() {
		/* ================ */

		// Invoke the main algorithm, passing as parameter the processor's id.
		return findSecondLargest(getID());
	}

	/*
	 * Receives as input a message containing several data items and the position of
	 * a data item, and it returns the corresponding data item. The first data item
	 * is at position 1, the second is at position 2, and so on.
	 */
	private String getDataItem(int numItem, Message msg) {
		String[] messages = unpack(msg.data());
		return messages[numItem - 1];
	}

	/*
	 * Creates a message <destination,source, dataItem1, dataItem2, ...> with data
	 * containing an arbitrary number of data items.
	 */
	private Message createMessage(String destination, String... dataItems) {
		String msg = "";
		for (int i = 0; i < dataItems.length - 1; ++i)
			msg = msg + dataItems[i] + ",";
		msg = msg + dataItems[dataItems.length - 1];
		return makeMessage(destination, msg);
	}

	/* Returns the id of the left neighbour of this processor */
	private String rightNeighbour() {
		Vector v = neighbours();
		return (String) v.elementAt(0);
	}

	/* Returns the id of the right neighbour of this processor */
	private String leftNeigbour() {
		Vector v = neighbours();
		return (String) v.elementAt(1);
	}

}
