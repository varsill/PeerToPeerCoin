package Security;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import Managers.Configurable;
import Managers.DebugManager;

public class SignatureManager  {
	/*private AsymetricCipherManager asymetric_cipher_manager=null;
	private HashManager hash_manager=null;
	
	public SignatureManager()
	{
		asymetric_cipher_manager = new AsymetricCipherManager();
		hash_manager = new HashManager();
	}
	
	
	public SignatureManager(String public_key, String private_key)
	{
		asymetric_cipher_manager = new AsymetricCipherManager(public_key, private_key);
		hash_manager = new HashManager();
	}
	
	
	public byte[] sign(byte[] message) throws TooLongMessageException
	{
		if(message.length>Integer.MAX_VALUE) throw new TooLongMessageException(message.length);
		byte[] hash = hash_manager.digest(message);
		byte[] result = asymetric_cipher_manager.sign(hash);
		return result;
	}
	
	
	public boolean isSignatureValid(byte[] signature, byte[] message, byte[] public_key) throws TooLongMessageException, SignatureManagerException
	{
		if(hash_manager==null)
		{
			throw new SignatureManagerException("HashManager hasn't been properly initialized");	
		}
		if(asymetric_cipher_manager==null) 		
		{
			throw new SignatureManagerException("AsymetricCipherManager hasn't been properly initialized");
		}
		
		byte[] sent_hash = asymetric_cipher_manager.unsign(signature, public_key);
		byte[] my_hash = hash_manager.digest(message);
		if(sent_hash.equals(my_hash)) return true;
		return false;
		
	}
	
	
	public boolean isSignatureValid(String signature, String message, String public_key) throws TooLongMessageException, SignatureManagerException
	{
		if(hash_manager==null)
		{
			throw new SignatureManagerException("HashManager hasn't been properly initialized");	
		}
		if(asymetric_cipher_manager==null) 		
		{
			throw new SignatureManagerException("AsymetricCipherManager hasn't been properly initialized");
		}
		
		byte[] sent_hash = asymetric_cipher_manager.unsign(signature, public_key);
		byte[] my_hash = hash_manager.digest(message);
		if(sent_hash.equals(my_hash)) return true;
		return false;
		
	}
	
	
	public boolean isSignatureValid(byte[] signature, byte[] message) throws TooLongMessageException, SignatureManagerException
	{
		if(hash_manager==null)
		{
			throw new SignatureManagerException("HashManager hasn't been properly initialized");	
		}
		if(asymetric_cipher_manager==null) 		
		{
			throw new SignatureManagerException("AsymetricCipherManager hasn't been properly initialized");
		}
		
		byte[] sent_hash = asymetric_cipher_manager.unsign(signature);
		byte[] my_hash = hash_manager.digest(message);
		for(int i=0; i<sent_hash.length; i++)
		{
			if(sent_hash[i]!=my_hash[i]) return false;
		}
		return true;
	}
	
	
	public boolean isSignatureValid(String signature, String message) throws TooLongMessageException, SignatureManagerException
	{
		if(hash_manager==null)
		{
			throw new SignatureManagerException("HashManager hasn't been properly initialized");	
		}
		if(asymetric_cipher_manager==null) 		
		{
			throw new SignatureManagerException("AsymetricCipherManager hasn't been properly initialized");
		}
		
		byte[] sent_hash = asymetric_cipher_manager.unsign(signature);
		byte[] my_hash = hash_manager.digest(message);
		for(int i=0; i<sent_hash.length; i++)
		{
			if(sent_hash[i]!=my_hash[i]) return false;
		}
		return true;
	}
	*/
	
	//NOWE NADESZ£O
	
	public static String sign(String msg)
	{
		String result=null;
		try
		{
		AsymetricCipherManager asymetric_cipher_manager = AsymetricCipherManager.getInstance();
		PrivateKey private_key = asymetric_cipher_manager.getPrivateKey();
		Signature private_signature = Signature.getInstance("SHA256withRSA");
	    private_signature.initSign(private_key);
	    private_signature.update(msg.getBytes(StandardCharsets.UTF_8));

	    byte[] signature = private_signature.sign();
	    result = Base64.getEncoder().encodeToString(signature);
	   
	    } catch(Exception e)
		{
	    	DebugManager.alert(e);
		}
		return result;
	}
	
	
	public static boolean verify(String signature, String plain_text, String public_key_string) throws Exception {
	    Signature public_signature = Signature.getInstance("SHA256withRSA");
	    PublicKey public_key =null;
	    try
	    {
		   public_key =  AsymetricCipherManager.loadPublicKey(public_key_string);
	    }catch (Exception e)
	    {
	    	DebugManager.alert(e);
	    }

	    public_signature.initVerify(public_key);
	    public_signature.update(plain_text.getBytes(StandardCharsets.UTF_8));

	    byte[] signature_bytes = Base64.getDecoder().decode(signature);

	    return public_signature.verify(signature_bytes);
	}
	
}

 class TooLongMessageException extends Exception
{
	  
	private static final long serialVersionUID = 1L;
	private int length=-1;
	public TooLongMessageException(int length)
	{
		super();
		this.length=length;
	}
	
	@Override
	public String getMessage()
	{
		return "The message you are trying to hash is too long. It's length equals: "+Integer.toString(length);
	}
}


class SignatureManagerException extends Exception
{
	  
	private static final long serialVersionUID = 1L;
	private String additional_info;
	public SignatureManagerException(String s)
	{
		super();
		this.additional_info=s;
	}
	
	@Override
	public String getMessage()
	{
		return "SignatureManagerException. More info: " + additional_info;
	}
}