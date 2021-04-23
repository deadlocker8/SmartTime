package de.deadlocker8.smarttime.core;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import de.thecodelabs.utils.io.PathUtils;
import de.thecodelabs.utils.util.SystemUtils;


public class Utils 
{   
	private static final ResourceBundle bundle = ResourceBundle.getBundle("de/deadlocker8/smarttime/", Locale.GERMANY);
	
    private static final String[] AVAILABLE_MONTH_NAMES = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
    
    public static final String DATABASE_NAME = "SmartTime.db";

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
			Path configDir = SystemUtils.getApplicationSupportDirectoryPath(bundle.getString("folder"));
			Reader reader = Files.newBufferedReader(configDir.resolve("settings.json"), Charset.forName("UTF-8"));
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

		Path configDir = SystemUtils.getApplicationSupportDirectoryPath(bundle.getString("folder"));
		PathUtils.createDirectoriesIfNotExists(configDir);
		Writer writer = Files.newBufferedWriter(configDir.resolve("settings.json"), Charset.forName("UTF-8"));
		writer.write(jsonString);
		writer.close();
	}
}