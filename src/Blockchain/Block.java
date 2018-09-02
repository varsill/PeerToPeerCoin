package Blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Security.HashManager;

public class Block implements Serializable
{

	
	
	private static final long serialVersionUID = 1L;
	private List<Parcel> list_of_parcels = null; //list of parcels, like transactions, info about entries and exits
	private long nonce = 0;
	private int difficulty = 0;
	private String previous_hash = null;
	private String hash = null;
	private int ID=0;
	private HashManager hash_manager;
	
	
	
	public Block(int ID, int difficulty, String previous_hash)
	{
		this.ID = ID;
		this.difficulty = difficulty;
		this.previous_hash = previous_hash;
		this.list_of_parcels=new ArrayList<Parcel>();
	}
	
	
	public void addParcel(Parcel parcel)
	{
		this.list_of_parcels.add(parcel);
	}
	
	
	public void removeParcel(int id)
	{
		this.list_of_parcels.remove(id);
	}
	
	
	private String createDataString()
	{
		String result = "";
		result=String.valueOf(this.ID)+String.valueOf(this.difficulty)+this.previous_hash;
		for(int i=0; i<list_of_parcels.size(); i++)
		{
			result = result+list_of_parcels.get(i).toString();
		}
		return result;
	}
	
	
	private String createString()
	{
		String result = createDataString();
		result=result+String.valueOf(nonce);
		return result;
	}
	
	
	public String digest()
	{
		
		if(hash_manager==null) hash_manager = new HashManager();
		return new String(hash_manager.digest(createString()));
	}
	
	
	public String digest(String data)
	{
		if(hash_manager==null) hash_manager = new HashManager();
		return new String(hash_manager.digest(data.getBytes()));
	}
	
	
	public void proof()
	{
		nonce  = -1;
		String data = createDataString();
		String data_with_nonce;
		
		do
		{
			nonce++;
			data_with_nonce=data+String.valueOf(nonce);
		}while(!isHashProper(digest(data_with_nonce)));
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
	
	
	public boolean isHashProper(String hash)
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
		String hash  = digest();
		return isHashProper(hash);
	}
	
	
}
