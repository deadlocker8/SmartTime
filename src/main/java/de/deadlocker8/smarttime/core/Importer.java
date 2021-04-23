package de.deadlocker8.smarttime.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.ui.Alerts;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Importer
{
	private final String path;
	private final Stage stage;

	public Importer(String path, Stage stage)
	{
		this.path = path;
		this.stage = stage;
	}

	public void importFromSmartTime(File file)
	{
		try
		{
			ArrayList<String> lines = readFile(file);
			ArrayList<LogObject> objects = new ArrayList<>();
			for(String item : lines)
			{
				String[] parts = item.split("\t");

				String date = parts[0];
				String[] dateParts = date.split("-");

				LogObject current = new LogObject(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]), parts[3], parts[4], Long.parseLong(parts[7]), parts[5], parts[6]);
				objects.add(current);
			}

			SQL sql = new SQL(path);
			for(LogObject o : objects)
			{
				sql.insert(o);
			}

			Platform.runLater(() -> Alerts.getInstance().createAlert(AlertType.INFORMATION, "Erfolgreich importiert", "Der Importvorgang wurde erfolgreich abgeschlossen.", stage).show());
		}
		catch(Exception e)
		{
			Logger.error(e);
			Platform.runLater(() -> Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Importieren der Daten ist ein Fehler aufgetreten.", stage).show());
		}
	}

	public void importFromDB(File file)
	{
		SQL sql = new SQL(file.getAbsolutePath());
		try
		{
			ArrayList<LogObject> objects = sql.getLogObjects();
			SQL currentDB = new SQL(path);
			for(LogObject item : objects)
			{
				currentDB.insert(item);
			}

			Platform.runLater(() -> Alerts.getInstance().createAlert(AlertType.INFORMATION, "Erfolgreich importiert", "Der Importvorgang wurde erfolgreich abgeschlossen.", stage).show());
		}
		catch(Exception e)
		{
			Logger.error(e);
			Platform.runLater(() -> Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Importieren der Daten ist ein Fehler aufgetreten.", stage).show());
		}
	}

	public void importFromJSON(File file)
	{
		try
		{
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			StringBuilder text = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null)
			{
				text.append(line);
			}
			reader.close();

			JsonObject allItems = new JsonParser().parse(text.toString()).getAsJsonObject();

			SQL sql = new SQL(path);
			JsonArray loadedItems = (JsonArray) allItems.get("logObjects");
			for(int i = 0; i < loadedItems.size(); i++)
			{
				JsonObject item = (JsonObject) loadedItems.get(i);
				String date = item.get("date").getAsString();
				String startTime = item.get("startTime").getAsString();
				String endTime = item.get("endTime").getAsString();
				long duration = item.get("duration").getAsLong();
				String project = item.get("project").getAsString();
				String task = item.get("task").getAsString();

				String[] dateParts = date.split(Pattern.quote("."));

				LogObject loadedObject = new LogObject(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]), startTime, endTime, duration, project, task);
				sql.insert(loadedObject);
			}
		}
		catch(Exception e)
		{
			Logger.error(e);
			Platform.runLater(() -> Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Importieren der Daten ist ein Fehler aufgetreten.", stage).show());
		}
	}

	private ArrayList<String> readFile(File file) throws Exception
	{
		FileInputStream fis = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		ArrayList<String> list = new ArrayList<>();

		String line;
		while((line = reader.readLine()) != null)
		{
			list.add(line);
		}

		reader.close();
		return list;
	}
}
