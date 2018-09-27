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
import Builders.PrizeBuilder;
import Builders.TransactionBuilder;
import Communication.P2PConnection;
import Managers.Configurable;
import Managers.DebugManager;
import Managers.PropertiesManager;
import Managers.SerializationManager;
import Security.AsymetricCipherManager;
import Builders.PrizeBuilder;

public class Ledger implements Configurable {
	
	private ArrayList<Block> list_of_blocks = null; 
	private String path_to_blockchain="";
	private String path_to_balance_register="";
	private String path_to_active_peers_register = "";
	private ActivePeersRegister active_peers_register = null;
	private BalanceRegister balance_register = null;
	private ArrayList<Parcel> unprocessed_parcels=null;
	private ArrayList<Peer> previous_network_state=null;
	private int T=0;
	public static double PRIZE = 2*10;// 10 coins for miner who has mined the block and 10 coins for rest of the miners, proportionally for their effort in minning the previous block
	AsymetricCipherManager asymetric_cipher_manager=null;
	//Singleton
	
			private Ledger() 
			{
				list_of_blocks = new ArrayList<Block>();
				active_peers_register = ActivePeersRegister.getInstance();
				balance_register = BalanceRegister.getInstance();
				unprocessed_parcels=new ArrayList<Parcel>();
				asymetric_cipher_manager=AsymetricCipherManager.getInstance();
				T=10 * 60; //10 min in seconds
				//DO USUNIECIA
				
				
				
				
				previous_network_state=new ArrayList<Peer>();
				previous_network_state.add(new Peer("192.54.53.4", 100, "dupa_blada"));
				previous_network_state.add(new Peer("192.6.51.44", 300, "chuj_wam_w_dupe"));
				previous_network_state.add(new Peer("145.63.56.4", 560, "i tobie tez"));
				
				
				
				
				
				
				configure();
				loadLastBlock();
				loadActivePeersRegisterFromFile();
				loadBalanceRegisterFromFile();
				System.out.println("Last block has been loaded");
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
			
			
			//GET
			
			private Block getLastBlock() 
			{
				if(list_of_blocks==null||list_of_blocks.size()==0) return null;
				return list_of_blocks.get(list_of_blocks.size()-1);
			}
			
			
			public Block getBlock(int id)
			{
				Block result;
				for(int i=0; i<list_of_blocks.size(); i++)
				{
					result = list_of_blocks.get(i);
					if(result.getID()==id)
					{
						return result;
					}
				}
				return null;
			}
			
			
			public Block getBlock(String hash)
			{
				Block result;
				for(int i=0; i<list_of_blocks.size(); i++)
				{
					result = list_of_blocks.get(i);
					if(result.getHash()==hash)
					{
						return result;
					}
				}
				return null;
			}
			
			
			
		
			
			//CREATE
			
			
			public Block createBlock()
			{
				BlockBuilder builder = BlockBuilder.getInstance();
				Block previous_block = getLastBlock();
				builder.createBlockFromScratch(previous_block.getID(), getDifficulty(active_peers_register.getNetworksHashRate()), previous_block.getHash());
				PrizeBuilder prize_builder = PrizeBuilder.getInstance();
				prize_builder.createPrizeFromScratch(AsymetricCipherManager.getInstance().getPublicKeyAsString());
				prize_builder.prepareNew();
				Prize prize=null;
				try {
					prize = (Prize) prize_builder.createPart();
				} catch (Exception e1) {
					DebugManager.alert(e1);
				}
				
				addPrize(prize);
				for(Parcel p:unprocessed_parcels)
				{
					builder.addParcel(p);
				}
				
				builder.prepareNew();
				
				
				try {
					return (Block) builder.createPart();
				} catch (Exception e) {
					DebugManager.alert(e);
				}
				return null;
				
				
				
			}
			
			
			
			private int getDifficulty(double hash_rate) {
				long number_of_hashes = (long) ((long) T*hash_rate);
				int result =(int) Math.ceil(Math.log(number_of_hashes)/Math.log(2));
				return result;
			}
			
			//ADD

			
			public void addTransaction(Transaction transaction)
			{
				try
				{
					this.unprocessed_parcels.add(transaction);
				}catch(Exception e)
				{
					DebugManager.alert(e);
				}
				
			}
			
			public void addPrize(Prize prize)
			{
				try
				{
					this.unprocessed_parcels.add(prize);
					
				}
				catch(Exception e)
				{
					DebugManager.alert(e);
				}
			}
			
			
			public void addEntry(Entry entry)
			{
				try
				{
					this.unprocessed_parcels.add(entry);
				}catch(Exception e)
				{
					DebugManager.alert(e);
				}
			
			}
			
			
			
			public void addExit(Exit exit)
			{
				try
				{
				
				this.active_peers_register.update(exit);
				this.unprocessed_parcels.add(exit);
				}
				catch(Exception e)
				{
					DebugManager.alert(e);
				}
			}
			
			

			public boolean addBlock(Block block)
			{
				
				if(BlockBuilder.getInstance(block).isReady())
				{
					try
					{
					if(getLastBlock()==null)
					{
						list_of_blocks.add(block);
						balance_register.update(block);
						active_peers_register.update(block);
						PrizeBuilder.getInstance().saveNetworkState();
						return true;
					}
					
					
					if(block.getPreviousHash().equals(getLastBlock().getHash()))
					{
						list_of_blocks.add(block);
						balance_register.update(block);
						active_peers_register.update(block);
						PrizeBuilder.getInstance().saveNetworkState();
						return true;
					}
					throw new Exception("Previous hash of the new block doesn't match the hash of our last block");
					}
					catch(Exception e)
					{
						DebugManager.alert(e);
						return false;
					}
				}
				return false;
			}
			
		
			
			
			//WRITE
			
			public void writeBlockViaNetwork(String IP, int port, Block block)
			{
				P2PConnection p2p = P2PConnection.getInstance();
				p2p.connect(IP, port);
				try {
					String s=SerializationManager.saveObjectToString(block);
					p2p.write(IP,s);
				} catch (Exception e) {
					DebugManager.alert(e);
				}
				
			}
			
			
			
			
			private boolean writeBlockToFile(Block block)
			{
				try
				{
					
				
				String path = path_to_blockchain+"/block#"+Integer.toString(block.getID())+".blo";
				FileOutputStream fos = new FileOutputStream(path);
				String s = SerializationManager.saveObjectToString(block);
				byte b[] =s.getBytes();
				fos.write(b);
				fos.close();
				return true;
				}catch(Exception e)
				{
					DebugManager.alert(e);
					return false;
				}
			}
			
			
			private boolean writeBalanceRegisterToFile()
			{
				try
				{
				if(this.balance_register==null) return false;
				FileOutputStream fos = new FileOutputStream(path_to_balance_register);
				fos.write(balance_register.saveToString().getBytes());
				fos.close();
				}catch(Exception e)
				{
					DebugManager.alert(e);
					return false;
				}
				return true;
				
			}
			
			
			private boolean writeActivePeersRegisterToFile()
			{
				try
				{
				if(this.active_peers_register==null) return false;
				FileOutputStream fos = new FileOutputStream(path_to_active_peers_register);
				fos.write(active_peers_register.saveToString().getBytes());
				fos.close();
				}catch(Exception e)
				{
					DebugManager.alert(e);
					return false;
				}
				return true;
			}
			
			
			//READ
			
			
			private Block readBlockFromString(String s)
			{
				BlockBuilder builder = BlockBuilder.getInstance();
				builder.loadPartFromString(s);
				Block block = null;
				try {
					 block = (Block) builder.createPart();
				} catch (Exception e) {
					
					DebugManager.alert(e);
				}
				return block;
			
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
			
			
			//LOAD = READ+ADD

			public boolean loadBlockFromString(String s)
			{
				Block block = readBlockFromString(s);
				if(block==null) return false;
				addBlock(block);
				return true;
			}
			
			
			public boolean loadBalanceRegisterFromFile()
			{
				try
				{
				
					FileInputStream fis = new FileInputStream(path_to_balance_register);
					byte[] b = new byte[fis.available()];
					fis.read(b);
					String s = new String(b);
					balance_register.loadRegisterFromString(s);					
					fis.close();
				
				}catch(Exception e)
				{
					DebugManager.alert(e);
					return false;
				}
				return true;
				
				
			}
			
			
			private boolean loadLastBlock()
			{
				File directory = new File(path_to_blockchain);
				if(!directory.isDirectory()) return false;
				String[] file_names = directory.list();
				int max_number = 0;
				
				for(int i=0; i<file_names.length; i++)
				{
					String number_chars = file_names[i].substring(file_names[i].indexOf('#')+1, file_names[i].indexOf('.'));
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
			
			
			public boolean loadActivePeersRegisterFromFile()
			{
				try
				{
				
					FileInputStream fis = new FileInputStream(path_to_active_peers_register);
					byte[] b = new byte[fis.available()];
					fis.read(b);
					String s = new String(b);
					active_peers_register.loadRegisterFromString(s);					
					fis.close();
				
				}catch(Exception e)
				{
					DebugManager.alert(e);
					return false;
				}
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