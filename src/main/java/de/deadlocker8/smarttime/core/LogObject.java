package de.deadlocker8.smarttime.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.deadlocker8.smarttime.core.ConvertTo;

/**
 * Klasse "Logobject" - generiert Objekte, die alle wichtigen Eigenschaften für das Logfile enthalten
 * @author Robert
 *
 */
public class LogObject
{
	private int year;
	private int month;
	private int day;
	private String startTime;	
	private String endTime;
	private long duration;
	private String project;
	private String task;	
	
	
	public LogObject(int year, int month, int day, String startTime, String endTime, long duration, String project, String task)
	{		
		this.year = year;
		this.month = month;
		this.day = day;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.project = project;
		this.task = task;
	}
	
	public LogObject()
	{		
	}
	
	public String toString()
	{
		return day + "." + month + "." + year + "  " + startTime + " bis " + endTime + "\t" + project + "\t" + task +"\t" + duration + " (" +  ConvertTo.ConvertMillisToTime(duration)+ ")"; 
	}
	
	public void createStartTime()
	{
		LocalDateTime date = LocalDateTime.now();
		year = date.getYear();
		month = date.getMonthValue();
		day = date.getDayOfMonth();	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");	
		startTime = date.format(formatter); 	
	}
	
	public void createEndTime()
	{
		LocalDateTime date = LocalDateTime.now();	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");	
		endTime = date.format(formatter); 			
	}
	
	public void setDuration(long duration)
	{
		this.duration = duration;
	}
	
	public void setProject(String p)
	{
		project = p;
	}

	public void setTask(String t)
	{
		task = t;
	}
	
	public String getDate()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		LocalDateTime dateTime = LocalDateTime.of(year, month, day, 12, 00);
		return dateTime.format(formatter);
	}	
	
	public String getStartTime()
	{
		return startTime;
	}
	
	public String getEndTime()
	{
		return endTime;
	}	
	
	public long getDuration()
	{
		return duration;
	}
	
	public String getProject()
	{
		return project;
	}

	public String getTask()
	{
		return task;
	}

	public int getYear()
	{
		return year;
	}

	public int getMonth()
	{
		return month;
	}

	public int getDay()
	{
		return day;
	}	
}