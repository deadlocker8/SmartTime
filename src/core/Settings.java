package core;

public class Settings
{
	private String lastProject;
	private String lastTask;
	
	public Settings()
	{
		
	}

	public Settings(String lastProject, String lastTask)
	{
		this.lastProject = lastProject;
		this.lastTask = lastTask;
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
		return "Settings [lastProject=" + lastProject + ", lastTask=" + lastTask + "]";
	}
}