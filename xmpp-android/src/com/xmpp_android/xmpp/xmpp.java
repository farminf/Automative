package com.xmpp_android.xmpp;

import java.io.IOException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import android.util.Log;




public class xmpp{
	
	private String serverAddress;			
	private String loginUser;		
	private String passwordUser;	
	private int serverPort;	
	
	boolean isConnected = false;
	
	private XMPPConnection connection;
	
	public xmpp(String serverAddress, int serverPort, String loginUser, String passwordUser){
		this.serverAddress = serverAddress;
		this.loginUser = loginUser;
		this.passwordUser = passwordUser;
		this.serverPort = serverPort;
	}
	
	public String logconnected;
	
	//Connecting to Server - should call this method in MainActivity
	public void connect() throws SmackException, IOException{
			
		Log.d("x", serverAddress + serverPort);		
		ConnectionConfiguration config = new ConnectionConfiguration(serverAddress , serverPort , "farmin.virtus.it");
		config.setReconnectionAllowed(true);
		config.setSecurityMode(SecurityMode.disabled);
		
		XMPPConnection xmppcon = new XMPPTCPConnection(config);
		
		//connection = new XMPPTCPConnection(config);

		try {
					Log.d("XX","here");
					xmppcon.connect();
					Log.d("XX","here2");
					xmppcon.login(loginUser, passwordUser);
					Presence presence = new Presence(Presence.Type.available);
					xmppcon.sendPacket(presence);
					
				}
				catch (final XMPPException e) {
					Log.d("XX", "Could not connect to Xmpp server.", e);
					
				}

				if (!connection.isConnected()) {
					Log.d("XX", "Could not connect to the Xmpp server.");
				}

				Log.i("", "Yey! We're connected to the Xmpp server!");

			}
}