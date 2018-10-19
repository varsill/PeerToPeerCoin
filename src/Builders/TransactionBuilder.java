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
	private double total_amount=0;
	
	//Singleton
	
	protected TransactionBuilder()
	{
		balance_register = BalanceRegister.getInstance();
	}
	
	protected TransactionBuilder(Transaction x)
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
		
		isReady();
		Transaction result = new Transaction(this);
		return result;
		
	}

	
	@Override
	 public void isReady() throws Exception
	{
		if(signature==null) throw new Exception("Signature is not set");
		if(public_key==null) throw new Exception("Public Key is not set");
		if(payer==null) throw new Exception("Payer is not set");
		if(time==-1) throw new Exception("Time is not set");
			
		if(payer.getAmount()>BalanceRegister.getInstance().getBalanceAsDoubleByAddress(payer.getPublicKey()))throw new Exception("Payer does not have the money he considers himself to have");
			
		
		if(total_amount>(payer.getAmount())) throw new Exception("Payer does not have enough money");
		if(!isSignatureValid()) throw new Exception("Signature is invalid");
		
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
		String[] information=SerializationManager.makeSubstrings(s, "#BEGIN", "<Blockchain.PayInformation>", SerializationManager.SEPARATOR);
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
			parts_of_transaction = x.split(SerializationManager.SEPARATOR);
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
