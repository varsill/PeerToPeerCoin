package Main;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import Managers.PropertiesManager;
import Security.HashManager;

public class Main {
	private static HashManager hm;
	private static String message="";
	public static void main(String[] args) 
	{
		
		try
		{
		PropertiesManager.PASSWORD="haslo";
		Scanner reader = new Scanner(System.in);  
		System.out.println("Enter a number of iterations: ");
		int x = reader.nextInt(); 
		System.out.println("Enter length of msg: ");
		int n = reader.nextInt(); 
		for(int i=0; i<n; i++)
		{
			message=message+"X";
		}
		hm = new HashManager();
		//System.out.println(proof(16));
		System.out.println(testHashes(x));
		
		}
		catch(Exception e)
		{
			
		}
	
	}
	
	
	public static double testHashes(int number_of_tries)
	{
		long begin = System.currentTimeMillis();
		for(int i =0; i<number_of_tries; i++)
		{
			hm.digest(message);
		}
		return ((double)(System.currentTimeMillis()-begin))/1000.0;
	}
	
	public static int proof(int how_many_zeros)
	{
		
		byte[] msg;
		byte[] digest;
		String result="";
		String current_result="";
		for(int i=0; i<how_many_zeros; i++)
		{
			result=result+"0";
		}
		int nonce=0;
		do
		{	
			
			msg = (message+String.valueOf(nonce)).getBytes();
			digest=hm.digest(msg);
			current_result = toBinary(digest);
			nonce++;
		}while(!current_result.startsWith(result));

		nonce=nonce-1;
		return nonce;
	}

	public static String toBinary(byte[] b)
	{
		String result="";
		String to_be_added;
		String x;
		for(int i =0; i<b.length; i++)
		{
			x="";
			to_be_added=Integer.toBinaryString(b[i]);
				for(int j =0; j<(8-to_be_added.length()); j++)
				{
					x=x+"0";
				}
				x=x+to_be_added;
			result=result+x;
		}
		return result;
	}
}
