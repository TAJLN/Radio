package moonplex.tajln.utils;

public class InvalidAnnotationException
	extends Exception{
	private static final long serialVersionUID = 1L;

	public InvalidAnnotationException() {}
  
	public InvalidAnnotationException(String message)
	{
		super(message);
	}
  
	public InvalidAnnotationException(Throwable cause)
	{
		super(cause);
	}
  
	public InvalidAnnotationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}