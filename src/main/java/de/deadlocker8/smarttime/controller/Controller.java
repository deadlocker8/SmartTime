package de.deadlocker8.smarttime.controller;

import de.deadlocker8.smarttime.charts.ChartGUIController;
import de.deadlocker8.smarttime.core.Timer;
import de.deadlocker8.smarttime.core.*;
import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.io.PathUtils;
import de.thecodelabs.utils.ui.Alerts;
import de.thecodelabs.utils.util.SystemUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;


public class Controller
{
	@FXML
	private Label aktuellesProjektAusgabe;
	@FXML
	private Label aktuellerTaskAusgabe;
	@FXML
	public Label labelTime;
	@FXML
	private Accordion accordion;
	@FXML
	private TitledPane Projekte;
	@FXML
	private TitledPane gesamtesLog;
	@FXML
	private AnchorPane MainFrame;
	@FXML
	private ToggleButton startButton;
	@FXML
	private TableView<LogObject> table;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private Label labelSeparator;
	@FXML
	private Label labelSavePath;

	private Stage stage;
	private Timer timer;
	public boolean projektExistiertFlag;
	private ArrayList<TreeItem<HBox>> aktuelleTasks;
	private TreeItem<HBox> item;
	private LogObject log;
	private long startTimestamp;
	private long endTimestamp;
	private int longestProject;
	private ArrayList<LogObject> logObjects = new ArrayList<>();
	private final String DEFAULT_SAVE_PATH = SystemUtils.getApplicationSupportDirectoryPath("Deadlocker", "SmartTime").toString();
	private SQL sql;
	private Stage waitingStage = new Stage();
	private Image icon;
	private final ResourceBundle bundle = ResourceBundle.getBundle("de/deadlocker8/smarttime/", Locale.GERMANY);
	private Settings settings;

	public void init(Stage stage)
	{
		this.stage = stage;

		labelSeparator.setStyle("-fx-background-color: #cdc6c6; -fx-font-size: 0.7");
		PathUtils.createDirectoriesIfNotExists(new File(DEFAULT_SAVE_PATH).toPath());
		icon = new Image("/de/deadlocker8/smarttime/icon.png");
		accordion.setExpandedPane(gesamtesLog);
		projektExistiertFlag = false;
		labelTime.setText("0 h  0  min  0 sek");

		loadSettings();
		createLogView();
		loadAll();

		setLabelSavePath();

		// verwaltet den Start/Stopp-Button
		startButton.setOnAction(event -> {
			if(projektExistiertFlag)
			{
				if(startButton.isSelected())
				{
					startButton.setText("Stopp");
					startClock();
					timer = new Timer(labelTime);
					timer.start();
				}
				else
				{
					startButton.setText("Start");
					timer.stop();
					endClock();
					loadAll();
				}
			}
			else
			{
				Alerts.getInstance().createAlert(AlertType.WARNING, "Warnung", "Kein Projekt ausgewählt.", stage);
				startButton.setSelected(false);
			}
		});
	}

	/**
	 * Fängt die Aufforderung das Fenster zu schließen ab, um vorher noch eine
	 * Prüfung duchzuführen
	 */
	public void closeRequest()
	{
		// Prüft, ob die Stoppuhr noch läuft
		if(isTimerRunning())
		{
			Alerts.getInstance().createAlert(AlertType.WARNING, "Warnung", "Stoppuhr läuft noch!", stage);
		}
		else
		{
			stage.close();
		}
	}

	public boolean isTimerRunning()
	{
		return timer != null && timer.isRunning();
	}

	public void setLabels(String project, String task)
	{
		aktuellesProjektAusgabe.setText(project);
		aktuellerTaskAusgabe.setText(task);
	}

	private void setLabelSavePath()
	{
		String savePathForLabel = settings.getSavePath() + "/" + Utils.DATABASE_NAME;
		savePathForLabel = savePathForLabel.replace("\\", "/");
		savePathForLabel = savePathForLabel.replaceAll("//", "/");
		labelSavePath.setText(savePathForLabel);
	}

