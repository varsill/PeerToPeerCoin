package Security;

import java.nio.charset.StandardCharsets;

import Interfaces.PropertiesManager;

public class Main {

	public static void main(String[] args) 
	{
		try
		{
		PropertiesManager.PASSWORD="haslo";
		SignatureManager signature = new SignatureManager();
		
		System.out.println(signature.isSignatureValid(signature.sign("dupa hahahah to ja to pisze xddd".getBytes()), "dupa hahahah to ja to pisze xddd".getBytes()));
		}
		catch(Exception e)
		{
			
		}
	
		
	}

}
