package Builders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import Blockchain.BalanceRegister;
import Blockchain.Block;
import Blockchain.Entry;
import Blockchain.Exit;
import Blockchain.Ledger;
import Blockchain.Parcel;
import Blockchain.Prize;
import Blockchain.Transaction;
import Managers.DebugManager;
import Managers.PropertiesManager;
import Managers.SerializationManager;
import Security.HashManager;

public class BlockBuilder extends Block implements  Builder {
	 
	//Singleton
	
		private BlockBuilder()
		{
		}
		
		private BlockBuilder(Block b)
		{
			super(b);
		}
		
		static public BlockBuilder getInstance()
		{
			SingletonHolder.INSTANCE.reset();
			return SingletonHolder.INSTANCE;
		}
		
		static public BlockBuilder getInstance(Block b)
		{
			SingletonHolder.INSTANCE=new BlockBuilder(b);
			return SingletonHolder.INSTANCE;
		}
		
		
		static private class SingletonHolder
		{
			static BlockBuilder INSTANCE = new BlockBuilder();
		}
		
		
	//Rest
	@Override
	public Object createPart() throws Exception 
	{
		isReady();
		Block result = new Block((Block)this);
		return  result;
		
	}

	
	@Override
	public void loadPartFromString(String s)
	{
		try
		{
		//Main block stuff
			String[] info = SerializationManager.makeSubstrings(s, "#BEGIN", "<Blockchain", SerializationManager.SEPARATOR);
			if(info.length!=5)
			{
				return;
			}
			nonce = Long.parseLong(info[0]);
			difficulty = Integer.parseInt(info[1]);
			previous_hash = info[2];
			ID = Integer.parseInt(info[3]);
			time=Long.parseLong(info[4]);
			Builder builder = null;
			//Prize 
			info = SerializationManager.makeSubstrings(s,  "<Blockchain.Prize>", "</Blockchain.Prize>","&");
			PrizeBuilder prize_builder = PrizeBuilder.getInstance();
			prize_builder.loadPartFromString(info[0]);
			addParcel((Prize)prize_builder.createPart());
			
			
		//Transactions
			info = SerializationManager.makeSubstrings(s, "<Blockchain.Transaction>", "</Blockchain.Transaction>", "&");
			
			
			
			
			for(int i=0; i<info.length; i++)
			{
				
					builder = TransactionBuilder.getInstance();
					builder.loadPartFromString(info[i]);
					addParcel((Transaction)builder.createPart());
			}
			
			
		//Entries
			info =SerializationManager.makeSubstrings(s, "<Blockchain.Entry>", "</Blockchain.Entry>", "&");
			
			for(int i=0; i<info.length; i++)
			{
				if(info[i].equals("")) continue;
					builder = EntryBuilder.getInstance();
					builder.loadPartFromString(info[i]);
					addParcel((Entry)builder.createPart());
			}
			
		//Exits
			info = SerializationManager.makeSubstrings(s, "<Blockchain.Exit>", "</Blockchain.Exit>", "&");
			
			for(int i=0; i<info.length; i++)
			{
				
					builder = ExitBuilder.getInstance();
					builder.loadPartFromString(info[i]);
					addParcel((Exit)builder.createPart());
			}
		}catch (Exception e)
		{
			DebugManager.alert(e);
		}
		
		setMerkleRoot();
	
		
		return;
			
		
	}
	
	
	@Override
	public void isReady() throws Exception
	{
	
		if(nonce==-1) throw new Exception("Nonce is not set");
		if(list_of_parcels==null) throw new Exception("List of parcels is not set");
		if(difficulty==-1) throw new Exception("Difficulty is not set");
		if(previous_hash == null) throw new Exception("Previous hash is not set");
		if(ID==-1) throw new Exception("ID is not set");
		if(time==-1) throw new Exception("Time is not set");
		if(merkle_root==null) throw new Exception("Merkle root is not set");
		if(!isHashProper()) throw new Exception("Hash is inproper");
		if(list_of_parcels.size()==0) throw new Exception("List of parcels is empty");
		
	
		
	}

	
	
	@Override
	public void reset()
	{
		list_of_parcels=null;
		nonce=-1;
		difficulty=-1;
		previous_hash=null;
		ID=-1;
		time=-1;
		merkle_root=null;
		this.list_of_parcels=new ArrayList<Parcel>();
	}
	
	
	public void createBlockFromScratch(int ID, int  difficulty, String previous_hash)
	{
		this.ID=ID;
		this.difficulty=difficulty;
		this.previous_hash=previous_hash;
		
	}
	
	@Override
	public void prepareNew()
	{
		this.time=System.currentTimeMillis();
		setMerkleRoot();
		proof();
		
	}
	
	@Override
	public void addParcel(Parcel parcel)
	{
		super.addParcel(parcel);
	}
	
	@Override 
	public void removeParcel(int id)
	{
		super.removeParcel(id);
	}
	
	
}