	public void openProjectGUI(ActionEvent e)
	{
		if(!isTimerRunning())
		{
			try
			{
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/deadlocker8/smarttime/fxml/ProjectGUI.fxml"));
				Parent root = fxmlLoader.load();
				Stage newStage = new Stage();
				newStage.setScene(new Scene(root, 455, 300));
				newStage.setTitle("Neues Projekt");
				newStage.initOwner(stage);

				newStage.getIcons().add(icon);

				ProjectController pfc = fxmlLoader.getController();
				pfc.init(this, newStage, settings);

				newStage.setResizable(false);
				newStage.initModality(Modality.APPLICATION_MODAL);
				newStage.showAndWait();
			}
			catch(IOException d)
			{
				Logger.error(d);
			}
		}
		else
		{
			Alerts.getInstance().createAlert(AlertType.WARNING, "Warnung", "Stoppuhr läuft noch!", "Projekt und Task können nur geändert werden,\nwenn die Stoppuhr nicht läuft.", stage);
		}
	}

	private void createTreeView()
	{
		// generiert den root-Knoten
		Label labelRoot = new Label("Gesamt");
		labelRoot.setPrefWidth((longestProject * 6) + 150);
		Label labelRootTime = new Label(completeTime(logObjects));
		labelRootTime.setPrefWidth(150);

		HBox boxRoot = new HBox();
		boxRoot.getChildren().add(labelRoot);
		boxRoot.getChildren().add(labelRootTime);

		TreeItem<HBox> gesamt = new TreeItem<>(boxRoot);
		gesamt.setExpanded(true);

		try
		{
			ArrayList<String> projectNames = sql.getProjectNames();
			Collections.sort(projectNames);

			ArrayList<TreeItem<HBox>> alleTasks = new ArrayList<>();

			for(String projectName : projectNames)
			{
				Label labelProjekt = new Label(projectName);
				labelProjekt.setPrefWidth((longestProject * 6) + 150);
				Label labelProjektTime = new Label(completeTime(sql.getByProject(projectName)));
				labelProjektTime.setPrefWidth(200);

				HBox box = new HBox();
				box.getChildren().add(labelProjekt);
				box.getChildren().add(labelProjektTime);

				item = new TreeItem<>(box);

				aktuelleTasks = new ArrayList<>();

				ArrayList<String> taskNames = sql.getTaskNamesByProject(projectName);
				Collections.sort(taskNames);
				for(String taskName : taskNames)
				{
					Label labelTask = new Label(taskName);
					labelTask.setPrefWidth((longestProject * 6) + 150);
					Label labelTaskTime = new Label(completeTime(sql.getByProjectAndTask(projectName, taskName)));
					labelTaskTime.setPrefWidth(200);

					HBox box2 = new HBox();
					box2.getChildren().add(labelTask);
					box2.getChildren().add(labelTaskTime);

					// und ein neus TreeItem erzeugt, was später Kind des
					// übergeordneten Knoten mit dem Projektnamen sein wird
					aktuelleTasks.add(new TreeItem<>(box2));
				}

				// fügt alle TreeItems der Ansicht hinzu
				item.getChildren().setAll(aktuelleTasks);
				alleTasks.add(item);
			}

			gesamt.getChildren().setAll(alleTasks);
			TreeView<HBox> tree = new TreeView<>(gesamt);
			Projekte.setContent(tree);
		}
		catch(Exception e)
		{
			Logger.error(e);
			Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Laden der Daten ist ein Fehler aufgetreten.", stage);
		}
	}

	private void loadFromDB()
	{
		sql = new SQL(settings.getSavePath() + "/" + Utils.DATABASE_NAME);
		try
		{
			logObjects = sql.getLogObjects();

			longestProject = 0;

			for(LogObject current : logObjects)
			{
				int length = current.getProject().length();
				if(length > longestProject)
				{
					longestProject = length;
				}
			}
		}
		catch(Exception e)
		{
			Logger.error(e);
			try
			{
				sql.createDB();
			}
			catch(Exception ex)
			{
				Logger.error(ex);
				Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Fehler beim Erstellen der Datenbank.", stage);
			}
		}
	}

	private void updateTableView()
	{
		table.getItems().clear();
		ObservableList<LogObject> objectsForTable = FXCollections.observableArrayList(logObjects);
		table.setItems(objectsForTable);
	}

