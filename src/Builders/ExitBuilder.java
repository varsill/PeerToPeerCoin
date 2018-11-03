package Builders;

import Blockchain.Entry;
import Blockchain.Exit;
import Managers.SerializationManager;

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
				isReady();
				Exit result = new Exit((Exit)this);
				reset();
				return result;
			}
			
			
			public Object createPart(String public_key) throws Exception {
				this.public_key=public_key;
				isReady();
				Exit result = new Exit((Exit)this);
				reset();
				return result;
			}


			@Override
			public void loadPartFromString(String s) throws Exception {
				String[] info=s.split(SerializationManager.SEPARATOR);
				
					if(info.length!=3) throw new Exception("Wrong number of arguments to loadPartFromString");
					this.public_key=SerializationManager.unescape(info[0]);
					this.time=Long.parseLong(info[1]);
				
					this.signature=SerializationManager.unescape(info[2]);
				
					
			}


			@Override
			public void reset() {
				signature=null;
				public_key=null;
				time=-1;
				
			}


			@Override
			public void isReady() throws Exception{
				
				if(signature==null) throw new Exception("Signature is not set");
				if(public_key==null) throw new Exception("Public Key is not set");
				if(time==-1) throw new Exception("Time is not set");
				if(!isSignatureValid()) throw new Exception("Signature is invalid");
				
				
			}
			


			@Override
			public void prepareNew() {
			this.time=System.currentTimeMillis();
			sign();
				
			}
			
			
			
}
