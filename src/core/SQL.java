package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

public class SQL
{
	private String path;	

	public SQL(String path)
	{
		this.path = path;
	}	

	public void createDB() throws Exception
	{
		Connection c = null;
		Statement stmt = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);

		stmt = c.createStatement();
		String sql = "CREATE TABLE IF NOT EXISTS SMARTTIME(YEAR INT, MONTH INT, DAY INT, STARTTIME TEXT, ENDTIME TEXT, DURATION INT, PROJECT TEXT, TASK TEXT)";
		stmt.executeUpdate(sql);
		stmt.executeUpdate("VACUUM;");
		stmt.close();

		c.close();
	}
	
	public void deleteDB() throws Exception
	{
		Connection c = null;
		Statement stmt = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);

		stmt = c.createStatement();
		String sql = "DELETE FROM SMARTTIME;";
		stmt.executeUpdate(sql);
		stmt.close();

		c.close();
	}	

	public void insert(LogObject log) throws Exception
	{
		Connection c = null;
		Statement stmt = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);

		stmt = c.createStatement();
		String sql = "INSERT INTO SMARTTIME VALUES(" + log.getYear() + "," + log.getMonth() + "," + log.getDay() + "," + "'" + log.getStartTime() + "'," + "'" + log.getEndTime() + "',"
				+ log.getDuration() + "," + "'" + log.getProject() + "'," + "'" + log.getTask() + "')";
		stmt.executeUpdate(sql);
		stmt.close();

		c.close();
	}
	
	private ArrayList<LogObject> extractLogObjects(ResultSet rs) throws Exception
	{
		ArrayList<LogObject> logObjects = new ArrayList<LogObject>();	
		while(rs.next())
		{
			int year = rs.getInt("YEAR");
			int month = rs.getInt("MONTH");
			int day = rs.getInt("DAY");
			String startTime = rs.getString("STARTTIME");
			String endTime = rs.getString("ENDTIME");
			int duration = rs.getInt("DURATION");
			String project = rs.getString("PROJECT");
			String task = rs.getString("TASK");

			LogObject current = new LogObject(year, month, day, startTime, endTime, duration, project, task);
			logObjects.add(current);
		}
		return logObjects;
	}
	
	public ArrayList<LogObject> getLogObjects() throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME ORDER BY YEAR DESC, MONTH DESC, DAY DESC, STARTTIME DESC;");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<LogObject> getByProject(String projectName) throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME WHERE PROJECT='"+ projectName +"'ORDER BY TASK, YEAR DESC, MONTH DESC, DAY DESC, STARTTIME DESC;");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<LogObject> getByProjectAndTask(String projectName, String taskName) throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:"+path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME WHERE PROJECT='"+ projectName + "' AND TASK='"+ taskName +"' ORDER BY YEAR DESC, MONTH DESC, DAY DESC, STARTTIME DESC;");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<LogObject> getByProjectAndTaskAndYear(String projectName, String taskName, int year) throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:"+path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME WHERE PROJECT='"+ projectName + "' AND TASK='"+ taskName +"' AND YEAR='"+ year +"'ORDER BY MONTH DESC, DAY DESC, STARTTIME DESC;");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<LogObject> getByProjectAndTaskAndYearAndMonth(String projectName, String taskName, int year, int month) throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:"+path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME WHERE PROJECT='"+ projectName + "' AND TASK='"+ taskName +"' AND YEAR='"+ year +"' AND MONTH='"+ month +"'ORDER BY DAY DESC, STARTTIME DESC;");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<LogObject> getByProjectAndYear(String projectName, int year) throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:"+path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME WHERE PROJECT='"+ projectName + "' AND YEAR='"+ year +"'ORDER BY MONTH DESC, DAY DESC, STARTTIME DESC;");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<LogObject> getByProjectAndYearAndMonth(String projectName, int year, int month) throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:"+path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME WHERE PROJECT='"+ projectName + "' AND YEAR='"+ year +"' AND MONTH='"+ month +"'ORDER BY DAY DESC, STARTTIME DESC;");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<LogObject> getByYear(int year) throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:"+path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME WHERE YEAR='"+ year +"' ORDER BY PROJECT, TASK");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<LogObject> getByYearAndMonth(int year, int month) throws Exception
	{
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:"+path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME WHERE YEAR='"+ year + "' AND MONTH='"+ month +"'ORDER BY PROJECT, TASK;");
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}
	
	public ArrayList<String> getProjectNames() throws Exception
	{		
		Connection c = null;
		Statement stmt = null;			

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:"+path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME ORDER BY PROJECT;");
		
		ArrayList<LogObject> objects= extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();	
	
		HashSet<String> names = new HashSet<String>();
		for(LogObject current : objects)
		{
			names.add(current.getProject());
		}
		
		return new ArrayList<String>(names);
	}
	
	public ArrayList<String> getTaskNamesByProject(String projectName) throws Exception
	{		
		ArrayList<LogObject> objects = getByProject(projectName);
		HashSet<String> names = new HashSet<String>();
		for(LogObject current : objects)
		{
			names.add(current.getTask());
		}
		
		return new ArrayList<String>(names);
	}
	
	public ArrayList<String> getYears() throws Exception
	{		
		ArrayList<LogObject> objects = getLogObjects();
		HashSet<String> names = new HashSet<String>();
		for(LogObject current : objects)
		{
			names.add(String.valueOf(current.getYear()));
		}
		
		return new ArrayList<String>(names);
	}
	
	public void update(LogObject oldLog, LogObject newLog) throws Exception
	{
		Connection c = null;
		Statement stmt = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);

		stmt = c.createStatement();
		String sql = "UPDATE SMARTTIME SET PROJECT='" + newLog.getProject() + "' , TASK='" + newLog.getTask() + "' "
				+ "WHERE YEAR='" + oldLog.getYear() 
				+ "' AND MONTH='" + oldLog.getMonth() 
				+ "' AND DAY='" + oldLog.getDay() 
				+ "' AND STARTTIME='" + oldLog.getStartTime() 
				+ "' AND ENDTIME='" + oldLog.getEndTime() 
				+ "' AND DURATION='" + oldLog.getDuration()
				+ "' AND PROJECT='" + oldLog.getProject()
				+ "' AND TASK='" + oldLog.getTask() + "';";			
		stmt.executeUpdate(sql);
		stmt.close();

		c.close();
	}
	
	public void delete(LogObject log) throws Exception
	{
		Connection c = null;
		Statement stmt = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);

		stmt = c.createStatement();
		String sql = "DELETE FROM SMARTTIME "
				+ "WHERE YEAR='" + log.getYear() 
				+ "' AND MONTH='" + log.getMonth() 
				+ "' AND DAY='" + log.getDay() 
				+ "' AND STARTTIME='" + log.getStartTime() 
				+ "' AND ENDTIME='" + log.getEndTime() 
				+ "' AND DURATION='" + log.getDuration()
				+ "' AND PROJECT='" + log.getProject()
				+ "' AND TASK='" + log.getTask() + "';";			
		stmt.executeUpdate(sql);
		stmt.close();

		c.close();
	}
}