package org.stbot.common.utils;

public class Trigonometry
{
  /**
   * Given a slope, return the degree between the Y-axis and the hypotenuse.
   * @param dSlope
   * @return
   */
  public double getDegreeFromYAxisToHypotenuse(final double dSlope)
  {
    final double dRightAngle = 90.0d;
    if(dSlope>0) // Slope is positive.
    {
      return dRightAngle - this.getSlopeDegree(dSlope);
    }
    else  // Slope is negative.
    {
      return 180.0 - dRightAngle - this.getSlopeDegree(Math.abs(dSlope));
    }
  }
  
  /**
   * Get the slope in degree.
   * @param dSlope
   * @return
   */
  public double getSlopeDegree(final double dSlope)
  {
    double dRadian = Math.atan(dSlope);
    return Math.toDegrees(dRadian);   
  }
}
