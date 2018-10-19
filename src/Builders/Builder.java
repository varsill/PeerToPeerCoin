package Builders;

import Managers.Configurable;

public interface Builder {
	
	 public Object createPart() throws Exception;
	
	 public void loadPartFromString(String s) throws Exception;
	
	 public void reset();

	 public void isReady() throws Exception;

	 public void prepareNew();
}


 class BuildingFailedException extends Exception
{
	 
	private static final long serialVersionUID = 1L;
	private String class_name = "";
	
	public BuildingFailedException(String class_name)
	{
		this.class_name=class_name;
	}
	

	@Override
	public String getMessage()
	{
		return "Couldn't build given part - class "+class_name;
	}
	
}