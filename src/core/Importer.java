package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.jason.JSONArray;
import org.jason.JSONObject;

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
				Alert alert = new Alert(AlertType.INFORMATION);			
				alert.setTitle("Erfolgreich importiert");
				alert.setHeaderText("");
				alert.setContentText("Der Importvorgang wurde erfolgreich abgeschlossen.");
				alert.initOwner(stage);
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(icon);
				alert.showAndWait();
			});
		}
		catch(Exception e)
		{			
			e.printStackTrace();
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Fehler");
				alert.setHeaderText("");
				alert.setContentText("Beim Importieren der Daten ist ein Fehler aufgetreten.");
				alert.initOwner(stage);
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(icon);
				alert.showAndWait();
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
				Alert alert = new Alert(AlertType.INFORMATION);			
				alert.setTitle("Erfolgreich importiert");
				alert.setHeaderText("");
				alert.setContentText("Der Importvorgang wurde erfolgreich abgeschlossen.");
				alert.initOwner(stage);
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(icon);
				alert.showAndWait();
			});
		}
		catch(Exception e)
		{			
			e.printStackTrace();
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Fehler");
				alert.setHeaderText("");
				alert.setContentText("Beim Importieren der Daten ist ein Fehler aufgetreten.");
				alert.initOwner(stage);
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(icon);
				alert.showAndWait();
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
			
			JSONObject allItems = new JSONObject(text);		
			
			SQL sql = new SQL(path);
			JSONArray loadedItems = (JSONArray)allItems.get("logObjects");
			for(int i = 0; i < loadedItems.length(); i++)
			{
				JSONObject item = (JSONObject) loadedItems.get(i);
				String date = item.getString("date");
				String startTime = item.getString("startTime");
				String endTime = item.getString("endTime");
				long duration = item.getLong("duration");
				String project = item.getString("project");
				String task = item.getString("task");
				
				String[] dateParts = date.split(Pattern.quote("."));
				
				LogObject loadedObject = new LogObject(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]), startTime, endTime, duration, project, task);				
				sql.insert(loadedObject);
			}
		}
		catch(Exception e)
		{			
			e.printStackTrace();
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Fehler");
				alert.setHeaderText("");
				alert.setContentText("Beim Importieren der Daten ist ein Fehler aufgetreten.");
				alert.initOwner(stage);
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(icon);
				alert.showAndWait();
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
