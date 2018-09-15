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
	public void addChildToList(Object object) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public XSerializable[] getObjectList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
