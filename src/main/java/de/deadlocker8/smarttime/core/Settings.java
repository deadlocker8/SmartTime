package de.deadlocker8.smarttime.core;

public class Settings
{
	private String savePath;
	private String lastProject;
	private String lastTask;

	public Settings()
	{

	}

	public Settings(String savePath, String lastProject, String lastTask)
	{
		this.savePath = savePath;
		this.lastProject = lastProject;
		this.lastTask = lastTask;
	}

	public String getSavePath()
	{
		return savePath;
	}

	public void setSavePath(String savePath)
	{
		this.savePath = savePath;
	}

	public String getLastProject()
	{
		return lastProject;
	}

	public void setLastProject(String lastProject)
	{
		this.lastProject = lastProject;
	}

	public String getLastTask()
	{
		return lastTask;
	}

	public void setLastTask(String lastTask)
	{
		this.lastTask = lastTask;
	}

	@Override
	public String toString()
	{
		return "Settings [savePath=" + savePath + ", lastProject=" + lastProject + ", lastTask=" + lastTask + "]";
	}
}