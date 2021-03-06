package com.phdroid.smsb.filter;

import android.content.ContentProvider;
import android.net.Uri;
import com.phdroid.smsb.SmsPojo;
import com.phdroid.smsb.TestSmsPojo;
import com.phdroid.smsb.base.ProviderTestBase;
import com.phdroid.smsb.base.SmsMessageTransferStub;
import com.phdroid.smsb.exceptions.ApplicationException;
import com.phdroid.smsb.storage.ApplicationSettings;
import com.phdroid.smsb.storage.dao.*;
import junit.framework.Assert;

import java.util.Hashtable;

/**
 * Test class for WhiteListSpamFilter.
 */
public class WhiteListSpamFilterTest extends ProviderTestBase {
	private static String SENDER = "(097) 112 33 26";

	@Override
	public Hashtable<Uri, ContentProvider> getTestContentProviders() {
		Hashtable<Uri, ContentProvider> settings = new Hashtable<Uri, ContentProvider>();
		settings.put(SenderContentProvider.CONTENT_URI, new SenderContentProvider());
		settings.put(SmsContentProvider.CONTENT_URI, new SmsContentProvider());
		return settings;
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		Session m = new Session(new ApplicationSettings(this.getContext()), getContentResolver());
        SmsMessageSenderEntry spamSender = m.insertOrSelectSender("1346");
        SmsMessageEntry spam = new SmsMessageEntry();
		spam.setSenderId(spamSender.getId());
		spam.setMessage("Novaja aktsia ot magazinov Kharkova");
		spam.setReceived((int) (System.currentTimeMillis() / 1000L));
		getContentResolver().insert(SmsContentProvider.CONTENT_URI, spam.toContentValues());

        SmsMessageSenderEntry sender = m.insertOrSelectSender(SENDER);
		SmsMessageEntry notSpam = new SmsMessageEntry();
		notSpam.setSenderId(sender.getId());
		notSpam.setMessage("Let's grab some whisky");
		notSpam.setReceived((int) (System.currentTimeMillis() / 1000L));
		notSpam.setMarkedNotSpamByUser(true);
		getContentResolver().insert(SmsContentProvider.CONTENT_URI, notSpam.toContentValues());
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		getContentResolver().delete(SmsContentProvider.CONTENT_URI, null, null);
	}

	public void testWhiteList() throws ApplicationException {
		SmsPojo spam1 = new TestSmsPojo();
		spam1.setSender("1346");
		spam1.setMessage("Novaja aktsia ot magazinov Kharkova");
		spam1.setReceived((int) (System.currentTimeMillis() / 1000L));

		SmsPojo spam2 = new TestSmsPojo();
		spam2.setSender("1343");
		spam2.setMessage("Staraja aktsia ot magazinov Kharkova");
		spam2.setReceived((int) (System.currentTimeMillis() / 1000L));

		SmsPojo notSpam = new TestSmsPojo();
		notSpam.setSender(SENDER);
		notSpam.setMessage("Let's grab some whisky again");
		notSpam.setReceived((int) (System.currentTimeMillis() / 1000L));

		ISpamFilter filter = new WhiteListSpamFilter(getSession());
		Assert.assertEquals(true, filter.isSpam(new SmsMessageTransferStub(spam1)));
		Assert.assertEquals(true, filter.isSpam(new SmsMessageTransferStub(spam2)));
		Assert.assertEquals(false, filter.isSpam(new SmsMessageTransferStub(notSpam)));
	}
}
