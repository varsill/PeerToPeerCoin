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
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import Blockchain.Register;
import Blockchain.ActivePeersRegister;
import Blockchain.BalanceRegister;
import Blockchain.Block;
import Blockchain.Entry;
import Blockchain.Exit;
import Blockchain.Ledger;
import Blockchain.Parcel;
import Blockchain.PayInformation;
import Blockchain.Prize;
import Blockchain.Transaction;
import Builders.BlockBuilder;
import Builders.EntryBuilder;
import Builders.ExitBuilder;
import Builders.PrizeBuilder;
import Builders.TransactionBuilder;
import Communication.P2PConnection;
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
		
		entbuilder.createEntryFromScratch(asym.getPublicKeyAsString(), "124.13.41.123", 421.41);
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
		fis.close();
		}
		catch(Exception e)
		{
			DebugManager.alert(e);
		}
	}
	public static void main(String[] args) throws Exception 
	{
	}
