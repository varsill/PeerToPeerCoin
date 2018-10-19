package Blockchain;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Managers.DebugManager;
import Security.HashManager;

public class Block implements XSerializable
{


	protected List<Parcel> list_of_parcels = null; //list of parcels, like transactions, info about entries and exits
	protected long nonce = -1;
	protected int  difficulty = -1;
	protected String previous_hash = null;
	protected int ID=-1;
	protected HashManager hash_manager;
	protected long time = -1;
	protected String merkle_root=null;
	
	
	
	public int getID()
	{
		return this.ID;
	}
	
	public Block()
	{
		
	}	
	
	public Block(Block b)
	{
		this.difficulty=b.difficulty;
		this.ID=b.ID;
		this.list_of_parcels=b.list_of_parcels;
		this.nonce=b.nonce;
		this.previous_hash=b.previous_hash;
		this.time = b.time;
		setMerkleRoot();
		
	}
	
	public String getPreviousHash()
	{
		return previous_hash;
	}
	
	
	protected void addParcel(Parcel parcel)
	{
		
		this.list_of_parcels.add(parcel);
	}
	
	
	protected void removeParcel(int id)
	{
		this.list_of_parcels.remove(id);
	}
	
	
	private String createDataString()
	{
		String result="";
		result+=Long.toString(difficulty);
		result+=previous_hash;
		result+=Integer.toString(ID);
		result+=Long.toString(time);
		result+=merkle_root;
		return result;
	}
	
	
	private String createString()
	{
		String result = createDataString();
		
		result=result+String.valueOf(nonce);
		return result;
	}
	
	
	protected String digest()
	{
		
		if(hash_manager==null) hash_manager = new HashManager();
		try {
			return new String(hash_manager.digest(createString()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected String digest(String msg)
	{
		
		if(hash_manager==null) hash_manager = new HashManager();
		try {
			return new String(hash_manager.digest(msg), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String getHash()
	{
		if(hash_manager==null) hash_manager = new HashManager();
		try {
			return new String(hash_manager.digest(createString()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void proof()
	{
	
		nonce  = -1;
		String data = createDataString();
		String data_with_nonce="";
		String hash=null;
		do
		{
			nonce++;
			data_with_nonce=data+String.valueOf(nonce);
			hash=digest(data_with_nonce);
		}while(!isHashProper(hash));
	}
	
	
	public void proof(long beginning, long end)
	{
		
		nonce = beginning-1;
		String data = createDataString();
		String data_with_nonce;
		
		do
		{
			nonce++;
			data_with_nonce=data+String.valueOf(nonce);
		}while(!isHashProper(digest(data_with_nonce))&&nonce<=end);

		
	}
	
	
	protected boolean isHashProper(String hash)
	{
		byte[] array_of_hashs_bytes = new byte[0];
		try {
			array_of_hashs_bytes = hash.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int number_of_full_bytes = difficulty/8;
		int rest_bits = difficulty%8;
		if(number_of_full_bytes>array_of_hashs_bytes.length) return false;
		int i;
		
		for(i=0; i<number_of_full_bytes; i++)
		{
			if(array_of_hashs_bytes[i]!=0) return false;
		}
		
		byte last_byte = (byte) (array_of_hashs_bytes[i+1]>>(8-rest_bits));
		
		if(last_byte!=0) return false;
		return true;
		
	}
	
	
	public boolean isHashProper()
	{
		String s = createString();
		try {
			return isHashProper(new String(hash_manager.digest(s), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public List<Parcel> getParcels()
	{
		return this.list_of_parcels;
	}
	
	public List<Transaction> getTransactions()
	{
		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		for(Parcel p: list_of_parcels)
		{
			if(p.getClass().equals(Transaction.class))
			{
				transactions.add((Transaction)p);
			}
		}
		
		return transactions;
	}
	
	
	public List<Entry> getEntries()
	{
		ArrayList<Entry> entries = new ArrayList<Entry>();
		for(Parcel p: list_of_parcels)
		{
			if(p.getClass().equals(Entry.class))
			{
				entries.add((Entry)p);
			}
		}
		
		return entries;
	}
	
	
	public List<Exit> getExits()
	{
		ArrayList<Exit> exits = new ArrayList<Exit>();
		for(Parcel p: list_of_parcels)
		{
			if(p.getClass().equals(Exit.class))
			{
				exits.add((Exit)p);
			}
		}
		
		return exits;
	}
	
	public Prize getPrize()
	{
		
		for(Parcel p:list_of_parcels)
		{
			if(p.getClass().equals(Prize.class))
			{
				return (Prize) p;
			}
		}
		return null;
	}
	
	
	protected void setMerkleRoot()
	{
		
		String s_trans = "";
		String s_entries="";
		String s_exits="";
		String s_prize="";
		for(Parcel p:list_of_parcels)
		{
			if(p.getClass().equals(Transaction.class))
			{
				s_trans+=p.getHash();
			}
			else	if(p.getClass().equals(Entry.class))
			{
				s_entries+=p.getHash();
			}
			else if(p.getClass().equals(Entry.class))
			{
				s_exits+=p.getHash();
			}
			else if(p.getClass().equals(Prize.class))
			{
				s_prize+=p.getHash();
			}
			String s = s_trans+s_entries+s_exits+s_prize;
			this.merkle_root= new String(digest(s));
		}
	}

	@Override
	public String[] getListOfObjectNames() {
		String[] s = {"nonce", "difficulty", "previous_hash", "ID", "time"};
		return s;
	}


	@Override
	public XSerializable[] getObjectList() {
		
		XSerializable[] list = new XSerializable[list_of_parcels.size()];
		int i=0;
		for(Parcel p:list_of_parcels)
		{
			list[i]=(XSerializable) p;
			i++;
		}
		
		return list;
	}
	
	
}
