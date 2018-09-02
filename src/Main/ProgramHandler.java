package Main;



public class ProgramHandler {
	//Singleton
		private ProgramHandler() {}
		
		
		public static ProgramHandler getInstance()
		{
			return SingletonHolder.INSTANCE;
		}
		
		
		private static class SingletonHolder
		{
			static ProgramHandler INSTANCE = new ProgramHandler();
		}
		
}
