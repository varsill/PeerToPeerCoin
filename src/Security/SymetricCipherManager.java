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
	private String algorithm_name;
	public SymetricCipherManager(String algorithm_name,String key_string) throws Exception
	{
		byte[] key=key_string.getBytes(StandardCharsets.UTF_8);
		key=fixKeyLength(key);
		this.algorithm_name=algorithm_name;
		this.key_spec = new SecretKeySpec(key, algorithm_name);
		this.cipher = Cipher.getInstance(algorithm_name);
	}
	public SymetricCipherManager(String algorithm_name,String key_string, KEY_LENGTH key_length) throws Exception
	{
		byte[] key=key_string.getBytes(StandardCharsets.UTF_8);
		key=fixKeyLength(key);
		this.key_spec = new SecretKeySpec(key, algorithm_name);
		this.algorithm_name=algorithm_name;
		this.key_length=key_length;
	}
	
	public byte[] encrypt(byte[] input)
	{
		
		try
		{
			
			this.cipher = Cipher.getInstance(algorithm_name);
			this.cipher.init(Cipher.ENCRYPT_MODE, this.key_spec);
			byte[] output =  this.cipher.doFinal(input);
			return output;
		}
		catch (Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
		
	}
	
	public byte[] decrypt(byte[] input_bytes)
	{
		try
		{
			this.cipher = Cipher.getInstance(algorithm_name);
			this.cipher.init(Cipher.DECRYPT_MODE, this.key_spec);
			return this.cipher.doFinal(input_bytes);
			
		}
		catch (Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
		
	}
	
	private byte[] fixKeyLength(byte[] key)
	{

		byte [] result=new byte[key_length.getOrdinal()];
		for(int i=0; i<key_length.getOrdinal(); i++)
		{
			result[i]=key[i%key.length];
		}
		return result;
		
	}
	
}
