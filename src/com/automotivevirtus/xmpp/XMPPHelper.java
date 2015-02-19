package com.automotivevirtus.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import android.app.Activity;

public class XMPPHelper extends Activity {

	static XMPPService xmppService = new XMPPService();

	Boolean isConnectedService;

	static String[] incomingMSG;

	// ********************************************************************
	// ********************************************************************

	public static void stopXMPPService() {

		xmppService.stopServiceManually();

	}

	public static void sendMessage(String addressedUser2, String sendmsg)
			throws NotConnectedException {
		// TODO Auto-generated method stub
		xmppService.chat(addressedUser2, sendmsg);
	}

	public static String[] getReceivedMessage() {
		String body = xmppService.incomingMSGBody;
		String sender = xmppService.incomingMSGFrom;
		incomingMSG[0] = sender;
		incomingMSG[1] = body;
		return incomingMSG;

	}

	public void addRosterEntry(String rosterNameToAdd,
			String rosterNickNameToAdd) throws Exception {
		// TODO Auto-generated method stub
		xmppService.createEntry(rosterNameToAdd, rosterNickNameToAdd);
	}

//	public static void createPubSubNode(String nodeName) throws NoResponseException,
//			XMPPErrorException, NotConnectedException {
//		xmppService.createPubSubNode(nodeName);
//	}

	public static void subscribeToNode(String nodeName)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {

		xmppService.subscribePubSubNode(nodeName);
	}

	public static void unSubscribeToNode(String nodeName)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {

		xmppService.unsubscribePubSubNode(nodeName);
	}

	public void publishToNode(String nodeName) throws NoResponseException,
			XMPPErrorException, NotConnectedException {

		xmppService.publishToPubSubNode(nodeName);
	}

	public void sendAdhocCommand(String username, String command)
			throws XMPPException, SmackException {

		xmppService.sendAdHocCommands(username, command);
	}

}
