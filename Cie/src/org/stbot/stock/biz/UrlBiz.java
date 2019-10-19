package org.stbot.stock.biz;

import java.util.Calendar;
/**
 * Use to generate the correct urls for other classes.
 * @author Xuan Ngo
 *
 */
public class UrlBiz
{
  /**
   * oFromDate = Last entry from db, oToDate = Today's date depending on threshold time.
   * @param sSymbol
   * @param oFromDate
   * @param oToDate
   * @return url string
   */
  public final String getUrl(final String sSymbol, java.sql.Date oFromDate, java.sql.Date oToDate)
  {
    Calendar oCalendar = Calendar.getInstance();
    
    // oToDate: Expected latest date
    oCalendar.setTime(new java.util.Date(oFromDate.getTime()));
    final int to_yyyy = oCalendar.get(Calendar.YEAR);
    final int to_mm = oCalendar.get(Calendar.MONTH); // Jan = 0
    final int to_dd = oCalendar.get(Calendar.DAY_OF_MONTH);
    
    // oFromDate: Latest date from db.
    int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
    oCalendar.setTime(new java.util.Date(oToDate.getTime()+MILLIS_IN_DAY)); // Get start from next day.
    final int from_yyyy = oCalendar.get(Calendar.YEAR);
    final int from_mm = oCalendar.get(Calendar.MONTH);
    final int from_dd = oCalendar.get(Calendar.DAY_OF_MONTH); 
    
    return "http://ichart.finance.yahoo.com/table.csv?s="+sSymbol+"&d="+from_mm+"&e="+from_dd+"&f="+from_yyyy+"&g=d&a="+to_mm+"&b="+to_dd+"&c="+to_yyyy+"&ignore=.csv";
  }
}
