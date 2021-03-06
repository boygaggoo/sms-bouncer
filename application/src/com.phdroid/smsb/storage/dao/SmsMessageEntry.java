package com.phdroid.smsb.storage.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.telephony.SmsMessage;
import com.phdroid.smsb.SmsPojo;
import com.phdroid.smsb.utility.SmsMessageTransferObject;

/**
 * DAO for SmsMessage
 */
public class SmsMessageEntry extends SmsPojo {
	public static final String _ID = "_id";
	public static final String SENDER_ID = "sender_id";
	public static final String SENDER = "sender";
	public static final String MESSAGE = "message";
	public static final String RECEIVED = "received";
	public static final String READ = "read";
	public static final String ACTION = "action";

	private long id;
	private long senderId;
	private String message;
	private long received;
	private boolean read;
	private boolean markedNotSpamByUser;
	private String sender;

	public SmsMessageEntry() {
	}

	public SmsMessageEntry(Cursor c) {
		this.id = c.getInt(c.getColumnIndex(SmsMessageEntry._ID));
		this.senderId = c.getInt(c.getColumnIndex(SmsMessageEntry.SENDER_ID));
		this.sender = c.getString(c.getColumnIndex(SmsMessageEntry.SENDER));
		this.message = c.getString(c.getColumnIndex(SmsMessageEntry.MESSAGE));
		this.received = c.getLong(c.getColumnIndex(SmsMessageEntry.RECEIVED));
		this.read = c.getInt(c.getColumnIndex(SmsMessageEntry.READ)) == 1;
	}

	public SmsMessageEntry(SmsMessageSenderEntry sender, SmsMessage message) {
		this.senderId = sender.getId();
		this.sender = sender.getValue();
		this.message = message.getMessageBody();
		this.received = message.getTimestampMillis();
		this.read = false;
	}

	public SmsMessageEntry(SmsMessageSenderEntry sender, SmsMessageTransferObject message) {
		this.senderId = sender.getId();
		this.sender = sender.getValue();
		this.message = message.getMessage();
		this.received = message.getReceived();
		this.read = message.isRead();
	}

	public SmsMessageEntry(SmsMessageSenderEntry sender, String message, long received) {
		this.senderId = sender.getId();
		this.sender = sender.getValue();
		this.message = message;
		this.received = received;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(SENDER_ID, this.getSenderId());
		values.put(MESSAGE, this.getMessage());
		values.put(RECEIVED, this.getReceived());
		values.put(READ, this.isRead());
		return values;
	}

	public long getId() {
		return id;
	}

	public long getSenderId() {
		return senderId;
	}

	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}

	public String getSender() {
		return sender;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean r) {
		read = r;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getReceived() {
		return received;
	}

	public void setReceived(long received) {
		this.received = received;
	}

	public boolean isMarkedNotSpamByUser() {
		return markedNotSpamByUser;
	}

	public void setMarkedNotSpamByUser(boolean markedNotSpamByUser) {
		this.markedNotSpamByUser = markedNotSpamByUser;
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
