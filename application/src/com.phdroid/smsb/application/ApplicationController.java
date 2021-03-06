package com.phdroid.smsb.application;

import android.app.Application;
import com.phdroid.smsb.SmsPojo;
import com.phdroid.smsb.activity.base.ActivityBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Inheritor of Application. Accessed via getApplicationContext()
 */
public class ApplicationController extends Application {
	private List<NewSmsEventListener> listeners;
	private ActivityBase currentActivity;

	public ActivityBase getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(ActivityBase currentActivity) {
		this.currentActivity = currentActivity;
	}

	public void attachNewSmsListener(NewSmsEventListener listener) {
		this.listeners.add(listener);
	}

	public void detachNewSmsListener(NewSmsEventListener listener) {
		this.listeners.remove(listener);
	}

	public void raiseNewSmsEvent(SmsPojo[] smsMessages) {
		NewSmsEvent event = new NewSmsEvent(this, smsMessages);
		for (NewSmsEventListener listener : this.listeners) {
			listener.onNewSms(event);
		}
	}

	public ApplicationController() {
		this.listeners = new ArrayList<NewSmsEventListener>();
	}
}
