package Builders;
 
import java.util.ArrayList;

import Blockchain.ActivePeersRegister;
import Blockchain.BalanceRegister;
import Blockchain.Ledger;
import Blockchain.PayInformation;
import Blockchain.Peer;
import Blockchain.Prize;
import Blockchain.Transaction;
import Managers.DebugManager;
import Managers.SerializationManager;
import Security.AsymetricCipherManager;

public class PrizeBuilder extends Prize implements Builder {
	private ActivePeersRegister previous_network_state = null;
//SINGLETON
	private PrizeBuilder()
	{
		
	}
	
	private PrizeBuilder(Prize x)
	{
		
		
	}
	
	static public PrizeBuilder getInstance()
	{
		SingletonHolder.INSTANCE.reset();
		return SingletonHolder.INSTANCE;
		
	}
	
	static class SingletonHolder
	{
		static private PrizeBuilder INSTANCE = new PrizeBuilder();
	}
	
	//REST
	
	public void saveNetworkState()
	{
		previous_network_state = ActivePeersRegister.getInstance().getSavedActivePeersRegister();
	}
	
	
	@Override
	 public Object createPart() throws Exception
	{
		
		
		isReady();
		
		Prize result = new Prize(this);
		return result;
		
	}

	
	@Override
	 public void isReady() throws Exception
	{
		if(signature==null) throw new Exception("Signature is not set");
		if(public_key==null) throw new Exception("Public Key is not set");
		if(time==-1) throw new Exception("Time is not set");
		if(!isSignatureValid()) throw new Exception("Signature is invalid");
		if(!isPrizeValid())throw new Exception("Prize is invalid");
		
	
	}
	
	
	@Override
	public void reset()
	{
		 signature=null;
		 public_key=null;
		 payees_list=new ArrayList<PayInformation>();
		
		 time=-1;
	}
	

	@Override
	public void addPayee(String public_key, double amount)
	{
		if(amount<0)return;
		super.addPayee(public_key, amount);
	}


	@Override
	public void loadPartFromString(String s) throws Exception {
		String[] information=SerializationManager.makeSubstrings(s, "#BEGIN", "<Blockchain.PayInformation>", SerializationManager.SEPARATOR);
		if(information.length!=3) return;
			
			this.public_key=information[0];
			this.time=Long.parseLong(information[1]);
			this.signature=information[2];
			
			
		String[] string_array = SerializationManager.makeSubstrings(s, "<Blockchain.PayInformation>", "</Blockchain.PayInformation>",  SerializationManager.ARRAY_SEPARATOR);
		String public_key;
		double amount;
		String[] parts_of_transaction;
		for(String x:string_array)
		{
			parts_of_transaction = SerializationManager.makeSubstrings(x, "#BEGIN", "#END", SerializationManager.SEPARATOR);
			if(parts_of_transaction.length!=2) throw new Exception("Wrong number of parameters. Couldn't create prize");
			public_key=SerializationManager.unescape(parts_of_transaction[0]);
			amount = Double.parseDouble(parts_of_transaction[1]);
			addPayee(public_key, amount);
		}

	}	
	
	
	
	
	@Override
	public void prepareNew()
	{
		this.time=System.currentTimeMillis();
		sign();
	}
	
	
	private boolean isPrizeValid()
	{
		PayInformation p;
		if(previous_network_state==null||previous_network_state.getPeersList().size()==0) 
		{
			if(payees_list.size()!=1) return false;
			p = payees_list.get(0);
			if(p.getAmount()!=Ledger.PRIZE/2) return false;
			return true;
		}
		
		double total_previous_hash_rate=previous_network_state.getNetworksHashRate();
		double prize;
		
		p=payees_list.get(0);
		prize=p.getAmount();
		if(p.getAmount()!=Ledger.PRIZE/2) return false;
		
		for(int i=1; i<payees_list.size(); i++)
		{
			p=payees_list.get(i);
			prize=p.getAmount();
			
			if(previous_network_state.getHashRateByPublicKey(p.getPublicKey())/total_previous_hash_rate*Ledger.PRIZE/2!=prize) return false;
		}
		return true;
	}
	
	
	public void createPrizeFromScratch()
	{
		this.public_key=AsymetricCipherManager.getInstance().getPublicKeyAsString();
		addPayee(public_key, Ledger.PRIZE/2);
		if(previous_network_state==null)return;
		ArrayList<Peer> list = previous_network_state.getPeersList();
		double prize;
		for(Peer p: list)
		{
			prize = p.getHashRate()/previous_network_state.getNetworksHashRate()*Ledger.PRIZE/2;
			addPayee(p.getPublicKey(), prize);
		}
		
	}
	
	
}
