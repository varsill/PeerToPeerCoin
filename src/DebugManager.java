
public class DebugManager {
	private static final boolean debug=true;
	private DebugManager()
	{
		
	}
	
	
	public static void alert(Exception e)
	{
		if(debug) System.out.println("DebugManager: "+e.getMessage());
	}

}
