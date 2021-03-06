package com.phdroid.smsb;


/**
 * Plain old java object for Sms message.
 */
public abstract class SmsPojo {
	protected SmsPojo() {
	}

	public abstract String getSender();

	public abstract long getSenderId();

	public void setSender(String sender) {
	}

	public abstract long getId();

	public abstract boolean isRead();

	public abstract void setRead(boolean r);

	public abstract String getMessage();

	public abstract void setMessage(String message);

	public abstract long getReceived();

	public abstract void setReceived(long received);

	public abstract boolean isMarkedNotSpamByUser();

	public abstract void setMarkedNotSpamByUser(boolean markedNotSpamByUser);

	@Override
	public String toString() {
		return getMessage();
	}
}
