package Blockchain;

import java.io.Serializable;

import Security.SignatureManager;

public class Parcel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name = "";
	private String hash = "";
	private String signature="";
	private SignatureManager signature_manager = null;
	
	public void sign()
	{
		signature_manager = new SignatureManager();
	}
	
	public String createString()
	{
		return name+hash+signature;
	}
}
