package Security;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Properties;

import Interfaces.Configurable;
import Interfaces.PropertiesManager;
import Managers.DebugManager;

public class HashManager implements Configurable {
	private static String HASHING_ALGORITHM_NAME;//="SHA-256"
	private MessageDigest message_digest=null;
	private byte[] result;
	private byte[] message;
	
	
	@Override
	public void configure()
	{
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(PropertiesManager.PATH_TO_PROPERTIES_FILE));
			HASHING_ALGORITHM_NAME = properties.getProperty("HASHING_ALGORITHM_NAME");
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
		
		
		
	}
	public HashManager()
	{
		try
		{
			configure();
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
		DigestiveThread digestive_thread = new DigestiveThread();
		digestive_thread.start();
		synchronized(this)
		{
			
		
		try 
		{
			System.out.println("Digesting message");
			this.wait();
			System.out.println("Message has been digested");
		}
		
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
	}
		return result;
	}
	private class DigestiveThread extends Thread
	{
		@Override
		public void run()
		{
			synchronized(HashManager.this)
			{
			try
			{
				result = message_digest.digest(message);
				HashManager.this.notifyAll();
			}
			catch(Exception e)
			{
				DebugManager.alert(e);
			}
		}
	}
	}
}
