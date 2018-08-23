package Security;

public class WrongKeyException extends Exception {

	private static final long serialVersionUID = 1L;
	private int key_length=-1;
	
	WrongKeyException(int key_length)
	{
		this.key_length=key_length;
	}
	
	@Override
	public String getMessage()
	{
		return "Key length is wrong and equals: "+Integer.toString(key_length);
	}
}
