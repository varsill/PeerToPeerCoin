package Blockchain;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
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
		return new String(hash_manager.digest(createString()));
	}
	
	protected String digest(String msg)
	{
		
		if(hash_manager==null) hash_manager = new HashManager();
		return new String(hash_manager.digest(msg));
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
		byte[] array_of_hashs_bytes = hash.getBytes();
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
		return isHashProper(new String(hash_manager.digest(createString())));
	}
	
	
	public List<Parcel> getParcels()
	{
		return this.list_of_parcels;
	}
	
	protected void setMerkleRoot()
	{
	/*	ArrayList<String> list_of_hashes = new ArrayList<String>();
		ArrayList<String> new_list_of_hashes = new ArrayList<String>();
		for(Parcel p: list_of_parcels)
		{
			list_of_hashes.add(p.getHash());
		}
		
		while(list_of_hashes.size()!=1)
		{
			for(int i=0; i<list_of_hashes.size()/2; i++)
			{
				new_list_of_hashes.add(new String(digest((list_of_hashes.get(2*i)+list_of_hashes.get(2*i+1)))));
			}
			if(list_of_hashes.size()%2==1) new_list_of_hashes.add(list_of_hashes.get(list_of_hashes.size()-1));
			list_of_hashes=(ArrayList<String>) new_list_of_hashes.clone();
			new_list_of_hashes.clear();
		}
		
		merkle_root=list_of_hashes.get(0);
		*/
		
		String s_trans = "";
		String s_entries="";
		String s_exits="";
		
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
			String s = s_trans+s_entries+s_exits;
			this.merkle_root= new String(digest(s));
		}
	}

	@Override
	public String[] getListOfObjectNames() {
		String[] s = {"nonce", "difficulty", "previous_hash", "ID", "time"};
		return s;
	}

	@Override
	public void addChildToList(Object object) {
		// TODO Auto-generated method stub
		
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
