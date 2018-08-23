package Security;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Managers.DebugManager;

public class SymetricCipherManager {

	private Cipher cipher;
	private KEY_LENGTH key_length=KEY_LENGTH.SHORT_VERSION;
	
	public enum KEY_LENGTH
	{
		
		SHORT_VERSION(16),
		LONG_VERSION(32);
		
		private int wartosc;
		
		private KEY_LENGTH(int w)
		{
			this.wartosc=w;
		}
		
		public int getOrdinal()
		{
			return this.wartosc;
		}
		
		
	}
	
	
	private SecretKeySpec key_spec;
	public SymetricCipherManager(String algorithm_name, String key) throws Exception
	{
		key=fixKeyLength(key);
		byte[] key_in_bytes = key.getBytes(StandardCharsets.US_ASCII);
		this.key_spec = new SecretKeySpec(key_in_bytes, algorithm_name);
		this.cipher = Cipher.getInstance(algorithm_name);
	}
	public SymetricCipherManager(String algorithm_name, String key, KEY_LENGTH key_length) throws Exception
	{
		key=fixKeyLength(key);
		byte[] key_in_bytes = key.getBytes(StandardCharsets.US_ASCII);
		this.key_spec = new SecretKeySpec(key_in_bytes, algorithm_name);
		this.cipher = Cipher.getInstance(algorithm_name);
		this.key_length=key_length;
	}
	
	public String encrypt(String message)
	{
		String result="";
		try
		{
			this.cipher.init(Cipher.ENCRYPT_MODE, this.key_spec);
			byte[] output_bytes = this.cipher.doFinal(message.getBytes(StandardCharsets.US_ASCII));
			  result = new String(output_bytes, StandardCharsets.US_ASCII);
		}
		catch (Exception e)
		{
			DebugManager.alert(e);
		}
		return result;
	}
	
	public String decrypt(String message)
	{
		String result = "";
		try
		{
			this.cipher.init(Cipher.DECRYPT_MODE, this.key_spec);
			byte[] output_bytes = this.cipher.doFinal(message.getBytes(StandardCharsets.US_ASCII));
			  result = new String(output_bytes, StandardCharsets.US_ASCII);
		}
		catch (Exception e)
		{
			DebugManager.alert(e);
		}
		return result;
	}
	
	private String fixKeyLength(String key)
	{
		String result = "";
		for(int i=0; i<key_length.getOrdinal(); i++)
		{
			result+=key.charAt(i%key.length());
		}
		return result;
	}
	
}
