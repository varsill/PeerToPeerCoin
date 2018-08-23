package Security;
public class Main {

	public static void main(String[] args) 
	{
		try
		{
			SymetricCipherManager manager = new SymetricCipherManager("AES", "dupa");
			System.out.println(manager.decrypt(manager.encrypt("Zuziatosukaaaaaa")));
		}catch(Exception e)
		{
			
		}
	
		
	}

}
