package Blockchain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import Managers.DebugManager;
import Managers.SerializationManager;

public class ActivePeersRegister extends Register implements XSerializable, Cloneable {
	
	private Hashtable<String, Peer> list_of_peers=null;
	private double hash_rate=0;
	
	//Singleton
	private ActivePeersRegister()
	{
		this.list_of_peers = new Hashtable<String, Peer>();
	}
	
	static public ActivePeersRegister getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	static private class SingletonHolder
	{
		static	ActivePeersRegister INSTANCE = new ActivePeersRegister();
	}
	
	//Rest
	
	public double getNetworksHashRate()
	{
		return this.hash_rate;
	}
	
	
	public ArrayList<Peer> getPeersList()
	{
		Set<String> keys = list_of_peers.keySet();
		Peer p;
		ArrayList<Peer> result = new ArrayList<Peer>();
		for(String k:keys)
		{
			p = list_of_peers.get(k);
			if(p!=null)
				{
					p.namePeer(k);
				}
				result.add(p);
			
		}
		return result;
		
	}
	
	
	public void update(Entry e) throws Exception
	{
		
		if(!e.isSignatureValid()) throw new Exception("Signature is valid");
		Peer peer = list_of_peers.get(e.public_key);
		if(peer==null) 
			{
				peer = new Peer(e.IP, e.hash_rate);
				list_of_peers.put(e.public_key, peer);
			}
		else
		{
			hash_rate=hash_rate-peer.getHashRate()+e.hash_rate;
			peer.updatePeer(e.IP, e.hash_rate);
			
		}
	
		
	}
	
	
	
	public void update(Block b) throws Exception
	{
		ArrayList<Entry> entries = (ArrayList<Entry>)b.getEntries();
		ArrayList<Exit> exits = (ArrayList<Exit>)b.getExits();
		
		for(Entry e:entries)
		{
			update(e);
		}
		
		for(Exit e:exits)
		{
			update(e);
		}
		
		
	}
	
	public void update(Exit e) throws Exception
	{
		if(!e.isSignatureValid()) throw new Exception("Signature is valid");
		Peer peer = list_of_peers.get(e.public_key);
		if(peer==null) return;
		
		hash_rate-=peer.getHashRate();
		
		list_of_peers.remove(e.public_key); 
	}
	
	
	public String saveToString() throws Exception
	{
		String s = SerializationManager.saveObjectToString(this);
		return s;
	}
	
	
	public void loadRegisterFromString(String s) throws Exception
	{
		
		this.list_of_peers = new Hashtable<String, Peer>();
		this.hash_rate=0;
		String[] string_array = SerializationManager.makeSubstrings(s, ">", "</", "&");
		
		for(String x: string_array)
		{
			String[] string_array2 = SerializationManager.makeSubstrings(x, "#BEGIN", "#END", SerializationManager.SEPARATOR);
			if(string_array2.length!=3) throw new Exception("Wrong number of parameters. Couldn't create ActivePeersRegister from string");
			 double hash_rate =Double.parseDouble(string_array2[2]);
			list_of_peers.put(string_array2[0], new Peer(string_array2[1], hash_rate));
			this.hash_rate+=hash_rate;
			
		}
		
	}
	
	public String getIPByPublicKey(String public_key)
	{
		Peer p = list_of_peers.get(public_key);
		if(p==null )return null;
		return p.getIP();
	}

	@Override
	public String[] getListOfObjectNames() {
		
		return null;
	}

	@Override
	public XSerializable[] getObjectList() {
		Set<String>public_keys = list_of_peers.keySet();
		
		XSerializableType x;
		
		XSerializableType[] result = new XSerializableType[public_keys.size()];
		
		int i=0;
		for(String key: public_keys)
		{
			x=new XSerializableType();
			x.public_key = key;
			x.IP=((Peer)list_of_peers.get(key)).getIP();
			x.hash_rate = ((Peer)list_of_peers.get(key)).getHashRate();
			result[i]=x;
			i++;
		}
		return result;
	}
	
	
	public double getHashRateByPublicKey(String public_key)
	{
		Peer p = list_of_peers.get(public_key);
		if(p==null ) return 0;
		return p.getHashRate();
	}
	
	public ActivePeersRegister getSavedActivePeersRegister()
	{
		try {
			return (ActivePeersRegister) this.clone();
		} catch (CloneNotSupportedException e) {
			DebugManager.alert(e);
		}
		return null;
	}
	
	private class XSerializableType implements XSerializable
	{
		public String public_key="";
		public String IP="";
		public double hash_rate =0;
		@Override
		public String[] getListOfObjectNames() {
			String[] s = {"public_key", "IP", "hash_rate"};
			return s;
		}
		@Override
		public XSerializable[] getObjectList() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}



