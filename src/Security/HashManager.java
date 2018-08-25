package Security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import Managers.DebugManager;

public class HashManager extends Thread {
	private static final String HASHING_ALGORITHM_NAME = "SHA-256";
	private MessageDigest message_digest=null;
	private byte[] result;
	private byte[] message;
	
	
	public HashManager()
	{
		try
		{
			message_digest=MessageDigest.getInstance(HASHING_ALGORITHM_NAME);
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
	}
	
	public byte[] digest(String message_string)
	{
		return digest(message_string.getBytes(StandardCharsets.UTF_8));
	}
	
	public byte[] digest(byte[] message)
	{
		this.message=message;
		start();
		try 
		{
			System.out.println("Digesting message");
			wait();
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
		
		return result;
	}
	
	@Override
	public void run()
	{
		result = message_digest.digest(message);
		notify();
	}
}
