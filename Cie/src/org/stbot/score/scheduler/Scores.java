package org.stbot.score.scheduler;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.stbot.score.rule.AverageDailyTradedValue;
import org.stbot.score.rule.LeastVolatilePrice;
import org.stbot.score.rule.ListedAge;
import org.stbot.score.rule.VolumeActivity;
import org.stbot.stock.biz.JobBiz;
import org.stbot.stock.biz.Price180Biz;
import org.stbot.stock.model.Cie;
import org.stbot.stock.model.Price180;

import org.stbot.common.utils.Chronometer;

/**
 * Compute the score of each stock.
 * @author Xuan Ngo
 *
 */
public class Scores
{
  private static final Logger log = LoggerFactory.getLogger(Scores.class);
  
  public static void main(String[] args)
  {
    // Start the Chronometer.
    Chronometer oChronometer = new Chronometer();
    
    // Instantiate rule objects.
    AverageDailyTradedValue oAverageDailyTradedValue = new AverageDailyTradedValue();
    oAverageDailyTradedValue.updateRule(); // Don't run this too much because ID is auto increment.
    
    LeastVolatilePrice oLeastVolatilePrice = new LeastVolatilePrice();
    oLeastVolatilePrice.updateRule(); // Don't run this too much because ID is auto increment.
    
    ListedAge oListedAge = new ListedAge();
    oListedAge.updateRule();
    
    VolumeActivity oVolumeActivity = new VolumeActivity();
    oVolumeActivity.updateRule();
    
    
    // Start calculating scores.
    oChronometer.start();
    Price180Biz oPrice180Biz = new Price180Biz();
    JobBiz oJobBiz = new JobBiz();
    ArrayList<Cie> lCieWithPrices = oJobBiz.getCiesWithPrices();
    int z = 0;
    for(int i=0; i<lCieWithPrices.size(); i++)
    {
      // Logging: Make sure to report the stock first before computing so that we know on which stock the errors occur.
      if(log.isInfoEnabled())
      {
        Object[] oParameters = new Object[]{
                                              String.format("%06d", z+1), // e.g. 0000X
                                              lCieWithPrices.size(),
                                              lCieWithPrices.get(i).getId(),
                                            };
        log.info("{}/{}: Calculating score of cie_id={} ...", oParameters);
        z++;
      }

      
      /**
       * Compute rules scores.
       */
      final int iCie_id = lCieWithPrices.get(i).getId();
      final ArrayList<Price180> lPrices = oPrice180Biz.getPricesOf(iCie_id);
      
      // Rule 1: Average Daily Traded Value 
      oAverageDailyTradedValue.calculate(iCie_id, lPrices);
      
      // Rule 2: Least Volatile Price
      oLeastVolatilePrice.calculate(iCie_id, lPrices);

      // Rule 3: Listed Age
      oListedAge.calculate(iCie_id);

      // Rule 4: Volume Activity
      oVolumeActivity.calculate(iCie_id, lPrices);
    }
    
    // Stop the Chronometer.
    oChronometer.stop();
    oChronometer.report("Calculate scores");
  }

}
