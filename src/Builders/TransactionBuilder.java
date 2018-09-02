package Builders;
import Blockchain.Transaction;
import Main.ProgramHandler;
public class TransactionBuilder implements Builder {

	Transaction transaction = null;
	ProgramHandler program_handler = null;
	
	//Singleton
	private TransactionBuilder() {}
	
	
	public static TransactionBuilder getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	
	private static class SingletonHolder
	{
		static TransactionBuilder INSTANCE = new TransactionBuilder();
	}
	
	
	//Rest of the methods
	@Override
	public void createPart()
	{
		transaction  = new Transaction();
		program_handler = ProgramHandler.getInstance();
		
		transaction.build();
	}

	
	public Transaction getPart() throws BuildingFailedException
	{
		if(transaction==null) throw new BuildingFailedException(Transaction.class.getName());
		return transaction;
	}
	
	
	
	
	
}
