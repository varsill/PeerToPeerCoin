package Managers;


public class DebugManager {
	
	private final static boolean  debug=true;
	private DebugManager()
	{
		
	}
	
	public static void alert(Exception e)
	{
		if(debug)
		{
		//	System.out.println("DebugManager: "+e.getMessage());
			e.printStackTrace();
		}
	
	}
	
}
