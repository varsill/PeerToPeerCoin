package Security;

import java.io.File;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import Managers.DebugManager;
import javax.crypto.Cipher;

@SuppressWarnings("unused")
public class AsymetricCipherManager {

	public class KeyGenerator extends Thread
	{
		private int key_length=-1;
		private boolean is_ready=false;
		private String algorithm_name = "";
		private KeyPair key_pair;
		private String path_to_file;
		public KeyGenerator(int key_length, String algorithm_name)
		{
			this.key_length=key_length;
			this.algorithm_name=algorithm_name;
		}
		
		private KeyPair buildKeys(final int key_length, String algorithm_name) throws NoSuchAlgorithmException, WrongKeyException
		{
			if(key_length<0) throw new WrongKeyException(key_length);
			KeyPairGenerator key_pair_generator = KeyPairGenerator.getInstance(algorithm_name); 
			key_pair_generator.initialize(key_length);
			return key_pair_generator.genKeyPair();
		}
		
		
		private void writeKeysToFile(String path_to_file)//with symetric encryption
		{
			PrintWriter print_writer=null;
			try
			{
				print_writer = new PrintWriter(path_to_file, "UTF-8");
				print_writer.println(key_pair.getPublic());
				print_writer.println(key_pair.getPrivate());
			}
			catch (Exception e)
			{
				DebugManager.alert(e);
			}
			if(print_writer!=null)print_writer.close();
					
		}
		
		private void writeKeysToFile(String path_to_file, String password)//with symetric encryption
		{
			PrintWriter print_writer=null;
			try
			{
				print_writer = new PrintWriter(path_to_file, "UTF-8");
				print_writer.println(key_pair.getPublic());
				print_writer.println(key_pair.getPrivate());
			}
			catch (Exception e)
			{
				DebugManager.alert(e);
			}
			if(print_writer!=null)print_writer.close();
					
		}
		
		public void prepareKeys()
		{
			
				start();//buildKeys()
				writeKeysToFile(path_to_file);
				
		}
		
		
		@Override
		public void run()
		{
			try
			{
				key_pair = buildKeys(key_length, algorithm_name);
				is_ready=true;
			}
			catch (Exception e)
			{
				DebugManager.alert(e);
			
			}
		
		}
	}
	

	
}
