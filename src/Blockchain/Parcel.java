package Blockchain;


import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import Managers.DebugManager;
import Security.HashManager;
import Security.SignatureManager;

public class Parcel implements  Cloneable
{
	
	
	protected String public_key = null;
	protected SignatureManager signature_manager = null;
	protected HashManager hash_manager=null;
	protected long time=-1;
	protected String signature=null;
	public Parcel()
	{
		
	}
	
	
	public boolean sign()
	{
		this.time = System.currentTimeMillis();
		try
		{
			String s = createString();
			this.signature = SignatureManager.sign(s);
		}catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		return true;
	}
	
	public void setPublicKey(String public_key)
	{
		this.public_key=public_key;
	}
	
	
	protected String createString()
	{
		return public_key+Long.toString(time);
	}
	

	
	public String getSignature()
	{
		return signature;
	}
	
	public String getHash()
	{
		HashManager hash_manager = new HashManager();
		return new String(hash_manager.digest(createString()));
	}

	
	public boolean isSignatureValid()
	{

		try
		{
			String s = createString();
			return SignatureManager.verify(this.signature, s, public_key);
		}catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		
	}
	
	
}
