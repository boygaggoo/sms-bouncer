package com.phdroid.smsb;

import android.content.ContentProvider;
import android.net.Uri;
import com.phdroid.smsb.base.ProviderTestBase;
import com.phdroid.smsb.storage.ApplicationSettings;
import com.phdroid.smsb.storage.dao.*;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Testing SmsMessageEntry.
 */
public class SmsMessageEntryTest extends ProviderTestBase {
	private String sender = "911";
	private String message = "testing storing-loading messages";
	private long time;
	private Session session;

	/**
	 * Prepares list of content providers and corresponding Uri's for context setup.
	 *
	 * @return content providers and corresponding Uri's
	 */
	@Override
	public Hashtable<Uri, ContentProvider> getTestContentProviders() {
		Hashtable<Uri, ContentProvider> res = new Hashtable<Uri, ContentProvider>();
		res.put(SmsContentProvider.CONTENT_URI, new SmsContentProvider());
		res.put(SenderContentProvider.CONTENT_URI, new SenderContentProvider());
		return res;
	}

	public Session getSession() {
		if(session == null) {
			session = new Session(new ApplicationSettings(this.getContext()), this.getContentResolver());
		}
		return session;
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		Calendar c = Calendar.getInstance();

		time = c.getTimeInMillis();

		SmsMessageSenderEntry senderEntry = getSession().insertOrSelectSender(sender);

		SmsMessageEntry sms = new SmsMessageEntry(senderEntry, message, time);
		getSession().insertMessage(sms);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		List<SmsPojo> smsList = getSession().getSmsList();
		SmsPojo[] smsListArray = new SmsPojo[smsList.size()];
		getSession().deleteMessages(smsList.toArray(smsListArray));
	}

	public void testSms_message_save_load() throws Exception {
		List<SmsPojo> smsList = getSession().getSmsList();
		assertEquals(1, smsList.size());
		SmsPojo smsLoaded = smsList.get(0);
		assertEquals("Message not OK", message, smsLoaded.getMessage());
		assertEquals("Received not OK", time, smsLoaded.getReceived());
		assertEquals("Sender not OK", sender, smsLoaded.getSender());
	}
}