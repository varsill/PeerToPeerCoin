 package Blockchain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

import Managers.Configurable;
import Managers.DebugManager;
import Managers.PropertiesManager;


public class Ledger implements Configurable {
	
	private ArrayList<Block> list_of_blocks = null; 
	private String path_to_blockchain="";
	private ActivePeersRegister active_peers_register = null;
	private BalanceRegister balance_register = null;
	//Singleton
	
			private Ledger() 
			{
				list_of_blocks = new ArrayList<Block>();
				active_peers_register = ActivePeersRegister.getInstance();
				balance_register = BalanceRegister.getInstance();
				configure();
			}
			
				
			public static Ledger getInstance()
			{
				return SingletonHolder.INSTANCE;
			}
			
			
			private static class SingletonHolder
			{
				static Ledger INSTANCE = new Ledger();
			}
			
	//REST
			@Override
			public void configure()
			{
				Properties properties = new Properties();
				try
				{
					properties.load(new FileInputStream(PropertiesManager.PATH_TO_PROPERTIES_FILE));
					path_to_blockchain = properties.getProperty("PATH_TO_BLOCKCHAIN");
				}
				catch(Exception e)
				{
					DebugManager.alert(e);
				}
				
			}
			
			
			public void scrollThroughLedger()
			{
				
				
				
				
			}
	
			
			private boolean addBlock(Block block)
			{
				
				if(verify(block))
				{
					try
					{
						
					if(block.getPreviousHash().equals(getLastBlock().getPreviousHash()))
					{
						list_of_blocks.add(block);
						return true;
					}
					
					}
					catch(Exception e)
					{
						DebugManager.alert(e);
						return false;
					}
				}
				return false;
			}
			
			
			private Block getLastBlock() throws BlockchainIsEmptyException
			{
				if(list_of_blocks==null||list_of_blocks.size()==0) throw new BlockchainIsEmptyException();
				return list_of_blocks.get(list_of_blocks.size()-1);
			}
			
			
			private void reduceTail(int how_many_blocks_to_be_left)
			{
				int how_many_to_delete = list_of_blocks.size()-how_many_blocks_to_be_left;
				if(how_many_to_delete<=0) return;
				for(int i =0; i<how_many_to_delete; i++)
				{
					if(writeBlockToFile(list_of_blocks.get(i))) list_of_blocks.remove(i);
					else return;
				}
				return;
			}
			
			
			private boolean loadLastBlock()
			{
				File directory = new File(path_to_blockchain);
				if(!directory.isDirectory()) return false;
				String[] file_names = directory.list();
				int max_number = 0;
				
				for(int i=0; i<file_names.length; i++)
				{
					String number_chars = file_names[i].substring(file_names[i].indexOf('#'), file_names[i].indexOf('.'));
					int number = Integer.parseInt(number_chars);
					if(number>max_number) max_number=number;
					}
				
				try
				{
					addBlock(readBlockFromFile(new File(path_to_blockchain+"/block#"+max_number+".blo")));
				} 
				catch (CouldntReadBlockException e) 
				{
					DebugManager.alert(e);
				}
				return true;
			}
			
			private boolean writeBlockToFile(Block block)
			{
				ObjectOutputStream ous=null;
				if(block==null) return false;
				try
				{
					ous = new ObjectOutputStream(new FileOutputStream(path_to_blockchain+"/block#"+block.getID()+".blo"));
					oos.writeObject(block);
				}
				catch(Exception e)
				{
				if(ous!=null)
					try
					{
						ous.close();
					} 
					catch (Exception e1) 
					{
						DebugManager.alert(e1);
					}
					DebugManager.alert(e);
				}
				
				try
				{
					ous.close();
				}
				catch (Exception e)
				{
					DebugManager.alert(e);
				}
				return true;
			}
			
			
			
			private void readBlocksFromNetwork(InputStream stream)
			{
				ObjectInputStream ois = null;
				try
				{
					ois = new ObjectInputStream(stream);
				}
				catch(Exception e)
				{
					DebugManager.alert(e);
				}
				try {
					while(ois.available()>0)
					{
						addBlock((Block)ois.readObject());
					}
				}
				catch (Exception e)
				{
					DebugManager.alert(e);
				}
				
			}
			
			
			private Block readBlockFromFile(File file) throws CouldntReadBlockException
			{
				Block block = null;
				ObjectInputStream ois = null;
				try
				{
				    ois = new ObjectInputStream(new FileInputStream(file));
					block = (Block) ois.readObject();
				}catch(Exception e)
				{
					DebugManager.alert(e);
				}
				if(block==null) throw new CouldntReadBlockException();
				return block;
			}
			
		
			private boolean verify(Block block)
			{
				if(block==null) return false;
				if(!block.isHashProper()) return false;
				Parcel parcel = null;
				ArrayList<Parcel> parcels = (ArrayList<Parcel>) block.getParcels();
				for(int i=0; i<parcels.size(); i++)
				{
					parcel = parcels.get(i);
					if(!parcel.isSignatureValid()) return false;
					if(parcel.getClass().equals(Transaction.class))
					{
						//verifying transaction
						if(!verify((Transaction)parcel)) return false;
					}
					
					else if(parcel.getClass().equals(Entry.class))
					{
						//verifying entry
						if(!verify((Entry)parcel) return false;
					}
					

					else if(parcel.getClass().equals(Exit.class))
					{
						//verifying exit
						if(!verify((Exit)parcel)) return false;
					}
				}
				return true;
			}
			
			
			private boolean verify(Transaction transaction)
			{
				return true;
			}
			
			
			private boolean verify(Entry entry)
			{
				return true;
			}
			
			
			private boolean verify(Exit exit)
			{
				return true;
			}
}


			




class BlockchainIsEmptyException extends Exception
{
	
	private String msg = "";

	
	public BlockchainIsEmptyException()
	{
		
	}
	
	
	public BlockchainIsEmptyException(String msg)
	{
		this.msg = msg;
	}
	
	
	@Override
	public String getMessage()
	{
		return "Blockchain is empty. "+msg;
	}
}


class CouldntReadBlockException extends Exception
{
	@Override
	public String getMessage()
	{
		return "Couldn't read block";
	}
}