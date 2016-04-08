package core;

import javafx.application.Platform;
import userInterface.UserInterfaceController;

/**
 * Thread für die zeitgleiche Ausführung der Zeitmessung ohne Beeinträchtigung der Hauptanwendung
 * @author Robert
 *
 */
public class Counter extends Thread
{
	public static boolean running;
	public static long ausgabe;
	public static UserInterfaceController uic;	
			
	@Override
	public void run() 
	{		
			//löscht zu Beginn den Text des Labels
			uic.labelTime.setText("");
			//initialisiert die Zählvariable
			ausgabe = 0;
				
			 while (running) 
			 {
				 try 
				 {			
					 //konvertiert die bereits verstrichenen Millisekunden in Stunden, Minuten und Sekunden
					 //und gibt diese auf dem Label aus
					 Platform.runLater(()->{
						uic.labelTime.setText(ConvertToTime.ConvertMillisToTime(ausgabe));
					 });
					 
					 //schläft 1000 Millisekunden
					 Thread.sleep(1000);
					 //erhöht die Zählvariable um 1000 Millisekunden
					 ausgabe = ausgabe + 1000;
				 }
				 //reagiert auf eine InterruptedException, die ausgelöst wird, wenn der Stopp-Button gedrückt wird
				 catch (InterruptedException e) 
				 {
					 running = false;
				 }			
			}	
	}
}