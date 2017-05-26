package core;

import java.util.Arrays;

public class Utils 
{   
    private static final String[] AVAILABLE_MONTH_NAMES = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};

    public static String getMonthName(int monthNumberOneIndexed)
    {
        return AVAILABLE_MONTH_NAMES[monthNumberOneIndexed - 1];
    }    
    
    public static int getMonthNumber(String monthName)
    {
        return Arrays.asList(AVAILABLE_MONTH_NAMES).indexOf(monthName) + 1;
    }
}