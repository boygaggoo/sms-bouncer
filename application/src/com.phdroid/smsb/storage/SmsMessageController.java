package com.phdroid.smsb.storage;

import com.phdroid.smsb.SmsPojo;
import com.phdroid.smsb.storage.dao.Session;

import java.util.Hashtable;
import java.util.List;

/*
 * Message Controller
 */
public class SmsMessageController implements IMessageProvider {
	private int unreadCount;
	private List<SmsPojo> data;
	private Session session;

	public SmsMessageController(Session session) {
		this.session = session;
	}

	private Session getSession() {
		return session;
	}

	private List<SmsPojo> getSmsList() {
		if (data == null) {
			dataBind();
		}
		return data;
	}

	private void dataBind() {
		int unread = 0;
		List<SmsPojo> smsPojoList = getSession().getSmsList();
		for (SmsPojo sms : smsPojoList) {
			if (!sms.isRead()) {
				unread++;
			}
		}
		unreadCount = unread;
		data = smsPojoList;
	}

	public int size() {
		return getSmsList().size();
	}

	public List<SmsPojo> getMessages() {
		return getSmsList();
	}

	public Hashtable<SmsPojo, SmsAction> getActionMessages() {
		return session.getActions();
	}

	public void delete(long id) {
		performActions();
		SmsPojo sms = get(id);
		if (sms != null) {
			session.setAction(sms, SmsAction.Deleted);
			getSmsList().remove(sms);
		}
	}

	public void delete(long[] ids) {
		performActions();
		for (long id = ids.length - 1; id >= 0; id--) {
			SmsPojo sms = get(ids[(int) id]);
			if (sms != null) {
				if (!sms.isRead())
					unreadCount--;
				session.setAction(sms, SmsAction.Deleted);
				getSmsList().remove(sms);
			}
		}
	}

	public void deleteAll() {
		List<SmsPojo> smsPojoList = getSession().getSmsList();
		for (SmsPojo sms : smsPojoList) {
			session.setAction(sms, SmsAction.Deleted);
			getSmsList().remove(sms);
		}
	}

	public void notSpam(long id) {
		performActions();
		SmsPojo sms = get(id);
		if (sms != null) {
			session.setAction(sms, SmsAction.MarkedAsNotSpam);
			getSmsList().remove(sms);
		}
	}

	public void notSpam(long[] ids) {
		performActions();
		for (long id = ids.length - 1; id >= 0; id--) {
			SmsPojo sms = get(ids[(int) id]);
			if (sms != null) {
				if (!sms.isRead())
					unreadCount--;
				session.setAction(sms, SmsAction.MarkedAsNotSpam);
				getSmsList().remove(sms);
			}
		}
	}

	@Override
	public int getIndex(SmsPojo message) {
		return getMessages().indexOf(message);
	}

	@Override
	public SmsPojo getMessageByOrdinal(int index) {
		return this.getMessages().get(index);
	}

	public SmsPojo getMessage(long id) {
		return get(id);
	}

	@Override
	public void invalidateCache() {
		dataBind();
	}

	public void read(long id) {
		SmsPojo smsPojo = get(id);
		read(smsPojo);
	}

	@Override
	public void read(SmsPojo sms) {
		if (sms != null && !sms.isRead()) {
			sms.setRead(true);
			getSession().update(sms);
			unreadCount--;
		}
	}

	public int getUnreadCount() {
		return unreadCount;
	}

	public void undo() {
		session.undoActions();
		dataBind();
	}

	public void performActions() {
		Hashtable<SmsPojo, SmsAction> actions = getActionMessages();
		for (SmsPojo sms : actions.keySet()) {
			SmsAction action = actions.get(sms);
			switch (action) {
				case Deleted:
					getSession().delete(sms);
					break;
				case MarkedAsNotSpam:
					getSession().putSmsToSystemLog(sms);
					getSession().delete(sms);
					break;
				default:
			}
		}
		dataBind();
	}

	private SmsPojo get(long id) {
		return getSession().getSms(id);
	}
}
