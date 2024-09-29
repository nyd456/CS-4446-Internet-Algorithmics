import java.util.Vector; //Do not delete or modify this line

public class DoubleBFS extends Algorithm {

	public String bfsTrees(String id) {
		try {
			Message m; // Variable used to read messages
			int rounds_left = -1;// Variable used to know when to terminate the algorithm

			/* Declare and initialize your variables here */

			// Variable used to send message and acknowledgement of root1 and root2
			Message mssg1 = null, ack1 = null;
			Message mssg2 = null, ack2 = null;
			String parent1 = null, parent2 = null; // Parent of this node in the BFS tree of root1 and root2

			// The root nodes will send messages in the first round. 
			if (equal(id, "1")) { // root1
				mssg1 = messageForNeighbours("", id, "?", "1"); // no parent, current id, root1
				parent1 = "null"; // no parent, print out "null"
			} else if (equal(id, "2")) { // root2
				mssg2 = messageForNeighbours("", id, "?", "2");// no parent, current id, root2
				parent2 = "null"; // no parent, print out "null"
			} else { // If processor is not the root node.
				mssg1 = null;
				mssg2 = null;
				parent1 = null;
				parent2 = null;
			}
			ack1 = null;
			ack2 = null;
			rounds_left = -1;

			while (waitForNextRound()) {
				/* Add your code to send messages here */

				// Send requests to all neighbors except parent
				// and wait two rounds to get responses to requests
				if (mssg1 != null) {
					send(mssg1);
					rounds_left = 1;
				}
				if (mssg2 != null) {
					send(mssg2);
					rounds_left = 1;
				}

				// Send acknowledgement to parent
				if (ack1 != null) {
					send(ack1);
				}
				if (ack2 != null) {
					send(ack2);
				}
				mssg1 = null;
				mssg2 = null;
				ack1 = null;
				ack2 = null;

				/*
				 * Add code below to read the messages. Code for reading the first message was
				 * added for you
				 */
				m = receive();
				while (m != null) {
					// get data from received message
					String pId = getDataItem(1, m); // sender id
					String type = getDataItem(2, m); // "?" or "Y"
					String root = getDataItem(3, m); // "1" or "2"

					if (type.equals("?")) { // Request for adoption
						if (equal(root, "1")) { // Root1
							if (parent1 == null) { // Parent not set yet
								parent1 = pId;
								// send message to all neighbors except parent
								mssg1 = messageForNeighbours(parent1, id, "?", "1");
								// send ack to parent and agree to be child of parent
								ack1 = createMessage(parent1, id, "Y", "1");
							} else {
								Vector<String> adjacent1 = neighbours();
								adjacent1.remove(pId);
							}
						} else if (equal(root, "2")) {
							if (parent2 == null) { // Parent not set yet
								parent2 = pId;
								// send message to all neighbors except parent
								mssg2 = messageForNeighbours(parent2, id, "?", "2");
								// send ack to parent and agree to be child of parent
								ack2 = createMessage(parent2, id, "Y", "2");
							} else {
								Vector<String> adjacent2 = neighbours();
								adjacent2.remove(pId);
							}
						}
					} else if (type.equals("Y")) // Neighbor agreed to be child of this processor
					{
						if (equal(root, "1")) {
							addChild1(pId);
						} else if (equal(root, "2")) {
							addChild2(pId);
						}
					}
					m = receive();
				}

				/*
				 * Terminate the algorithm two rounds after the last message was sent. You need
				 * to determine when the value of rounds_left must be changed from -1 to 1.
				 */
				if (parent1 != null && parent2 != null) {
					if (rounds_left == 0) {
						printParentsChildren(parent1, parent2, children1, children2);
						return "";
					} else if (rounds_left == 1) {
						rounds_left = 0;
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

	private Vector<String> children1 = new Vector<String>(), children2 = new Vector<String>();

	public Object run() {
		String result = bfsTrees(getID());
		return result;
	}

	/* Add a child to the list of children for the tree with root "1" */
	private void addChild1(String child) {
		children1.add(child);
	}

	/* Add a child to the list of children for the tree with root "2" */
	private void addChild2(String child) {
		children2.add(child);
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
	private Message messageForNeighbours(String parent, String... dataItems) {
		Vector<String> adjacent = neighbours();
		if (!parent.equals(""))
			adjacent.remove(parent);
		String msg = "";
		for (int i = 0; i < dataItems.length - 1; ++i)
			msg = msg + dataItems[i] + ",";
		msg = msg + dataItems[dataItems.length - 1];
		return makeMessage(adjacent, msg);
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

	/*
	 * Print information about the parent and children of this processor in both BFS
	 * trees
	 */
	private void printParentsChildren(String parent1, String parent2, Vector<String> children1,
			Vector<String> children2) {
		String outMssg = "[" + parent1 + ":";
		for (int i = 0; i < children1.size() - 1; ++i)
			outMssg = outMssg + children1.elementAt(i) + " ";
		if (children1.size() > 0)
			outMssg = outMssg + children1.elementAt(children1.size() - 1) + "] [" + parent2 + ":";
		else
			outMssg = outMssg + "] [" + parent2 + ":";
		for (int i = 0; i < children2.size() - 1; ++i)
			outMssg = outMssg + children2.elementAt(i) + " ";
		if (children2.size() > 0)
			outMssg = outMssg + children2.elementAt(children2.size() - 1) + "]";
		else
			outMssg = outMssg + "]";
		showMessage(outMssg);
		printMessage(outMssg);
	}
}
