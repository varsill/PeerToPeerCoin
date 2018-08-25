package Security;

import java.nio.charset.StandardCharsets;

public class Main {

	public static void main(String[] args) 
	{
		try
		{
		
			AsymetricCipherManager manager = new AsymetricCipherManager("dupa", "kluczyk.ks");
			manager.getKeys();

		}
		catch(Exception e)
		{
			
		}
	
		
	}

}
