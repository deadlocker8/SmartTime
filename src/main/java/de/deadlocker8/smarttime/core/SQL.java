package de.deadlocker8.smarttime.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		
		String sql = "INSERT INTO SMARTTIME VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = c.prepareStatement(sql);
		stmt.setInt(1, log.getYear());
		stmt.setInt(2, log.getMonth());
		stmt.setInt(3, log.getDay());
		stmt.setString(4, log.getStartTime());
		stmt.setString(5, log.getEndTime());
		stmt.setLong(6, log.getDuration());
		stmt.setString(7, log.getProject());
		stmt.setString(8, log.getTask());

		stmt.executeUpdate();
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

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);	
		
		PreparedStatement stmt = c.prepareStatement("SELECT * FROM SMARTTIME WHERE PROJECT=? ORDER BY TASK, YEAR DESC, MONTH DESC, DAY DESC, STARTTIME DESC;");
		stmt.setString(1, projectName);
		ResultSet rs = stmt.executeQuery();
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);
		
		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}

	public ArrayList<LogObject> getByProjectAndTask(String projectName, String taskName) throws Exception
	{
		Connection c = null;

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);

		PreparedStatement stmt = c.prepareStatement("SELECT * FROM SMARTTIME WHERE PROJECT=? AND TASK=? ORDER BY YEAR DESC, MONTH DESC, DAY DESC, STARTTIME DESC;");
		stmt.setString(1, projectName);
		stmt.setString(2, taskName);
		ResultSet rs = stmt.executeQuery();

		ArrayList<LogObject> logObjects = extractLogObjects(rs);

		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}

	public ArrayList<LogObject> getByProjectAndTaskAndYear(String projectName, String taskName, int year) throws Exception
	{
		Connection c = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false); 
		
		String sql = "SELECT * FROM SMARTTIME WHERE PROJECT= ? AND TASK= ? AND YEAR= ? ORDER BY MONTH DESC, DAY DESC, STARTTIME DESC;";

		PreparedStatement stmt = c.prepareStatement(sql);
		stmt.setString(1, projectName);
		stmt.setString(2, taskName);
		stmt.setInt(3, year);
		
		ResultSet rs = stmt.executeQuery();		
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);

		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}

	public ArrayList<LogObject> getByProjectAndTaskAndYearAndMonth(String projectName, String taskName, int year, int month) throws Exception
	{
		Connection c = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);
		
		String sql = "SELECT * FROM SMARTTIME WHERE PROJECT= ? AND TASK= ? AND YEAR= ? AND MONTH= ? ORDER BY DAY DESC, STARTTIME DESC;";
		PreparedStatement stmt = c.prepareStatement(sql);
		stmt.setString(1, projectName);
		stmt.setString(2, taskName);
		stmt.setInt(3, year);
		stmt.setInt(4, month);
		
		ResultSet rs = stmt.executeQuery();
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);

		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}

	public ArrayList<LogObject> getByProjectAndYear(String projectName, int year) throws Exception
	{
		Connection c = null;		

		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);
		
		String sql = "SELECT * FROM SMARTTIME WHERE PROJECT= ? AND YEAR= ? ORDER BY MONTH DESC, DAY DESC, STARTTIME DESC;";
		PreparedStatement stmt = c.prepareStatement(sql);
		stmt.setString(1, projectName);
		stmt.setInt(2, year);
		
		ResultSet rs = stmt.executeQuery();
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);

		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}

	public ArrayList<LogObject> getByProjectAndYearAndMonth(String projectName, int year, int month) throws Exception
	{
		Connection c = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);

		String sql = "SELECT * FROM SMARTTIME WHERE PROJECT= ? AND YEAR= ? AND MONTH= ? ORDER BY DAY DESC, STARTTIME DESC;";

		PreparedStatement stmt = c.prepareStatement(sql);
		stmt.setString(1, projectName);
		stmt.setInt(2, year);
		stmt.setInt(3, month);
		
		ResultSet rs = stmt.executeQuery();
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);

		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}

	public ArrayList<LogObject> getByYear(int year) throws Exception
	{
		Connection c = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);

		String sql = "SELECT * FROM SMARTTIME WHERE YEAR= ? ORDER BY PROJECT, TASK;";

		PreparedStatement stmt = c.prepareStatement(sql);	
		stmt.setInt(1, year);
		
		ResultSet rs = stmt.executeQuery();
		
		ArrayList<LogObject> logObjects = extractLogObjects(rs);

		rs.close();
		stmt.close();
		c.close();

		return logObjects;
	}

	public ArrayList<LogObject> getByYearAndMonth(int year, int month) throws Exception
	{
		Connection c = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);
		
		String sql = "SELECT * FROM SMARTTIME WHERE YEAR= ? AND MONTH= ? ORDER BY PROJECT, TASK;";

		PreparedStatement stmt = c.prepareStatement(sql);	
		stmt.setInt(1, year);
		stmt.setInt(2, month);
		
		ResultSet rs = stmt.executeQuery();
		
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
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		c.setAutoCommit(false);

		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM SMARTTIME ORDER BY PROJECT;");

		ArrayList<LogObject> objects = extractLogObjects(rs);

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
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		
		String sql = "UPDATE SMARTTIME SET PROJECT= ? , TASK= ? WHERE YEAR= ? AND MONTH= ? AND DAY= ? AND STARTTIME= ? AND ENDTIME= ? AND DURATION= ? AND PROJECT= ? AND TASK= ?;";
		
		PreparedStatement stmt = c.prepareStatement(sql);
		stmt.setString(1, newLog.getProject());
		stmt.setString(2, newLog.getTask());
		stmt.setInt(3, oldLog.getYear());
		stmt.setInt(4, oldLog.getMonth());
		stmt.setInt(5, oldLog.getDay());
		stmt.setString(6, oldLog.getStartTime());
		stmt.setString(7, oldLog.getEndTime());
		stmt.setLong(8, oldLog.getDuration());
		stmt.setString(9, oldLog.getProject());
		stmt.setString(10, oldLog.getTask());
		
		stmt.executeUpdate();
		stmt.close();

		c.close();
	}

	public void delete(LogObject log) throws Exception
	{
		Connection c = null;
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + path);
		
		String sql = "DELETE FROM SMARTTIME " + "WHERE YEAR= ? AND MONTH= ? AND DAY= ? AND STARTTIME= ? AND ENDTIME= ? AND DURATION= ? AND PROJECT= ? AND TASK= ?;";
	
		PreparedStatement stmt = c.prepareStatement(sql);		
		stmt.setInt(1, log.getYear());
		stmt.setInt(2, log.getMonth());
		stmt.setInt(3, log.getDay());
		stmt.setString(4, log.getStartTime());
		stmt.setString(5, log.getEndTime());
		stmt.setLong(6, log.getDuration());
		stmt.setString(7, log.getProject());
		stmt.setString(8, log.getTask());
		
		stmt.execute();
		stmt.close();

		c.close();
	}
}