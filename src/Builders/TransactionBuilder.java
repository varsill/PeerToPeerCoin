package Builders;
import java.util.ArrayList;

import java.util.List;

import Blockchain.Transaction;
import Managers.DebugManager;
import Managers.SerializationManager;
import Blockchain.BalanceRegister;
import Blockchain.PayInformation;
public class TransactionBuilder extends Transaction implements Builder {
	
	private BalanceRegister balance_register = BalanceRegister.getInstance();
	private int total_amount=0;
	//Singleton
	
	private TransactionBuilder()
	{
		balance_register = BalanceRegister.getInstance();
	}
	
	private TransactionBuilder(Transaction x)
	{
		super(x);
		balance_register = BalanceRegister.getInstance();
		
	}
	
	static public TransactionBuilder getInstance()
	{
		SingletonHolder.INSTANCE.reset();
		return SingletonHolder.INSTANCE;
	}
	
	static public TransactionBuilder getInstance(Transaction x)
	{
		SingletonHolder.INSTANCE = new TransactionBuilder(x);
		return SingletonHolder.INSTANCE;
	}
	
	
	static private class SingletonHolder
	{
		static TransactionBuilder INSTANCE = new TransactionBuilder();
	}
	
	
	//Rest
	@Override
	 public Object createPart() throws Exception
	{
		
		if(!isReady()) throw new Exception("Couldn't create transaction");
		Transaction result = new Transaction(this);
		return result;
		
	}

	
	@Override
	 public boolean isReady()
	{
		if(signature==null) return false;
		if(public_key==null) return false;
		if(payer==null) return false;
		if(time==-1) return false;
		if(total_amount>(payer.getAmount())) return false;
		if(!isSignatureValid()) return false;
		return true;
	}
	
	
	@Override
	public void reset()
	{
		 signature=null;
		 public_key=null;
		 payer=null;
		 payees_list=new ArrayList<PayInformation>();
		 total_amount = 0;
		 time=-1;
	}
	

	@Override
	public void addPayee(String public_key, double amount)
	{
		if(amount<0)return;
		super.addPayee(public_key, amount);
		total_amount+=amount;
	}


	@Override
	public void loadPartFromString(String s) throws Exception {
		String[] information=SerializationManager.makeSubstrings(s, "#BEGIN", "<Blockchain.PayInformation>", ";");
		if(information.length!=3) return;
			
			this.public_key=information[0];
			addPayer(public_key, balance_register.getBalanceAsDoubleByAddress(public_key));
			this.time=Long.parseLong(information[1]);
			this.signature=information[2];
			
			
		String[] string_array = SerializationManager.makeSubstrings(s, "<Blockchain.PayInformation>", "</Blockchain.PayInformation>", "&");
		String public_key;
		double amount;
		String[] parts_of_transaction;
		for(String x:string_array)
		{
			parts_of_transaction = x.split(";");
			if(parts_of_transaction.length!=2) throw new Exception("Wrong number of parameters. Couldn't create transaction");
			public_key=parts_of_transaction[0];
			amount = Double.parseDouble(parts_of_transaction[1]);
			addPayee(public_key, amount);
		}

	}	
	
	public void addPayer(String public_key)
	{
		try {
			payer = new PayInformation(public_key, balance_register.getBalanceAsDoubleByAddress(public_key));
		} catch (Exception e) {
			DebugManager.alert(e);
		}
		this.public_key=public_key;
	}
	
	
	@Override
	public void prepareNew()
	{
		this.time=System.currentTimeMillis();
		sign();
	}
	
}
