package Blockchain;

public class Exit extends Parcel implements XSerializable{

	public Exit()
	{
		
	}
	
	
	public Exit(Exit x)
	{
		this.public_key=x.public_key;
		this.signature=x.signature;
		this.time=x.time;
	}
	
	@Override
	public String createString()
	{
		String result=super.createString();
		return result;
	}


	@Override
	public String[] getListOfObjectNames() {
		String[] s = {"public_key", "time", "signature"};
		return s;
	}



	@Override
	public XSerializable[] getObjectList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
