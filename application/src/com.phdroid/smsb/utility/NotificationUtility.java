package com.phdroid.smsb.utility;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.phdroid.blackjack.ui.notify.TrayNotification;
import com.phdroid.blackjack.ui.notify.TrayNotificationManager;
import com.phdroid.smsb.R;
import com.phdroid.smsb.SmsPojo;
import com.phdroid.smsb.application.ApplicationController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Please, write short description of what this file is for.
 */
public class NotificationUtility {
	private static NotificationUtility instance;
	private Context context;
	private TrayNotification notification;
	private TrayNotificationManager notificationManager;
	private List<SmsPojo> attachedSmsMessages;
	private final Object lock = new Object();

	NotificationUtility(Context context) {
		this.context = context;
		this.attachedSmsMessages = new ArrayList<SmsPojo>();
	}

	public static NotificationUtility getInstance(Context context) {
		if (instance == null) {
			instance = new NotificationUtility(context);
		}
		return instance;
	}

	public void show(String tickerText, String title, String message, long when, Class activity) {
		synchronized (lock) {
			if (notification == null) {
				notification = getNotificationManager().createNotification(tickerText, title, message, when);
			} else {
				notification.setMessage(message);
				notification.setTickerText(tickerText);
				notification.setTitle(title);
				notification.setWhen(when);
			}

			notificationManager.notify(notification, activity);
		}
	}

	private void attachMessages(SmsPojo[] smsPojos) {
		Collections.addAll(this.attachedSmsMessages, smsPojos);
	}

	public void showAutoGenerated(SmsPojo[] spamMessages, String tickerText, long when, Class activity) {
		attachMessages(spamMessages);

		String title;
		String message;

		final int size = attachedSmsMessages.size();
		final Resources resources = getContext().getResources();
		if (size == 0) {
			return;
		}
		if (size == 1) {
			SmsPojo sms = attachedSmsMessages.get(0);
			final String format = resources.getQuantityString(R.plurals.notify_title, size);
			title = String.format(format, sms.getSender());
			message = sms.getMessage();

		} else {
			SmsPojo sms1 = attachedSmsMessages.get(0);
			SmsPojo sms2 = attachedSmsMessages.get(1);
			final String format = resources.getQuantityString(R.plurals.notify_title, size);
			title = String.format(format, size);
			final String format1 = resources.getQuantityString(R.plurals.notify_message, size);
			message = String.format(
					format1,
					sms1.getSender(),
					sms2.getSender());
		}

		ApplicationController application = (ApplicationController) getContext().getApplicationContext();
		if (application.getCurrentActivity() == null) {
			Log.d("sms-bouncer", String.format("[get] currentActivity = null"));
			show(tickerText, title, message, when, activity);
		}
	}

	public void clearAll() {
		synchronized (lock) {
			this.attachedSmsMessages = new ArrayList<SmsPojo>();
			getNotificationManager().cancelAll();
		}
	}

	private Context getContext() {
		return context;
	}

	public TrayNotificationManager getNotificationManager() {
		if(notificationManager == null) {
			notificationManager = new TrayNotificationManager(getContext());
		}
		return notificationManager;
	}
}
