package org.stbot.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stbot.score.rule.LeastVolatilePrice;

public class DateUtil
{
  private static final Logger log = LoggerFactory.getLogger(LeastVolatilePrice.class);
  
  /**
   * Get current date and time with the specified format.
   * @param sDateFormat
   * @return
   */
  public static final String getCurrDateTime(final String sDateFormat)
  {
    Date oCurrentDate = new Date();
    SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(sDateFormat);
    return oSimpleDateFormat.format(oCurrentDate);
  }
  
  /**
   * 
   * @param sDatePatern e.g. "dd/MM/yyyy"
   * @param sDateValue e.g. "30/12/2010"
   * @return
   */
  public static final java.sql.Date getSqlDate(final String sDatePatern, final String sDateValue)
  {
    DateFormat df = new SimpleDateFormat(sDatePatern);
    try
    {
      return new java.sql.Date(df.parse(sDateValue).getTime());
    }
    catch(ParseException ex)
    {
      log.error("Can't parse: pattern="+sDatePatern+", value="+sDateValue, ex);
    }
    return null;
  }
}
