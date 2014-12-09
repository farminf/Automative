package com.xmpp_android.xmpp;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import android.os.AsyncTask;
import android.util.Log;




public class xmppConnect{
	
	private String serverAddress;			
	private String loginUser;		
	private String passwordUser;	
	
	private XMPPConnection connection;
	
	public xmppConnect(String serverAddress, String loginUser, String passwordUser){
		this.serverAddress = serverAddress;
		this.loginUser = loginUser;
		this.passwordUser = passwordUser;
	}
	
	public String logconnected;
	
	//Connecting to Server - should call this method in MainActivity
	public void connect(){
		AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>(){			
			@Override
			protected Boolean doInBackground(Void... arg0){
				boolean isConnected = false;

				ConnectionConfiguration config = new ConnectionConfiguration(serverAddress , 5222 , "farmin.virtus.it");
				config.setReconnectionAllowed(true);
				config.setSecurityMode(SecurityMode.disabled);
				
				connection = new XMPPTCPConnection(config);

				XMPPConnectionListener connectionListener = new XMPPConnectionListener();
				connection.addConnectionListener(connectionListener);
			    //XMPPConnection.DEBUG_ENABLED = true;

				try{

					connection.connect();

					isConnected = true;
				} catch (IOException e){
				} catch (SmackException e){
				} catch (XMPPException e){
				}
				 if (isConnected = true){
				 }

				 return isConnected;
				
			}
		};		
		connectionThread.execute();
	}

	
	private void login(XMPPConnection connection, final String loginUser, final String passwordUser){
		try{
			connection.login(loginUser, passwordUser);
			
			Presence presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			
			
			} catch (NotConnectedException e){	
			// If is not connected, a timer is schedule and a it will try to reconnect
				new Timer().schedule(new TimerTask()
				{
				@Override
				public void run()
				{
						connect();				
				}
			}, 5 * 1000);			
		} catch (SaslException e){
		} catch (XMPPException e){
		} catch (SmackException e){
		} catch (IOException e){
		}
	}
	

	public class XMPPConnectionListener implements ConnectionListener{	
		@Override
		public void connected(final XMPPConnection connection){
			if(!connection.isAuthenticated())
				login(connection, loginUser, passwordUser);
		}
		@Override
		public void authenticated(XMPPConnection arg0){}
		@Override
		public void connectionClosed(){}
		@Override
		public void connectionClosedOnError(Exception arg0){}
		@Override
		public void reconnectingIn(int arg0){}
		@Override
		public void reconnectionFailed(Exception arg0){}
		@Override
		public void reconnectionSuccessful(){}
	}

	
}