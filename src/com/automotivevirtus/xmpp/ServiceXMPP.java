//package com.automotivevirtus.xmpp;
//
//import static java.util.concurrent.TimeUnit.SECONDS;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//
//import org.apache.harmony.javax.security.sasl.SaslException;
//import org.jivesoftware.smack.Chat;
//import org.jivesoftware.smack.ChatManager;
//import org.jivesoftware.smack.ChatManagerListener;
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.ConnectionListener;
//import org.jivesoftware.smack.MessageListener;
//import org.jivesoftware.smack.Roster;
//import org.jivesoftware.smack.RosterEntry;
//import org.jivesoftware.smack.RosterListener;
//import org.jivesoftware.smack.SmackAndroid;
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
//import org.jivesoftware.smack.SmackException.NoResponseException;
//import org.jivesoftware.smack.SmackException.NotConnectedException;
//import org.jivesoftware.smack.XMPPException.XMPPErrorException;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//import org.jivesoftware.smackx.commands.AdHocCommandManager;
//import org.jivesoftware.smackx.commands.RemoteCommand;
//import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
//import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
//import org.jivesoftware.smackx.disco.packet.DiscoverItems;
//import org.jivesoftware.smackx.pubsub.AccessModel;
//import org.jivesoftware.smackx.pubsub.ConfigureForm;
//import org.jivesoftware.smackx.pubsub.FormType;
//import org.jivesoftware.smackx.pubsub.Item;
//import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
//import org.jivesoftware.smackx.pubsub.LeafNode;
//import org.jivesoftware.smackx.pubsub.PayloadItem;
//import org.jivesoftware.smackx.pubsub.PubSubManager;
//import org.jivesoftware.smackx.pubsub.PublishModel;
//import org.jivesoftware.smackx.pubsub.SimplePayload;
//import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
//
//import com.automotivevirtus.R;
//import com.automotivevirtus.adhoc.Custom_Command;
//import com.automotivevirtus.adhoc.Custom_Command_Send;
//import com.automotivevirtus.location.LocationService;
//import com.automotivevirtus.xmpp.XMPPService.XMPPConnectionListener;
//
//import android.app.NotificationManager;
//import android.app.ProgressDialog;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.os.Process;
//import android.preference.PreferenceManager;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
//public class ServiceXMPP extends Service {
//
////	public ServiceXMPP() {
////
////	}
//
//	
//
//	private static SmackAndroid asmk = null;
//
//	SharedPreferences sharedPref;
//
//	int notificationID = 111;
//
//	String username;
//	String password;
//	String serveraddress;
//	static String domain;
//	int serverport = 5222;
//
//	// Schedule job parameters
//	private final ScheduledExecutorService scheduler = Executors
//			.newScheduledThreadPool(1);
//	static ScheduledFuture<?> senderHandle;
//	// Maybe this is correct
//	// static ScheduledFuture senderHandle;
//
//	static NotificationManager mNotificationManager;
//
//	public ProgressDialog progressDialog;
//
//	LocalBroadcastManager mLocalBroadcastManager;
//
//	public Boolean isConnectedService = false;
//
//	public String[] incomingMSG;
//
//	private final static String TAG = "ServiceXMPP";
//
//	XMPPConnectionListener connectionListener;
//
//	private static XMPPConnection connection;
//
//	static ChatManager chatmanager;
//
//	public boolean isConnectedXMPP = false;
//
//	// pubsub Parameters
//	PubSubManager pubsubmgr;
//	LeafNode Createdleaf;
//
//	// ad-hoc parameter
//	int timeout = 5000;
//
//	public String logconnected;
//
//	// Incoming MSG parameter
//	public String incomingMSGBody;
//	public String incomingMSGFrom;
//	// public String[] incomingMSG;
//
//	Context context;
//
//	LocationService currentLocation;
//	double currentLatitude;
//	double currentLongitude;
//	String curLat;
//	String curLong;
//
//	// ************************************************************
//	// ************************************************************
//	// ************************************************************
//	// ************************************************************
//	// ************************************************************
//	@Override
//	@Deprecated
//	public void onStart(Intent intent, int startId) {
//		// TODO Auto-generated method stub
//		//super.onStart(intent, startId);
//		createBroadcastMessage("ShowProgressBar");
//		Log.d("Broadcast", "Broadcasting message in start service");
//
//	}
//	
//	
//	@Override
//	public void onCreate() {
//		// TODO Auto-generated method stub
//		// run service on seperate thread	
//		asmk = SmackAndroid.init(ServiceXMPP.this);
//		domain = getString(R.string.domain_name);
//		getSharedPreference();
//		Log.d("service", "before connect in servive");
//		
//		HandlerThread thread = new HandlerThread("ServiceStartArguments",
//				Process.THREAD_PRIORITY_BACKGROUND);
//				thread.start();
//				
//		isConnectedService = XMPPconnect();
//
//		if (isConnectedService) {
//			// Schedule function call
//			sendForAnHour();
//
//			createBroadcastMessage("XMPPConnected");
//			createBroadcastMessage("DismissProgressBar");
//			Log.d("Broadcast",
//					"Broadcasting DismissProgressBar message because we are connected");
//
//			// Notification
//			createNotificationIcon();
//
//		} else {
//
//			Log.d("Broadcast", "you're not connected , in else of handle");
//			createBroadcastMessage("NoXMPPDialog");
//		}
//
//		//super.onCreate();
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		// TODO Auto-generated method stub
//		createBroadcastMessage("ShowProgressBar");
//		Log.d("Broadcast", "Broadcasting message in start service");
//		//super.onStartCommand(intent, flags, startId);
//		return START_STICKY;
//	}
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		createBroadcastMessage("DismissProgressBar");
//		Log.d("Broadcast",
//				"Broadcasting DismissProgressBar message in destroy service");
//		stopServiceManually();
//
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	// ************************************************************
//	// ************************************************************
//
//	private void createBroadcastMessage(String action) {
//		mLocalBroadcastManager = LocalBroadcastManager
//				.getInstance(getApplicationContext());
//		Intent broadcastIntent = new Intent();
//		broadcastIntent.setAction(action);
//		// broadcastIntent.putExtra(whateverExtraData you need to pass back);
//		sendBroadcast(broadcastIntent);
//	}
//
//	// Function to do something periodically
//	public void sendForAnHour() {
//
//		final Runnable sender = new Runnable() {
//			public void run() {
//				getCurrentLocation();
//				Log.d("Current Location in Service", curLat + " " + curLong);
//				System.out.println("sent");
//				Log.d("F", "sendF");
//			}
//		};
//
//		senderHandle = scheduler.scheduleAtFixedRate(sender, 10, 10, SECONDS);
//
//		scheduler.schedule(new Runnable() {
//			public void run() {
//				senderHandle.cancel(true);
//			}
//		}, 60 * 60, SECONDS);
//	}
//
//	// Function for cancel periodic job
//	public void sendForAnHourCancel() {
//
//		senderHandle.cancel(true);
//		System.out.println("schedule job cancelled");
//
//	}
//
//	public void stopServiceManually() {
//		// Disconnecting from Server
//		try {
//			disconnectFromOpenFireServer();
//		} catch (NotConnectedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Log.d("Disconnection Error", e.getMessage());
//
//		}
//		// Deleting all notifications
//		try {
//			cancellAllNotifications();
//		} catch (NotConnectedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Log.d("Notification Error", e.getMessage());
//		}
//		// Cancel periodic job
//		// sendForAnHourCancel();
//	}
//
//	public void disconnectFromOpenFireServer() throws NotConnectedException {
//		// TODO Auto-generated method stub
//		disconnect();
//		if (asmk != null) {
//			asmk.onDestroy();
//			asmk = null;
//		}
//	}
//
//	public void cancellAllNotifications() throws NotConnectedException {
//		// TODO Auto-generated method stub
//		mNotificationManager.cancelAll();
//	}
//
//	private void getCurrentLocation() {
//		// TODO Auto-generated method stub
//		if (currentLocation.canGetLocation()) {
//			currentLatitude = currentLocation.getLatitude();
//			currentLongitude = currentLocation.getLongitude();
//
//			curLat = String.valueOf(currentLatitude);
//			curLong = String.valueOf(currentLongitude);
//
//			Log.v("Location", curLat + " " + curLong);
//
//		} else {
//			// GPS or Network no available and ask user to turn on in setting
//			currentLocation.showSettingsAlert();
//		}
//
//	}
//
//	private void createNotificationIcon() {
//		// TODO Auto-generated method stub
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//				this).setSmallIcon(R.drawable.ic_launcher)
//				.setContentTitle("XMPP Service")
//				.setContentText("You're Connected to Server");
//		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		mNotificationManager.notify(notificationID, mBuilder.build());
//
//	}
//
//	private void getSharedPreference() {
//		// TODO Auto-generated method stub
//		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//		username = sharedPref.getString("textUsername", "android");
//		password = sharedPref.getString("textPassword", "android");
//		serveraddress = sharedPref
//				.getString("textServerAddress", "192.168.1.1");
//		domain = getString(R.string.domain_name);
//		Log.d("Settings in Service", username + "," + password + ","
//				+ serveraddress + ":" + serverport + "," + domain);
//	}
//
//	public Boolean XMPPconnect() {
//		Boolean retVal = false;
//
//		AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
//
//			@Override
//			protected void onProgressUpdate(Void... values) {
//				// TODO Auto-generated method stub
//
//				// super.onProgressUpdate(values);
//			}
//
//			@Override
//			protected void onCancelled(Boolean result) {
//				// TODO Auto-generated method stub
//				super.onCancelled(result);
//				Log.e("error", "onCancelled Async: ");
//
//			}
//
//			@Override
//			protected void onPreExecute() {
//				// TODO Auto-generated method stub
//				// super.onPreExecute();
//
//			}
//
//			@Override
//			protected Boolean doInBackground(Void... arg0) {
//
//				// isConnectedXMPP = false;
//				ConnectionConfiguration config = new ConnectionConfiguration(
//						serveraddress, serverport, domain);
//				config.setReconnectionAllowed(true);
//				config.setSecurityMode(SecurityMode.disabled);
//				config.setDebuggerEnabled(true);
//
//				connection = new XMPPTCPConnection(config);
//
//				connectionListener = new XMPPConnectionListener();
//				connection.addConnectionListener(connectionListener);
//				// XMPPConnection.DEBUG_ENABLED = true;
//
//				try {
//					Log.d("XMPP Service", "before connect");
//					connection.connect();
//					isConnectedXMPP = true;
//					Log.d("XMPP Service", "after connect");
//
//				} catch (IOException e) {
//					Log.e("error", "IO Exception error : " + e.getMessage());
//					isConnectedXMPP = false;
//
//				} catch (SmackException e) {
//					Log.e("error", "Smack Exception error : " + e.getMessage());
//					isConnectedXMPP = false;
//
//				} catch (XMPPException e) {
//					Log.e("error", "XMPP Exception error : " + e.getMessage());
//					isConnectedXMPP = false;
//
//				}
//
//				return isConnectedXMPP;
//
//			}
//
//			@Override
//			protected void onPostExecute(Boolean result) {
//				// print result
//				// System.out.println(" result in post execute is : " + result);
//
//				if (result) {
//					// print username
//					String connectedusername = connection.getUser();
//					Log.d("Connected Username", connectedusername);
//
//					// Listener for Chat, if someone sends msg (Start Session by
//					// other user)
//					chatmanager = ChatManager.getInstanceFor(connection);
//					chatmanager.addChatListener(new ChatManagerListener() {
//						@Override
//						public void chatCreated(Chat chat,
//								boolean createdLocally) {
//							if (!createdLocally)
//								chat.addMessageListener(new MyMessageListener() {
//
//									@Override
//									public void processMessage(Chat chat,
//											Message message) {
//										// TODO Auto-generated method stub
//										super.processMessage(chat, message);
//										if (message.getBody() != null) {
//											// String [] MSG =
//											// getIncomingMessage(message);
//											getIncomingMessage(message);
//											createBroadcastMessage("receivedMessage");
//											Log.d("ProcessMessage",
//													"message.body is NOT null");
//										} else {
//											Log.d("ProcessMessage",
//													"message.body is null");
//
//										}
//									}
//
//								});
//						}
//					});
//					// Printing all Roster entries
//					Roster roster = connection.getRoster();
//					Collection<RosterEntry> entries = roster.getEntries();
//					for (RosterEntry entry : entries) {
//						System.out.println(String.format(
//								"Buddy:%1$s - Status:%2$s", entry.getName(),
//								entry.getStatus()));
//					}
//					// Roster Listener,if other users's presences changed ,
//					// it'll
//					// print
//					roster.addRosterListener(new RosterListener() {
//
//						@Override
//						public void presenceChanged(Presence presence) {
//							// TODO Auto-generated method stub
//							System.out.println("Presence changed: "
//									+ presence.getFrom() + " " + presence);
//						}
//
//						@Override
//						public void entriesUpdated(Collection<String> arg0) {
//							// TODO Auto-generated method stub
//
//						}
//
//						@Override
//						public void entriesDeleted(Collection<String> arg0) {
//							// TODO Auto-generated method stub
//
//						}
//
//						@Override
//						public void entriesAdded(Collection<String> arg0) {
//							// TODO Auto-generated method stub
//
//						}
//					});
//
//					// PubSub Node Methods
//					// Create a pubsub manager using an existing XMPPConnection
//					pubsubmgr = new PubSubManager(connection);
//
//					// Register Ad-hoc commands
//					try {
//						// Process root = Runtime.getRuntime().exec("su");
//						receiveAdHocCommands();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} else {
//					Log.d("if Error", "Connection unsuccessfull");
//				}
//			}
//
//		};
//
//		connectionThread.execute();
//
//		// Checking if connection was successful or not
//		try {
//			retVal = connectionThread.get();
//			System.out.println(" result is : " + retVal);
//		} catch (InterruptedException | ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Log.e("error", "connection error : " + e.getMessage());
//
//		}
//		return retVal;
//
//	}
//
//	// login in server
//	private void login(XMPPConnection connection, final String loginUser,
//			final String passwordUser) {
//		try {
//			connection.login(username, password);
//			setStatus(true);
//
//		} catch (NotConnectedException e) {
//			// If is not connected, a timer is schedule and a it will try to
//			// reconnect
//			new Timer().schedule(new TimerTask() {
//				@Override
//				public void run() {
//					XMPPconnect();
//				}
//			}, 5 * 1000);
//		} catch (SaslException e) {
//			Log.e("error", "connection error : " + e.getMessage());
//		} catch (XMPPException e) {
//			Log.e("error", "connection error : " + e.getMessage());
//		} catch (SmackException e) {
//			Log.e("error", "connection error : " + e.getMessage());
//		} catch (IOException e) {
//			Log.e("error", "connection error : " + e.getMessage());
//		}
//	}
//
//	// Set Presence or Status of user
//	public void setStatus(boolean available) throws NotConnectedException {
//		// TODO Auto-generated method stub
//		if (available) {
//			// connection.sendPacket(new Presence(Presence.Type.available));
//			Presence presence = new Presence(Presence.Type.available);
//			presence.setStatus("What's up? (presence status)");
//			connection.sendPacket(presence);
//		}
//
//		else
//			connection.sendPacket(new Presence(Presence.Type.unavailable));
//	}
//
//	// listener for keeping connection connect
//	public class XMPPConnectionListener implements ConnectionListener {
//		@Override
//		public void connected(final XMPPConnection connection) {
//			if (!connection.isAuthenticated())
//				login(connection, username, password);
//		}
//
//		@Override
//		public void authenticated(XMPPConnection arg0) {
//		}
//
//		@Override
//		public void connectionClosed() {
//			Log.d(TAG,
//					" [MyConnectionListener] The connection was closed normally.");
//		}
//
//		@Override
//		public void connectionClosedOnError(Exception e) {
//			Log.d(TAG,
//					" [MyConnectionListener] The connection was closed due to an exception. Error:"
//							+ e.getMessage());
//
//		}
//
//		@Override
//		public void reconnectingIn(int sec) {
//			Log.d(TAG,
//					" [MyConnectionListener] The connection will retry to reconnect in "
//							+ sec + " seconds.");
//
//		}
//
//		@Override
//		public void reconnectionFailed(Exception e) {
//			Log.d(TAG,
//					" [MyConnectionListener] An attempt to connect to the server has failed. Error:"
//							+ e.getMessage());
//
//		}
//
//		@Override
//		public void reconnectionSuccessful() {
//			Log.d(TAG,
//					" [MyConnectionListener] The connection has reconnected successfully to the server.");
//
//		}
//	}
//
//	// Sending message to addressed user
//	public void chat(String AddressedUser, String sendmsg)
//			throws NotConnectedException {
//		// Create username whom we want to send a message
//		String userToSend = AddressedUser + "@" + domain;
//		chatmanager = ChatManager.getInstanceFor(connection);
//		Chat newChat = chatmanager.createChat(userToSend,
//				new MessageListener() {
//					@Override
//					public void processMessage(Chat chat, Message message) {
//						// TODO Auto-generated method stub
//						System.out.println("Received message is: " + message);
//						// super.processMessage(chat, message);
//						if (message.getBody() != null) {
//							// String [] MSG = getIncomingMessage(message);
//							// getIncomingMessage(message);
//							// createBroadcastMessage("receivedMessage");
//							Log.d("ProcessMessage", "message.body is NOT null");
//						} else {
//							Log.d("ProcessMessage", "message.body is null");
//
//						}
//
//					}
//				});
//
//		try {
//
//			newChat.sendMessage(sendmsg);
//
//		} catch (XMPPException e) {
//			System.out.println("Error Delivering block");
//		}
//
//	}
//
//	// Adding to Roster
//	public void createEntry(String user, String nickname) throws Exception {
//		String rosterUsernameToAdd = user + "@" + domain;
//		System.out.println(String.format(
//				"Creating entry for buddy '%1$s' with name %2$s",
//				rosterUsernameToAdd, nickname));
//		Roster roster = connection.getRoster();
//		roster.createEntry(rosterUsernameToAdd, nickname, null);
//	}
//
//	// Disconnect from server
//	public void disconnect() {
//		if (connection != null && connection.isConnected()) {
//
//			// connectionListener.connectionClosed();
//			connection.removeConnectionListener(connectionListener);
//			Presence unavailablePresence = new Presence(
//					Presence.Type.unavailable);
//			try {
//				// Thread.sleep(5000);
//				connection.disconnect(unavailablePresence);
//			} catch (NotConnectedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				Log.d("Disconnect", "cannot disconnet: " + e.getMessage());
//			}
//
//		} else {
//			Log.d("Error-f",
//					"You are not connected to server so how you want to disconnect it?!");
//		}
//	}
//
//	public void getIncomingMessage(Message message) {
//		// TODO Auto-generated method stub
//		System.out.println(String.format(
//				"this is what I get , body : %1$s , from: %2$s",
//				message.getBody(), message.getFrom()));
//		incomingMSGBody = message.getBody().toString();
//		incomingMSGFrom = message.getFrom().toString();
//		// incomingMSG[0] = incomingMSGFrom;
//		// incomingMSG[1] = incomingMSGBody;
//		Log.d("Incoming msg", "inFunction");
//		// return incomingMSG;
//	}
//
//	private void receiveAdHocCommands() throws IOException {
//		// granting root permission for app
//		// Process root = Runtime.getRuntime().exec("su");
//
//		AdHocCommandManager commandManager = AdHocCommandManager
//				.getAddHocCommandsManager(connection);
//
//		commandManager.registerCommand("first_custom_command",
//				"First Custom Command", Custom_Command.class);
//		commandManager.registerCommand("send_msg_command",
//				"Send Message Command", Custom_Command_Send.class);
//	}
//
//	// Send AdHoc command Function
//	public void sendAdHocCommands(String username, String command)
//			throws XMPPException, SmackException {
//
//		String usernameToSend = username + "farmin.virtus.it/Smack";
//		DiscoverInfo discoInfo = null;
//		ServiceDiscoveryManager disco = ServiceDiscoveryManager
//				.getInstanceFor(connection);
//		try {
//			discoInfo = disco.discoverInfo(usernameToSend);
//		} catch (XMPPException e1) {
//			e1.printStackTrace();
//		} catch (NoResponseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NotConnectedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// Search the receiver commands and send one of them
//		AdHocCommandManager commandManager = AdHocCommandManager
//				.getAddHocCommandsManager(connection);
//		DiscoverItems cmds = null;
//
//		// Retrieves all the commands provided by the receiver
//		cmds = commandManager.discoverCommands(usernameToSend);
//		String commandName = null;
//
//		// Verify the present command
//		for (org.jivesoftware.smackx.disco.packet.DiscoverItems.Item item : cmds
//				.getItems()) {
//			if (item.getNode().compareTo(command) == 0) {
//				commandName = item.getNode();
//			}
//		}
//		RemoteCommand remoteCommand = null;
//
//		// Retrieve the command to be executed
//		if (commandName != null) {
//			remoteCommand = commandManager.getRemoteCommand(usernameToSend,
//					commandName);
//		}
//		remoteCommand.execute();
//		System.out.println("Command executed. Wait " + timeout / 1000
//				+ " seconds...\n");
//		try {
//			Thread.sleep(timeout);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	// ******************* Publish / Subscribe Functions
//	// ***********************************************
//	// public void createPubSubInstantNode() throws NoResponseException,
//	// XMPPErrorException, NotConnectedException {
//	// // Create the node
//	// LeafNode leaf = pubsubmgr.createNode();
//	// //return leaf;
//	// }
//
//	public void createPubSubNode(String nodeName) throws NoResponseException,
//			XMPPErrorException, NotConnectedException {
//		// Create the node
//		ConfigureForm form = new ConfigureForm(FormType.submit);
//		form.setAccessModel(AccessModel.open);
//		form.setDeliverPayloads(false);
//		form.setNotifyRetract(true);
//		form.setPersistentItems(true);
//		form.setPublishModel(PublishModel.open);
//		Createdleaf = (LeafNode) pubsubmgr.createNode(nodeName, form);
//
//		// return leaf;
//	}
//
//	@SuppressWarnings("unchecked")
//	public void publishToPubSubNode(String nodeName)
//			throws NoResponseException, XMPPErrorException,
//			NotConnectedException {
//		// Get the node
//		LeafNode node = pubsubmgr.getNode(nodeName);
//
//		// Publish an Item, let service set the id
//		// node.send(new Item());
//
//		// Publish an Item with the specified id
//		// node.send(new Item("123abc"));
//
//		// Publish an Item with payload
//		node.send(new PayloadItem("test" + System.currentTimeMillis(),
//				new SimplePayload("book", "pubsub:test:book", "Two Towers")));
//
//	}
//
//	public void subscribePubSubNode(String nodeName)
//			throws NoResponseException, XMPPErrorException,
//			NotConnectedException {
//
//		// Get the node
//		LeafNode node = pubsubmgr.getNode(nodeName);
//		node.addItemEventListener(new ItemEventListener<Item>() {
//
//			@Override
//			public void handlePublishedItems(ItemPublishEvent<Item> items) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//
//		node.subscribe(connection.getUser());
//		Log.d("subscribe", " [pubsub] User " + connection.getUser()
//				+ " subscribed successfully to node " + node);
//	}
//
//}
