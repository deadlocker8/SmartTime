package userInterface;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logger.FileOutputMode;
import logger.Logger;
import tools.AlertGenerator;
import tools.PathUtils;


public class Main extends Application
{
	@Override
	public void start(Stage stage)
	{
		try
		{			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("userInterface.fxml"));
			Parent root = (Parent)loader.load();

			Scene scene = new Scene(root, 800, 800);
			
			stage.setMinHeight(500);
			stage.setMinWidth(700);

			stage.setResizable(true);
			stage.setTitle("SmartTime");
			stage.setScene(scene);
			
			UserInterfaceController controller = (UserInterfaceController)loader.getController();			
			controller.init(stage);

			stage.getIcons().add(new Image("/userInterface/icon.png"));
			stage.show();

			// fängt die Aufforderung das Fenster zu schließen ab, um vorher
			// noch eine Prüfung duchzuführen
			stage.setOnCloseRequest(new EventHandler<WindowEvent>()
			{
				public void handle(WindowEvent we)
				{
					if(controller.isTimerRunning())
					{
						AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", "Die Stoppuhr läuft noch!", new Image("/userInterface/icon.png"), stage, null, false);
						
						// "schluckt" die Aufforderung das Fenster zu schließen
						// (Fenster wird dadurch nicht geschlossen)
						we.consume();
					}
					else
					{
						stage.close();
					}
				}
			});
		}
		catch(Exception e)
		{
			Logger.error(e);
		}
	}
	
	@Override
	public void init() throws Exception
	{
		ResourceBundle bundle = ResourceBundle.getBundle("userInterface/", Locale.GERMANY);
		
		Parameters params = getParameters();
		String logLevelParam = params.getNamed().get("loglevel");		
		Logger.setLevel(logLevelParam);	
		
		File logFolder = new File(PathUtils.getOSindependentPath() + "/Deadlocker/" + bundle.getString("app.name"));			
		PathUtils.checkFolder(logFolder);
		Logger.enableFileOutput(logFolder, System.out, System.err, FileOutputMode.COMBINED);
		
		Logger.appInfo(bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"));		
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}