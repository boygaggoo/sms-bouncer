package com.phdroid.smsb.utility;

import android.content.Context;
import com.phdroid.blackjack.ui.notify.TrayNotification;
import com.phdroid.blackjack.ui.notify.TrayNotificationManager;
import com.phdroid.smsb.SmsPojo;

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
			if (notificationManager == null) {
				notificationManager = new TrayNotificationManager(getContext());
			}
			if (notification == null) {
				notification = notificationManager.createNotification(tickerText, title, message, when);
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

		if (attachedSmsMessages.size() == 0) {
			return;
		}
		if (attachedSmsMessages.size() == 1) {
			SmsPojo sms = attachedSmsMessages.get(0);
			title = String.format("Blocked message from %s", sms.getSender());
			message = sms.getMessage();

		} else {
			SmsPojo sms1 = attachedSmsMessages.get(0);
			SmsPojo sms2 = attachedSmsMessages.get(1);
			title = String.format("Blocked messages (%d)", attachedSmsMessages.size());
			message = String.format("Blocked messages from %s, %s and others",
					sms1.getSender(),
					sms2.getSender());
		}

		show(tickerText, title, message, when, activity);
	}

	private Context getContext() {
		return context;
	}
}
