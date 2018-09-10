package Blockchain;

import java.io.Serializable;

import Managers.DebugManager;
import Security.HashManager;
import Security.SignatureManager;

public class Parcel implements Serializable
{
	
	
	private static final long serialVersionUID = 1L;
	protected String hash = "";
	protected String signature="";
	protected String public_key = "";
	protected SignatureManager signature_manager = null;
	protected HashManager hash_manager=null;
	
	
	protected boolean sign()
	{
		signature_manager = new SignatureManager();
		try
		{
			this.signature = new String(signature_manager.sign(createString().getBytes()));
		}catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		return true;
	}
	
	
	protected String createString()
	{
		return public_key+signature;
	}
	
	
	protected void makeHash()
	{
		hash_manager = new HashManager();
		this.hash= new String(hash_manager.digest(createString().getBytes()));
	}
	
	
	public String getSignature()
	{
		return signature;
	}
	
	
	public boolean isSignatureValid()
	{
		signature_manager = new SignatureManager();
		try
		{
			String signature = new String(signature_manager.sign(createString().getBytes()));
			if(signature.equals(this.signature)) return true;
			else return false;
		}catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		
	}
	
}
