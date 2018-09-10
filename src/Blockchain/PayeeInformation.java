package Blockchain;

public class PayeeInformation
{
	private String payee_public_key = "";
	private double amount = 0;
	
	
	public PayeeInformation(String payee_public_key, double amount)
	{
		this.payee_public_key=payee_public_key;
		this.amount = amount;
	}
	
	
	String getPublicKey()
	{
		return payee_public_key;
	}
	
	
	double getAmount()
	{
		return amount;
	}


}

