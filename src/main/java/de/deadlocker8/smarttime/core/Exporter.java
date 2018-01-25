package de.deadlocker8.smarttime.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logger.Logger;
import tools.AlertGenerator;

public class Exporter
{
	private String path;
	private Stage stage;
	private Image icon;
	
	public Exporter(String path, Stage stage, Image icon)
	{
		this.path = path;
		this.stage = stage;
		this.icon = icon;
	}
	
	public void exportAsJSON(File file)
	{
		try
		{			
			SQL sql = new SQL(path);			
			ArrayList<LogObject> objects = sql.getLogObjects();
			
			JsonObject allItems = new JsonObject();			
			allItems.addProperty("SmartTime", new Date().toString());
			
			JsonArray allLogObjects = new JsonArray();			
			
			for(LogObject current : objects)
			{
				JsonObject item = new JsonObject();	
				item.addProperty("date", current.getDate());
				item.addProperty("startTime", current.getStartTime());
				item.addProperty("endTime", current.getEndTime());
				item.addProperty("duration", current.getDuration());	
				item.addProperty("project", current.getProject());
				item.addProperty("task", current.getTask());
				allLogObjects.add(item);
			}	
			
			allItems.add("logObjects", allLogObjects);
		
			BufferedWriter out = new BufferedWriter(new FileWriter(file));		
            out.write(allItems.toString());           
            out.close();   
            
			Platform.runLater(()->{				
				AlertGenerator.showAlert(AlertType.INFORMATION, "Erfolgreich exportiert", "", "Der Exportvorgang wurde erfolgreich abgeschlossen.", icon, stage, null, false);
			});
		}
		catch(Exception e)
		{			
			Logger.error(e);
			Platform.runLater(()->{
				AlertGenerator.showAlert(AlertType.ERROR, "Fehler", "", "Beim Exportieren der Daten ist ein Fehler aufgetreten.", icon, stage, null, false);
			});
		}		
	}	
}