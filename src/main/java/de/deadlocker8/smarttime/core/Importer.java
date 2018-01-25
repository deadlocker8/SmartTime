package de.deadlocker8.smarttime.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logger.Logger;
import tools.AlertGenerator;

public class Importer
{
	private String path;
	private Stage stage;
	private Image icon;
	
	public Importer(String path, Stage stage, Image icon)
	{
		this.path = path;
		this.stage = stage;
		this.icon = icon;
	}
	
	public void importFromSmartTime(File file)
	{
		try
		{
			ArrayList<String > lines = readFile(file);
			ArrayList<LogObject> objects = new ArrayList<LogObject>();
			for(String item : lines)
			{
				String[] parts = item.split("\t");
				
				String date = parts[0];
				String[] dateParts = date.split("-");
				
				LogObject current = new LogObject(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]), parts[3], parts[4], Long.parseLong(parts[7]), parts[5], parts[6]);
				objects.add(current);	
			}
			
			SQL sql = new SQL(path);			
			for(LogObject o : objects)
			{
				sql.insert(o);
			}
			
			Platform.runLater(()->{				
				AlertGenerator.showAlert(AlertType.INFORMATION, "Erfolgreich importiert", "", "Der Importvorgang wurde erfolgreich abgeschlossen.", icon, stage, null, false);
			});
		}
		catch(Exception e)
		{			
			Logger.error(e);
			Platform.runLater(()->{
				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", "Beim Importieren der Daten ist ein Fehler aufgetreten.", icon, stage, null, false);
			});
		}		
	}	
	
	public void importFromDB(File file)
	{		
		SQL sql = new SQL(file.getAbsolutePath());	
		try
		{
			ArrayList<LogObject> objects = sql.getLogObjects();
			SQL currentDB = new SQL(path);
			for(LogObject item : objects)
			{
				currentDB.insert(item);
			}
			
			Platform.runLater(()->{				
				AlertGenerator.showAlert(AlertType.INFORMATION, "Erfolgreich importiert", "", "Der Importvorgang wurde erfolgreich abgeschlossen.", icon, stage, null, false);
			});
		}
		catch(Exception e)
		{			
			Logger.error(e);
			Platform.runLater(()->{
				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", "Beim Importieren der Daten ist ein Fehler aufgetreten.", icon, stage, null, false);
			});
		}		
	}
	
	public void importFromJSON(File file)
	{							
		try 
		{			
			FileInputStream fis = new FileInputStream(file);			
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));		
			String text = "";		
			String line;		
			while ((line = reader.readLine()) != null) 
			{
				text = text + line;
			}			
			reader.close();			
			
			JsonObject allItems = new JsonParser().parse(text).getAsJsonObject();
			
			SQL sql = new SQL(path);
			JsonArray loadedItems = (JsonArray)allItems.get("logObjects");
			for(int i = 0; i < loadedItems.size(); i++)
			{
				JsonObject item = (JsonObject) loadedItems.get(i);
				String date = item.get("date").getAsString();
				String startTime = item.get("startTime").getAsString();
				String endTime = item.get("endTime").getAsString();
				long duration = item.get("duration").getAsLong();
				String project = item.get("project").getAsString();
				String task = item.get("task").getAsString();
				
				String[] dateParts = date.split(Pattern.quote("."));
				
				LogObject loadedObject = new LogObject(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]), startTime, endTime, duration, project, task);				
				sql.insert(loadedObject);
			}
		}
		catch(Exception e)
		{			
			Logger.error(e);
			Platform.runLater(()->{
				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", "Beim Importieren der Daten ist ein Fehler aufgetreten.", icon, stage, null, false);
			});
		}					
	}
	
	private ArrayList<String> readFile(File file) throws Exception
	{
		FileInputStream fis = new FileInputStream(file);
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
