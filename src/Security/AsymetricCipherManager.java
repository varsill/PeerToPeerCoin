package Security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;


public class AsymetricCipherManager {

	public class KeyGenerator extends Thread
	{
		private int key_length=-1;
		private String algorithm_name = "";
		
		public KeyGenerator(int key_length, String algorithm_name)
		{
			this.key_length=key_length;
			this.algorithm_name=algorithm_name;
		}
		@SuppressWarnings("unused")
		private KeyPair buildKeys(final int key_length, String algorithm_name) throws NoSuchAlgorithmException, WrongKeyException
		{
			if(key_length<0) throw new WrongKeyException(key_length);
			KeyPairGenerator key_pair_generator = KeyPairGenerator.getInstance(algorithm_name); 
			key_pair_generator.initialize(key_length);
			return key_pair_generator.genKeyPair();
		}
		private void writeToFile()
		{
			
		}
		
		public void prepareKeys()
		{
			try
			{
				KeyPair key_pair = buildKeys(key_length, algorithm_name);
			}
			catch(Exception e)
			{
				DebugManager.alert(e);
			}
			
		}
	}
	

	
}
