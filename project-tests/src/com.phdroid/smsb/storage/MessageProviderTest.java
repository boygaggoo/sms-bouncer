package com.phdroid.smsb.storage;

import android.test.AndroidTestCase;
import com.phdroid.smsb.SmsPojo;
import com.phdroid.smsb.TestSmsPojo;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MessageProviderTest extends AndroidTestCase{
	IMessageProvider mProvider;
	// TODO: change indexes for real message provider
	private static final int MESSAGES_COUNT = 10;
	private static final int UNREAD_MESSAGE_ID = 0;
	private static final int READ_MESSAGE_ID = 3;
	private static final int OVER_THE_TOP_ID = 100;
	private static final int NEGATIVE_ID = -1;
	private static final int INITIAL_UNREAD_MESSAGES_COUNT = 6;

	@Override
	public void setUp(){
		// TODO: replace this with real message provider
		mProvider = new SmsMessageController(new TestSession(new ApplicationSettings(this.getContext()), this.getContext().getContentResolver()));
	}

	public void test_getMessages_returns_full_messages_list(){
		List<SmsPojo> messages = mProvider.getMessages();
		 assertEquals(MESSAGES_COUNT, messages.size());
		 assertEquals(mProvider.size(), messages.size());
	}

	public void test_getMessage_doesnt_throw_if_id_is_not_in_list(){
		SmsPojo sms = mProvider.getMessage(NEGATIVE_ID);
		assertEquals(null, sms);
		sms = mProvider.getMessage(OVER_THE_TOP_ID);
		assertEquals(null, sms);
	}

	public void test_read_marks_unread_message_as_read(){
		mProvider.read(UNREAD_MESSAGE_ID);
		assertEquals(true, mProvider.getMessage(UNREAD_MESSAGE_ID).isRead());
	}

	public void test_read_leaves_read_message_as_it_was(){
		boolean b = mProvider.getMessage(READ_MESSAGE_ID).isRead();
		mProvider.read(READ_MESSAGE_ID);
		assertEquals(b, mProvider.getMessage(READ_MESSAGE_ID).isRead());
	}

	public void test_read_doesnt_throw_if_message_id_is_not_in_list(){
		mProvider.read(NEGATIVE_ID);
		mProvider.read(OVER_THE_TOP_ID);
		assertEquals(true, true);
	}

	public void test_delete_moves_message_to_action_messages_pool(){
		int size = mProvider.size();
		SmsPojo sms = mProvider.getMessage(READ_MESSAGE_ID);
		mProvider.delete(READ_MESSAGE_ID);
		assertEquals(size - 1, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(1, actionMessages.size());
		assertEquals(SmsAction.Deleted, actionMessages.get(sms));
	}

	public void test_delete_performs_actions_with_messages_before_deleting(){
		mProvider.delete(UNREAD_MESSAGE_ID);
		int size = mProvider.size();
		SmsPojo sms = mProvider.getMessage(READ_MESSAGE_ID);
		mProvider.delete(READ_MESSAGE_ID);
		assertEquals(size - 1, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(1, actionMessages.size());
		assertEquals(SmsAction.Deleted, actionMessages.get(sms));
	}

	public void test_delete_doesnt_throw_if_message_id_is_not_in_list(){
		int size = mProvider.size();
		mProvider.delete(NEGATIVE_ID);
		assertEquals(size, mProvider.size());
		mProvider.delete(OVER_THE_TOP_ID);
		assertEquals(size, mProvider.size());
	}

	public void test_bulk_delete_moves_message_to_action_messages_pool(){
		int size = mProvider.size();
		SmsPojo sms1 = mProvider.getMessage(READ_MESSAGE_ID), sms2 = mProvider.getMessage(UNREAD_MESSAGE_ID);
		mProvider.delete(new long[]{UNREAD_MESSAGE_ID, READ_MESSAGE_ID});
		assertEquals(size - 2, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(2, actionMessages.size());
		assertEquals(SmsAction.Deleted, actionMessages.get(sms1));
		assertEquals(SmsAction.Deleted, actionMessages.get(sms2));
	}

	public void test_bulk_delete_performs_actions_with_messages_before_deleting(){
		mProvider.delete(UNREAD_MESSAGE_ID);
		int size = mProvider.size();
		SmsPojo sms1 = mProvider.getMessage(READ_MESSAGE_ID), sms2 = mProvider.getMessage(UNREAD_MESSAGE_ID);
		mProvider.delete(new long[]{UNREAD_MESSAGE_ID, READ_MESSAGE_ID});
		assertEquals(size - 2, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(2, actionMessages.size());
		assertEquals(SmsAction.Deleted, actionMessages.get(sms1));
		assertEquals(SmsAction.Deleted, actionMessages.get(sms2));
	}

	public void test_bulk_delete_doesnt_throw_if_message_id_is_not_in_list(){
		int size = mProvider.size();
		mProvider.delete(new long[]{NEGATIVE_ID, OVER_THE_TOP_ID});
		assertEquals(size, mProvider.size());
	}

	public void test_delete_all_moves_all_messages_to_action_messages_pool(){
		int size = mProvider.size();
		mProvider.delete(new long[]{READ_MESSAGE_ID, UNREAD_MESSAGE_ID});
		assertEquals(size - 2, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(2, actionMessages.size());
		for(Object action : actionMessages.values().toArray()){
			assertEquals(SmsAction.Deleted, action);
		}
	}

	public void test_not_spam_moves_message_to_action_messages_pool(){
		int size = mProvider.size();
		SmsPojo sms = mProvider.getMessage(READ_MESSAGE_ID);
		mProvider.notSpam(READ_MESSAGE_ID);
		assertEquals(size - 1, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(1, actionMessages.size());
		assertEquals(SmsAction.MarkedAsNotSpam, actionMessages.get(sms));
	}

	public void test_not_spam_performs_actions_with_messages_before_deleting(){
		mProvider.delete(UNREAD_MESSAGE_ID);
		int size = mProvider.size();
		SmsPojo sms = mProvider.getMessage(READ_MESSAGE_ID);
		mProvider.notSpam(READ_MESSAGE_ID);
		assertEquals(size - 1, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(1, actionMessages.size());
		assertEquals(SmsAction.MarkedAsNotSpam, actionMessages.get(sms));
	}

	public void test_not_spam_doesnt_throw_if_message_id_is_not_in_list(){
		int size = mProvider.size();
		mProvider.notSpam(NEGATIVE_ID);
		assertEquals(size, mProvider.size());
		mProvider.notSpam(OVER_THE_TOP_ID);
		assertEquals(size, mProvider.size());
	}

	public void test_bulk_not_spam_moves_message_to_action_messages_pool(){
		int size = mProvider.size();
		SmsPojo sms1 = mProvider.getMessage(READ_MESSAGE_ID), sms2 = mProvider.getMessage(UNREAD_MESSAGE_ID);
		mProvider.notSpam(new long[]{UNREAD_MESSAGE_ID, READ_MESSAGE_ID});
		assertEquals(size - 2, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(2, actionMessages.size());
		assertEquals(SmsAction.MarkedAsNotSpam, actionMessages.get(sms1));
		assertEquals(SmsAction.MarkedAsNotSpam, actionMessages.get(sms2));
	}

	public void test_bulk_not_spam_performs_actions_with_messages_before_deleting(){
		mProvider.delete(UNREAD_MESSAGE_ID);
		int size = mProvider.size();
		SmsPojo sms1 = mProvider.getMessage(READ_MESSAGE_ID), sms2 = mProvider.getMessage(UNREAD_MESSAGE_ID);
		mProvider.notSpam(new long[]{UNREAD_MESSAGE_ID, READ_MESSAGE_ID});
		assertEquals(size - 2, mProvider.size());
		Hashtable<SmsPojo, SmsAction> actionMessages = mProvider.getActionMessages();
		assertEquals(2, actionMessages.size());
		assertEquals(SmsAction.MarkedAsNotSpam, actionMessages.get(sms1));
		assertEquals(SmsAction.MarkedAsNotSpam, actionMessages.get(sms2));
	}

	public void test_bulk_not_spam_doesnt_throw_if_message_id_is_not_in_list(){
		int size = mProvider.size();
		mProvider.notSpam(new long[]{NEGATIVE_ID, OVER_THE_TOP_ID});
		assertEquals(size, mProvider.size());
	}

	public void test_getUnreadCount_returns_unread_messages_count(){
		assertEquals(INITIAL_UNREAD_MESSAGES_COUNT, mProvider.getUnreadCount());
	}

	public void test_getUnreadCount_updates_unread_messages_count(){
		mProvider.read(UNREAD_MESSAGE_ID);
		assertEquals(INITIAL_UNREAD_MESSAGES_COUNT - 1, mProvider.getUnreadCount());
	}

	public void test_undo_returns_action_items_back(){
		int initialSize = mProvider.size();
		mProvider.delete(READ_MESSAGE_ID);
		mProvider.undo();
		assertEquals(initialSize, mProvider.size());
	}

	public void test_undo_returns_items_to_their_positions(){
		SmsPojo sms1 = mProvider.getMessage(READ_MESSAGE_ID), sms2 = mProvider.getMessage(UNREAD_MESSAGE_ID);
		mProvider.notSpam(new long[]{UNREAD_MESSAGE_ID, READ_MESSAGE_ID});
		mProvider.undo();
		assertEquals(sms1, mProvider.getMessage(READ_MESSAGE_ID));
		assertEquals(sms2, mProvider.getMessage(UNREAD_MESSAGE_ID));
	}

	public void test_performActions_clears_action_list(){
		SmsPojo sms1 = mProvider.getMessage(READ_MESSAGE_ID), sms2 = mProvider.getMessage(UNREAD_MESSAGE_ID);
		mProvider.notSpam(new long[]{UNREAD_MESSAGE_ID, READ_MESSAGE_ID});
		mProvider.performActions();
		assertEquals(0, mProvider.getActionMessages().size());
	}

	public void test_getIndex_gets_message_index(){
		SmsPojo sms = mProvider.getMessage(READ_MESSAGE_ID);
		assertEquals(READ_MESSAGE_ID, mProvider.getIndex(sms));
	}

	public void test_getIndex_return_minus_one_if_message_wasnt_found(){
		TestSmsPojo sms = new TestSmsPojo();
		assertEquals(-1, mProvider.getIndex(sms));
	}
}
