import java.util.StringTokenizer;


public class Product 
{
	private String fieldsDelimiter = Character.toString((char) 1);
	private int id;
	public String title;
	public String descriptionShort;
	public String descriptionLong;
	public String category;
	public float price;
	
	public String getImagePathSmall()
	{
		String path = String.format("images/03dS.jpg", id);
		return path;
	}
	
	public String getImagePathLarge()
	{
		String path = String.format("images/03dL.jpg", id);
		return path;
	}
		
	public Product(int id, String title, String descriptionShort, String descriptionLong, String category, float price)
	{
		this.id = id;
		this.title = title;
		this.descriptionLong = descriptionLong;
		this.descriptionShort = descriptionShort;
		this.category = category;
		this.price = price;
	}
	
	public Product(String record) throws InternalExceptions
	{
		StringTokenizer st = new StringTokenizer(record, fieldsDelimiter);
		if (st.countTokens() != 6)
		{
			InternalExceptions e = new InternalExceptions();
			e.setErrorCode(InternalExceptions.ERR_MALFORMED_RECORD);
			e.setErrorDescription("Unable to parse record: '" + record + "'");
			throw e;
		}
		id = Integer.parseInt(st.nextToken());
		category = st.nextToken();
		title = st.nextToken();
		descriptionShort = st.nextToken();
		descriptionLong = st.nextToken();
		price = Float.parseFloat(st.nextToken());
	}
}
