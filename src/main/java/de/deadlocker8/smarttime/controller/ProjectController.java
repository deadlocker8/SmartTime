package de.deadlocker8.smarttime.controller;

import de.deadlocker8.smarttime.core.SQL;
import de.deadlocker8.smarttime.core.Settings;
import de.deadlocker8.smarttime.core.Utils;
import de.thecodelabs.utils.ui.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Controllerklasse für das Projektfenster
 *
 * @author Robert
 */

public class ProjectController
{
	@FXML
	private Button abbrechenButton;
	@FXML
	private ComboBox<String> dropdown;
	@FXML
	private ComboBox<String> dropdownTasks;
	private Stage stage;
	private Controller controller;

	public void init(Controller controller, Stage stage, Settings settings)
	{
		this.controller = controller;
		this.stage = stage;
		this.stage.setOnCloseRequest((handle) -> {
			controller.projektExistiertFlag = false;
			stage.close();
		});

		ArrayList<String> objects = new ArrayList<String>();

		SQL sql = new SQL(settings.getSavePath() + "/" + Utils.DATABASE_NAME);
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

		dropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
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
		});

		dropdown.getEditor().textProperty().addListener((a, oldValue, newValue) -> {
			dropdown.setValue(newValue);
		});

		dropdownTasks.getEditor().textProperty().addListener((a, oldValue, newValue) -> {
			dropdownTasks.setValue(newValue);
		});
	}

	public void okButton(ActionEvent e)
	{
		String project = dropdown.getValue();

		String task = dropdownTasks.getValue();

		if(project == null || project.equals("") || task == null || task.equals(""))
		{
			Alerts.getInstance().createAlert(AlertType.WARNING, "Warnung", "Die Felder dürfen nicht leer sein.", stage);
		}
		else
		{
			controller.setLabels(project, task);
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