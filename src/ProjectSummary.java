import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProjectSummary {
	protected class ClassSummary 
	{
		private String name = "";
		private int numOfMethods = 0;
		private int numOfFields = 0;
		private int numOfLines = 0;
		public ClassSummary(Class<?> objClass)
		{
			name = objClass.getName();
			numOfMethods = objClass.getDeclaredMethods().length;
			numOfFields = objClass.getDeclaredFields().length;
			if (sourceLinesCount.get(name) != null)
			{
				numOfLines = sourceLinesCount.get(name).intValue();
			}
		}
		public int getNumOfMethods() {
			return numOfMethods;
		}
		public int getNumOfFields() {
			return numOfFields;
		}
		public int getNumOfLines() {
			return numOfLines;
		}
		public String getName() {
			return name;
		}
	}

	private ArrayList<ClassSummary> classInfo = null;
	private Map<String, Integer> sourceLinesCount = new HashMap<String, Integer>();
			
	private void getSourceInfo(File root)
	{
		String[] files = root.list();
		for(String file: files)
		{
			File temp = new File(root.getAbsoluteFile() + "/" + file);
			if (temp.isDirectory())
			{
				getSourceInfo(temp);
			}
			else if (temp.getName().endsWith(".java"))
			{
				int lineCounter = 0;
				String className = temp.getName().substring(0, temp.getName().indexOf(".java"));
				FileReader fr = null;
				try 
				{
					fr = new FileReader(temp.getAbsoluteFile());
					BufferedReader br = new BufferedReader(fr); 
					while(true)
					{
						String t = br.readLine();
						if(t == null)
							break;
						t.trim();
						if (!t.startsWith("//") && t.compareTo("") != 0)
						{
							lineCounter++;
						}
					};
					br.close();
					fr.close();
				}
				catch (FileNotFoundException e1) 
				{
					;
				} 
				catch (IOException e) 
				{
					;
				}
				sourceLinesCount.put(className, lineCounter);
			}
		}
	}
	
	private void getClassInfo(File root, File srcRoot)
	{
		String rootPath = root.getAbsolutePath();
		String[] files = root.list();
		for (String fi : files)
		{
			File temp = new File(rootPath + "/" + fi);
			if (temp.isDirectory())
			{
				getClassInfo(temp, srcRoot);
			}
			else if ((rootPath + "/" + fi).endsWith(".class"))
			{
				Class<?> objClass = null;
				try 
				{
					objClass = Class.forName(fi.substring(0, fi.indexOf(".class")));
				} 
				catch (ClassNotFoundException e) 
				{
					// Class not foung. Nothing to do with it
					continue;
				}
				ClassSummary cs = new ClassSummary(objClass);
				classInfo.add(cs);
			}
		}
	}

	public ProjectSummary(String srcPath)
	{
		classInfo = new ArrayList<ClassSummary>();
		File root = new File(".");
		File srcRoot = null;
		if (srcPath != null)
		{
			srcRoot = new File(srcPath);
			getSourceInfo(srcRoot);
		}
		getClassInfo(root, srcRoot);
	}
	
	public int getNumberOfClasses()
	{
		return classInfo.size();
	}
	
	public ClassSummary getClassSummary(int i)
	{
		return classInfo.get(i);
	}

	public int getNumberOfMethods() 
	{
		int tot = 0;
		for(ClassSummary cs: classInfo)
		{
			tot += cs.numOfMethods;
		}
		return tot;
	}

	public int getNumberOfLines() 
	{
		int tot = 0;
		for(String s: sourceLinesCount.keySet())
		{
			tot += sourceLinesCount.get(s).intValue();
		}
		return tot;
	}
}
