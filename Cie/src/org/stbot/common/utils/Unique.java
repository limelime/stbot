package org.stbot.common.utils;

import java.util.Random;

public class Unique
{
  /**
   * Get current data and time with the default format.
   * @param iRndSuffix Suffix the date and time string with a random number to ensure its uniqueness.
   * @return
   */
  public static final String getCurrDateTime(final int iRndSuffix)
  {
    Random oRandom = new Random();
    return DateUtil.getCurrDateTime("yyyy-MM-dd_HH.mm.ss.SSSS")+"_"+oRandom.nextInt(iRndSuffix);
  }

}
