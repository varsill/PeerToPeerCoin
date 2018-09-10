package Main;

import java.util.Hashtable;

import Blockchain.Ledger;

public class CurrentState<T1, T2> {
	private Ledger ledger = null ;
	private Hashtable<T1, T2> elements = null;
	
	public CurrentState()
	{
		elements = new Hashtable<T1, T2>();
		ledger = Ledger.getInstance();
	}
	
	
	public void catchUpOnState()
	{
		if(ledger==null) return;
		while(ledger.is)
	}
}
