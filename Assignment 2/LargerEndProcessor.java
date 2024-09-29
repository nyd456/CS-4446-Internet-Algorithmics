import java.util.Vector; //We need this for the Vector class.

/* 
 * Implementing a synchronous distributed algorithm that returns the larger of the ids of
 * the two processors located at the ends of the network
 */

public class LargerEndProcessor extends Algorithm {

	public String findLargerEndProcessor(String id) {
		// Write your initialization code here

		boolean isLeftmost = isLeftmost();
		boolean isRightmost = isRightmost();
		// case for only one processor
		if (isLeftmost && isRightmost)
			return id;

		String leftNeighbor = leftNeighbour();
		String rightNeighbor = rightNeighbour();

		Message sendMsg = null; // sending message
		Message receiveMsg; // receiving message

		if (isLeftmost()) { // start from the leftmost processor
			// the first data item stores the processor's own id or the <END> message
			// the second data item stores the leftmost processor id
			// that will be passed to the rightmost processor
			// to do a comparison
			sendMsg = createMessage(rightNeighbor, id, id);
		}
		try {
			while (waitForNextRound()) { // Processors wait here for the next round
				// Write your code to send messages here
				if (sendMsg != null) {
					send(sendMsg); // send message to the right neighbor
					// get the processor id or the "END" message from the first data item .
					String data = getDataItem(1, sendMsg);
					if (data != null && equal(data, "END")) {
						return getDataItem(2, sendMsg); // return the larger end processor id
					}
				}
				sendMsg = null;
				// Write your code to receive messages and process them here
				// To receive a message use method receive(). If receive() returns
				// null means that no message was received in this round.
				receiveMsg = receive();
				if (receiveMsg != null) {
					// get the leftmost processor id from the second data item
					String data = getDataItem(2, receiveMsg);
					if (data != null && data.length() > 0) // check for empty message
					{
						// message from left to right
						if (equal(receiveMsg.source(), leftNeighbor) && !equal(data, "END")) {

							// reach the rightmost processor
							if (isRightmost()) {
								// compare the leftmost processor id with the rightmost
								// processor id, keep the larger one and send it back
								// and also update the message to <END>
								if (larger(id, data)) {
									sendMsg = createMessage(leftNeighbor, "END", id);
								} else {
									sendMsg = createMessage(leftNeighbor, "END", data);
								}
							} else {
								sendMsg = createMessage(rightNeighbor, id, data);
							}
						} else { // message from right to left
							sendMsg = createMessage(leftNeighbor, "END", data);
							// the last message
							if (isLeftmost()) {
								sendMsg = createMessage(rightNeighbor, "END", data);
							}
						}
					}
				}
			}
		} catch (SimulatorException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return "";
	}

	/*
	 * =============================================================================
	 * == Do not modify any of the methods below
	 * =============================================================================
	 * ==
	 */

	public Object run() {
		String larger = findLargerEndProcessor(getID());
		return larger;
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
	private String leftNeighbour() {
		Vector v = neighbours();
		return (String) v.elementAt(1);
	}

	/* Returns true if this processor is the leftmost one in the network */
	private boolean isLeftmost() {
		Vector<String> v = neighbours();
		String leftNeighbour = (String) v.elementAt(1);
		if (equal(leftNeighbour, "0"))
			return true;
		else
			return false;
	}

	/* Returns true if this processor is the rightmost one in the network */
	private boolean isRightmost() {
		Vector<String> v = neighbours();
		String rightNeighbour = (String) v.elementAt(0);
		if (equal(rightNeighbour, "0"))
			return true;
		else
			return false;
	}

}
