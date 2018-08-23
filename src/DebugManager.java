
public class DebugManager {
	
	private DebugManager()
	{
		
	}
	
	public static DebugManager getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
	
	public void alert(Exception e)
	{
		System.out.println(e.getMessage());
	}
	s
	private static class InstanceHolder
	{
		private static final DebugManager INSTANCE = new DebugManager();
	}
}
