package Security;

import java.nio.charset.StandardCharsets;

public class Main {

	public static void main(String[] args) 
	{
		try
		{
		//	byte[] encryptionKey = "dupa".getBytes(StandardCharsets.UTF_8);
		//	byte[] plainText = "Zuzia to suka".getBytes(StandardCharsets.UTF_8);
			SymetricCipherManager advancedEncryptionStandard = new SymetricCipherManager("AES","dupaaa");
			byte[] cipherText = advancedEncryptionStandard.encrypt("Zuzia to suka");
			byte[] decryptedCipherText = advancedEncryptionStandard.decrypt(cipherText);

			System.out.println(new String("Zuzia to suka"));
			System.out.println(new String(cipherText));
			System.out.println(new String(decryptedCipherText));
			

		}catch(Exception e)
		{
			
		}
	
		
	}

}
