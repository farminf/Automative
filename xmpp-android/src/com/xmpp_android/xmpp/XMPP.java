package com.xmpp_android.xmpp;

import java.io.IOException;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.commands.AdHocCommandManager;
import org.jivesoftware.smackx.commands.RemoteCommand;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import com.xmpp_android.adhoc.Custom_Command;
import com.xmpp_android.adhoc.Custom_Command_Send;

import android.os.AsyncTask;
import android.util.Log;

public class XMPP {

	private final static String TAG = "ServiceXMPP";

	XMPPConnectionListener connectionListener;

	// Login Parameters
	private String serverAddress;
	private String loginUser;
	private String passwordUser;

	private XMPPConnection connection;

	ChatManager chatmanager;

	private String serverDomain;

	private boolean isConnected = false;

	// pubsub Parameters
	PubSubManager pubsubmgr;
	LeafNode Createdleaf;

	// ad-hoc parameter
	int timeout = 5000;

	// Service Methods
	// *********************************************************************

	// *******************************************************************
	// Defining XMPP Class
	public XMPP(String serverAddress, String loginUser, String passwordUser,
			String domain) {
		this.serverAddress = serverAddress;
		this.loginUser = loginUser;
		this.passwordUser = passwordUser;
		this.serverDomain = domain;
	}

	public String logconnected;

	// Connecting to Server - should call this method in MainActivity
	public Boolean connect() {
		Boolean retVal = false;
		AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				// pdia = new ProgressDialog();
				// pdia.setMessage("Loading...");
				// pdia.show();

			}

			@Override
			protected Boolean doInBackground(Void... arg0) {

				isConnected = false;
				ConnectionConfiguration config = new ConnectionConfiguration(
						serverAddress, 5222, serverDomain);
				config.setReconnectionAllowed(true);
				config.setSecurityMode(SecurityMode.disabled);
				config.setDebuggerEnabled(true);

				connection = new XMPPTCPConnection(config);

				connectionListener = new XMPPConnectionListener();
				connection.addConnectionListener(connectionListener);
				// XMPPConnection.DEBUG_ENABLED = true;

				try {
					connection.connect();
					isConnected = true;

				} catch (IOException e) {
					Log.e("error", "IO Exception error : " + e.getMessage());
				} catch (SmackException e) {
					Log.e("error", "Smack Exception error : " + e.getMessage());
				} catch (XMPPException e) {
					Log.e("error", "XMPP Exception error : " + e.getMessage());
				}
				return isConnected;

			}

