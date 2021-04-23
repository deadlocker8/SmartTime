package de.deadlocker8.smarttime.controller;

import de.thecodelabs.utils.ui.icon.FontAwesomeType;
import de.thecodelabs.utils.ui.icon.FontIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class TimePickerController implements Initializable
{
	@FXML
	private HBox hbox;

	private Label labelHours;
	private Label labelMinutes;
	private Label labelSeconds;

	private Button buttonHoursUp;
	private Button buttonHoursDown;

	private Button buttonMinutesUp;
	private Button buttonMinutesDown;

	private Button buttonSecondsUp;
	private Button buttonSecondsDown;

	private int hours = 0;
	private int minutes = 0;
	private int seconds = 0;

	private InsertTimeController controller;

	public void setController(InsertTimeController controller)
	{
		this.controller = controller;
	}

	public int getHours()
	{
		return hours;
	}

	public int getMinutes()
	{
		return minutes;
	}

	public int getSeconds()
	{
		return seconds;
	}

	public void setTime(int hours, int minutes, int seconds)
	{
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}

	public void refresh(String item, String direction)
	{
		controller.refresh(this, hours, minutes, seconds, item, direction);
	}

	private String getCorrectedString(int number)
	{
		if(number < 10)
		{
			return "0" + number;
		}
		else
		{
			return "" + number;
		}
	}

	public void init()
	{
		labelHours.setText(getCorrectedString(hours));
		labelMinutes.setText(getCorrectedString(minutes));
		labelSeconds.setText(getCorrectedString(seconds));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		FontIcon arrowUp = new FontIcon(FontAwesomeType.ARROW_UP);
		arrowUp.setSize(10);
		arrowUp.setColor(Color.web("#000000"));

		FontIcon arrowDown = new FontIcon(FontAwesomeType.ARROW_DOWN);
		arrowDown.setSize(10);
		arrowDown.setColor(Color.web("#000000"));

		//VBoxHours
		buttonHoursUp = new Button("", arrowUp);
		buttonHoursUp.setOnAction(event -> refresh("hours", "up"));

		labelHours = new Label("00");
		labelHours.setStyle("-fx-font-size: 18; ");

		buttonHoursDown = new Button("", arrowDown);
		buttonHoursDown.setOnAction(event -> refresh("hours", "down"));

		VBox vboxHours = new VBox();
		vboxHours.getChildren().add(buttonHoursUp);
		vboxHours.getChildren().add(labelHours);
		vboxHours.getChildren().add(buttonHoursDown);
		vboxHours.setAlignment(Pos.CENTER);

		//VBoxMinutes

		arrowUp = new FontIcon(FontAwesomeType.ARROW_UP);
		arrowUp.setSize(10);
		arrowUp.setColor(Color.web("#000000"));

		arrowDown = new FontIcon(FontAwesomeType.ARROW_DOWN);
		arrowDown.setSize(10);
		arrowDown.setColor(Color.web("#000000"));

		buttonMinutesUp = new Button("", arrowUp);
		buttonMinutesUp.setOnAction(event -> refresh("minutes", "up"));

		labelMinutes = new Label("00");
		labelMinutes.setStyle("-fx-font-size: 18;");

		buttonMinutesDown = new Button("", arrowDown);
		buttonMinutesDown.setOnAction(event -> refresh("minutes", "down"));

		VBox vboxMinutes = new VBox();
		vboxMinutes.getChildren().add(buttonMinutesUp);
		vboxMinutes.getChildren().add(labelMinutes);
		vboxMinutes.getChildren().add(buttonMinutesDown);
		vboxMinutes.setAlignment(Pos.CENTER);

		//VBoxSeconds
		arrowUp = new FontIcon(FontAwesomeType.ARROW_UP);
		arrowUp.setSize(10);
		arrowUp.setColor(Color.web("#000000"));

		arrowDown = new FontIcon(FontAwesomeType.ARROW_DOWN);
		arrowDown.setSize(10);
		arrowDown.setColor(Color.web("#000000"));

		buttonSecondsUp = new Button("", arrowUp);
		buttonSecondsUp.setOnAction(event -> refresh("seconds", "up"));

		labelSeconds = new Label("00");
		labelSeconds.setStyle("-fx-font-size: 18; ");

		buttonSecondsDown = new Button("", arrowDown);
		buttonSecondsDown.setOnAction(event -> refresh("seconds", "down"));

		VBox vboxSeconds = new VBox();
		vboxSeconds.getChildren().add(buttonSecondsUp);
		vboxSeconds.getChildren().add(labelSeconds);
		vboxSeconds.getChildren().add(buttonSecondsDown);
		vboxSeconds.setAlignment(Pos.CENTER);

		//Hinzufügen zur HBox
		Label separator = new Label(" : ");
		separator.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
		Label separator2 = new Label(" : ");
		separator2.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

		hbox.getChildren().add(vboxHours);
		hbox.getChildren().add(separator);
		hbox.getChildren().add(vboxMinutes);
		hbox.getChildren().add(separator2);
		hbox.getChildren().add(vboxSeconds);
	}
}