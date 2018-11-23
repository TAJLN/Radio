package moonplex.tajln.utils;

public class RegisterCommandException
	extends Exception{		
	private static final long serialVersionUID = 1L;

	public RegisterCommandException() {}

	public RegisterCommandException(String message)
	{
		super(message);
	}
   
	public RegisterCommandException(Throwable cause)
	{
		super(cause);
	}
   
	public RegisterCommandException(String message, Throwable cause)
	{
		super(message, cause);
	}
}