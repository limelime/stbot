package org.stbot.stock.scheduler;

import org.stbot.common.utils.Chronometer;
import org.stbot.stock.biz.Price180Biz;

/**
 * Recurrent jobs that have to run:
 * - Trim Price tables
 * @author Xuan Ngo
 */
public class Cronjobs
{

  public static void main(String[] args)
  {
    // Start the Chronometer.
    Chronometer oChronometer = new Chronometer();
    oChronometer.start();
    
    // Main application starts here.
    Price180Biz oPrice180Biz = new Price180Biz();
    oPrice180Biz.deletePricesOlderThan(180);
    
    // Stop the Chronometer.
    oChronometer.stop();
    oChronometer.report("Cron Jobs");
  }

}
