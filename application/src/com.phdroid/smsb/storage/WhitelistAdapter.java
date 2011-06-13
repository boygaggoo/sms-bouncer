package com.phdroid.smsb.storage;

import android.content.Context;
import android.database.Cursor;
import com.phdroid.smsb.storage.dao.SmsContentProvider;
import com.phdroid.smsb.storage.dao.SmsMessageEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides whitelist read operations.
 */
public class WhitelistAdapter {
	private Context context;

	public WhitelistAdapter(Context context) {
		this.context = context;
	}

	private Context getContext() {
		return context;
	}

	private Cursor getWhitelistData(){
		String[] data = {"True"};
		return getContext().getContentResolver().query(SmsContentProvider.CONTENT_URI,
				null,
				SmsMessageEntry.USER_FLAG_NOT_SPAM + " = :1",
				data,
				null);
	}

	public List<String> getWhitelist() {
		ArrayList<String> res = new ArrayList<String>();
		Cursor whitelist = getWhitelistData();
		try {
			whitelist.moveToFirst();
			int count = whitelist.getCount();
			for (int i = 0; i < count; i++) {
				String sender = whitelist.getString(whitelist.getColumnIndexOrThrow(SmsMessageEntry.SENDER_ID));
				if (!res.contains(sender)) {
					//todo:check address book
					res.add(sender);
				}
				whitelist.moveToNext();
			}
		} finally {
			whitelist.close();
		}
		return null;
	}
}