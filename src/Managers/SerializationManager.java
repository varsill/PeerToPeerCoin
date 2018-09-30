package Managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Blockchain.Block;
import Blockchain.Ledger;
import Blockchain.XSerializable;
import Builders.BlockBuilder;

public class SerializationManager {

	
	
	
	static public String[] makeSubstrings(String string, String beginning_expression, String end_expression, String splitting_exp)
	{
		String proper_string = "";
		
		if(beginning_expression=="#BEGIN"&&end_expression=="#END")
		{
			proper_string=string;
		}
		else if(beginning_expression=="#BEGIN") 
			{
			proper_string=string.substring(0, string.indexOf(end_expression));
			}
		else if(end_expression=="#END")  
			{
			proper_string=string.substring(string.indexOf(beginning_expression)+beginning_expression.length());
			}
		
		else 
			{
			if((!string.contains(beginning_expression))||(!string.contains(end_expression))) return new String[0];
			proper_string=string.substring(string.indexOf(beginning_expression)+beginning_expression.length(), string.indexOf(end_expression));
			}
		
		
		if(proper_string.contains("<"))
		{
			
			ArrayList<String> array_list = new ArrayList<String>();
			int i=0;
			while((i=proper_string.indexOf(splitting_exp, proper_string.indexOf("</")))!=-1)
			{
				
				array_list.add(proper_string.substring(0, i));
				proper_string=proper_string.substring(i+1) ;
			}
			array_list.add(proper_string);
			
			
			String[] array=new String[array_list.size()];
			
			for(int j=0; j<array_list.size(); j++)
			{
				array[j]=array_list.get(j);
			}
			return array;
		}
		else return  proper_string.split(splitting_exp);
		
	}
	

	
	static public String saveObjectToString(XSerializable x) throws Exception
	{
		StringBuilder result = new StringBuilder();
	
		
			ArrayList<String> fields_names_list =new ArrayList<String>();
		String [] fields_names_from_XSerializable =	x.getListOfObjectNames();
		if(fields_names_from_XSerializable!=null)
		{
			
		
			for(String s: fields_names_from_XSerializable)
			{
				fields_names_list.add(s);
			}
			String field_name="";
		 
		ArrayList<Field> fields = new ArrayList<Field>();	
		fields=  (ArrayList<Field>) getAllFields(fields, x.getClass());
		for( Field f: fields)
		{
			if(fields_names_list.contains(f.getName()))
			{
			field_name=f.getName();
			f.setAccessible(true);
			
			
			Class<?> type = f.getType();
		
			
			if(type.equals(String.class))
			{
			String s = 	(String)f.get(x);
			result=result.append(s);
			}
			else if(type.equals(int.class))
			{
				int i = (int) f.get(x);
				result=result.append(Integer.toString(i));
			}
			else if(type.equals(long.class))
			{
				long l =  (long)f.get(x);
				result=result.append(Long.toString(l));
			}
			else if(type.equals(double.class))
			{
				double d = (double)f.get(x);
				result=result.append(Double.toString(d));
			}
			else	throw new Exception("Unknow type of field");
			result.append(";");
			}
			
		}
		
		}
		
		if(result.length()>0&&result.charAt(result.length()-1)==';') 
		{
			result=result.delete(result.length()-1, result.length());
		}
		
		XSerializable[] object_list = x.getObjectList();
		if(object_list==null) return result.toString();
		
		for(XSerializable o:object_list)
		{
			
			String type = "<"+o.getClass().getName()+">";
			String closing_type = "</"+o.getClass().getName()+">";
			if(result.indexOf(type)==-1)
			{
				result.append(type);
				result.append(closing_type);
			}
			result.insert(result.indexOf(closing_type), saveObjectToString(o)+"&");
			
		}
		
		
		int index=0;
		while((index = result.indexOf("&<"))!=-1)
		{
			result.delete(index, index+1);
		}
		
	/*	while((index = result.indexOf(">&"))!=-1)
		{
			result.delete(index+1, index+2);
		}
	*/	
		
		return result.toString();
	}
	
	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
	    fields.addAll(Arrays.asList(type.getDeclaredFields()));

	    if (type.getSuperclass() != null) {
	        getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
	}
	
}
