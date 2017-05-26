package userInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import core.LogObject;
import core.SQL;


public class EditController
{
	@FXML private Button abbrechenButton;
	@FXML private ComboBox<String> dropdown;
	@FXML private ComboBox<String> dropdownTasks;
	private Stage stage;
	private UserInterfaceController controller;
	private Image icon;
	private LogObject object;

	public void init(UserInterfaceController controller, Stage stage, String savePath, Image icon, LogObject object)
	{
		this.controller = controller;
		this.stage = stage;
		this.icon = icon;
		this.object = object;

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

				if(newValue != null && ! newValue.equals(""))
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
		
		dropdown.getSelectionModel().select(object.getProject());
		dropdownTasks.getSelectionModel().select(object.getTask());		
	}

	public void okButton(ActionEvent e)
	{
		String project = dropdown.getValue();

		String task = dropdownTasks.getValue();

		if(project == null || project.equals("") || task == null || task.equals(""))
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
			LogObject newLog = new LogObject(object.getYear(), object.getMonth(), object.getDay(), object.getStartTime(), object.getEndTime(), object.getDuration(), project, task);
			controller.updateEntry(object, newLog);
			stage.close();
		}
	}

	public void buttonDelete()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Löschen");
		alert.setHeaderText("");
		alert.setContentText("Möchten Sie den Eintrag wirklich unwiederruflich aus der Datenbank löschen?");
		Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(icon);
		
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK)
		{
			controller.deleteEntry(object);
			stage.close();
		}		
	}

	public void abbrechenButton(ActionEvent e)
	{
		stage.close();
	}
}