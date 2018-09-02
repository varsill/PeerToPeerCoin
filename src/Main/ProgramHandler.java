package Main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ProgramHandler {
		
		private PriorityQueue<AvailableTransaction> available_transactions=null;
		
		
		//Singleton
		private ProgramHandler() {
		
			Comparator<AvailableTransaction> comparator = new Comparator<AvailableTransaction>() {

			    @Override
			    public int compare(AvailableTransaction left, AvailableTransaction right) {
			    	if(left.getAmount()<right.getAmount()) return -1;
			    	else if(left.getAmount()>right.getAmount()) return 1;
			    	return 0;
			    } 
			};
			available_transactions = new PriorityQueue<AvailableTransaction>(comparator);
					
		}
		
		
		public static ProgramHandler getInstance()
		{
			return SingletonHolder.INSTANCE;
		}
		
		
		private static class SingletonHolder
		{
			static ProgramHandler INSTANCE = new ProgramHandler();
		}
		
		
		public PriorityQueue<AvailableTransaction> getAvailableTransactions()
		{
			return available_transactions;
		}
}



class AvailableTransaction
{
	private double amount = 0;
	private String hash = "";
	
	
	public AvailableTransaction(String hash, double amount)
	{
		this.hash = hash;
		this.amount=amount;
	}
	
	
	public double  getAmount()
	{
		return amount;
	}
	
	public String getHash()
	{
		return hash;
	}
}