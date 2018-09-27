package Blockchain;

import java.util.List;

public class Prize  extends Parcel implements XSerializable {

	protected List<PayInformation> payees_list = null; 
	protected PayInformation payer = null;


	public Prize()
	{
		
	}

	public Prize(Prize t)
	{
		this.payees_list=t.payees_list;
		this.public_key=t.public_key;
		this.signature=t.signature;
		this.time=t.time;
	}


	public void addPayer(String public_key, double amount)
	{
		payer = new PayInformation(public_key, amount);
		this.public_key=public_key;
		this.time=System.currentTimeMillis();
	}


	public void addPayee(String payee_public_key, double amount)
	{
		if(amount<0) return;
		payees_list.add(new PayInformation(payee_public_key, amount));
	}


	protected String createString()
	{
		String result = super.createString();
		
		
		for(int i=0; i<payees_list.size(); i++)
		{
				result+=payees_list.get(i).getPublicKey()+Double.toString(payees_list.get(i).getAmount());
		}
		
		return result;
	}

	public List<PayInformation> getPayees()
	{
		if(payees_list!=null) return payees_list;
		return null;
	}

	public PayInformation getPayer()
	{
		return payer;
	}

	@Override
	public String[] getListOfObjectNames() {
		String[] s= {"public_key", "time", "signature"};
		return s;
	}


	@Override
	public XSerializable[] getObjectList() {

		XSerializable[] list = new XSerializable[payees_list.size()];
		int i=0;
		for(PayInformation p:payees_list)
		{
			list[i]=(XSerializable)p;
			i++;
		}
		return list;
	}
}
