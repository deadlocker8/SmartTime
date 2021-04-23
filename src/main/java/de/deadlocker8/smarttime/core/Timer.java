package de.deadlocker8.smarttime.core;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Timer
{
	Timeline timeline;
	long startTime;

	public Timer(Label label)
	{
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), event -> label.setText(ConvertTo.ConvertMillisToTime(System.currentTimeMillis() - startTime))));
	}

	public void start()
	{
		startTime = System.currentTimeMillis();
		timeline.playFromStart();
	}

	public void stop()
	{
		timeline.stop();
	}

	public boolean isRunning()
	{
		return timeline.getStatus().equals(Status.RUNNING);
	}
}