	private void createLogView()
	{
		table.getItems().clear();
		table.getColumns().clear();

		TableColumn<LogObject, String> dates = new TableColumn<>("Datum");
		dates.setCellValueFactory(param -> {
			StringProperty value = new SimpleStringProperty();
			value.set(param.getValue().getDate());
			return value;
		});
		dates.setStyle("-fx-alignment: CENTER;");
		dates.setComparator((input1, input2) -> {
			// 0 --> input1 == input2
			// 1 --> input1 > input2
			// -1 --> input1 < input2

			if(input1.equals(input2))
			{
				return 0;
			}
			else
			{
				String[] date1 = input1.split(Pattern.quote("."));
				String[] date2 = input2.split(Pattern.quote("."));

				String newDate1 = date1[2] + "." + date1[1] + "." + date1[0];
				String newDate2 = date2[2] + "." + date2[1] + "." + date2[0];

				return newDate1.compareTo(newDate2);
			}
		});

		TableColumn<LogObject, String> startTimes = new TableColumn<>("Startzeit");
		startTimes.setCellValueFactory(new PropertyValueFactory<>("startTime"));
		startTimes.setStyle("-fx-alignment: CENTER;");

		TableColumn<LogObject, String> endTimes = new TableColumn<>("Endzeit");
		endTimes.setCellValueFactory(new PropertyValueFactory<>("endTime"));
		endTimes.setStyle("-fx-alignment: CENTER;");

		TableColumn<LogObject, String> projects = new TableColumn<>("Projekt");
		projects.setCellValueFactory(new PropertyValueFactory<>("project"));
		projects.setStyle("-fx-alignment: CENTER;");

		TableColumn<LogObject, String> tasks = new TableColumn<>("Task");
		tasks.setCellValueFactory(new PropertyValueFactory<>("task"));
		tasks.setStyle("-fx-alignment: CENTER;");

		TableColumn<LogObject, String> durations = new TableColumn<>("Dauer");
		durations.setCellValueFactory(param -> {
			StringProperty value = new SimpleStringProperty();
			value.set(ConvertTo.ConvertMillisToTime(param.getValue().getDuration()));
			return value;
		});
		durations.setStyle("-fx-alignment: CENTER;");

		table.getColumns().add(dates);
		table.getColumns().add(startTimes);
		table.getColumns().add(endTimes);
		table.getColumns().add(projects);
		table.getColumns().add(tasks);
		table.getColumns().add(durations);

		table.setFixedCellSize(26);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		table.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void handle(MouseEvent event)
			{
				if(event.isPrimaryButtonDown() && event.getClickCount() == 2)
				{
					Node node = ((Node) event.getTarget()).getParent();
					TableRow<LogObject> row;
					if(node instanceof TableRow)
					{
						row = (TableRow<LogObject>) node;
					}
					else
					{
						// clicking on text part
						row = (TableRow<LogObject>) node.getParent();
					}

					editEntry(row.getItem());
				}
			}
		});

		table.prefWidthProperty().bind(scrollPane.widthProperty().subtract(2));
		table.prefHeightProperty().bind(scrollPane.heightProperty().subtract(2));
	}

	@FXML
	private void charts()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/deadlocker8/smarttime/fxml/ChartGUI.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add("/de/deadlocker8/smarttime/css/Chart.css");
			Stage newStage = new Stage();
			newStage.setScene(scene);
			newStage.setTitle("Diagramme");
			ChartGUIController controller = fxmlLoader.getController();
			controller.init(settings, newStage);
			newStage.getIcons().add(icon);
			newStage.initOwner(stage);

			newStage.setResizable(true);
			newStage.setMinWidth(800);
			newStage.setMinHeight(600);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.showAndWait();
		}
		catch(IOException e)
		{
			Logger.error(e);
		}
	}

	public void loadAll()
	{
		loadFromDB();
		updateTableView();
		createTreeView();
	}

	public void newProject(String project, String task)
	{
		log = new LogObject();
		log.setProject(project);
		log.setTask(task);
		settings.setLastProject(project);
		settings.setLastTask(task);
		saveSettings();
	}

	private void startClock()
	{
		log.createStartTime();
		startTimestamp = System.currentTimeMillis();
	}

	private void endClock()
	{
		log.createEndTime();
		endTimestamp = System.currentTimeMillis();
		log.setDuration(endTimestamp - startTimestamp);

		SQL sql = new SQL(settings.getSavePath() + "/" + Utils.DATABASE_NAME);
		try
		{
			sql.insert(log);
		}
		catch(Exception e)
		{
			Logger.error(e);
			Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Fehler beim Speichern des Eintrags.", stage);
		}

		loadAll();
	}

	private String completeTime(ArrayList<LogObject> list)
	{
		long total = 0;
		for(LogObject current : list)
		{
			total += current.getDuration();
		}

		return ConvertTo.ConvertMillisToTime(total);
	}

	public void insertTime()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/deadlocker8/smarttime/fxml/InsertTimeGUI.fxml"));
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 540, 400);
			Stage newStage = new Stage();
			newStage.setScene(scene);
			newStage.setTitle("Zeit nachträglich einfügen");

			InsertTimeController controller = fxmlLoader.getController();
			controller.init(newStage, this, settings, icon);
			newStage.getIcons().add(icon);
			newStage.initOwner(stage);

			newStage.setResizable(false);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.show();
		}
		catch(IOException e)
		{
			Logger.error(e);
		}
	}

	public void importFromSmartTime()
	{
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import von SmartTime bis v4.5.0");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SmartTime-Logdateien", "*.log"));
		File file = fileChooser.showOpenDialog(stage);
		if(file != null)
		{
			Thread importThread = new Thread(() -> {
				Platform.runLater(() -> {
					showWaitingDialog("Importiere...", "Bitte warten...");
				});
				Importer importer = new Importer(settings.getSavePath() + "/" + Utils.DATABASE_NAME, stage);
				importer.importFromSmartTime(file);
				Platform.runLater(() -> {
					closeWaitingDialog();
					loadAll();
				});
			});
			importThread.start();
		}
	}

	public void importFromDB()
	{
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import von SmartTime Datenbank");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SmartTime Datenbank", "*.db"));
		File file = fileChooser.showOpenDialog(stage);
		if(file != null)
		{
			Thread importThread = new Thread(() -> {
				Platform.runLater(() -> showWaitingDialog("Importiere...", "Bitte warten..."));
				Importer importer = new Importer(settings.getSavePath() + "/" + Utils.DATABASE_NAME, stage);
				importer.importFromDB(file);
				Platform.runLater(() -> {
					closeWaitingDialog();
					loadAll();
				});
			});
			importThread.start();
		}
	}

	public void importFromJSON()
	{
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import von JSON");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON", "*.json"));
		File file = fileChooser.showOpenDialog(stage);
		if(file != null)
		{
			Thread importThread = new Thread(() -> {
				Platform.runLater(() -> showWaitingDialog("Importiere...", "Bitte warten..."));
				Importer importer = new Importer(settings.getSavePath() + "/" + Utils.DATABASE_NAME, stage);
				importer.importFromJSON(file);
				Platform.runLater(() -> {
					closeWaitingDialog();
					loadAll();
				});
			});
			importThread.start();
		}
	}

	public void exportAsDB()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export als SmartTime Datenbank");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SmartTime-Datenbank", "*.db");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(stage);
		if(file != null)
		{
			try
			{
				Files.copy(new File(settings.getSavePath() + "/" + Utils.DATABASE_NAME).toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(IOException e)
			{
				Logger.error(e);
				Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Exportieren der Daten ist ein Fehler aufgetreten.", stage);
			}

			Alerts.getInstance().createAlert(AlertType.INFORMATION, "Erfolgreich exportiert", "Export erfolgreich abgeschlossen.", stage);
		}
	}

	public void exportAsJSON()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export als JSON");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(stage);
		if(file != null)
		{
			Thread exportThread = new Thread(() -> {
				Platform.runLater(() -> showWaitingDialog("Exportiere...", "Bitte warten..."));
				Exporter exporter = new Exporter(settings.getSavePath() + "/" + Utils.DATABASE_NAME, stage);
				exporter.exportAsJSON(file);
				Platform.runLater(this::closeWaitingDialog);
			});
			exportThread.start();
		}
	}

	public void showWaitingDialog(String title, String text)
	{
		HBox hboxWaiting = new HBox();
		ProgressIndicator indicator = new ProgressIndicator();
		indicator.setPrefWidth(40.0);
		indicator.setPrefHeight(40.0);
		Label labelWait = new Label(text);
		labelWait.setStyle("-fx-font-size: 20;");
		hboxWaiting.getChildren().add(indicator);
		hboxWaiting.getChildren().add(labelWait);
		hboxWaiting.setAlignment(Pos.CENTER);
		HBox.setMargin(labelWait, new Insets(0.0, 0.0, 0.0, 30.0));
		hboxWaiting.setPadding(new Insets(20.0));
		waitingStage = new Stage();
		waitingStage.setTitle(title);
		waitingStage.setScene(new Scene(hboxWaiting, 250, 75));
		waitingStage.getIcons().add(icon);
		waitingStage.initOwner(stage);
		waitingStage.setResizable(false);
		waitingStage.initModality(Modality.APPLICATION_MODAL);
		waitingStage.setOnCloseRequest(Event::consume);
		waitingStage.show();
	}

	public void closeWaitingDialog()
	{
		if(waitingStage.isShowing())
		{
			waitingStage.close();
		}
	}

	private void editEntry(LogObject object)
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/deadlocker8/smarttime/fxml/EditGUI.fxml"));
			Parent root = fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.setScene(new Scene(root, 455, 280));
			newStage.setTitle("Eintrag bearbeiten");
			newStage.getIcons().add(icon);
			newStage.initOwner(stage);

			EditController pfc = fxmlLoader.getController();
			pfc.init(this, newStage, settings, icon, object);

			newStage.setResizable(false);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.showAndWait();
		}
		catch(IOException d)
		{
			Logger.error(d);
		}
	}

	public void updateEntry(LogObject oldLog, LogObject newLog)
	{
		try
		{
			sql.update(oldLog, newLog);
			loadAll();
		}
		catch(Exception e)
		{
			Logger.error(e);
			Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Aktualisieren des Eintrags ist ein Fehler aufgetreten.", stage);
		}
	}

	public void deleteEntry(LogObject object)
	{
		try
		{
			sql.delete(object);
			loadAll();
		}
		catch(Exception e)
		{
			Logger.error(e);
			Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Löschen des Eintrags ist ein Fehler aufgetreten.", stage);
		}
	}

	@FXML
	public void changeSavePath()
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Speicherort auswählen");
		File file = directoryChooser.showDialog(stage);
		if(file != null)
		{
			String oldSavePath = settings.getSavePath();
			settings.setSavePath(file.getAbsolutePath());
			saveSettings();
			if(!oldSavePath.equals(settings.getSavePath()))
			{
				if(new File(oldSavePath + "/" + Utils.DATABASE_NAME).exists())
				{
					try
					{
						Files.move(new File(oldSavePath + "/" + Utils.DATABASE_NAME).toPath(), new File(settings.getSavePath() + "/" + Utils.DATABASE_NAME).toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
					catch(IOException e)
					{
						Logger.error(e);
						Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Verschieben der Datenbank ist ein Fehler aufgetreten.", stage);
					}
				}
				else
				{
					loadFromDB();
					loadAll();
				}
			}
			setLabelSavePath();
		}
	}

	@FXML
	public void deleteDB()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Löschen");
		alert.setHeaderText("");
		alert.setContentText("Möchten Sie die gesamte Datenbank wirklich unwiederruflich löschen?");
		Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(icon);

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK)
		{
			try
			{
				sql.deleteDB();
				sql.createDB();
				loadAll();
			}
			catch(Exception e)
			{
				Logger.error(e);
				Alerts.getInstance().createAlert(AlertType.ERROR, "Fehler", "Beim Löschen der Datenbank ist ein Fehler aufgetreten.", stage);
			}
		}
	}

	public void saveSettings()
	{
		try
		{
			Utils.saveSettings(settings);
		}
		catch(IOException e)
		{
			Logger.error(e);
		}
	}

	public void loadSettings()
	{
		settings = Utils.loadSettings();
		if(settings != null)
		{
			setLabels(settings.getLastProject(), settings.getLastTask());
			projektExistiertFlag = true;
			newProject(settings.getLastProject(), settings.getLastTask());
			if(settings.getSavePath() == null)
			{
				settings.setSavePath(DEFAULT_SAVE_PATH);
			}
		}
		else
		{
			settings = new Settings();
			settings.setSavePath(DEFAULT_SAVE_PATH);
		}
	}

	@FXML
	public void createReport()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/deadlocker8/smarttime/fxml/ReportGUI.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.setScene(new Scene(root, 500, 500));
			newStage.setMinWidth(300);
			newStage.setMinHeight(300);
			newStage.setTitle("PDF-Report erstellen");
			newStage.getIcons().add(icon);
			newStage.initOwner(stage);

			ReportController reportController = (ReportController) fxmlLoader.getController();
			reportController.init(this, newStage, settings, icon);

			newStage.setResizable(true);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.showAndWait();
		}
		catch(IOException d)
		{
			Logger.error(d);
		}
	}

	public void about()
	{
		ArrayList<String> creditLines = new ArrayList<>();
		creditLines.add(bundle.getString("credits"));

		// TODO
//		AlertGenerator.showAboutAlertWithCredits(bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"), "Robert Goldmann", creditLines, icon, stage, null, false);
	}
}