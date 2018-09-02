package Managers;

public class DebugManager {
	
	private static boolean debug=PropertiesManager.DEBUG;
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
