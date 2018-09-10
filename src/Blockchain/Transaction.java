package Blockchain;

import java.util.List;

import Managers.DebugManager;

public class Transaction extends Parcel {
	
	
private List<String> available_transactions_hashes = null;
private List<PayeeInformation> payees_information_list = null; 


public void addTransaction(String hash)
{
	available_transactions_hashes.add(hash);
}


public void addPayeeInformation(String payee_public_key, double amount)
{
	payees_information_list.add(new PayeeInformation(payee_public_key, amount));
}


public boolean build()
{
	makeHash();
	if(hash=="") return false;
	if(!sign()) return false;
	return true;
}


protected boolean sign()
{
	super.sign();
	try
	{
		signature_manager.sign(createString().getBytes());
	}catch(Exception e)
	{
		DebugManager.alert(e);
		return false;
	}
	return true;
	
}


protected void makeHash()
{
	super.makeHash();//ciekawe czy odpali createString st¹d czy z Parcel
}


protected String createString()
{
	String result = super.createString();
	
	for(int i=0; i<available_transactions_hashes.size(); i++)
	{
			result+=available_transactions_hashes.get(i);
	}
	
	for(int i=0; i<payees_information_list.size(); i++)
	{
			result+=payees_information_list.get(i).getPublicKey()+payees_information_list.get(i).getAmount();
	}
	
	return result;
}


}


