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
		
		if(!isReady()) throw new Exception("Couldn't create transaction");
		Prize result = new Prize(this);
		return result;
		
	}

	
	@Override
	 public boolean isReady()
	{
		if(signature==null) return false;
		if(public_key==null) return false;
		if(payer==null) return false;
		if(time==-1) return false;
		if(!isSignatureValid()) return false;
		if(!isPrizeValid()) return false;
		
		return true;
	}
	
	
	@Override
	public void reset()
	{
		 signature=null;
		 public_key=null;
		 payer=null;
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
		String[] information=SerializationManager.makeSubstrings(s, "#BEGIN", "<Blockchain.PayInformation>", ";");
		if(information.length!=3) return;
			
			this.public_key=information[0];
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
	
	
	
	
	@Override
	public void prepareNew()
	{
		this.time=System.currentTimeMillis();
		sign();
	}
	
	
	private boolean isPrizeValid()
	{
		ArrayList<PayInformation> payees = (ArrayList<PayInformation>) getPayees();
		double total_previous_hash_rate=previous_network_state.getNetworksHashRate();
		double prize;
		for(PayInformation p:payees)
		{
			prize=p.getAmount();
			if(p.getPublicKey()==this.public_key) prize-=Ledger.PRIZE;
			if(previous_network_state.getHashRateByPublicKey(p.getPublicKey())/total_previous_hash_rate*Ledger.PRIZE!=prize) return false;
		}
		return true;
	}
	
	
	public void createPrizeFromScratch(String public_key)
	{
		
		addPayee(public_key, Ledger.PRIZE);
		ArrayList<Peer> list = previous_network_state.getPeersList();
		double prize;
		for(Peer p: list)
		{
			prize = p.getHashRate()/previous_network_state.getNetworksHashRate()*Ledger.PRIZE;
			addPayee(p.getPublicKey(), prize);
		}
		
	}
	
	
}
