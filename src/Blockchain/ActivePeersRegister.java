package Blockchain;

public class ActivePeersRegister extends Register {
	//Singleton
	private ActivePeersRegister()
	{
		
	}
	
	static public ActivePeersRegister getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	static private class SingletonHolder
	{
		static	ActivePeersRegister INSTANCE = new ActivePeersRegister();
	}
	
	//Rest
	
	
	public void update(Entry e)
	{
		
	}
	
	
	public void update(Exit e)
	{
		
	}
	
}
