package de.deadlocker8.smarttime.controller;

import java.util.ArrayList;
import java.util.Collections;

import de.deadlocker8.smarttime.core.SQL;
import de.deadlocker8.smarttime.core.Settings;
import de.deadlocker8.smarttime.core.Utils;
import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.ui.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ReportController
{
	@FXML private ComboBox<String> comboBoxProject;
	@FXML private ListView<CheckBox> listViewTasks;
	@FXML private RadioButton radioButtonList;
	@FXML private RadioButton radioButtonGroupByTasks;
	@FXML private RadioButton radioButtonGroupByDate;
	
	private Controller controller;
	private Stage stage;
	private Image icon;
	private Settings settings;
	private SQL sql;
	private final String ALL_PROJECTS = "Alle";

	public void init(Controller controller, Stage stage, Settings settings, Image icon)
	{
		this.controller = controller;
		this.stage = stage;
		this.settings = settings;
		this.icon = icon;
		
		sql = new SQL(settings.getSavePath() + "/" + Utils.DATABASE_NAME);
		prefill();
	}
	
	private void prefill()
	{
		ToggleGroup toggleGroup = new ToggleGroup();
		radioButtonList.setToggleGroup(toggleGroup);
		radioButtonGroupByTasks.setToggleGroup(toggleGroup);
		radioButtonGroupByDate.setToggleGroup(toggleGroup);
		
		radioButtonList.setSelected(true);		
		
		try
		{
			ArrayList<String> projectNames = sql.getProjectNames();
			Collections.sort(projectNames);
			comboBoxProject.getItems().add(ALL_PROJECTS);
			comboBoxProject.getItems().addAll(projectNames);
		}
		catch(Exception e)
		{
			Logger.error(e);
		}
		
		comboBoxProject.valueProperty().addListener((observable, oldValue, newValue)->{
			if(newValue.equals(ALL_PROJECTS))
			{
				radioButtonGroupByTasks.setDisable(true);
				listViewTasks.getItems().clear();
				return;
			}
			
			try
			{
				ArrayList<String> tasks = sql.getTaskNamesByProject(newValue);
				Collections.sort(tasks);
				listViewTasks.getItems().clear();
				for(String currentTask : tasks)
				{
					CheckBox currentCheckBox = new CheckBox(currentTask);
					currentCheckBox.setSelected(true);
					currentCheckBox.selectedProperty().addListener((obs, oldVal, newVal)->{
						checkTasks();
					});
					listViewTasks.getItems().add(currentCheckBox);
				}
				
				radioButtonGroupByTasks.setDisable(false);
			}
			catch(Exception e)
			{
				Logger.error(e);
			}
			
			checkTasks();
		});
		
		comboBoxProject.setValue(ALL_PROJECTS);
	}
	
	private int getNumberOfActivatedTasks()
	{
		int numberOfActivatedTasks = 0;
		for(CheckBox currentCheckBox : listViewTasks.getItems())
		{
			if(currentCheckBox.isSelected())
			{
				numberOfActivatedTasks++;
			}
		}
		
		return numberOfActivatedTasks;
	}
	
	private void checkTasks()
	{		
		if(getNumberOfActivatedTasks() > 1)
		{
			radioButtonGroupByTasks.setDisable(false);
		}
		else
		{
			radioButtonGroupByTasks.setDisable(true);
		}	
	}
	
	@FXML
	public void generateReport()
	{
		String project = comboBoxProject.getValue();
		if(project == null || project.equals(""))
		{
			Alerts.getInstance().createAlert(AlertType.WARNING, "Warnung", "Bitte wähle ein Projekt aus", stage);
			return;
		}
		
		if(!project.equals(ALL_PROJECTS) && getNumberOfActivatedTasks() == 0)
		{
			Alerts.getInstance().createAlert(AlertType.WARNING, "Warnung", "Bitte wähle mindestens einen Task aus", stage);
			return;
		}
		
		//TODO file chooser
		//TODO generate report
	}

	@FXML
	public void cancel()
	{
		stage.close();
	}
}