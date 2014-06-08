

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class UserInterface extends JFrame 
						   implements ActionListener, WindowStateListener, 
						   			  MouseListener, FocusListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    static private final int FILEEXIT = 1;
    static private final int ABOUT = 2;
    static private final int COMBOSORT = 3;
    static private final int NEXTPAGE = 4;
    static private final int PREVPAGE = 5;
    static private final int SHOWCART = 6;
    static private final int FILTER = 7;
    static private final int CHECKOUT = 8;
    static private final int ORDERHISTORY = 9;
    
    String srcPath = null;
    
    private Object[] sourceObj = new Object[10];
    JPanel frameLeftSide = new JPanel();
    JPanel frameRightSide = new JPanel();
    JPanel itemListContainer = new JPanel();
    JPanel detailsContainer = new JPanel();
    JPanel cartCont = new JPanel();
    JPanel orderPanel = new JPanel();
    JScrollPane listScroller = new JScrollPane();
    JScrollPane detailsScroller = new JScrollPane();
    JDialog cart;
    JDialog orderHistory;
    String[] howToPay = {"", "PayPal", "CreditCard"};


    private JButton previousPage = new JButton("<");
    private JButton nextPage = new JButton(">");
    private JButton showCart = new JButton("Show cart");
    private JButton filter = new JButton("Filter");
    private JButton checkOut = new JButton("Checkout");
    private JButton fileOrderHistory = new JButton ("Open Order Hystory");
    private JTextField filterText = new JTextField("", 15);
	private String[] choiches = {"category", "price"};
    private JComboBox comboBox = new JComboBox(choiches);
    private JLabel comboBoxLab = new JLabel("Sort by:");
    private JTextField cartItemNote;
    private JComboBox paymentMethod = new JComboBox(howToPay);
    private JTextField nameOfBuyer = new JTextField("",25);

	private Container frameCont = this.getContentPane();
	
	private String idInCart = "";
	
	DoubleLinkedList filteredList = null, currentPage = null;
	DoubleLinkedList productsList = null;
	
	@Override
	public void windowStateChanged(WindowEvent e) 
	{
		if (e.getNewState() == WindowEvent.WINDOW_CLOSED)
			System.exit(0);
	}

	private void saveCartFile()
	{
		// Save the selected ids on file for restart handling
		FileOutputStream fop = null;
		try 
		{
			File file = new File("data/cart.dat");
			fop = new FileOutputStream(file);
			// if file doesnt exists, then create it
			if (!file.exists()) 
			{
				file.createNewFile();
			}
			// get the content in bytes
			byte[] contentInBytes = idInCart.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		}
		catch (IOException ex) 
		{
			// TODO: generate popup error message
		} 
		finally 
		{
			try 
			{
				if (fop != null) 
				{
					fop.close();
				}
			} 
			catch (IOException ex) 
			{
				;
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand().compareTo("Select to buy") == 0)
		{
			for(int i = 0; i < itemListContainer.getComponentCount(); i++)
			{
				JPanel item = (JPanel) ((JPanel) itemListContainer.getComponent(i)).getComponent(1);
				JCheckBox cbItem = (JCheckBox) (item.getComponent(5));
				if (e.getSource().equals(cbItem))
				{
					Product itemSelected = 
							(Product) filteredList.search(Integer.parseInt(cbItem.getName().substring(2)));
					if (itemSelected != null)
					{
						itemSelected.clientNote = "";
						if (cbItem.isSelected())
						{
							itemSelected.selected = true;
						}
						else
						{
							itemSelected.selected = false;
						}
					}
					int idActioned = itemSelected.getId();
					boolean selected = itemSelected.selected;
					
					// Prepare the list of selected ids to push to a file in order to 
					// get the right checkbox selected status on restart
					idInCart = "|";
					DoubleLinkedList temp = filteredList;
					temp.first();
					showCart.setEnabled(false);
					while(temp != null)
					{
						if ((itemSelected = (Product) temp.current()) == null)
							break;
						if (itemSelected.selected)
						{
							idInCart += String.valueOf(itemSelected.getId()) + "|**|";
							showCart.setEnabled(true);
						}
						temp.next();
					}
					
					// Mark on the original productsList the selected status on the actioned checkbox
					// so that on next filtering the selected status will be preserved
					temp = productsList;
					temp.first();
					while(temp != null)
					{
						if (idActioned == ((Product) temp.current()).getId())
						{
							((Product) temp.current()).selected = selected;
							((Product) temp.current()).clientNote = "";
							break;
						}
						temp.next();
					}

					saveCartFile();
					break;
				}
			}
			return;
		}
		
		String displayMsg = "";
		// All objects having set an actionListener have been added to the sourceObj array
		// we compare the source of the ActionEvent received with each element of the array
		// to decide which action is needed
		int sourceIndex;
		for(sourceIndex = 0; sourceIndex < sourceObj.length; sourceIndex++)
		{
			if (e.getSource().equals(sourceObj[sourceIndex]))
				break;
		}

		switch(sourceIndex)
		{
		case FILEEXIT:
			// Menu File->Exit
			System.exit(0);
			break;
			
		case ABOUT:
			// Menu About
			// Credit goes to Piotr Gwiazda
			// reference http://stackoverflow.com/questions/7483421/how-to-get-source-file-name-line-number-from-a-java-lang-class-object
			ProjectSummary ps = new ProjectSummary(srcPath);
			displayMsg = "Name:   eCommerce\n" +
						 "Author: Lorenzo Lucchini\n" + 
						 "\nProject summary:\n" +
						 "- Total number of classes " + ps.getNumberOfClasses() + "\n" +
						 "- Total number of methods " + ps.getNumberOfMethods() + "\n" +
						 "- Total number of lines   " + ps.getNumberOfLines() + "\n" +
						 "\nPer class summary:\n";
			for(int i = 0; i < ps.getNumberOfClasses(); i++)
			{
				displayMsg += "- Class '" + ps.getClassSummary(i).getName() + "'\n" +
							  "        Total number of methods    : " + ps.getClassSummary(i).getNumOfMethods() + "\n" +
							  "        Total number of Variables  : " + ps.getClassSummary(i).getNumOfFields() + "\n" +
							  "        Number of source code lines: " + ps.getClassSummary(i).getNumOfLines() + "\n";
			}
			JOptionPane.showMessageDialog(this, displayMsg);
			break;
		
		case COMBOSORT:
	        String selectedItem = (String) comboBox.getSelectedItem();
	        if(selectedItem.compareTo("category") == 0)
	        {
	           filteredList.sort(0);
	        }
	        else
	        {
	        	filteredList.sort(1);
	        }
	        
	        filteredList.initPageManager(10);
	        currentPage = filteredList.getNextPage();
	        previousPage.setEnabled(filteredList.hasPrevPage());
	        nextPage.setEnabled(filteredList.hasNextPage());
	        drawItemList();	        
	        break;
	        	        
		case NEXTPAGE:
			if (filteredList.hasNextPage())
			{
				currentPage = filteredList.getNextPage();
				drawItemList();
				listScroller.repaint();
			}
			previousPage.setEnabled(filteredList.hasPrevPage());
			nextPage.setEnabled(filteredList.hasNextPage());
			break;
			
		case PREVPAGE:
			if (filteredList.hasPrevPage())
			{
				currentPage = filteredList.getPrevPage();
				drawItemList();
				listScroller.repaint();
			}
			previousPage.setEnabled(filteredList.hasPrevPage());
			nextPage.setEnabled(filteredList.hasNextPage());
			break;
			
		case SHOWCART:
		    cart = new JDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
		    cart.setTitle("cart");
		    cart.setLayout(new BorderLayout());

		    JPanel buttonPanel = new JPanel();
		    cartCont.setLayout(new BoxLayout(cartCont, BoxLayout.PAGE_AXIS));
		    DoubleLinkedList selected = filteredList;
		    selected.first();
		    Product node = null;
		    
		    while((node = (Product) selected.current()) != null)
		    {
		    	if (node != null && node.selected)
		    	{
				    cartCont.add(cartElement(node));
		    	}
		    	selected.next();
		    }
		    JScrollPane scroller = new JScrollPane();
		    scroller.setViewportView(cartCont);
		    JLabel textBoxName = new JLabel("Enter your Credentials ");
		    JLabel paymentMethodLabel = new JLabel("Select payment Method ");
		    buttonPanel.add(paymentMethodLabel);
		    buttonPanel.add(paymentMethod);
		    buttonPanel.add(textBoxName);
		    buttonPanel.add(nameOfBuyer);
		    buttonPanel.add(checkOut);
		    
		    cart.add(scroller, BorderLayout.NORTH);	 
		    cart.add(buttonPanel, BorderLayout.SOUTH);
		    cart.setSize(400, 700);
		    cart.setVisible(true);
		    break;
		
		
		case FILTER:
			filteredList = productsList.filter(filterText.getText());
			filteredList.initPageManager(10);
			currentPage = filteredList.getNextPage();
			previousPage.setEnabled(filteredList.hasPrevPage());
			nextPage.setEnabled(filteredList.hasNextPage());
			drawItemList();
			break;
		
		case CHECKOUT:
			String paymentMethodChoice = (String) paymentMethod.getSelectedItem();
			String credentials = nameOfBuyer.getText();
			Date date = new Date();
			if ((paymentMethodChoice.compareTo("") != 0) && (credentials != null))
			{
		    	try
		    	{
		    		File file = new File("data/checkedoutOrdersHistory.txt");
		 
		    		if(!file.exists())
		    		{
		    			file.createNewFile();
		    		}
		 
		    		// true = append file
		    		FileWriter fileWriter = new FileWriter(file.getName(), true);
	    	        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
	    	        String line = "Order submitted on " + date + "\n";
	    	        bufferWriter.write(line);
	    	        line = "  User: " + credentials + " - Payment method " + paymentMethodChoice + "\n";
	    	        bufferWriter.write(line);
					for(int i = 0; i < cartCont.getComponentCount(); i ++)
					{
						int id = Integer.parseInt(((JPanel) cartCont.getComponent(i)).getName());
						Product p = (Product) productsList.search(id);
						line = "  Articolo " + p.getId() + " - Title " + p.title + " - Price " + p.price + "\n";
						bufferWriter.write(line);
						line = "  Client notes: \n" + p.clientNote + "\n\n";
						bufferWriter.write(line);
						// remove from virtual cart
						p.selected = false;
						p.clientNote = "";
					}
					bufferWriter.write("\n*********** END OF ORDER **********\n\n\n");
					bufferWriter.flush();
	    	        bufferWriter.close();
		    	}
		    	catch(IOException e1)
		    	{
		    		// TODO: hadnle exception{
		    	}
				idInCart = "|";
				saveCartFile();
				cartCont.removeAll();
				cartCont.repaint();
				cart.dispose();
				filterText.setText("");
				filteredList = productsList.filter("");
				filteredList.initPageManager(10);
				currentPage = filteredList.getNextPage();
				showCart.setEnabled(false);
				nextPage.setEnabled(filteredList.hasNextPage());
				previousPage.setEnabled(filteredList.hasPrevPage());
				drawItemList();
			}
			else
			{
				JOptionPane.showMessageDialog(null,"Please enter your credentials and select payment method");				
			}
			break;
		case ORDERHISTORY:
			orderHistory = new JDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
			orderHistory.setTitle("Order History");
			orderHistory.setLayout(new BorderLayout());
			orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.PAGE_AXIS));
			JScrollPane orderScroller = new JScrollPane();
			orderScroller.setViewportView(orderPanel);
			JLabel orders = new JLabel(" all orders made up to now");
			orderPanel.add(orders);
			orderHistory.add(orderScroller, BorderLayout.NORTH);	 
		    orderHistory.add(orderPanel, BorderLayout.SOUTH);
			System.out.println("file to be printed");
			orderHistory.setSize(400, 700);
			orderHistory.setVisible(true);
			
		
		}
		
			
	}
	
	private JPanel cartElement(Product node)
	{
		JPanel itemInCart = new JPanel();
		itemInCart.setLayout(new GridLayout(1, 4));
		BufferedImage img = null;
		try 
		{
			img = ImageIO.read(new File(node.getImagePathSmall()));
		} 
		catch (IOException e) 
		{
			// TODO: Deal with the exception
		}
		itemInCart.add(new JLabel(new ImageIcon(img)));
		itemInCart.add(new JLabel(node.title));
		itemInCart.add(new JLabel(String.valueOf(node.price)));
		cartItemNote = new JTextField(node.clientNote, 40);
		cartItemNote.addFocusListener(this);
		cartItemNote.setName(String.valueOf(node.getId()));
		itemInCart.add(cartItemNote);
		itemInCart.setName(String.valueOf(node.getId()));
		return itemInCart;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		int i = Integer.parseInt(arg0.getComponent().getName());
		Product p = (Product) currentPage.get(i);
		BufferedImage img = null;
		try 
		{
			img = ImageIO.read(new File(p.getImagePathLarge()));
		} 
		catch (IOException e) 
		{
			// TODO: Deal with the exception
			System.out.println("image " + p.getImagePathLarge() + " not found");
		}
		detailsContainer.removeAll();
		detailsContainer.add(new JLabel(new ImageIcon(img)), BorderLayout.NORTH);
		detailsContainer.add(new JLabel(p.title), BorderLayout.CENTER);
		detailsContainer.add(new JLabel(p.descriptionLong), BorderLayout.SOUTH);

		frameCont.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
	}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}


	private void loadFile() throws InternalExceptions
	{
        String exception = null;
        int errorCode = -1;
    	boolean noRecordsFound = true;
    	BufferedReader br = null; 

    	// Read ids contained in cart from cart file
		File file = new File("data/cart.dat");
		FileInputStream fis = null;
		idInCart = "";
		try {
			fis = new FileInputStream(file);
 			byte[] content = new byte[fis.available()];
			if (fis.read(content, 0, fis.available()) != -1) 
			{
				idInCart = new String(content);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if (fis != null)
					fis.close();
			}
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}
    	productsList = new DoubleLinkedList();
		showCart.setEnabled(false);
    	try
    	{
    		FileInputStream in = new FileInputStream("data/products.dat");
    		br = new BufferedReader(new InputStreamReader(in));
        	String record = "";
    		while((record = br.readLine())!= null)
    		{
 				noRecordsFound = false;
				try 
				{
					Product p = new Product(record);
					if (idInCart.contains("|" + String.valueOf(p.getId()) + "|"))
					{
						p.selected = true;
						showCart.setEnabled(true);
						String id = "|" + String.valueOf(p.getId()) + "|";
						int startOfNote = idInCart.lastIndexOf(id);
						startOfNote += id.length() + 1;
						int endOfNote = idInCart.indexOf("|", startOfNote) - 1;
						p.clientNote = idInCart.substring(startOfNote, endOfNote);
					}
					productsList.insertTail(p);
				}
				catch (InternalExceptions e) 
				{
					exception = "Record malformed reading prodcuts ('" + record.substring(0,  15) + "....'";
					errorCode = InternalExceptions.ERR_MALFORMED_RECORD;
					break;
				}
       		}
		}
		catch (FileNotFoundException e) 
		{
			exception = "Products file not found";
			errorCode = InternalExceptions.ERR_PRODUCTS_FILE_NOT_FOUND;
		}
    	catch (IOException e) 
    	{
			exception = "I/O exception on loading products (" + e.getMessage() + ")";
			errorCode = InternalExceptions.ERR_EXTERNAL;
    	}

		try
		{
			br.close();
		}
		catch(Exception e)
		{
			// TODO: Log the error somewhere
			;
		}

		if (noRecordsFound)
		{
			exception = "File corrupted or malformed. No records found";
			errorCode = InternalExceptions.ERR_MALFORMED_RECORD;
		}
		
		if (exception != null)
			throw new InternalExceptions(exception, errorCode);
		
		filteredList = productsList.filter("");
	}
	
	private void drawItemList()
	{
		itemListContainer.removeAll();
		
        Product p = (Product) currentPage.first();
        int count = 0;
        while(p != null)
        {
        	itemListContainer.add(listElement(p, count++));
        	p = (Product) currentPage.next();
        }
        itemListContainer.repaint();
        listScroller.repaint();
	}

	public UserInterface(String srcPath)
	{
        try
        {
        	loadFile();
        }
        catch(InternalExceptions e)
        {
        	Object[] options = { "Exit program" };
        	JOptionPane.showOptionDialog(null, e.getMessage(), "Error",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                    null, options, options[0]);
			System.exit(0);
        }
        filteredList.initPageManager(10);
        currentPage = filteredList.getNextPage();

		this.srcPath = srcPath;
		
		setTitle("eCommerce");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
	    // Creating menu bar and related menu items and subitems.
        JMenu mFile = new JMenu("File");
        mFile.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.setMnemonic(KeyEvent.VK_E);
        fileExit.setName("exit");
        fileExit.addActionListener(this);
        mFile.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileOrderHistory = new JMenuItem("Order History");
        fileOrderHistory.setMnemonic(KeyEvent.VK_E);
        fileOrderHistory.setName("Order History");
        fileOrderHistory.addActionListener(this);
        mFile.add(fileOrderHistory);
        mFile.add(fileExit);
        JMenuItem mAbout = new JMenuItem("About");
        mAbout.addActionListener(this);
        JMenuItem mDummy = new JMenuItem("");
        mDummy.setEnabled(false);
        JMenuBar jmb = new JMenuBar();
        jmb.add(mFile);
        jmb.add(mAbout);
        jmb.add(mDummy);
        
        frameCont.add(jmb, BorderLayout.NORTH);
        
        
        // Container for left side of the screen:
        // - container for sort type selector, move to previous and next page (NORTH)
        // - scroll container for container of list of products (CENTER)
        itemListContainer.setLayout(new BoxLayout(itemListContainer, BoxLayout.PAGE_AXIS));
        itemListContainer.setAlignmentX(LEFT_ALIGNMENT);
        drawItemList();
        listScroller.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        listScroller.setViewportView(itemListContainer);

        JPanel commandsContainer = new JPanel();
        comboBox.setActionCommand("combo");
        comboBox.addActionListener(this);
        filter.addActionListener(this);
        commandsContainer.add(comboBoxLab);
        commandsContainer.add(comboBox);
        commandsContainer.add(previousPage);
        commandsContainer.add(nextPage);
        commandsContainer.add(showCart);
        commandsContainer.add(new JLabel("Filter by: "));
        commandsContainer.add(filterText);
        commandsContainer.add(filter);
        nextPage.addActionListener(this);
        nextPage.setName("next");
        nextPage.setEnabled(filteredList.hasNextPage());
        previousPage.addActionListener(this);
        previousPage.setName("prev");
        previousPage.setEnabled(filteredList.hasPrevPage());
        showCart.setName("showCart");
        showCart.addActionListener(this);
        checkOut.setName("checkOut");
        checkOut.addActionListener(this);
        checkOut.setEnabled(true);
        frameLeftSide.setLayout(new BorderLayout());
        frameLeftSide.add(commandsContainer, BorderLayout.NORTH);
        frameLeftSide.add(listScroller, BorderLayout.CENTER);
        frameCont.add(frameLeftSide, BorderLayout.WEST);
        
        detailsContainer.setLayout(new BorderLayout());
        detailsScroller.setViewportView(detailsContainer);

        frameRightSide.setLayout(new BorderLayout());
        frameRightSide.add(detailsScroller, BorderLayout.CENTER);
        frameRightSide.setMaximumSize(new Dimension(500, frameLeftSide.getHeight()));
        frameCont.add(frameRightSide, BorderLayout.EAST);
        

        // All objects having actionPerformed event associated are listed in an 
        // array in order to determine which is the source of the current handled action
        // in the actionPerformed method above
        sourceObj[FILEEXIT] = fileExit;
        sourceObj[ABOUT] = mAbout;
        sourceObj[COMBOSORT] = comboBox;
        sourceObj[NEXTPAGE] = nextPage;
        sourceObj[PREVPAGE] = previousPage;
        sourceObj[SHOWCART] = showCart;
        sourceObj[FILTER] = filter;
        sourceObj[CHECKOUT] = checkOut;
        sourceObj[ORDERHISTORY] = fileOrderHistory;
        
 		// grid.editCellAt(3, 5);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        final int WIDTH = screenSize.width;
        final int HEIGHT = screenSize.height;
        // Setup the frame accordingly
        // This is assuming you are extending the JFrame //class
        this.setSize(WIDTH / 2, HEIGHT / 2);
        this.setLocation(WIDTH / 4, WIDTH / 4);        
	}
	
	public JPanel listElement(Product node, int position)
	{
		JLabel title = new JLabel("Title: " + node.title);
		JLabel artist = new JLabel("Artist: " + node.artist);
		JLabel category = new JLabel("Category: " + node.category);
		JLabel descriptionShort = new JLabel(node.descriptionShort);
		JLabel price = new JLabel(String.valueOf("Price: " + node.price));
		JCheckBox checkBox = new JCheckBox("Select to buy");
		checkBox.setName("cb" + node.getId());
		checkBox.addActionListener(this);
		if (node.selected)
		{
			checkBox.setSelected(true);
		}
		BufferedImage img = null;
		try 
		{
			img = ImageIO.read(new File(node.getImagePathSmall()));
		} 
		catch (IOException e) 
		{
			// TODO: Deal with the exception
		}
		ImageIcon icon = new ImageIcon(img);
		JLabel image = new JLabel(icon);
		JPanel newElement = new JPanel();
		JPanel leftPart = new JPanel();
		JPanel rightPart = new JPanel();
		
		rightPart.setLayout(new GridLayout(6,1));
		leftPart.add(image);
		rightPart.add(title);
		rightPart.add(artist);
		rightPart.add(category);
		rightPart.add(descriptionShort);
		rightPart.add(price);
		rightPart.add(checkBox);
		newElement.add(leftPart);
		newElement.add(rightPart);
		newElement.setName(String.valueOf(position));
		newElement.addMouseListener(this);
		return newElement;
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) 
	{
		String id = "|" + arg0.getComponent().getName() + "|";
		int startOfNote = idInCart.lastIndexOf(id);
		startOfNote += id.length() + 1;
		int endOfNote = idInCart.indexOf("|", startOfNote) - 1;
		String textEdited = ((JTextField) arg0.getComponent()).getText();
		String temp = idInCart.substring(0, startOfNote) + textEdited + idInCart.substring(endOfNote);
 		idInCart = temp;
 		Product product = 
				(Product) filteredList.search(Integer.parseInt(arg0.getComponent().getName()));
		product.clientNote = textEdited;
		product = (Product) productsList.search(Integer.parseInt(arg0.getComponent().getName()));
		product.clientNote = textEdited;
		saveCartFile();
	}
	
}
