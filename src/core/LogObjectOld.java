package core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Klasse "Logobject" - generiert Objekte, die alle wichtigen Eigenschaften für das Logfile enthalten
 * @author Robert
 *
 */
public class LogObjectOld
{
	// Startzeit in Millisekunden
	private long starttime;
	// Startzeit als Uhrzeit
	private String startUhrzeit;
	// Endzeit in Millisekunden
	private long endtime;
	// Endzeit als Uhrzeit
	private String endUhrzeit;
	private String date;
	private String project;
	private String task;
	// Berechnete Differenz zwischen starttime und endtime
	private long differenz;
	// in Stunden, Minuten und Sekunden umgerechnete Differenz
	private String time;
	
	/**
	 * Konstruktor nach dem Einlesen aus Datei (komplettes Objekt)
	 * @param date String - Datum
	 * @param start long - Startzeit in Millisekunden (Unixtime)
	 * @param startUhrzeit String - Startuhrzeit
	 * @param end long - Startzeit in Millisekunden (Unixtime)
	 * @param endUhrzeit String- Enduhrzeit
	 * @param project String - Projektname
	 * @param task String - Aufgabenname
	 * @param differenz long - verstrichene zeit in Millisekunden zwischen start und end
	 * @param time String - Differenz umgerechnet in Stunden, Minuten und Sekunden
	 */
	public LogObjectOld(String date, long start, String startUhrzeit, long end, String endUhrzeit, String project, String task, long differenz, String time)
	{
		this.date = date;
		starttime = start;
		this.startUhrzeit = startUhrzeit;
		endtime = end;
		this.endUhrzeit = endUhrzeit;
		this.project = project;
		this.task = task;
		this.differenz = differenz;
		this.time = time;
	}

	/**
	 * leerer Konstruktor
	 */
	public LogObjectOld()
	{
	}

	/**
	 * toString für Ausgabe in Datei
	 * @return String - date + starttime + endtime + startUhtzeit + endUhrzeit + project + task + differenz + time
	 */
	public String toString()
	{
		return date + "\t" + starttime + "\t" + endtime + "\t" + startUhrzeit + "\t" + endUhrzeit + "\t" + project + "\t" + task + "\t"	+ differenz + "\t" + time;
	}

	/**
	 * toString für Ausgabe im Programm
	 * @return String - date + startUhtzeit + endUhrzeit + project + task + time
	 */
	public String toString2()
	{
		return String.format("%-15s\t %-15s\t %-15s\t %-30s\t %-30s\t %s\t", date, startUhrzeit, endUhrzeit, project, task, time);
	}
	
	/**
	 * erzeugt die Startzeit
	 */
	public void createStarttime()
	{
		Calendar cal = Calendar.getInstance();
		starttime = cal.getTimeInMillis();
		startUhrzeit = new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	/**
	 * erzeugt das Datum
	 */
	public void createDate()
	{
		date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	}

	/**
	 * erzeugt die Endzeit
	 */
	public void createEndtime()
	{
		Calendar cal = Calendar.getInstance();
		endtime = cal.getTimeInMillis();
		endUhrzeit = new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	/**
	 * setzt das Projekt
	 * @param p String - Projektname
	 */
	public void setProject(String p)
	{
		project = p;
	}

	/**
	 * setzt den Task
	 * @param t String - Taskname
	 */
	public void setTask(String t)
	{
		task = t;
	}

	/**
	 * gibt den Projektnamen zurück
	 * @return String - Projektname
	 */
	public String getProject()
	{
		return project;
	}

	/**
	 * gibt den Tasknamen zurück
	 * @return String - Taskname
	 */
	public String getTask()
	{
		return task;
	}
	
	/**
	 * gibt die Differenz in Millisekundne zurück
	 * @return long - Differenz
	 */
	public long getDifferenz()
	{
		return differenz;
	}	

	/**
	 * gibt das Datum zurück
	 * @return String - Datum
	 */
	public String getDate()
	{
		return date;
	}	
	
	/**
	 * gibt die Startuhrzeit zurück
	 * @return String - Startuhrzeit
	 */
	public String getStartUhrzeit()
	{
		return startUhrzeit;
	}

	/**
	 * gibt die Enduhrzeit zurück
	 * @return String - Enduhrzeit
	 */
	public String getEndUhrzeit()
	{
		return endUhrzeit;
	}

	/**
	 * gibt die Differenz zurück
	 * @return String - Differenz
	 */
	public String getTime()
	{
		return time;
	}

	/**
	 * setzt die Differenz
	 * @param differenz long - Differenz
	 */
	public void setDifferenz(long differenz)
	{
		this.differenz = differenz;
	}

	/**
	 * gibt die Startuhrzeit zurück
	 * @return String - Startuhrzeit
	 */
	public long getStarttime()
	{
		return starttime;
	}

	/**
	 * gibt die Enduhrzeit zurück
	 * @return String - Enduhrzeit
	 */
	public long getEndtime()
	{
		return endtime;
	}

	/**
	 * setzt den Wert für die Differenz in Stunden, Minuten und Sekunden
	 * @param time String - Zeit in der Form "Stunden + Minuten + Sekunden"
	 */
	public void setTime(String time)
	{
		this.time = time;
	}	
}