package com.automotivevirtus.adhoc;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.commands.LocalCommand;
import org.jivesoftware.smackx.xdata.Form;

public class Custom_Command extends LocalCommand  {

	@Override
	public boolean hasPermission(String arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isLastStage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancel() throws NoResponseException, XMPPErrorException,
			NotConnectedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void complete(Form arg0) throws NoResponseException,
			XMPPErrorException, NotConnectedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() throws NoResponseException, XMPPErrorException,
			NotConnectedException {
		// TODO Auto-generated method stub
		System.out.println("Command Executed.");
	}

	@Override
	public void next(Form arg0) throws NoResponseException, XMPPErrorException,
			NotConnectedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prev() throws NoResponseException, XMPPErrorException,
			NotConnectedException {
		// TODO Auto-generated method stub
		
	}

}
