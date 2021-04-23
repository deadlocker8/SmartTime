package de.deadlocker8.smarttime.core;

public class ConvertTo
{
	private ConvertTo()
	{
	}

	/**
	 * Konvertiert Millisekunden in Stunden, Minuten und Sekunden
	 *
	 * @param millis long - Millisekunden
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
	 * @param seconds long - Sekunden
	 * @return String - Stunden + Minuten + Sekunden
	 */
	public static String ConvertSecondsToTime(long seconds)
	{
		long sek = seconds % 60;
		long min = seconds / 60 % 60;
		long hour = seconds / (60 * 60);

		return hour + " h " + min + " min " + sek + " sek";
	}
}