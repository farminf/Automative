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
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import android.os.AsyncTask;
import android.util.Log;

public class XMPP {

	// Handler that receives messages from the thread

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
	public void createEntry(String user, String name) throws Exception {
		System.out.println(String.format(
				"Creating entry for buddy '%1$s' with name %2$s", user, name));
		Roster roster = connection.getRoster();
		roster.createEntry(user, name, null);
	}

	// Disconnect from server
	public void disconnect() throws NotConnectedException {
		if (connection != null && connection.isConnected()) {

			setStatus(false);
			connection.removeConnectionListener(connectionListener);
			// ChatManager.getInstanceFor(connection).removeChatListener(
			// (ChatManagerListener) chatmanager);
			connectionListener.connectionClosed();
			connection.disconnect();
		}
	}

}