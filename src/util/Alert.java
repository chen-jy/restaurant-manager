package util;

import visual.gui.Employee;

/**
 * A class representing an alert
 */
public class Alert {
	private String message; //The alert message
	private Employee sender; // The sender of the alert
	private Employee receiver;//The receiver of the alter
	private boolean isRead = false; //If the alert has been read
	private boolean isPinned;//if the alert is a pinned message

	/**
	 * Creates an alert.
	 *
	 * @param message  the message of the alert
	 * @param sender   the alert sender
	 * @param receiver the alert receiver
	 * @param isPinned if an alert is pinned
	 */
	public Alert(String message, Employee sender, Employee receiver, boolean isPinned) {
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.isPinned = isPinned;
	}

	/**
	 * Executes and sends an alter.
	 */
	public void execute() {
		this.receiver.alert(this);
		sender.logf("Sent an alert to %s", receiver.toString());
	}

	/**
	 * return the message sent in an alert
	 *
	 * @return a String.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the employee that receives the alert.
	 *
	 * @return an employee.
	 */
	public Employee getReceiver() {
		return receiver;
	}

	/**
	 * Returns the employee that sent the alert.
	 *
	 * @return an employee
	 */
	public Employee getSender() {
		return sender;
	}

	/**
	 * Return if a alert has been read.
	 *
	 * @return true is a alert has been read, false otherwise.
	 */
	public boolean isRead() {
		return isRead;
	}


	/**
	 * Returns if a message is pinned.
	 *
	 * @return true if a message is pinned. False otherwise.
	 */
	public boolean isPinned() {
		return isPinned;
	}

	/**
	 * Reads a message.
	 */
	public void read() {
		this.isRead = true;
	}

	/**
	 * Returns a String representation of the alert.
	 *
	 * @return a String
	 */
	@Override
	public String toString() {
		return "(" + this.sender.toString() + ") " + this.message;
	}
}
