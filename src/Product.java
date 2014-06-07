import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class Product 
{
	private String fieldsDelimiter = Character.toString((char) 1);
	private int id;
	public String artist = "";
	public String title = "";
	public String descriptionShort = "";
	public String descriptionLong = "";
	public String category = "";
	public float price = 0.0f;
	
	public String getImagePathSmall()
	{
		String path = String.format("images/%03dS.jpg", id);
		return path;
	}
	
	public String getImagePathLarge()
	{
		String path = String.format("images/%03dL.jpg", id);
		return path;
	}
		
	public int getId() {
		return id;
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
		
		String fields[] = record.split("\\|");
		if (fields.length != 7)
		{
			InternalExceptions e = new InternalExceptions();
			e.setErrorCode(InternalExceptions.ERR_MALFORMED_RECORD);
			e.setErrorDescription("Unable to parse record: '" + record.substring(0,15) + "...'");
			throw e;
		}
		title = fields[1];
		artist = fields[2];
		category = fields[3];
		
		String fieldOnErr = "Id";
		try
		{
			id = Integer.parseInt(fields[0]);
			fieldOnErr = "Price";
			price = Float.parseFloat(fields[4].trim() + "f");
		}
		catch(Exception e)
		{
			throw new InternalExceptions(fieldOnErr + " format is incorrect (" + fields[4] + ")", InternalExceptions.ERR_MALFORMED_RECORD);
		}
		descriptionShort = fields[5];
		descriptionLong = fields[6];
	}
}
