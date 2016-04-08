package charts;

import java.util.ArrayList;
import java.util.HashSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import core.LogObject;
import core.SQL;

public class PieChartGenerator
{
	private SQL sql;
	
	public PieChartGenerator(String path)
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
	
	private PieChart createChart(ObservableList<PieChart.Data> data, String title)
	{
		PieChart chart = new PieChart(data);
		chart.setTitle(title);			
		         
		chart.getData().stream().forEach(tool -> {
		    Tooltip tooltip = new Tooltip();  
		    
		    double total = 0;
	        for (PieChart.Data d : chart.getData()) 
	        {
	        	total += d.getPieValue();
	        }	       
	        
	        double pieValue = tool.getPieValue();
	        double percentage = (pieValue/total) * 100;
	        String percent = String.valueOf(percentage);
	        percent = percent.substring(0, percent.indexOf(".") + 2);	      
	        String time = core.ConvertToTime.ConvertMillisToTime((long) pieValue);
		 
		    tooltip.setText(percent + " % \n" + time);
		    Tooltip.install(tool.getNode(), tooltip);
		    Node node = tool.getNode();
            node.setOnMouseEntered(new EventHandler<MouseEvent>() {
           	 
           	    @Override
           	    public void handle(MouseEvent event) {
           	        Point2D p = node.localToScreen(event.getX()+5,event.getY()+7);
           	        tooltip.show(node, p.getX(), p.getY());
           	    }
           	});
           	node.setOnMouseExited(new EventHandler<MouseEvent>() {
           	 
           	    @Override
           	    public void handle(MouseEvent event) {
           	    	tooltip.hide();
           	    }
           	});	
		});		
		return chart;
	}
	
	private ArrayList<String> getProjectNames(ArrayList<LogObject> objects)
	{
		HashSet<String> names = new HashSet<String>();
		for(LogObject current : objects)
		{
			names.add(current.getProject());
		}
		
		return new ArrayList<String>(names);
	}	
	
	private ArrayList<String> getTasknames(ArrayList<LogObject> objects)
	{
		HashSet<String> names = new HashSet<String>();
		for(LogObject current : objects)
		{
			names.add(current.getTask());
		}
		
		return new ArrayList<String>(names);
	}
	
	//all Projects for all time
	public PieChart getChart0000(String title) throws Exception
	{
		ArrayList<String> names = sql.getProjectNames();
      	ObservableList<PieChart.Data> data = FXCollections.observableArrayList();       	
      	for(String currentName : names)
      	{
			data.add(new PieChart.Data(currentName, completeTime(sql.getByProject(currentName))));			
		}         	
      	return createChart(data, title);	
	}
	
	//all Projects for one specific year
	public PieChart getChart0010(int year, String title) throws Exception
	{		
		ArrayList<String> names = getProjectNames(sql.getByYear(year));
      	ObservableList<PieChart.Data> data = FXCollections.observableArrayList();       	
      	for(String currentName : names)
      	{
			data.add(new PieChart.Data(currentName, completeTime(sql.getByProjectAndYear(currentName, year))));			
		}         	
      	return createChart(data, title);	
	}
	
	//all Projects for one specific year and month
	public PieChart getChart0011(int year, int month, String title) throws Exception
	{		
		ArrayList<String> names = getProjectNames(sql.getByYearAndMonth(year, month));
      	ObservableList<PieChart.Data> data = FXCollections.observableArrayList();       	
      	for(String currentName : names)
      	{
			data.add(new PieChart.Data(currentName, completeTime(sql.getByProjectAndYearAndMonth(currentName, year, month))));			
		}         	
      	return createChart(data, title);	
	}
	
	//one specific Project for all time
	public PieChart getChart1000(String projectName, String title) throws Exception
	{			
		ArrayList<LogObject> objects = sql.getByProject(projectName);
		ArrayList<String> names = getTasknames(objects);
      	ObservableList<PieChart.Data> data = FXCollections.observableArrayList();       	
      	for(String taskName : names)
      	{
			data.add(new PieChart.Data(taskName, completeTime(sql.getByProjectAndTask(projectName, taskName))));			
		}         	
      	return createChart(data, title);	
	}
	
	//one specific Project for one specific year
	public PieChart getChart1010(String projectName, int year, String title) throws Exception
	{			
		ArrayList<LogObject> objects = sql.getByProjectAndYear(projectName, year);
		ArrayList<String> names = getTasknames(objects);
      	ObservableList<PieChart.Data> data = FXCollections.observableArrayList();       	
      	for(String taskName : names)
      	{
			data.add(new PieChart.Data(taskName, completeTime(sql.getByProjectAndTaskAndYear(projectName, taskName, year))));			
		}         	
      	return createChart(data, title);	
	}
	
	//one specific Project for one specific year and month
	public PieChart getChart1011(String projectName, int year, int month, String title) throws Exception
	{			
		ArrayList<LogObject> objects = sql.getByProjectAndYearAndMonth(projectName, year, month);
		ArrayList<String> names = getTasknames(objects);
      	ObservableList<PieChart.Data> data = FXCollections.observableArrayList();       	
      	for(String taskName : names)
      	{
			data.add(new PieChart.Data(taskName, completeTime(sql.getByProjectAndTaskAndYearAndMonth(projectName, taskName, year, month))));			
		}         	
      	return createChart(data, title);	
	}	
}