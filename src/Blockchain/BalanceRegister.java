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

public class BalanceRegister extends Register implements Configurable, XSerializable {

	String path_to_file ="";
	
	//Singleton
	Hashtable<String, Double> balance_list = null;

	
	private BalanceRegister()
	{
		balance_list = new Hashtable<String, Double>();
		configure();
		readFromFile();
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
	
	
	public void updateWithTransaction(Transaction transaction) throws NotEnoughMoneyException
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
	
	public void addNewPeer(PayInformation pay_information)
	{
		balance_list.put(pay_information.getPublicKey(), pay_information.getAmount());
	}
	
	
	private void readFromFile()
	{
		try
		{
			
			FileInputStream fis = new FileInputStream(path_to_file);
			int x;
			byte[] b;
			String s ="";
			while((x=fis.available())!=0)
			{
				b=new byte[x];
				fis.read(b);
				s+=new String(b);
			}
			
			fis.close();
		}catch(Exception e)
		{
			DebugManager.alert(e);
		}
		
		
		
	}
	
	
	private void saveToFile()
	{
		try
		{
		byte[] b = SerializationManager.saveObjectToString(this).getBytes();
		FileOutputStream fos = new FileOutputStream(path_to_file);	
		fos.write(b);
		fos.close();
		}catch (Exception e)
		{
			DebugManager.alert(e);
		}
	}
	
	
	@Override
	public void configure()
	{
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(PropertiesManager.PATH_TO_PROPERTIES_FILE));
			path_to_file = properties.getProperty("PATH_TO_BALANCE_REGISTER");
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
	}
	//REST


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
	
	
	private class XSerializableType implements XSerializable
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
	
	
	public void update(Block block)
	{
		try
		{
			
		
		ArrayList<Transaction> transactions = (ArrayList<Transaction>) block.getTransactions();
		for(Transaction t:transactions)
		{
			updateWithTransaction(t);
		}
		
		}catch(NotEnoughMoneyException e)
		{
			DebugManager.alert(e);
		}
	}
	
}
class AddressNotInBalanceRegisterException extends Exception
{
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
