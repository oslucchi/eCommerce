import java.util.StringTokenizer;


public class Product 
{
	private String fieldsDelimiter = Character.toString((char) 1);
	private int id;
	public String artist;
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
		
	public Product(int id, String artist, String title, String category, float price, String descriptionShort, String descriptionLong)
	{
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.category = category;
		this.price = price;
		this.descriptionLong = descriptionLong;
		this.descriptionShort = descriptionShort;
	}
	
	public Product(String record) throws InternalExceptions
	{
		
		StringTokenizer st = new StringTokenizer(record, "|");
		if (st.countTokens() != 7)
		{
			InternalExceptions e = new InternalExceptions();
			e.setErrorCode(InternalExceptions.ERR_MALFORMED_RECORD);
			e.setErrorDescription("Unable to parse record: '" + record.substring(0,15) + "...'");
			throw e;
		}
		id = Integer.parseInt(st.nextToken());
		title = st.nextToken();
		artist = st.nextToken();
		category = st.nextToken();
		
		String priceStr = st.nextToken().trim();
		try
		{
			price = Float.parseFloat(priceStr);
		}
		catch(Exception e)
		{
			throw new InternalExceptions("Price format is incorrect (" + priceStr + ")", InternalExceptions.ERR_MALFORMED_RECORD);
		}
		descriptionShort = st.nextToken();
		descriptionLong = st.nextToken();
	}
}
