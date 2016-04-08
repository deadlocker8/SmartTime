package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * import old Logfiles
 * @author Robert
 *
 */
public class ReadFromFile
{
	public static void importFromSmartTime(File file)
	{
		
	}
	
	public static ArrayList<String> read(String directory) throws IOException
	{
		FileInputStream fis = new FileInputStream(directory + "/save.log");
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));		
		ArrayList<String> list = new ArrayList<>();

		String line;		
		while((line = reader.readLine()) != null)
		{
			list.add(line);
		}
		
		reader.close();
		return list;
	}	
}