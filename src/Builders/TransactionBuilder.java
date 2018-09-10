package Builders;
import java.util.ArrayList;
import java.util.List;

import Blockchain.Transaction;
import Main.ProgramHandler;

import Blockchain.PayeeInformation;
public class TransactionBuilder implements Builder {
	
	Transaction transaction = null;
	ProgramHandler program_handler = null;
	List <PayeeInformation> payees_information=null;
	static double total_amount = 0;
	
	//Singleton
	private TransactionBuilder()
	{
		payees_information = new ArrayList<PayeeInformation>();
	}
	
	
	public static TransactionBuilder getInstance()
	{
		
		total_amount = 0;
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
	
	
	public void addInformationAboutPayment(String public_key, double amount)
	{
		payees_information.add(new PayeeInformation(public_key, amount));
		total_amount+=amount;
	}
	
	
	//Greedy algorithm!
	private prepareAvailableTransactionsToMatchTotalAmount()
	{
		PriorityQueue<AvailableTransaction> program_handler.getAvailableTransactions();
		
	}
	
	
	
	
}
