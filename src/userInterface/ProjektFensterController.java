package userInterface;

import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import core.SQL;

/**
 * Controllerklasse für das Projektfenster
 * @author Robert
 *
 */

public class ProjektFensterController implements EventHandler<WindowEvent>
{	
	@FXML	private Button abbrechenButton;	
	@FXML	private ComboBox<String> dropdown;	
	@FXML	private ComboBox<String> dropdownTasks;	
	private Stage stage;
	private UserInterfaceController controller;	
	private Image icon;
	
	
	public void init(UserInterfaceController controller, String savePath, Image icon)
	{	
		this.controller = controller;		
		this.icon = icon;	
		
		ArrayList<String> objects = new ArrayList<String>();
		
		SQL sql = new SQL(savePath);
		try
		{					
			objects = sql.getProjectNames();	
			Collections.sort(objects);
		}
		catch(Exception e)
		{								
		}	
		
		dropdown.getItems().addAll(objects);			
		dropdown.setStyle("-fx-font-family: \"Arial\";-fx-font-size: 18px;");		
		dropdownTasks.setStyle("-fx-font-family: \"Arial\";-fx-font-size: 18px;");		
		
		dropdown.valueProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				dropdownTasks.getItems().clear();
				
				if(newValue != null && !newValue.equals(""))
				{
					try
					{
						ArrayList<String> tasks = sql.getTaskNamesByProject(newValue);
						Collections.sort(tasks);
						dropdownTasks.getItems().addAll(tasks);						
					}
					catch(Exception e)
					{							
					}
				}
			}
		});		
	}
		
	@Override
	public void handle(WindowEvent arg0) 
	{
		controller.projektExistiertFlag = false;
		stage.close();
	} 	
	
	public void setStage(Stage s)
	{
		stage = s;
		stage.setOnCloseRequest(this);
	}	
	
	public void okButton(ActionEvent e)
	{
			String project = dropdown.getValue();				
			
			String task = dropdownTasks.getValue(); 				
			
			if (project == null  || project.equals("") || task == null || task.equals(""))
			{			
				Alert alert = new Alert(AlertType.WARNING);	
				alert.setTitle("Warnung");
				alert.setHeaderText("");
				alert.setContentText("Die Felder dürfen nicht leer sein.");
				alert.initOwner(stage);
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(icon);
				alert.showAndWait();		
			}
			else
			{				
				controller.setLabels(project,task);
				controller.projektExistiertFlag = true;		
				controller.newProject(project, task);
				stage.close();
			}				 
	}	

	public void abbrechenButtond(ActionEvent e)
	{
		 stage.close();			
	}
}