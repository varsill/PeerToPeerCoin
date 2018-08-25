package Security;

import java.nio.charset.StandardCharsets;

public class Main {

	public static void main(String[] args) 
	{
		try
		{
		
		SignatureManager signature = new SignatureManager("haslo", "kluczyk");
		
		System.out.println(signature.isSignatureValid(signature.sign("dupa hahahah to ja to pisze xddd".getBytes()), "dupa hahahah toq ja to pisze xddd".getBytes()));
		}
		catch(Exception e)
		{
			
		}
	
		
	}

}
