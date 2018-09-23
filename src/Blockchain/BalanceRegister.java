package Blockchain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import Managers.Configurable;
import Managers.DebugManager;
import Managers.PropertiesManager;
import Managers.SerializationManager;
import Security.AsymetricCipherManager;

public class BalanceRegister extends Register implements XSerializable {


	//Singleton
	Hashtable<String, Double> balance_list = null;
	
	
	private BalanceRegister()
	{
		balance_list = new Hashtable<String, Double>();
	//	readFromFile();
	}
	
	
	static public BalanceRegister getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	
	private static class SingletonHolder
	{
		static BalanceRegister INSTANCE = new BalanceRegister();
	}
	
	
	public double getBalanceByAddress(String public_key) throws AddressNotInBalanceRegisterException
	{
		Double amount = balance_list.get(public_key);
		if(amount==null) 
			{
			
				throw new AddressNotInBalanceRegisterException(public_key);
			
			}
		return amount;
	}
	
	public double getBalanceAsDoubleByAddress(String public_key) throws AddressNotInBalanceRegisterException
	{
		return getBalanceByAddress(public_key);
	}
	
	
	public void update(Transaction transaction) throws NotEnoughMoneyException
	{
		PayInformation payer = transaction.getPayer();
		double payer_balance = balance_list.get(payer.getPublicKey());
		ArrayList<PayInformation> payees_list = (ArrayList<PayInformation>) transaction.getPayees();
		double total_amount= 0;
		double amount = 0;
		for(int i=0; i<payees_list.size(); i++)
		{
			total_amount+=(payees_list.get(i).getAmount());
		}
		
		
		
		if(total_amount>balance_list.get(payer.getPublicKey()))
		{
			throw new NotEnoughMoneyException(payer.getPublicKey(), (double)(payer.getAmount()-total_amount));
		}
		else payer.setAmount(payer_balance);
		
		for(int i=0; i<payees_list.size(); i++)
		{
			amount = payees_list.get(i).getAmount();
			Double current_peer_balance  = balance_list.get(payees_list.get(i).getPublicKey());
			payer_balance-=amount;
			if(current_peer_balance!=null)
			{
				current_peer_balance+=amount;
			}
			else
			{
				addNewPeer(new PayInformation(payees_list.get(i).getPublicKey(), amount));
			}
				
		}
		
	}
	
	
	
	public void addPrize()
	{
		String public_key=AsymetricCipherManager.getInstance().getPublicKeyAsString();
		Double amount = balance_list.get(public_key);
		if(amount==null)
		{
			addNewPeer(new PayInformation(public_key, 2*Ledger.PRIZE));
			return;
		}
		amount=amount+2*Ledger.PRIZE;
			
	}
	public void addNewPeer(PayInformation pay_information)
	{
		balance_list.put(pay_information.getPublicKey(), pay_information.getAmount());
	}
	
	
	public void loadRegisterFromString(String s) throws Exception
	{
		balance_list = new Hashtable<String, Double>();
		String[] string_array =SerializationManager.makeSubstrings(s, ">", "</", "&");
		
		for(String x: string_array)
		{
			String[] string_array2=SerializationManager.makeSubstrings(x, "#BEGIN", "#END", ";");
			if(string_array2.length!=2) throw new Exception("Wrong number of parameters. Couldn't create BlockRegister from string");
			balance_list.put(string_array2[0], Double.parseDouble(string_array2[1]));
		}
		
		
	}
	
	
	public String saveToString() throws Exception
	{
		
		String s = SerializationManager.saveObjectToString(this);
		return s;
		
	}


	@Override
	public String[] getListOfObjectNames() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public XSerializable[] getObjectList() {

				
				Set<String>public_keys = balance_list.keySet();
				
				XSerializableType x;
				
				XSerializableType[] result = new XSerializableType[public_keys.size()];
				
				int i=0;
				for(String key: public_keys)
				{
					x=new XSerializableType();
					x.public_key = key;
					x.amount = balance_list.get(key).doubleValue();
					result[i]=x;
					i++;
				}
		
		
		return result;
	}
	
	

	
	
	public void update(Block block)
	{
		try
		{
			
		
		ArrayList<Transaction> transactions = (ArrayList<Transaction>) block.getTransactions();
		for(Transaction t:transactions)
		{
			update(t);
		}
		
		}catch(NotEnoughMoneyException e)
		{
			DebugManager.alert(e);
		}
	}
	
}



 class XSerializableType implements XSerializable
{
	public String public_key;
	public double amount;
	@Override
	public String[] getListOfObjectNames() {

		String[] s = {"public_key", "amount"};
		return s;
		
	}


	@Override
	public XSerializable[] getObjectList() {
		// TODO Auto-generated method stub
		return null;
	}
	
}




class AddressNotInBalanceRegisterException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String address="";
	public AddressNotInBalanceRegisterException(String address)
	{
		super();
		this.address = address;
	}
	@Override
	public String getMessage()
	{
		return "Adress: "+address+ " is not in balance register";
	}
}

class NotEnoughMoneyException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String address="";
	double how_many_lacks = 0;
	
	
	public NotEnoughMoneyException(String address, double x)
	{
		this.address=address;
		this.how_many_lacks = x;
	}
	
	
	public NotEnoughMoneyException(String address, Amount x)
	{
		this.address=address;
		this.how_many_lacks=x.getAmount();
	}
	
	
	@Override
	public String getMessage()
	{
		return address+" has not enough money to fullfill the transaction. He needs: "+Double.toString(how_many_lacks)+ " more.";
	}
}
