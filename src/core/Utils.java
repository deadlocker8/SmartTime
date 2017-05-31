package core;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.gson.Gson;

import tools.PathUtils;

public class Utils 
{   
	private static final ResourceBundle bundle = ResourceBundle.getBundle("userInterface/", Locale.GERMANY);
	
    private static final String[] AVAILABLE_MONTH_NAMES = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};

    public static String getMonthName(int monthNumberOneIndexed)
    {
        return AVAILABLE_MONTH_NAMES[monthNumberOneIndexed];
    }    
    
    public static int getMonthNumber(String monthName)
    {
        return Arrays.asList(AVAILABLE_MONTH_NAMES).indexOf(monthName) + 1;
    }    
    
    public static Settings loadSettings()
	{
		Settings settings;
		try
		{
			Gson gson = new Gson();			
			Reader reader = Files.newBufferedReader(Paths.get(PathUtils.getOSindependentPath() + bundle.getString("folder") + "/settings.json"), Charset.forName("UTF-8"));
			settings = gson.fromJson(reader, Settings.class);	
			reader.close();
			return settings;
		}
		catch(IOException e)
		{
			return null;
		}
	}
	
	public static void saveSettings(Settings settings) throws IOException
	{		
		Gson gson = new Gson();
		String jsonString = gson.toJson(settings);
		PathUtils.checkFolder(new File(PathUtils.getOSindependentPath() + bundle.getString("folder")));
		Writer writer = Files.newBufferedWriter(Paths.get(PathUtils.getOSindependentPath() + bundle.getString("folder")  + "/settings.json"), Charset.forName("UTF-8"));
		writer.write(jsonString);
		writer.close();
	}
}