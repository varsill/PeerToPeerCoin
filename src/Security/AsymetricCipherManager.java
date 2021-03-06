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
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import Managers.Configurable;
import Managers.DebugManager;
import Managers.PropertiesManager;

import javax.crypto.Cipher;


@SuppressWarnings("unused")


public class AsymetricCipherManager implements Configurable {
	private static String ALGORITHM_TO_STORE_KEYS;// = "AES";
	private static String ASYMETRIC_ALGORITHM;// = "RSA";
	private static int KEY_LENGTH;//=4096;//in bits
	private String PATH_TO_FILE;
	private Cipher cipher;
	private String password;
	private KeyPair key_pair=null;
	
	
	public enum KEY_MODE
	{
		PUBLIC_KEY_MODE,
		PRIVATE_KEY_MODE;
	}
	
	//Singleton

	private AsymetricCipherManager()
	{
		configure();
		try
		{
			
			this.cipher = Cipher.getInstance(ASYMETRIC_ALGORITHM);
			this.password = PropertiesManager.PASSWORD;
			getKeys();
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
		
	}
	
	private AsymetricCipherManager(String public_key, String private_key)
	{
		configure();
		setKeyPair(public_key.getBytes(), private_key.getBytes());
		{
			try
			{
				
				this.cipher = Cipher.getInstance(ASYMETRIC_ALGORITHM);
				this.password = PropertiesManager.PASSWORD;
				getKeys();
			}
			catch(Exception e)
			{
				DebugManager.alert(e);
			}
		}
	}
	
	
	static public AsymetricCipherManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	static private class SingletonHolder
	{
		private static AsymetricCipherManager INSTANCE = new AsymetricCipherManager();
	}
	
	//REST

	@Override
	public void configure()
	{
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(PropertiesManager.PATH_TO_PROPERTIES_FILE));
			ALGORITHM_TO_STORE_KEYS = properties.getProperty("ALGORITHM_TO_STORE_KEYS");
			ASYMETRIC_ALGORITHM = properties.getProperty("ASYMETRIC_ALGORITHM");
			PATH_TO_FILE = properties.getProperty("PATH_TO_FILE");
			KEY_LENGTH = Integer.parseInt(properties.getProperty("KEY_LENGTH"));
			
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
	}
	
	
	private boolean getKeys()
	{
		if(!getKeysFromFile(password))
		{
			KeyGenerator key_generator=new KeyGenerator(KEY_LENGTH, ASYMETRIC_ALGORITHM);
			if(key_generator==null) return false;
			key_generator.prepareKeys();
				synchronized(this)
				{
				System.out.println("Generating keys");
				try
				{
					this.wait();
				}
				catch(Exception e)
				{
					DebugManager.alert(e);
				}
				System.out.println("Key has been generated");
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
			File file = new File(PATH_TO_FILE+"/private.key");
			byte[] private_key=Files.readAllBytes(file.toPath());
			file = new File(PATH_TO_FILE+"/public.key");
			byte[] public_key=Files.readAllBytes(file.toPath());
			if(private_key==null||public_key==null) return false;
			return decryptKeys(private_key, public_key, password);
			
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
	}
	
	
	private boolean getKeysFromFile()
	{
		FileInputStream in = null;
		try
		{
			File file = new File(PATH_TO_FILE+"/private.key");
			byte[] private_key=Files.readAllBytes(file.toPath());
			file = new File(PATH_TO_FILE+"/public.key");
			byte[] public_key=Files.readAllBytes(file.toPath());
			if(private_key==null||public_key==null) return false;
			setKeyPair(private_key, public_key);

		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		return true;
	}
	
	
	
	public boolean decryptKeys(byte[] private_key, byte[] public_key, String password)
	{
		try
		{
			SymetricCipherManager symetric_cipher_manager = new SymetricCipherManager(ALGORITHM_TO_STORE_KEYS, password);
			private_key=symetric_cipher_manager.decrypt(private_key);
			public_key=symetric_cipher_manager.decrypt(public_key);
			if(private_key==null||public_key==null) return false;
		
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return false;
		}
		setKeyPair(private_key, public_key);
		return true;
	}
	
	
	public Key makeKeyFromBytes(byte[] key, KEY_MODE key_mode)
	{
		try
		{
			KeyFactory kf = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
			if(key_mode==KEY_MODE.PRIVATE_KEY_MODE)
			{
				PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
				if(kf==null) return null;
				return kf.generatePrivate(spec);
			}
			else
			{
				X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
				if(kf==null) return null;
				return kf.generatePublic(spec);
			}
			
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
		
	}
	
	
	public Key makeKeyFromString(String key, KEY_MODE key_mode)
	{
		try
		{
			KeyFactory kf = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
			if(key_mode==KEY_MODE.PRIVATE_KEY_MODE)
			{
				return loadPrivateKey(key);
			}
			else
			{
				return loadPublicKey(key);
			}
			
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
		
	}
	
	
	private boolean setKeyPair(byte[] private_key, byte[] public_key)
	{
		try
		{
			 
			 PrivateKey privateKey= (PrivateKey) makeKeyFromBytes(private_key, KEY_MODE.PRIVATE_KEY_MODE);
			 PublicKey publicKey= (PublicKey) makeKeyFromBytes(public_key, KEY_MODE.PUBLIC_KEY_MODE);
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
	
	
	public byte[] encrypt(String message)
	{
		return encrypt(message.getBytes());
	}
	
	
	public byte[] encrypt(byte[] message)
	{
		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, key_pair.getPublic());
			byte[] result =  cipher.doFinal(message);
			return result;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
		
	}
	
	public byte[] sign(byte[] message)
	{
		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, key_pair.getPrivate());
			byte[] result =  cipher.doFinal(message);
			return result;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
	}
	
	public byte[] sign(byte[] message, byte[] private_key)
	{
		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, makeKeyFromBytes(private_key, KEY_MODE.PRIVATE_KEY_MODE));
			byte[] result =  cipher.doFinal(message);
			return result;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
	}
	
	public byte[] unsign(byte[] message)
	{
		try
		{
			cipher.init(Cipher.DECRYPT_MODE, key_pair.getPublic());
			byte[] result =  cipher.doFinal(message);
			return result;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
	}
	
	public byte[] unsign(String message, String public_key)
	{
		try
		{
			byte[] msg = {0, 2, 12, };
			cipher.init(Cipher.DECRYPT_MODE, makeKeyFromString(public_key, KEY_MODE.PUBLIC_KEY_MODE));
			byte[] result =  cipher.doFinal(msg);
			return result;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
	}
	
	
	public byte[] unsign(String message)
	{
		try
		{
			cipher.init(Cipher.DECRYPT_MODE, key_pair.getPublic());
			byte[] result =  cipher.doFinal(message.getBytes());
			return result;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
	}
	
	
	public byte[] unsign(byte[] message, byte[] public_key)
	{
		try
		{
			cipher.init(Cipher.DECRYPT_MODE, makeKeyFromBytes(public_key, KEY_MODE.PUBLIC_KEY_MODE));
			byte[] result =  cipher.doFinal(message);
			return result;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
	}
	
	
	public byte[] decrypt(String message)
	{
		return decrypt(message.getBytes());
	}
	
	
	public byte[] decrypt(byte[] message)
	{
		try
		{
			cipher.init(Cipher.DECRYPT_MODE, key_pair.getPrivate());
			byte[] result = cipher.doFinal(message);
			return result;
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
			return null;
		}
	}
	           
	
	
	
	
	public String getPublicKeyAsString() 
	{
		
		try
		{
			return savePublicKey(this.key_pair.getPublic());
		}catch(Exception e)
		{
			DebugManager.alert(e);
		}
		return null;
	}
	
	

	private String savePublicKey(PublicKey publ) throws Exception 
	{
    
	KeyFactory fact = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
    X509EncodedKeySpec spec = fact.getKeySpec(publ,
            X509EncodedKeySpec.class);
    return Base64.getEncoder().encodeToString(spec.getEncoded());
	}
	
	

	
	
	
	
	public static PrivateKey loadPrivateKey(String key64) throws Exception {
	    byte[] clear = Base64.getDecoder().decode(key64);
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
	    KeyFactory fact = KeyFactory.getInstance(keySpec.getFormat());
	    PrivateKey priv = fact.generatePrivate(keySpec);
	    Arrays.fill(clear, (byte) 0);
	    return priv;
	}


	public static PublicKey loadPublicKey(String key64) throws Exception {
	    byte[] data = Base64.getDecoder().decode(key64);
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
	    KeyFactory fact = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
	    return fact.generatePublic(spec);
	}
	
	private byte[] getPrivateKeyAsByte()
	{
		return new PKCS8EncodedKeySpec(key_pair.getPrivate().getEncoded()).getEncoded();
	}
	
	
	private byte[] getPublicKeyAsByte()
	{
		return new  X509EncodedKeySpec(key_pair.getPublic().getEncoded()).getEncoded();
	}
	
	public PrivateKey getPrivateKey()
	{
		return this.key_pair.getPrivate();
	}
	
	public PublicKey getPublicKey()
	{
		return this.key_pair.getPublic();
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
				
				os = new FileOutputStream(path_to_file+"/private.key");
				os.write(private_key);
				os.close();
				
				os = new FileOutputStream(path_to_file+"/public.key");
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
				
				
				byte[] private_key =getPrivateKeyAsByte();
				byte[] public_key=getPublicKeyAsByte();
				
				private_key = symetric_cipher_manager.encrypt(private_key);
				public_key = symetric_cipher_manager.encrypt(public_key);
				
				os = new FileOutputStream(path_to_file+"/private.key");
				os.write(private_key);
				os.close();
				
				os = new FileOutputStream(path_to_file+"/public.key");
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
			synchronized(AsymetricCipherManager.this)
			{
				
			
			try
			{
				key_pair = buildKeys(key_length, algorithm_name);
				AsymetricCipherManager.this.key_pair = key_pair;
				is_ready=true;
				AsymetricCipherManager.this.key_pair = this.key_pair;
				writeKeysToFile(PATH_TO_FILE, password);
				AsymetricCipherManager.this.notify();
				
			}
			catch (Exception e)
			{
				DebugManager.alert(e);
			}
			}
		}
	}
	

}
