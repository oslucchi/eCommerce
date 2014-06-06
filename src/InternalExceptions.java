

public class InternalExceptions extends Exception 
{
	private static final long serialVersionUID = -8166519137963232242L;
	public static final int ERR_MALFORMED_RECORD = 1;

	private String errorDescription;
	private int errorCode = 0;

	public InternalExceptions() 
	{
		super();
	}

	public InternalExceptions(String msg) 
	{
		super(msg);
	}
	
	public InternalExceptions(Exception e)
	{
		super("Exception: class '" + 
			  e.getStackTrace().getClass().getSimpleName() + "' error msg: '" + e.getMessage());
	}
	
	public InternalExceptions(String errorDescription, int errorCode) 
	{
		super("internal error: (" + errorCode +  ") " + errorDescription);
		this.errorDescription = errorDescription;
		this.errorCode = errorCode;
	}

	public InternalExceptions(Throwable t)
	{
		super("Tracker exception: class '" + 
			  t.getStackTrace().getClass().getSimpleName() + "' error msg: '" + t.getMessage());
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
