package de.deadlocker8.smarttime.main;

import de.deadlocker8.smarttime.controller.Controller;
import de.thecodelabs.logger.FileOutputOption;
import de.thecodelabs.logger.LogLevelFilter;
import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.ui.Alerts;
import de.thecodelabs.utils.ui.NVCStage;
import de.thecodelabs.utils.util.SystemUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


public class Main extends Application
{
	@Override
	public void start(Stage stage)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/deadlocker8/smarttime/fxml/MainGUI.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root, 800, 800);
			scene.getStylesheets().add("/de/deadlocker8/smarttime/css/application.css");
			stage.setMinHeight(500);
			stage.setMinWidth(700);
			stage.setResizable(true);
			stage.setTitle("SmartTime");
			stage.setScene(scene);

			Controller controller = loader.getController();
			controller.init(stage);

			stage.getIcons().add(new Image("/de/deadlocker8/smarttime/icon.png"));
			stage.show();

			// fängt die Aufforderung das Fenster zu schließen ab, um vorher
			// noch eine Prüfung duchzuführen
			stage.setOnCloseRequest(we -> {
				if(controller.isTimerRunning())
				{
					Alerts.getInstance().createAlert(AlertType.WARNING, "Warnung", "Die Stoppuhr läuft noch!", stage);

					// "schluckt" die Aufforderung das Fenster zu schließen
					// (Fenster wird dadurch nicht geschlossen)
					we.consume();
				}
				else
				{
					stage.close();
				}
			});
		}
		catch(Exception e)
		{
			Logger.error(e);
		}
	}

	@Override
	public void init()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("de/deadlocker8/smarttime/", Locale.GERMANY);

		Parameters params = getParameters();
		final Optional<String> isDebugOptional = params.getUnnamed().stream()
				.filter(param -> param.contains("debug"))
				.findAny();

		final boolean isDebug = isDebugOptional.isPresent();

		Logger.init(SystemUtils.getApplicationSupportDirectoryPath("Deadlocker", bundle.getString("app.name")));
		if(isDebug)
		{
			NVCStage.setDisabledSizeLoading(true);
			Logger.setLevelFilter(LogLevelFilter.DEBUG);
			Logger.setFileOutput(FileOutputOption.DISABLED);
		}
		else
		{
			Logger.setFileOutput(FileOutputOption.COMBINED);
		}
		Logger.info("Logging initialized (Running in LogLevel: {0})", Logger.getLevelFilter().toString());

		Logger.appInfo(bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"));
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}