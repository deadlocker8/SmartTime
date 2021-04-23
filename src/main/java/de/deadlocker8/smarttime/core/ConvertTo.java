package de.deadlocker8.smarttime.core;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.paint.Color;

/**
 * stellt Methoden zur Konvertierung von Einheiten zur Verfügung
 *
 * @author Robert
 *
 */
public class ConvertTo
{
	/**
	 * Konvertiert Millisekunden in Stunden, Minuten und Sekunden
	 *
	 * @param millis
	 *            long - Millisekunden
	 * @return String - Stunden + Minuten + Sekunden
	 */
	public static String ConvertMillisToTime(long millis)
	{
		long sek = (millis / 1000) % 60;
		long min = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60));

		return hour + " h  " + min + " min  " + sek + " sek";
	}

	/**
	 * Konvertiert Sekunden in Stunden, Minuten und Sekunden
	 *
	 * @param seconds
	 *            long - Sekunden
	 * @return String - Stunden + Minuten + Sekunden
	 */
	public static String ConvertSecondsToTime(long seconds)
	{
		long sek = seconds % 60;
		long min = seconds / 60 % 60;
		long hour = seconds / (60 * 60);

		return hour + " h " + min + " min " + sek + " sek";
	}

	/**
	 * Konvertiert Sekunden in Minuten und Sekunden
	 *
	 * @param seconds
	 *            long - Sekunden
	 * @return String - Minuten + Sekunden
	 */
	public static String ConvertMillisToMinutesAndSeconds(long millis)
	{
		long sek = (millis / 1000) % 60;
		long min = (millis / 1000) / 60;

		return min + ":" + String.format("%02d", sek);
	}

	/**
	 * Konvertiert Millisekunden in Datum und Uhrzeit
	 *
	 * @param millis
	 *            long - Millisekunden
	 * @return String - dd.MM.yyyy-hh:mm:ss.SSS
	 */
	public static String ConvertMillisToDateAndTime(long millis)
	{
		Date date = new Date(millis);

		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		return formatter.format(date);
	}

	/**
	 * Konvertiert einen Timestamp zurück in Millisekunden
	 *
	 * @param time
	 *            String - Timestamp
	 * @return long - Millisekunden
	 */
	public static long ConvertTimestampToMillis(String time)
	{
		try
		{
			Timestamp timestamp = Timestamp.valueOf(time);
			return timestamp.getTime();
		}
		catch(IllegalArgumentException e)
		{
			System.err.println("Falsches Eingabeformat \nString muss folgende Struktur haben: yyyy-mm-dd hh:mm:ss[.SSSSSSSSS]");
		}
		return 0;
	}

	/**
	 * Konvertiert Bytes zu MB, KB und Bytes
	 *
	 * @param bytes
	 *            long - Bytes
	 * @return String - Megabyte + Kilobyte + Byte;
	 */
	public static String ConvertBytesToDecimalSize(long bytes)
	{
		long normal = bytes % 1000;
		long kilo = (bytes / 1000) % 1000;
		long mega = (bytes / 1000000) % 1000000;

		return mega + " MB " + kilo + " KB " + normal;
	}

	/**
	 * Konvertiert Bytes zu MB, KB und Bytes
	 *
	 * @param bytes
	 *            long - Bytes
	 * @return String - Megabyte + Kilobyte + Byte;
	 */
	public static String ConvertBytesToBinarySize(long bytes)
	{
		long normal = bytes % 1024;
		long kilo = (bytes / 1024) % 1024;
		long mega = (bytes / 1048576) % 1048576;

		return mega + " MB " + kilo + " KB " + normal;
	}

	public static String toRGBHex(Color color)
	{
		return String.format("#%02X%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255), (int)(color.getOpacity() * 255));
	}

	public static String toRGBHexWithoutOpacity(Color color)
	{
		return String.format("#%02X%02X%02X", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));
	}

	/**
	 * get an appropriate readable text color for given background color
	 * @param color - background color
	 * @return Color - text color
	 */
	public static Color getAppropriateTextColor(Color color)
	{
		// Counting the perceptive luminance - human eye favors green color...
		double a = 1 - (0.299 * (int)(color.getRed()*255) + 0.587 * (int)(color.getGreen()*255) + 0.114 * (int)(color.getBlue()*255)) / 255;

		if(a < 0.5)
		{
			return Color.BLACK;
		}
		else
		{
			return Color.WHITE;
		}
	}
}