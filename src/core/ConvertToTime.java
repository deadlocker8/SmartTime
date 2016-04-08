package core;

/**
 * Klasse für die Umrechnung von Zeiten
 * @author Robert
 *
 */
public class ConvertToTime 
{	
	/**
	 * Konvertiert Millisekunden in Stunden, Minuten und Sekunden
	 * @param differenz long - Millisekunden
	 * @return String - Stunden + Minuten + Sekunden
	 */
	public static String ConvertMillisToTime(long differenz) 
	{		
		long sek = (differenz / 1000) % 60;		
		long min = (differenz / (1000 * 60)) % 60;
		long hour = (differenz / (1000 * 60 * 60));
					
		return hour + " h  " + min + " min  " + sek + " sek";
	}
	
	/**
	 * Berechnet die Differenz zwischen End- und Startzeit
	 * @param start long - Startzeit in Millisekunden
	 * @param end long - Endzeit in Millisekunden
	 * @return long - Differenz
	 */
	public static long CalculateDifference(long start, long end) 
	{		
		long difference = end - start;
		
		return difference;
	}
}