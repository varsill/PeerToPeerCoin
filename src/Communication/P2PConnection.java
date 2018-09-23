package Communication;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import Managers.Configurable;
import Managers.DebugManager;
import Managers.PropertiesManager;

public class P2PConnection implements Runnable {
	
	
	private  int port;
	private boolean run;
	private int timeout;
	private  InetAddress host = null;
	private Thread thread=null;
	private final static int NUMBER_OF_BYTES_PER_READ = 1024;
	private Selector selector=null;
	private HashMap<String, SocketChannel> list_of_clients = null;
	private ArrayList<String> messages_to_read=null;
	private HashMap<String, ArrayList<String>> messages_to_write=null;
	private Hashtable<String, String> unprepared_messages =null;
	private ServerSocketChannel server_socket_channel=null;
	private volatile boolean is_something_to_read=false;
//Singleton
	private P2PConnection() 
	{
		setRun(false);
		port = 0;
		try {
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			DebugManager.alert(e1);
		}
		timeout = 10000;
		list_of_clients = new HashMap<String, SocketChannel>();
		messages_to_write=new HashMap<String, ArrayList<String>>();
		messages_to_read = new ArrayList<String>();
		unprepared_messages = new Hashtable<String, String>();
		try
		{
			selector = Selector.open();
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
		
	}
	
	
	static public P2PConnection getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	
	
	static private class SingletonHolder
	{
		static private P2PConnection INSTANCE = new P2PConnection();
	}
	
	
	
	
	//REST
	
	private void serve() 
	{
		SocketChannel socket_channel = null;
		SelectionKey key = null;
		ByteBuffer bb = null;
		try
		{
		
		while(run)
		{
			
			if(selector.select()<=0)
				{
					continue; //zobaczyc potem
				}
			
			Set<SelectionKey> selection_keys = selector.selectedKeys();
			 Iterator iterator = selection_keys.iterator();
			    while (iterator.hasNext()) {
				key=(SelectionKey)iterator.next();
				iterator.remove();
				if(key.isAcceptable())
				{
					 server_socket_channel = (ServerSocketChannel)key.channel();
					 socket_channel=server_socket_channel.accept();
					 if(socket_channel!=null) {
						 socket_channel.configureBlocking(false);
						 socket_channel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
						 list_of_clients.put(socket_channel.getRemoteAddress().toString(), socket_channel);
						 System.out.println("Connection Accepted: " + socket_channel.getRemoteAddress());
					 }
					 
					
				}
				
				
				 if(key.isReadable())
				{
						socket_channel = (SocketChannel) key.channel();
						if(socket_channel==null) break;
						
						synchronized(socket_channel)
						{
							
						
						bb = ByteBuffer.allocate(NUMBER_OF_BYTES_PER_READ);
						String result = "";
						byte[] buffer;
						
					/*	while(socket_channel.read(bb)>0) //sprawdzic czy zawsze tak sie konczy;
						{
							buffer=bb.array();
							bb.position().
							result+= new String(buffer);
							if(bb.remaining()<=0) 
							{
								
								bb=ByteBuffer.allocate(NUMBER_OF_BYTES_PER_READ);
							}
						}
						*/
						
						
						int num_read = socket_channel.read(bb);
				
						if(num_read==-1)
						{
							System.out.println("Client: "+socket_channel.getRemoteAddress()+"has closed the connection");
							socket_channel.close();
							key.cancel();
						}
						if(num_read>0)
						{
							buffer = new byte[num_read];
							System.arraycopy(bb.array(), 0, buffer, 0, num_read);
							result = new String(buffer);
							String ip = socket_channel.getRemoteAddress().toString();
							ip=ip.substring(1, ip.indexOf(":"));
							//System.out.println(result+"KONIEC");
							try
							{
								prepareMessages(result, ip);
							}
							catch(Exception e)
							{
								DebugManager.alert(e);
							}
						}
						
						
						
						}
				}
				
				 if(key.isConnectable())
				{
					
					socket_channel  = (SocketChannel) key.channel();
					try {
				         while (socket_channel.isConnectionPending()) {
				          
				            list_of_clients.put(socket_channel.getRemoteAddress().toString(), socket_channel);
				            socket_channel.finishConnect();
				            System.out.println("Connected with: "+ socket_channel.getRemoteAddress().toString());
				         }
				      } catch (IOException e) {
				         key.cancel();
				         DebugManager.alert(e);
				      }
				}
				
				 if(key.isWritable())
				{
					socket_channel = (SocketChannel)key.channel();
					String ip = socket_channel.getRemoteAddress().toString();
					ip=ip.substring(1, ip.indexOf(":"));
					ArrayList<String> msgs;
					ByteBuffer buffer;
					String msg;
					if((msgs = messages_to_write.get(ip))!=null)
					{
						if(msgs.size()>0)
						{
							
						
						msg=msgs.get(0);
						buffer = ByteBuffer.wrap(msg.getBytes());
						socket_channel.write(buffer);
						msgs.remove(0);
						if(buffer.hasRemaining())
						{
							if(buffer.hasArray()) msgs.add(0, new String(buffer.array()));
						}
						}
					}
				}
			
				
			
			}
			
		}
		}catch(Exception e)
		{
			DebugManager.alert(e);
			try
			{
				key.cancel();
			socket_channel.close();
				}catch(Exception e1)
			{
				DebugManager.alert(e1);
			}
			
			if(run) 
			{
				serve();//stackoverflow possible xd
			}
		}
	}
	
	@Override
	public void run() {
		
		try {
			
		
			ServerSocketChannel server_socket_channel =  ServerSocketChannel.open();
			server_socket_channel.configureBlocking(false);
			server_socket_channel.bind(new InetSocketAddress(host, port));
			server_socket_channel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			DebugManager.alert(e);
		}
			
		serve();
			
	
		
	}


	
	
	
	
	public void start()
	{
		if(run==true) return;
		setRun(true);
		thread = new Thread(this);
		thread.start();
	}
	
	

	public void start(String IP)
	{
		try
		{
			this.host=InetAddress.getByName(IP);
		}catch(Exception e)
		{
			DebugManager.alert(e);
		}
		if(run==true) return;
		setRun(true);
		thread = new Thread(this);
		thread.start();
	}
	
	

	public void start(int port)
	{
		this.port=port;
		if(run==true) return;
		setRun(true);
		thread = new Thread(this);
		thread.start();
	}
	
	
	public void start(String IP, int port)
	{
		try
		{
			this.host=InetAddress.getByName(IP);
		}catch(Exception e)
		{
			DebugManager.alert(e);
		}
	
		this.port=port;
		if(run==true) return;
		setRun(true);
		thread = new Thread(this);
		thread.start();
	}
	
	
	public void stop()
	{
		setRun(false);
		selector.wakeup();
		try {
			thread.join(timeout);
		} catch (InterruptedException e) {
			DebugManager.alert(e);
		}
	}
	
	
	private void setRun(boolean run)
	{
		Object lock=new Object();
		synchronized(lock)
		{
			this.run=run;
		}

	}
	
	
	public synchronized void write(String IP, String msg)
	{
		msg="{"+msg+"}";
		ArrayList<String> msgs = messages_to_write.get(IP);
		if(msgs==null)
		{
			msgs=new ArrayList<String>();
			msgs.add(msg);
			messages_to_write.put(IP, msgs);
		}
		else
		{
			msgs.add(msg);
		}
		
	}
	
	
	public void connect(String IP, int port)
	{
		try
		{
			
		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_CONNECT |
		         SelectionKey.OP_READ | SelectionKey.
		            OP_WRITE);
		
		sc.connect(new InetSocketAddress(IP, port));
		}catch(Exception e)
		{
			DebugManager.alert(e);
		}
	}
	
	
	public void disconnect(String IP)
	{
		try
		{
			
		SocketChannel socket_channel = list_of_clients.get(IP);
		if(socket_channel==null) return;
		synchronized(socket_channel)
		{
			socket_channel.close();
			list_of_clients.remove(IP);
			
		}
		
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
	}
	

	public String read()
	{
		String s;
		if(messages_to_read.size()>0)
		{
			s=messages_to_read.get(0);
			messages_to_read.remove(0);
			if(messages_to_read.size()==0)
			{
				is_something_to_read=false;
			}
			return s;
		}
		
		
		return null;
		
	}
	
	public boolean isSomethingToRead()
	{
		return is_something_to_read;
	}
	
	public void prepareMessages(String msg, String ip) throws Exception
	{
	boolean msg_started = false;
	String previous_msg = 	unprepared_messages.get(ip);
	String s="";
	if(previous_msg!=null)
	{
		msg="{"+previous_msg+msg;
		unprepared_messages.remove(ip);
	}
	
	for(int i=0; i<msg.length(); i++)
	{
		char c = msg.charAt(i);
		
		if(c=='{')
		{
			if(msg_started==false)
			{
				msg_started=true;
			}else
			{
				throw new Exception("Wrong data has been sent");
			}
			
		}
		
		else if(c=='}')
		{
			if(msg_started==true)
			{
				messages_to_read.add(s);
				s="";
				is_something_to_read=true;
				msg_started=false;
				
				
			}
			else
			{
				throw new Exception("Wrong data has been sent");
			
			}
	}
		else if(msg_started)
		{
			s+=msg.charAt(i);
		}
		
	
	}
		
		unprepared_messages.put(ip, s);
	
	
	
	}
	
	
	
	
	
	
	
	

	
}
