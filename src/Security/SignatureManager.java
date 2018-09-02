package Security;

import Managers.Configurable;

public class SignatureManager  {
	private AsymetricCipherManager asymetric_cipher_manager=null;
	private HashManager hash_manager=null;
	
	public SignatureManager()
	{
		asymetric_cipher_manager = new AsymetricCipherManager();
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