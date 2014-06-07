

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumnModel;

public class UserInterface extends JFrame 
						   implements KeyListener, FocusListener, ActionListener, WindowStateListener, 
						   			  MouseListener, PopupMenuListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    static private final int FILEEXIT = 3;
    static private final int ABOUT = 4;
    
    private String srcPath = null;
    
    private Object[] sourceObj = new Object[10];
    
    private JButton predBtn = new JButton("Predict");
    private JButton chgBtn = new JButton("Change");
	private JTable grid = null;
	private JPopupMenu popUp = null; 
	private JLabel popupMenuName = new JLabel();

	private Container frameCont = this.getContentPane();

	private int statUserIndx = -1;
	
	DoubleLinkedList productsList = null, currentPage = null;

	@Override
	public void windowStateChanged(WindowEvent e) 
	{
		if (e.getNewState() == WindowEvent.WINDOW_CLOSED)
			System.exit(0);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
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
			
		}	
	}

	@Override
	public void focusLost(FocusEvent arg0) 
	{
		// Get the name of the component for which focus has been lost
		String compName = arg0.getComponent().getName();
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		// Only digits, Delete and Backspace are allowed in TextFields
		
		if ((arg0.getKeyChar() != KeyEvent.VK_DELETE) &&
			(arg0.getKeyChar() != KeyEvent.VK_BACK_SPACE))
		{
			if ((arg0.getKeyChar() < '0') || (arg0.getKeyChar() > '9'))
			{
				JOptionPane.showMessageDialog(this, "Only digits are allowed in this field");
			    arg0.consume();
				return;
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) 
	{
	}
	
	@Override
	public void keyPressed(KeyEvent arg0)
	{
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
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
		if (arg0.isPopupTrigger())
		{
			// Get the row on which the mouse was pressed and associate to the statistic user index var
			statUserIndx = grid.rowAtPoint(arg0.getPoint());
			grid.setRowSelectionInterval(statUserIndx, statUserIndx);
			
			// Set the menu label accordingly with the selected user
			((JLabel) popUp.getComponent(popUp.getComponentIndex(popupMenuName)))
				.setText("Statistic menu for user " + statUserIndx);
			popUp.show(arg0.getComponent(), arg0.getX(), arg0.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) 
	{
		grid.clearSelection();
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) 
	{
		grid.clearSelection();
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
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
			// TODO: Log the error somewere
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

	private void drawGrid()
	{
		// this method is used both to re-draw after a save or to draw a new table
		// after a load. Because the newly loaded matrix could be of different size
		// compared to the previously loaded, if the application had already instantiated 
		// a grid it has to be removed from the panel before we create the new one.
		if (grid != null)
			frameCont.remove(grid);

		// Create the new grid, set the renderer for cell coloring and add to the container
		grid.setRowHeight(20);
		grid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		grid.addMouseListener(this);
		grid.setEnabled(false);
        frameCont.add(grid, BorderLayout.CENTER);

        // Fill grid with items. Elements of the grid will be Ratings. This class has a type
        // and a value. The coloring is decided based on the type
		TableColumnModel cModel = grid.getColumnModel();
		
		// Populate matrix with data. The Rating type is assigned to each item based
		// on the value source		
	}
	
	public UserInterface(String srcPath)
	{
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

        // PopUp menu for top 4 recommendations and pearson correlation
        popUp = new JPopupMenu();
		popUp.add(popupMenuName);
		popUp.add(new JLabel(" "));
		JMenuItem topRecom = new JMenuItem("Top 4 Recommandation");
		topRecom.setMnemonic(KeyEvent.VK_T);
		topRecom.addActionListener(this);
		popUp.add(topRecom);
		JMenuItem pearCorr = new JMenuItem("Pearson correlation");
		pearCorr.setMnemonic(KeyEvent.VK_P);
		pearCorr.addActionListener(this);
		popUp.add(pearCorr);
		popUp.addPopupMenuListener(this);
 
        JPanel subPanel = new JPanel();
        
        frameCont.add(subPanel, BorderLayout.EAST);
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        
        // Adding buttons for user action. The current class implements actionListener
        // an actionPerformed method is provided to handle the button pressed event
        subPanel.add(predBtn);
        subPanel.add(chgBtn);
        

        //Examples
        // Add text input a name to identify which control is sending the event in 
        // the even listener above
        // chgItemRow.setName("row");

        // Add key and focus listeners to input text fields to control user input 
        // chgItemRow.addKeyListener(this);
		// chgItemRow.addFocusListener(this);
		       
        predBtn.addActionListener(this);
		chgBtn.addActionListener(this);

		// disable action and input components by default.
		// they will be enable once the first file is loaded
        chgBtn.setEnabled(false);
        predBtn.setEnabled(false);
        
        // All objects having actionPerformed event associated are listed in an 
        // array in order to determine which is the source of the current handled action
        // in the actionPerformed method above
        sourceObj[FILEEXIT] = fileExit;
        sourceObj[ABOUT] = mAbout;

 		// grid.editCellAt(3, 5);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        final int WIDTH = screenSize.width;
        final int HEIGHT = screenSize.height;
        // Setup the frame accordingly
        // This is assuming you are extending the JFrame //class
        this.setSize(WIDTH / 2, HEIGHT / 2);
        this.setLocation(WIDTH / 4, WIDTH / 4);
        
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
	}
}
