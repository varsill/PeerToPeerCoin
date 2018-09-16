package Main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;
import java.util.Scanner;

import Blockchain.Register;
import Blockchain.ActivePeersRegister;
import Blockchain.BalanceRegister;
import Blockchain.Block;
import Blockchain.Entry;
import Blockchain.Exit;
import Blockchain.Parcel;
import Blockchain.PayInformation;
import Blockchain.Transaction;
import Builders.BlockBuilder;
import Builders.EntryBuilder;
import Builders.ExitBuilder;
import Builders.TransactionBuilder;
import Managers.DebugManager;
import Managers.PropertiesManager;
import Managers.SerializationManager;
import Security.AsymetricCipherManager;
import Security.HashManager;

public class Main {
	private static HashManager hm;
	private static String message="";
	
	@SuppressWarnings("unchecked")
	static private <T> T make_substrings(String string, String beginning_expression, String end_expression, String splitting_exp)
	{
		String proper_string = "";
		if(beginning_expression=="#BEGIN") proper_string=string.substring(0, string.indexOf(end_expression));
		else if(end_expression=="#END")  proper_string=string.substring(string.indexOf(beginning_expression)+beginning_expression.length());
		else proper_string=string.substring(string.indexOf(beginning_expression)+beginning_expression.length(), string.indexOf(end_expression));
		String[] array =  proper_string.split(splitting_exp);
		if(array.length==1) return (T) array[0];
		else return (T)array;
		
	}
	
	public static KeyPair generateKeyPair() throws Exception {
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(2048, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();

	    return pair;
	}
	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
	    Signature privateSignature = Signature.getInstance("SHA256withRSA");
	    privateSignature.initSign(privateKey);
	    privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

	    byte[] signature = privateSignature.sign();

	    return Base64.getEncoder().encodeToString(signature);
	}
	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
	    Signature publicSignature = Signature.getInstance("SHA256withRSA");
	    publicSignature.initVerify(publicKey);
	    publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

	    byte[] signatureBytes = Base64.getDecoder().decode(signature);

