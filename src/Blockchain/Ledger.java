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

import Builders.BlockBuilder;
import Managers.Configurable;
import Managers.DebugManager;
import Managers.PropertiesManager;
import Managers.SerializationManager;


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
				
				if(BlockBuilder.getInstance(block).isReady())
				{
					try
					{
						
					if(block.getPreviousHash().equals(getLastBlock().getHash()))
					{
						list_of_blocks.add(block);
						balance_register.update(block);
						active_peers_register.update(block);
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
				try
				{
					
				
				for(int i =0; i<how_many_to_delete; i++)
				{
					if(writeBlockToFile(list_of_blocks.get(i))) list_of_blocks.remove(i);
					else return;
				}
				}
				catch(Exception e)
				{
					DebugManager.alert(e);
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
				catch (Exception e) 
				{
					DebugManager.alert(e);
				}
				return true;
			}
			
			private boolean writeBlockToFile(Block block, File file) throws Exception
			{
				
				FileOutputStream fos = new FileOutputStream(file);
				String s = SerializationManager.saveObjectToString(block);
				byte b[] =s.getBytes();
				fos.write(b);
				fos.close();
				return true;
				
			}
			
			
			private boolean writeBlockToFile(Block block) throws Exception
			{
				String path = path_to_blockchain+"/block"+Integer.toString(block.getID())+".blo";
				FileOutputStream fos = new FileOutputStream(path);
				String s = SerializationManager.saveObjectToString(block);
				byte b[] =s.getBytes();
				fos.write(b);
				fos.close();
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
			
			
			private Block readBlockFromFile(File file) throws Exception
			{
				String s="";
				byte[] b;
				int x;
				
					FileInputStream fis = new FileInputStream(file);
					while(( x = fis.available())!=0)
					{
						b=new byte[x];
						fis.read(b);
						s+=new String(b);
					}
				fis.close();
				
				BlockBuilder block_builder = BlockBuilder.getInstance();
				block_builder.loadPartFromString(s);
				
				Block block = (Block) block_builder.createPart();
				
				if(block==null) throw new CouldntReadBlockException();
				
				return block;
				
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