package org.stbot.stock.scheduler;

import org.stbot.common.utils.Chronometer;
import org.stbot.stock.biz.TargetPriceBiz;
import org.stbot.stock.biz.FloatShareBiz;

/**
 * Get financial information of each stock.
 * @author Xuan Ngo
 *
 */
public class Finances
{

  public static void main(String[] args)
  {
    // Start the Chronometer.
    Chronometer oChronometer = new Chronometer();
    oChronometer.start();
    
    // Main application starts here.
    TargetPriceBiz oTargetPriceBiz = new TargetPriceBiz();
    oTargetPriceBiz.run(); // TargetPriceBiz because it will create an entry if cie_id doesn't exist. Other collector class will only do updates. 

    FloatShareBiz oFloatShareBiz = new FloatShareBiz();
    oFloatShareBiz.run();
    
    // Stop the Chronometer.
    oChronometer.stop();
    oChronometer.report("Get financial information of each stock");    
  }

}
