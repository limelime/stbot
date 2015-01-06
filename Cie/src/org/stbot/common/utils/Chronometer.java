package org.stbot.common.utils;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Measure time elapsed.
 * @author Xuan Ngo
 *
 */
public class Chronometer 
{
  private static final Logger log = LoggerFactory.getLogger(Chronometer.class);

  private Calendar m_oStart = null;
	private Calendar m_oEnd = null;
	
	public Chronometer()
	{}
	
	public void start()
	{
		this.m_oStart = Calendar.getInstance();
	}
	public void stop()
	{
		this.m_oEnd = Calendar.getInstance();
	}
	
	/**
	 * @return Return the elapsed time measured in milliseconds.
	 */
	public final long getDiffInMillis()
	{
	  return this.m_oEnd.getTimeInMillis() - this.m_oStart.getTimeInMillis();
	}

	/**
	 * @return Return the elapsed time measured as HH:MM:SS.mmmm.
	 */
	public final String getDiffInString()
	{
    /***********************
     * Calculate statistics. 
     ***********************/
    long lTotalRuntime = this.getDiffInMillis();
    long lRuntime = lTotalRuntime;
     
    long lRuntimeHrs = lRuntime/(1000*3600);
    lRuntime = lRuntime - (lRuntimeHrs*1000*3600);// Runtime remaining.
    long lRuntimeMin = (lRuntime)/(1000*60);
    lRuntime = lRuntime - (lRuntimeMin*1000*60);  // Runtime remaining.
    long lRuntimeSec = lRuntime/(1000);
    lRuntime = lRuntime - (lRuntimeSec*1000);     // Runtime remaining.
    
    return String.format("%02d", lRuntimeHrs)+":"+String.format("%02d", lRuntimeMin)+":"+String.format("%02d", lRuntimeSec)+"."+lRuntime;
	}
	public void report()
	{
	  this.report("");
	}
	
	public void report(String sPrefix)
	{
	  if(log.isInfoEnabled())
	  {
	    sPrefix = sPrefix+":";
	    
      // Start at YYYY-MM-DD HH:MM:SS.mmm
      log.info("{} Start at {}.", sPrefix, this.getDateTimeFormatted(this.m_oStart));
      
      // Ran for HH:MM:SS.mmm (milliseconds)
      log.info("{} Ran for {}.", sPrefix, this.getDiffInString());
      
      // End at YYYY-MM-DD HH:MM:SS.mmm
      log.info("{} End at {}.", sPrefix, this.getDateTimeFormatted(this.m_oEnd));
	  }
	}
	
	private final String getDateTimeFormatted(Calendar oCalendar)
	{
    final String sDateFormat = "yyyy-MM-dd_HH:mm:ss.SSSS";
    
    Date oCurrentDate = oCalendar.getTime();
    SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(sDateFormat);
    return oSimpleDateFormat.format(oCurrentDate);
	}
}
