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

	public static String SEPARATOR =";";
	public static String ARRAY_SEPARATOR = "&";
	
	public static String[] specials = {SEPARATOR, ARRAY_SEPARATOR, "<"};
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
		
		ArrayList<String> array_list =   new ArrayList<String>();
		if(contains(proper_string, "<"))
		{
			
		
			int i=0;
			while((i=indexOf(proper_string, splitting_exp, indexOf(proper_string, "</")))!=-1)
			{
				
				array_list.add(proper_string.substring(0, i));
				proper_string=proper_string.substring(i+splitting_exp.length()) ;
			}
			array_list.add(proper_string);
			
			
			String[] result=new String[array_list.size()];
			
			for(int j=0; j<array_list.size(); j++)
			{
				result[j]=array_list.get(j);
			}
			return result;
		}
		else
		{
		
			String[] list = proper_string.split(splitting_exp);
			String additional="";
			if(list[0].equals(""))array_list.add(splitting_exp+splitting_exp);
			else array_list.add(list[0]);
			for(int i=1; i<list.length; i++)
			{
				if(list[i].equals(""))
				{
					array_list.set(array_list.size()-1, array_list.get(array_list.size()-1)+splitting_exp+splitting_exp);
				}
				else
				{
					if(array_list.get(array_list.size()-1).endsWith(splitting_exp)) array_list.set(array_list.size()-1, array_list.get(array_list.size()-1)+list[i]);
					else array_list.add(list[i]);
				}
			}
		}
		String[] result=new String[array_list.size()];
		
		for(int j=0; j<array_list.size(); j++)
		{
			result[j]=array_list.get(j);
		}
		return result;
		
	}
	
	static public boolean contains(String s, String c)
	{
		for(String x:specials) s=s.replace(x+x, "");
		return s.contains(c);
	}
	
	
	static public int indexOf(String s, String c, int i)
	{
		for(String x:specials) s= s.replace(x+x, "  ");
		return s.indexOf(c, i);
	}
	
	static public int indexOf(String s, String c)
	{
		return indexOf(s, c, 0);
	}
	
	static public String unescape(String s)
	{
		for(String c: specials)
		{
			s=s.replace(c,  c+c);
		}
		return s;
	}
	
	static private String escape(String s)
	{
		for(String c: specials)
		{
			s=s.replace(c+c, c);
		}
		return s;
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
			s=escape(s);
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
			result.append(SEPARATOR);
			}
			
		}
		
		}
		
		
		if(result.length()>SEPARATOR.length())
		{
			result=result.delete(result.lastIndexOf(SEPARATOR), result.length());
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
			result.insert(result.indexOf(closing_type), saveObjectToString(o)+ARRAY_SEPARATOR);
			
		}
		
		
		int index=0;
		while((index = result.indexOf(ARRAY_SEPARATOR+"<"))!=-1)
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
