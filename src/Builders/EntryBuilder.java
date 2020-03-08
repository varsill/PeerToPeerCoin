package Builders;

import Blockchain.Entry;
import Managers.SerializationManager;

public class EntryBuilder extends Entry implements Builder {
	//Singleton
	
			private EntryBuilder()
			{
			}
			
	 		private EntryBuilder(Entry e)
			{
				super(e);
				
			}
			static public EntryBuilder getInstance()
			{
				SingletonHolder.INSTANCE.reset();
				return SingletonHolder.INSTANCE;
			}
			
			static public EntryBuilder getInstance(Entry e)
			{
				SingletonHolder.INSTANCE=new EntryBuilder(e);
				return SingletonHolder.INSTANCE;
			}
			
			
			static private class SingletonHolder
			{
				static EntryBuilder INSTANCE = new EntryBuilder();
			}

			//Rest

			@Override
			public Object createPart() throws Exception {
				
				isReady();
				Entry result = new Entry((Entry)this);
				reset();
				return result;
			}


			@Override
			public void loadPartFromString(String s) throws Exception {
				String[] information=s.split(SerializationManager.SEPARATOR);
				if(information.length!=5) throw new Exception("Couldn't build Exit");
					
					this.IP=SerializationManager.unescape(information[0]);
					this.hash_rate=Double.parseDouble(information[1]);
					this.public_key=SerializationManager.unescape(information[2]);
					this.time=Long.parseLong(information[3]);
					this.signature=SerializationManager.unescape(information[4]);
			}




			@Override
			public void reset() {
				
				signature=null;
				public_key=null;
				this.hash_rate=-1;
				this.IP=null;
				
			}


			@Override
			public void isReady() throws Exception{
			
				if(signature==null) throw new Exception("Signature is not set");
				if(public_key==null) throw new Exception("Public Key is not set");
				if(time==-1) throw new Exception("Time is not set");
				if(!isSignatureValid()) throw new Exception("Signature is invalid");
				if(hash_rate==-1) throw new Exception("Hashrate is not set");
				if(IP==null) throw new Exception("IP is not set");
				
			}


			public void createEntryFromScratch(String public_key, String IP, double hash_rate)
			{
				this.public_key=public_key;
				this.hash_rate=hash_rate;
				this.IP=IP;
				sign();
			}

			@Override
			public void prepareNew() {
				this.time=System.currentTimeMillis();
				sign();
				
			}

}
