package Blockchain;

public class Entry extends Parcel implements XSerializable{
	protected String IP=null;
	protected double hash_rate=-1;
	
	
	public Entry()
	{
		
	}
	
	
	public Entry(Entry e)
	{
		this.hash_rate=e.hash_rate;
		this.IP=e.IP;
		this.public_key=e.public_key;
		this.signature=e.signature;
		this.time=e.time;
	}
	
	
	//DO USUNIECIA!!!!!!!!!!!!!!!!!!!!!!
	
	public Entry(double hash_rate, String IP, String public_key, String signature, long time)
	{
		this.hash_rate=hash_rate;
		this.IP=IP;
		this.public_key=public_key;
		this.signature=signature;
		this.time=time;
	}
	// 
	@Override
	public String createString()
	{
		String result=super.createString();
		result=IP+hash_rate;
		return result;
	}


	@Override
	public String[] getListOfObjectNames() {
		String[] s = {"public_key", "time", "IP", "hash_rate", "signature"};
		return s;
	}




	@Override
	public XSerializable[] getObjectList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
