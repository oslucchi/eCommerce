

import java.awt.EventQueue;

public class ECommerce {

	/**
	 * @param args
	 */
	public static void main(final String[] args) 
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				String srcPath = null;
				if (args.length == 1)
				{
					srcPath = args[0];
				}
				UserInterface ui = new UserInterface(srcPath);
				ui.setVisible(true);
			}
		});
	}
}
