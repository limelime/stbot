package org.stbot.stock.biz;
/**
 * Trading status of each stock.
 * If it still traded, then update the stock price.
 * Otherwise, don't.
 * @author Xuan Ngo
 *
 */
public class Status
{
  /** Update price. The stock is still traded.*/
  public static int TRADED    = 0; // Update price.
  
  /** Don't update price. The stock is not traded anymore. */
  public static int NOT_TRADED  = 1; // Don't update price.
}
