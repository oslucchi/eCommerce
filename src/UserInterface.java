

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumnModel;
import javax.swing.text.*;

import com.sun.j3d.utils.geometry.Box;

public class UserInterface extends JFrame 
						   implements ActionListener, WindowStateListener, 
						   			  MouseListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    static private final int FILEEXIT = 1;
    static private final int ABOUT = 2;
    static private final int COMBOSORT = 3;
    
    String srcPath = null;
    
    private Object[] sourceObj = new Object[10];
    JPanel frameLeftSide = new JPanel();
    JPanel frameRightSide = new JPanel();
    JPanel itemListContainer = new JPanel();
    JScrollPane listScroller = new JScrollPane();

    private JButton previousPage = new JButton("<");
    private JButton nextPage = new JButton(">");
	String[] choiches = {"category", "price"};
    JComboBox comboBox = new JComboBox(choiches);
    JLabel comboBoxLab = new JLabel("Sort by:");

	private Container frameCont = this.getContentPane();

	DoubleLinkedList productsList = null, currentPage = null;

	@Override
	public void windowStateChanged(WindowEvent e) 
	{
		if (e.getNewState() == WindowEvent.WINDOW_CLOSED)
			System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
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
	           productsList.sort(0);
	        }
	        else
	        {
	        	productsList.sort(1);
	        }
	        productsList.initPageManager(10);
	        currentPage = productsList.getNextPage();
	        drawItemList();
	        
	        break;
		}	
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		if (arg0.getComponent().getName().compareTo("next") == 0)
		{
			if (productsList.hasNextPage())
			{
				currentPage = productsList.getNextPage();
				drawItemList();
				listScroller.repaint();
			}
		}
		else if (arg0.getComponent().getName().compareTo("prev") == 0)
		{
			if (productsList.hasPrevPage())
			{
				currentPage = productsList.getPrevPage();
				drawItemList();
				listScroller.repaint();
			}
		}
		else
		{
			int i = Integer.parseInt(arg0.getComponent().getName());
			Product p = (Product) currentPage.get(i);
			System.out.println(p.getId() + " " + p.title + " '" + p.descriptionLong);			
		}

		if (productsList.hasPrevPage())
		{
			previousPage.setEnabled(true);
		}
		else
		{
			previousPage.setEnabled(false);
		}
		if (productsList.hasNextPage())
		{
			nextPage.setEnabled(true);
		}
		else
		{
			nextPage.setEnabled(false);
		}

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

    	productsList = new DoubleLinkedList();
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
        productsList.initPageManager(10);
        currentPage = productsList.getNextPage();

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
        commandsContainer.add(comboBoxLab);
        commandsContainer.add(comboBox);
        commandsContainer.add(previousPage);
        commandsContainer.add(nextPage);
     
        nextPage.addMouseListener(this);
        nextPage.setName("next");
        previousPage.addMouseListener(this);
        previousPage.setName("prev");
        previousPage.setEnabled(false);
        
        frameLeftSide.setLayout(new BorderLayout());
        frameLeftSide.add(commandsContainer, BorderLayout.NORTH);
        frameLeftSide.add(listScroller, BorderLayout.CENTER);
        frameCont.add(frameLeftSide, BorderLayout.WEST);
        // All objects having actionPerformed event associated are listed in an 
        // array in order to determine which is the source of the current handled action
        // in the actionPerformed method above
        sourceObj[FILEEXIT] = fileExit;
        sourceObj[ABOUT] = mAbout;
        sourceObj[COMBOSORT] = comboBox;

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
		JLabel title = new JLabel(node.title);
		JLabel artist = new JLabel(node.artist);
		JLabel category = new JLabel(node.category);
		JLabel descriptionShort = new JLabel(node.descriptionShort);
		JLabel price = new JLabel(String.valueOf(node.price));
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(node.getImagePathSmall()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageIcon icon = new ImageIcon(img);
		JLabel image = new JLabel(icon);
		JPanel newElement = new JPanel();
		JPanel leftPart = new JPanel();
		JPanel rightPart = new JPanel();
		rightPart.setLayout(new GridLayout(5,1));
		leftPart.add(image);
		rightPart.add(title);
		rightPart.add(artist);
		rightPart.add(category);
		rightPart.add(descriptionShort);
		rightPart.add(price);
		newElement.add(leftPart);
		newElement.add(rightPart);
		newElement.setName(String.valueOf(position));
		newElement.addMouseListener(this);
		return newElement;
		
	}
	
        
}
