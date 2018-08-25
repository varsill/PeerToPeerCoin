package Security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import Managers.DebugManager;
import javax.crypto.Cipher;


@SuppressWarnings("unused")


public class AsymetricCipherManager {
	private static final String ALGORITHM_TO_STORE_KEYS = "AES";
	private static final String ASYMETRIC_ALGORITHM = "RSA";
	private static final int KEY_LENGTH=2048;//in bits
	private String password;
	private KeyPair key_pair=null;
	private String path_to_file;
	
	
	public AsymetricCipherManager(String password, String path_to_file)
	{
		this.password = password;
		this.path_to_file=path_to_file;
		getKeys();
	}
	
	
	private boolean getKeys()
	{
		if(!getKeysFromFile(password))
		{
			KeyGenerator key_generator=new KeyGenerator(KEY_LENGTH, ASYMETRIC_ALGORITHM);
			if(key_generator==null) return false;
			key_generator.prepareKeys();
			while(key_generator.is_ready!=true)
			{
				System.out.println("Generating keys");
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception e)
				{
					DebugManager.alert(e);
				}
			}
			
		}
		if(key_pair==null) return false;
		return true;
		
	}
	
	
	private boolean getKeysFromFile(String password)
	{
		FileInputStream in = null;
		try
		{
			File file = new File(path_to_file+"/private.ks");
			byte[] private_key=Files.readAllBytes(file.toPath());
			file = new File(path_to_file+"/public.ks");
			byte[] public_key=Files.readAllBytes(file.toPath());
			if(private_key==null||public_key==null) return false;
			decryptKeys(private_key, public_key, password);
			
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		return true;
	}
	
	
	private boolean getKeysFromFile()
	{
		try
		{
			BufferedReader buffered_reader = new BufferedReader(new FileReader(path_to_file));
			 byte[] private_key = buffered_reader.readLine().getBytes(StandardCharsets.UTF_8);
			 byte[] public_key = buffered_reader.readLine().getBytes(StandardCharsets.UTF_8);
			buffered_reader.close();
			if(private_key==null||public_key==null) return false;
			saveKeysToKeyPair(private_key, public_key);

		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		return true;
	}
	
	
	private boolean decryptKeys(byte[] private_key, byte[] public_key, String password)
	{
		try
		{
			SymetricCipherManager symetric_cipher_manager = new SymetricCipherManager(ALGORITHM_TO_STORE_KEYS, password);
			private_key=symetric_cipher_manager.decrypt(private_key);
			public_key=symetric_cipher_manager.decrypt(public_key);
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		saveKeysToKeyPair(private_key, public_key);
		return true;
	}
	
	
	private boolean saveKeysToKeyPair(byte[] private_key, byte[] public_key)
	{
		try
		{
			 PKCS8EncodedKeySpec private_spec = new PKCS8EncodedKeySpec(private_key);
			 X509EncodedKeySpec public_spec = new X509EncodedKeySpec(public_key);
			 KeyFactory kf = KeyFactory.getInstance("RSA");
			 if(kf==null) return false;
			 PrivateKey privateKey= kf.generatePrivate(private_spec);
			 PublicKey publicKey= kf.generatePublic(public_spec);
			 if(privateKey==null||publicKey==null) return false;
			 key_pair = new KeyPair(publicKey, privateKey);
			 if(key_pair==null) return false;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		
		return true;
		
	}
	
	
	public class KeyGenerator extends Thread
	{
		private int key_length=-1;
		private boolean is_ready=false;
		private String algorithm_name = "";
		private KeyPair key_pair;
		
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
		
		
		private void writeKeysToFile(String path_to_file)//without encryption
		{
			
			FileOutputStream os = null;
			try
			{

				byte[] private_key =new  PKCS8EncodedKeySpec(key_pair.getPrivate().getEncoded()).getEncoded();
				byte[] public_key=new X509EncodedKeySpec(key_pair.getPublic().getEncoded()).getEncoded();
				
				os = new FileOutputStream(path_to_file+"/private.ks");
				os.write(private_key);
				os.close();
				
				os = new FileOutputStream(path_to_file+"/public.ks");
				os.write(public_key);
				os.close();
			}
			catch (Exception e)
			{
				DebugManager.alert(e);
			}
			
					
		}
		
		private void writeKeysToFile(String path_to_file, String password)//with symetric encryption based on AES
		{
			SymetricCipherManager symetric_cipher_manager=null; 
			FileOutputStream os = null;
			try
			{
				symetric_cipher_manager = new SymetricCipherManager(ALGORITHM_TO_STORE_KEYS, password);
				
				
				byte[] private_key =new  PKCS8EncodedKeySpec(key_pair.getPrivate().getEncoded()).getEncoded();
				byte[] public_key=new X509EncodedKeySpec(key_pair.getPublic().getEncoded()).getEncoded();
				
				private_key = symetric_cipher_manager.encrypt(private_key);
				public_key = symetric_cipher_manager.encrypt(public_key);
				
				os = new FileOutputStream(path_to_file+"/private.ks");
				os.write(private_key);
				os.close();
				
				os = new FileOutputStream(path_to_file+"/public.ks");
				os.write(public_key);
				os.close();
			}
			catch (Exception e)
			{
				DebugManager.alert(e);
			}
			
					
		}
		
		public void prepareKeys()
		{
			
				start();//buildKeys()
				
		}
		
		
		@Override
		public void run()
		{
			try
			{
				key_pair = buildKeys(key_length, algorithm_name);
				AsymetricCipherManager.this.key_pair = key_pair;
				is_ready=true;
				writeKeysToFile(path_to_file, password);
				AsymetricCipherManager.this.key_pair = this.key_pair;
			}
			catch (Exception e)
			{
				DebugManager.alert(e);
			
			}
		
		}
	}
	

}
