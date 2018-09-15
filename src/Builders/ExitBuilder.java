package Builders;

import Blockchain.Entry;
import Blockchain.Exit;

public class ExitBuilder extends Exit implements Builder {
	
	//Singleton
	
			private ExitBuilder()
			{
			}
			
			private ExitBuilder(Exit e)
			{
				super(e);
			}
			
			static public ExitBuilder getInstance()
			{
				SingletonHolder.INSTANCE.reset();
				return SingletonHolder.INSTANCE;
			}
			
			static public ExitBuilder getInstance(Exit e)
			{
				SingletonHolder.INSTANCE = new ExitBuilder(e);
				return SingletonHolder.INSTANCE;
				
			}
			
			
			static private class SingletonHolder
			{
				static ExitBuilder INSTANCE = new ExitBuilder();
			}


			@Override
			public Object createPart() throws Exception {
				if(!isReady()) throw new Exception("Couldn't build Entry");
				Exit result = new Exit((Exit)this);
				reset();
				return result;
			}
			
			
			public Object createPart(String public_key) throws Exception {
				this.public_key=public_key;
				if(!isReady()) throw new Exception("Couldn't build Entry");
				Exit result = new Exit((Exit)this);
				reset();
				return result;
			}


			@Override
			public void loadPartFromString(String s) throws Exception {
				String[] info=s.split(";");
				
					if(info.length!=3) throw new Exception("Wrong number of arguments to loadPartFromString");
					this.public_key=info[0];
					this.time=Long.parseLong(info[1]);
				
					this.signature=info[2];
				
					
			}


			@Override
			public void reset() {
				signature=null;
				public_key=null;
				time=-1;
				
			}


			@Override
			public boolean isReady() {
				
				if(signature==null) return false;
				if(public_key==null) return false;
				if(time==-1) return false;
				if(!isSignatureValid()) return false;	
				
				return true;
			}
			


			@Override
			public void prepareNew() {
			this.time=System.currentTimeMillis();
			sign();
				
			}
			
			
			
}
