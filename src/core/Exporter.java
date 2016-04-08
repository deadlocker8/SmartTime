package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.jason.JSONObject;

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
			
			JSONObject allItems = new JSONObject();
			
			allItems.put("SmartTime", new Date());		
			
			for(LogObject current : objects)
			{
				JSONObject item = new JSONObject();	
				item.put("date", current.getDate());
				item.put("startTime", current.getStartTime());
				item.put("endTime", current.getEndTime());
				item.put("duration", current.getDuration());	
				item.put("project", current.getProject());
				item.put("task", current.getTask());
				allItems.append("logObjects", item);
			}	
		
			BufferedWriter out = new BufferedWriter(new FileWriter(file));		
            out.write(allItems.toString());           
            out.close();    
			
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.INFORMATION);			
				alert.setTitle("Erfolgreich exportiert");
				alert.setHeaderText("");
				alert.setContentText("Der Exportvorgang wurde erfolgreich abgeschlossen.");
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
				alert.setContentText("Beim Exportieren der Daten ist ein Fehler aufgetreten.");
				alert.initOwner(stage);
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(icon);
				alert.showAndWait();
			});
		}		
	}	
}