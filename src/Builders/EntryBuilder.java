package Builders;

import Blockchain.Entry;

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
				
				if(!isReady()) throw new Exception("Couldn't build Entry");
				Entry result = new Entry((Entry)this);
				reset();
				return result;
			}


			@Override
			public void loadPartFromString(String s) throws Exception {
				String[] information=s.split(";");
				if(information.length!=5) throw new Exception("Couldn't build Exit");
					
					this.IP=information[0];
					this.hash_rate=Double.parseDouble(information[1]);
					this.public_key=information[2];
					this.time=Long.parseLong(information[3]);
					this.signature=information[4];
			}




			@Override
			public void reset() {
				
				signature=null;
				public_key=null;
				this.hash_rate=-1;
				this.IP=null;
				
			}


			@Override
			public boolean isReady() {
			
				if(signature==null) return false;
				if(public_key==null) return false;
				if(time==-1) return false;
				if(!isSignatureValid()) return false;
				if(hash_rate==-1) return false;
				if(IP==null) return false;
				return true;
			}


			public void setData(String public_key, String IP, double hash_rate)
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
