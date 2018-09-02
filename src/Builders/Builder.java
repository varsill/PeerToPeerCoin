package Builders;

public interface Builder {
	
	public void createPart();
	
	
}

 class BuildingFailedException extends Exception
{
	 
	 
	private String class_name = "";
	private static final long serialVersionUID = 1L;
	
	
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