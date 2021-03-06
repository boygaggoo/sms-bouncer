package com.phdroid.smsb.base;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.test.AndroidTestCase;
import com.phdroid.smsb.storage.ApplicationSettings;
import com.phdroid.smsb.storage.dao.Session;

import java.util.Hashtable;

/**
 * Base class for all provider test cases.
 */
public abstract class ProviderTestBase extends AndroidTestCase {
	MockedContextTestBase logic = new MockedContextTestBase();
	Session session = null;
	/**
	 * Prepares list of content providers and corresponding Uri's for context setup.
	 *
	 * @return content providers and corresponding Uri's
	 *
	 */
	public abstract Hashtable<Uri, ContentProvider> getTestContentProviders();

	@Override
	public Context getContext() {
		return logic.getContext();
	}

	public ContentResolver getContentResolver() {
		return logic.getContentResolver();
	}

	public Session getSession() {
		if (session == null) {
			session = new Session(new ApplicationSettings(getContext()), getContentResolver());
		}
		return session;
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		logic.tearDown();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		logic.setUp();

		Hashtable<Uri, ContentProvider> settings = getTestContentProviders();
		if (settings != null) {
			logic.attachContentProviders(settings);
		}
	}

	protected ProviderTestBase() {
	}
}