			@Override
			protected void onPostExecute(Boolean result) {
				// print username
				String connectedusername = connection.getUser();
				Log.d("Connected Username", connectedusername);

				// Listener for Chat, if someone sends msg (Start Session by
				// other user)
				chatmanager = ChatManager.getInstanceFor(connection);
				chatmanager.addChatListener(new ChatManagerListener() {
					@Override
					public void chatCreated(Chat chat, boolean createdLocally) {
						if (!createdLocally)
							chat.addMessageListener(new MyMessageListener());
					}
				});
				// Printing all Roster entries
				Roster roster = connection.getRoster();
				Collection<RosterEntry> entries = roster.getEntries();
				for (RosterEntry entry : entries) {
					System.out.println(String.format(
							"Buddy:%1$s - Status:%2$s", entry.getName(),
							entry.getStatus()));
				}
				// Roster Listener,if other users's presences changed , it'll
				// print
				roster.addRosterListener(new RosterListener() {

					@Override
					public void presenceChanged(Presence presence) {
						// TODO Auto-generated method stub
						System.out.println("Presence changed: "
								+ presence.getFrom() + " " + presence);
					}

					@Override
					public void entriesUpdated(Collection<String> arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void entriesDeleted(Collection<String> arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void entriesAdded(Collection<String> arg0) {
						// TODO Auto-generated method stub

					}
				});

				// PubSub Node Methods
				// Create a pubsub manager using an existing XMPPConnection
				pubsubmgr = new PubSubManager(connection);

				// Register Ad-hoc commands
				receiveAdHocCommands();

			}

			@Override
			protected void onCancelled() {
				// TODO Auto-generated method stub
				super.onCancelled();
			}

		};

		connectionThread.execute();

		// Checking if connection was successful or not
		try {
			retVal = connectionThread.get();
			System.out.println(" result is : " + retVal);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;

	}

	// login in server
	private void login(XMPPConnection connection, final String loginUser,
			final String passwordUser) {
		try {
			connection.login(loginUser, passwordUser);
			setStatus(true);

		} catch (NotConnectedException e) {
			// If is not connected, a timer is schedule and a it will try to
			// reconnect
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					connect();
				}
			}, 5 * 1000);
		} catch (SaslException e) {
		} catch (XMPPException e) {
		} catch (SmackException e) {
		} catch (IOException e) {
		}
	}

	// Set Presence or Status of user
	private void setStatus(boolean available) throws NotConnectedException {
		// TODO Auto-generated method stub
		if (available) {
			// connection.sendPacket(new Presence(Presence.Type.available));
			Presence presence = new Presence(Presence.Type.available);
			presence.setStatus("What's up? (presence status)");
			connection.sendPacket(presence);
		}

		else
			connection.sendPacket(new Presence(Presence.Type.unavailable));
	}

	// listener for keeping connection connect
	public class XMPPConnectionListener implements ConnectionListener {
		@Override
		public void connected(final XMPPConnection connection) {
			if (!connection.isAuthenticated())
				login(connection, loginUser, passwordUser);
		}

		@Override
		public void authenticated(XMPPConnection arg0) {
		}

		@Override
		public void connectionClosed() {
			Log.d(TAG,
					" [MyConnectionListener] The connection was closed normally.");
		}

		@Override
		public void connectionClosedOnError(Exception e) {
			Log.d(TAG,
					" [MyConnectionListener] The connection was closed due to an exception. Error:"
							+ e.getMessage());

		}

		@Override
		public void reconnectingIn(int sec) {
			Log.d(TAG,
					" [MyConnectionListener] The connection will retry to reconnect in "
							+ sec + " seconds.");

		}

		@Override
		public void reconnectionFailed(Exception e) {
			Log.d(TAG,
					" [MyConnectionListener] An attempt to connect to the server has failed. Error:"
							+ e.getMessage());

		}

		@Override
		public void reconnectionSuccessful() {
			Log.d(TAG,
					" [MyConnectionListener] The connection has reconnected successfully to the server.");

		}
	}

	// Sending message to addressed user
	public void chat(String AddressedUser, String sendmsg)
			throws NotConnectedException {
		// Create username whom we want to send a message
		String userToSend = AddressedUser + "@" + serverDomain;

		ChatManager chatmanager = ChatManager.getInstanceFor(connection);
		Chat newChat = chatmanager.createChat(userToSend,
				new MessageListener() {
					@Override
					public void processMessage(Chat chat, Message message) {
						// TODO Auto-generated method stub
						System.out.println("Received message: " + message);

					}
				});

		try {
			newChat.sendMessage(sendmsg);
		} catch (XMPPException e) {
			System.out.println("Error Delivering block");
		}

	}

	// Adding to Roster
	public void createEntry(String user, String nickname) throws Exception {
		String rosterUsernameToAdd = user + "@" + serverDomain;
		System.out.println(String.format(
				"Creating entry for buddy '%1$s' with name %2$s",
				rosterUsernameToAdd, nickname));
		Roster roster = connection.getRoster();
		roster.createEntry(rosterUsernameToAdd, nickname, null);
	}

	// Disconnect from server
	public void disconnect() {
		if (connection != null && connection.isConnected()) {

			// connectionListener.connectionClosed();
			connection.removeConnectionListener(connectionListener);
			Presence unavailablePresence = new Presence(
					Presence.Type.unavailable);
			try {
				// Thread.sleep(5000);
				connection.disconnect(unavailablePresence);
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("Disconnect", "cannot disconnet: " + e.getMessage());
			}

		} else {
			Log.d("Error-f",
					"You are not connected to server so how you want to disconnect it?!");
		}
	}

	// ******************* Publish / Subscribe Functions
	// ***********************************************
	// public void createPubSubInstantNode() throws NoResponseException,
	// XMPPErrorException, NotConnectedException {
	// // Create the node
	// LeafNode leaf = pubsubmgr.createNode();
	// //return leaf;
	// }

	public void createPubSubNode(String nodeName) throws NoResponseException,
			XMPPErrorException, NotConnectedException {
		// Create the node
		ConfigureForm form = new ConfigureForm(FormType.submit);
		form.setAccessModel(AccessModel.open);
		form.setDeliverPayloads(false);
		form.setNotifyRetract(true);
		form.setPersistentItems(true);
		form.setPublishModel(PublishModel.open);
		Createdleaf = (LeafNode) pubsubmgr.createNode(nodeName, form);

		// return leaf;
	}

	@SuppressWarnings("unchecked")
	public void publishToPubSubNode(String nodeName)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {
		// Get the node
		LeafNode node = pubsubmgr.getNode(nodeName);

		// Publish an Item, let service set the id
		// node.send(new Item());

		// Publish an Item with the specified id
		// node.send(new Item("123abc"));

		// Publish an Item with payload
		node.send(new PayloadItem("test" + System.currentTimeMillis(),
				new SimplePayload("book", "pubsub:test:book", "Two Towers")));

	}

	public void subscribePubSubNode(String nodeName)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {

		// Get the node
		LeafNode node = pubsubmgr.getNode(nodeName);
		node.addItemEventListener(new ItemEventListener<Item>() {

			@Override
			public void handlePublishedItems(ItemPublishEvent<Item> items) {
				// TODO Auto-generated method stub

			}
		});

		node.subscribe(connection.getUser());
		Log.d("subscribe", " [pubsub] User " + connection.getUser()
				+ " subscribed successfully to node " + node);
	}

	// *********************************************
	// *********** Ad-Hoc Commands Functions********
	// one for receiving , one for sending
	// **********************************************

	private void receiveAdHocCommands() {
		AdHocCommandManager commandManager = AdHocCommandManager
				.getAddHocCommandsManager(connection);
//		commandManager.registerCommand("first_custom_command",
//				"First Custom Command", Custom_Command.class);
		commandManager.registerCommand("send_msg_command",
		"Send Message Command", Custom_Command_Send.class);
	}

	// Send AdHoc command Function
	public void sendAdHocCommands() throws XMPPException, SmackException {
		DiscoverInfo discoInfo = null;
		ServiceDiscoveryManager disco = ServiceDiscoveryManager
				.getInstanceFor(connection);
		try {
			discoInfo = disco.discoverInfo("receiver1@farmin.virtus.it/Smack");
		} catch (XMPPException e1) {
			e1.printStackTrace();
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Search the receiver commands and send one of them

		AdHocCommandManager commandManager = AdHocCommandManager
				.getAddHocCommandsManager(connection);
		DiscoverItems cmds = null;
		// Retrieves all the commands provided by the receiver
		cmds = commandManager
				.discoverCommands("receiver1@farmin.virtus.it/Smack");
		String commandName = null;

		// Verify the present command
		for (org.jivesoftware.smackx.disco.packet.DiscoverItems.Item item : cmds
				.getItems()) {
			if (item.getNode().compareTo("first_custom_command") == 0) {
				commandName = item.getNode();
			}
		}
		RemoteCommand remoteCommand = null;

		// Retrieve the command to be executed
		if (commandName != null) {
			remoteCommand = commandManager.getRemoteCommand(
					"receiver1@farmin.virtus.it/Smack", commandName);
		}
		remoteCommand.execute();
		System.out.println("Command executed. Wait " + timeout / 1000
				+ " seconds...\n");
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}