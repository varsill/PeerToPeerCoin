package Blockchain;

public class UnspentTransactionsRegister extends Register {

	//Singleton
	
	private UnspentTransactionsRegister()
	{
	}
	
	
	static public UnspentTransactionsRegister getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	
	private static class SingletonHolder
	{
		static UnspentTransactionsRegister INSTANCE = new UnspentTransactionsRegister();
	}
}
