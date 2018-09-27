package Blockchain;
public class Peer 
{
	
	private String IP="";
	private double hash_rate=0;
	private String public_key=null;
	
	public Peer(String IP, double hash_rate)
	{
		this.IP = IP;
		this.hash_rate=hash_rate;
	}
	
	public Peer(String IP, double hash_rate, String public_key)
	{
		this.IP = IP;
		this.hash_rate=hash_rate;
		this.public_key=public_key;
	}
	
	
	public void updatePeer(String IP, double hash_rate)
	{
		this.IP=IP;
		this.hash_rate=hash_rate;
	}
	
	
	public void namePeer(String public_key)
	{
		this.public_key=public_key;
	}
	
	
	public String getPublicKey()
	{
		return public_key;
	}
	
	
	public void updatePeer(String IP)
	{
		this.IP=IP;
	}
	
	
	public void updatePeer(double hash_rate)
	{
		this.hash_rate=hash_rate;
	}
	
	
	public double getHashRate()
	{
		return this.hash_rate;
	}
	
	
	public String getIP()
	{
		return this.IP;
	}


	
	
	
	
}