	    return publicSignature.verify(signatureBytes);
	}
	public static void save()
	{
		try {
			AsymetricCipherManager asym = AsymetricCipherManager.getInstance();
			ExitBuilder exitbuilder = ExitBuilder.getInstance();
			exitbuilder.setPublicKey(asym.getPublicKeyAsString());
			exitbuilder.prepareNew();
			Exit exit  = (Exit)exitbuilder.createPart();
	
		TransactionBuilder transbuilder = TransactionBuilder.getInstance();
		transbuilder.addPayer(new String(asym.getPublicKeyAsString()), 120);
		transbuilder.addPayee("dfadfwfa", 12);
		transbuilder.addPayee("nsbdbqadw12413", 52);
		transbuilder.addPayee("adwad", 12);
		transbuilder.prepareNew();
		Transaction trans = (Transaction) transbuilder.createPart();
		
		EntryBuilder entbuilder = EntryBuilder.getInstance();
		
		entbuilder.setData(asym.getPublicKeyAsString(), "124.13.41.123", 421.41);
		entbuilder.prepareNew();
		BlockBuilder block_builder = BlockBuilder.getInstance();
		
		
		block_builder.createBlockFromScratch(12, 12, "genesis haha");
		block_builder.addParcel((Transaction)trans);
		block_builder.addParcel((Entry) entbuilder.createPart());
		block_builder.addParcel((Transaction)trans);
		block_builder.addParcel((Exit)exit);
		block_builder.prepareNew();
		Block block = (Block) block_builder.createPart();
		block_builder=BlockBuilder.getInstance(block);
		FileOutputStream fos = new FileOutputStream("testowy.blo");
//		fos.write(block_builder.savePartToString().getBytes());
		fos.close();
	}
	catch(Exception e)
	{
		DebugManager.alert(e);
	}
	}
		
	
	
	public static void load()
	{
		try
		{
		AsymetricCipherManager asym = AsymetricCipherManager.getInstance();	
			
		BalanceRegister balance = BalanceRegister.getInstance();
		balance.addNewPeer(new PayInformation(asym.getPublicKeyAsString(), 1000));
		
		BlockBuilder block_builder = BlockBuilder.getInstance();
		FileInputStream fis = new FileInputStream("testowy.blo");
		byte[] b = new byte[fis.available()];
		fis.read(b);
		String s = new String(b);
		block_builder.loadPartFromString(s);
		Block block2 = (Block) block_builder.createPart();
		System.out.println( block2.isHashProper() );
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
	}
	public static void main(String[] args) throws Exception 
	{

/*	
	BalanceRegister balance = BalanceRegister.getInstance();
	AsymetricCipherManager asym = AsymetricCipherManager.getInstance();
	balance.addNewPeer(new PayInformation(asym.getPublicKeyAsString(), 1000));
	
	
	
	
	
		ExitBuilder exitbuilder = ExitBuilder.getInstance();
		exitbuilder.setPublicKey(asym.getPublicKeyAsString());
		exitbuilder.prepareNew();
		Exit exit  = (Exit)exitbuilder.createPart();

	TransactionBuilder transbuilder = TransactionBuilder.getInstance();
	transbuilder.addPayer(new String(asym.getPublicKeyAsString()), 120);
	transbuilder.addPayee("dfadfwfa", 12);
	transbuilder.addPayee("nsbdbqadw12413", 52);
	transbuilder.addPayee("adwad", 12);
	transbuilder.addPayee("ad13wad", 14);
	transbuilder.prepareNew();
	Transaction trans = (Transaction) transbuilder.createPart();
	
	EntryBuilder entbuilder = EntryBuilder.getInstance();
	
	entbuilder.setData(asym.getPublicKeyAsString(), "124.13.41.123", 421.41);
	entbuilder.prepareNew();
	BlockBuilder block_builder = BlockBuilder.getInstance();
	
	
	block_builder.createBlockFromScratch(12, 12, "gen   esis 21haha");
	block_builder.addParcel((Transaction)trans);
	block_builder.addParcel((Entry) entbuilder.createPart());
	block_builder.addParcel((Transaction)trans);
	block_builder.addParcel((Exit)exit);
	block_builder.prepareNew();
	Block block = (Block) block_builder.createPart();
		
	String s = SerializationManager.saveObjectToString(block);
	
	FileOutputStream fos = new FileOutputStream("testowy.blo");
	fos.write(s.getBytes());
	fos.close();

	
	load();
	*/
		
		String s = "<Blockchain.ActivePeersRegister$XSerializableType>hahaha;124.52.13.14;10.0&hehs;63.151.253.54;10.0&xddd;52.51.53.54;0.2&lels;52.51.53.54;633.0</Blockchain.ActivePeersRegister$XSerializableType>";
	/*	
		ActivePeersRegister active = ActivePeersRegister.getInstance();
		Entry entry = new Entry(10, "124.52.13.14", "hahaha", "poidpis1", 2012);
		active.update(entry);
		 entry = new Entry(633, "52.51.53.54", "lels", "poidpis2", 5714);
		 active.update(entry);
		 entry = new Entry(0.2, "52.51.53.54", "xddd", "poidpis3", 1514);
		 active.update(entry);
		 entry = new Entry(10, "63.151.253.54", "hehs", "poidpis4", 5114);
		 active.update(entry);
		String s = SerializationManager.saveObjectToString(active);
		System.out.println(s);
		*/
		ActivePeersRegister active = ActivePeersRegister.getInstance();
		active.loadRegisterFromString(s);
		System.out.println(active.getIPByPublicKey("xddd"));
		return;
		}
	
	
	public static double testHashes(int number_of_tries)
	{
		long begin = System.currentTimeMillis();
		for(int i =0; i<number_of_tries; i++)
		{
			hm.digest(message);
		}
		return ((double)(System.currentTimeMillis()-begin))/1000.0;
	}
	
	public static int proof(int how_many_zeros)
	{
		
		byte[] msg;
		byte[] digest;
		String result="";
		String current_result="";
		for(int i=0; i<how_many_zeros; i++)
		{
			result=result+"0";
		}
		int nonce=0;
		do
		{	
			
			msg = (message+String.valueOf(nonce)).getBytes();
			digest=hm.digest(msg);
			current_result = toBinary(digest);
			nonce++;
		}while(!current_result.startsWith(result));

		nonce=nonce-1;
		return nonce;
	}

	public static String toBinary(byte[] b)
	{
		String result="";
		String to_be_added;
		String x;
		for(int i =0; i<b.length; i++)
		{
			x="";
			to_be_added=Integer.toBinaryString(b[i]);
				for(int j =0; j<(8-to_be_added.length()); j++)
				{
					x=x+"0";
				}
				x=x+to_be_added;
			result=result+x;
		}
		return result;
	}
}
