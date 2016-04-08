package charts;

import java.util.ArrayList;
import java.util.HashSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import core.ConvertToTime;
import core.LogObject;
import core.SQL;

public class SummaryGenerator
{
	private SQL sql;
	
	public SummaryGenerator(String path)
	{
		sql = new SQL(path);   
	}
	
	private long completeTime(ArrayList<LogObject> list)
	{		
		long total = 0;
		for(LogObject current : list)
		{
			total += current.getDuration();
		}		
		return total;	
	}	
	
	private int workingDays(ArrayList<LogObject> list)
	{		
		HashSet<String> dates = new HashSet<String>();
		for(LogObject current : list)
		{
			dates.add(current.getDate());
		}		
		return dates.size();	
	}	
	
	public VBox getSummaryByProjectAndTask(String project, String task) throws Exception
	{
		VBox vboxSummary = new VBox();
		Label labelName = new Label(project + " - " + task);
		labelName.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");	
		
		ArrayList<LogObject> objects = sql.getByProjectAndTask(project, task);
		
		Label labelTotalTime = new Label(ConvertToTime.ConvertMillisToTime(completeTime(objects)));
		labelTotalTime.setStyle("-fx-font-size: 25;");
		
		int days = workingDays(objects);
		Label labelDays;
		if(days == 1)
		{
			labelDays = new Label(workingDays(objects) + " Arbeitstag");
		}
		else
		{
			labelDays = new Label(workingDays(objects) + " Arbeitstage");
		}
		labelDays.setStyle("-fx-font-size: 25;");
		
		vboxSummary.setAlignment(Pos.CENTER);
		vboxSummary.getChildren().add(labelName);
		vboxSummary.getChildren().add(labelTotalTime);
		vboxSummary.getChildren().add(labelDays);
		VBox.setVgrow(labelName, Priority.ALWAYS);	
		VBox.setVgrow(labelTotalTime, Priority.ALWAYS);
		VBox.setVgrow(labelDays, Priority.ALWAYS);
		VBox.setMargin(labelName, new Insets(0.0, 0.0, 20.0, 0.0));
		VBox.setMargin(labelTotalTime, new Insets(20.0, 0.0, 20.0, 0.0));
		VBox.setMargin(labelDays, new Insets(20.0, 0.0, 0.0, 0.0));
		
		return vboxSummary;
	}
	
	public VBox getSummaryByProjectAndTaskAndYear(String project, String task, int year) throws Exception
	{
		VBox vboxSummary = new VBox();
		Label labelName = new Label(project + " - " + task + " - " + year);
		labelName.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");	
		
		ArrayList<LogObject> objects = sql.getByProjectAndTaskAndYear(project, task, year);
		
		Label labelTotalTime = new Label(ConvertToTime.ConvertMillisToTime(completeTime(objects)));
		labelTotalTime.setStyle("-fx-font-size: 25;");
		
		int days = workingDays(objects);
		Label labelDays;
		if(days == 1)
		{
			labelDays = new Label(workingDays(objects) + " Arbeitstag");
		}
		else
		{
			labelDays = new Label(workingDays(objects) + " Arbeitstage");
		}
		labelDays.setStyle("-fx-font-size: 25;");
		
		vboxSummary.setAlignment(Pos.CENTER);
		vboxSummary.getChildren().add(labelName);
		vboxSummary.getChildren().add(labelTotalTime);
		vboxSummary.getChildren().add(labelDays);
		VBox.setVgrow(labelName, Priority.ALWAYS);	
		VBox.setVgrow(labelTotalTime, Priority.ALWAYS);
		VBox.setVgrow(labelDays, Priority.ALWAYS);
		VBox.setMargin(labelName, new Insets(0.0, 0.0, 20.0, 0.0));
		VBox.setMargin(labelTotalTime, new Insets(20.0, 0.0, 20.0, 0.0));
		VBox.setMargin(labelDays, new Insets(20.0, 0.0, 0.0, 0.0));
		
		return vboxSummary;
	}
}