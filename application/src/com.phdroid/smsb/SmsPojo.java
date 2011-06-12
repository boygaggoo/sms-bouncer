package com.phdroid.smsb;

import android.content.ContentValues;
import android.telephony.SmsMessage;
import com.phdroid.smsb.storage.SmsPojoAdapter;

/**
 * Plain old java object for Sms message.
 */
public class SmsPojo {
	private String sender;
	private String message;
	private long received;
    private boolean read;
	private boolean markedNotSpamByUser;

	public SmsPojo() {
	}

	public SmsPojo(SmsMessage msg) {
		sender = msg.getOriginatingAddress();
		message = msg.getMessageBody();
		received = msg.getTimestampMillis();
        read = false;
	}

	public ContentValues toContentValues() {
		return SmsPojoAdapter.toContentValues(this);
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

    public boolean isRead(){
        return read;
    }

    public void setRead(boolean r){
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
    public String toString(){
        return getMessage();
    }
}
