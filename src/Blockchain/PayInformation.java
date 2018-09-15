package Blockchain;

public class PayInformation implements XSerializable
{
	private String public_key = "";
	private double amount = 0;
	
	
	
	
	public PayInformation(String public_key, double amount)
	{
		this.public_key = public_key;
		this.amount = amount;
	}
	
	
	public String getPublicKey()
	{
		return public_key;
	}
	
	
	public double getAmount()
	{
		return amount;
	}
	
	

	public void setAmount(double amount)
	{
		this.amount=amount;
	}

	@Override
	public String[] getListOfObjectNames() {
		String[] s = {"public_key", "amount"};
		return s;
	}

	@Override
	public void addChildToList(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public XSerializable[] getObjectList() {
		
		return null;
	}

}

 class Amount 
{
	private double amount;
	
	
	public Amount()
	{
		amount = 0;
	}
	
	
	public Amount(double amount)
	{
		this.amount=amount;
	}
	
	
	public double getAmount()
	{
		return amount;
	}
	
	
	public void addAmount(double extra)
	{
		amount+=extra;
	}
	
	
	public void addAmount(Amount amount)
	{
		this.amount+=amount.getAmount();
	}
	
	public boolean spentAmount(double how_much)
	{
		if((amount-how_much)>=0) 
		{
			amount=amount-how_much;
			return true;
		}
		return false;
	}
	
	
	public boolean spentAmount(Amount how_much)
	{
		if((amount-how_much.getAmount())>=0) 
		{
			this.amount-=how_much.getAmount();
			return true;
		}
		return false;
	}
	

}