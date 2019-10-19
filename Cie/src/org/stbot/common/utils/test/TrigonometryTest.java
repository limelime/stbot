package org.stbot.common.utils.test;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.stbot.common.utils.Trigonometry;

public class TrigonometryTest
{
  @Test
  public void getSlopeDegreeTest()
  {
    Trigonometry oTri = new Trigonometry();
    
    // Positive slope
    assertEquals(oTri.getSlopeDegree(1), 45.0d);
    
    // Negative slope
    assertEquals(oTri.getSlopeDegree(-1), -45.0d);
    
    // 0 degree
    assertEquals(oTri.getSlopeDegree(0), 0.0d);

    // 30 degree
    assertEquals(Math.round(oTri.getSlopeDegree(Math.sqrt(3)/3)), 30);

    // 60 degree
    assertEquals(Math.round(oTri.getSlopeDegree(Math.sqrt(3))), 60);

  }
  @Test
  public void getDegreeFromYAxisToHypotenuseTest()
  {
    Trigonometry oTri = new Trigonometry();
    
    // Positive slope
    assertEquals(oTri.getDegreeFromYAxisToHypotenuse(1), 45.0d);
    
    // Negative slope
    assertEquals(oTri.getDegreeFromYAxisToHypotenuse(-1), 45.0d);
    
    // 60 degree
    assertEquals(Math.round(oTri.getDegreeFromYAxisToHypotenuse(Math.sqrt(3)/3)), 60);
    assertEquals(-Math.round(oTri.getDegreeFromYAxisToHypotenuse(Math.sqrt(3)/3)), -60);

    // 30 degree
    assertEquals(Math.round(oTri.getDegreeFromYAxisToHypotenuse(Math.sqrt(3))), 30);
    assertEquals(-Math.round(oTri.getDegreeFromYAxisToHypotenuse(Math.sqrt(3))), -30);
    
    for(int i=0; i<1000000; i+=7)
    {
      assertTrue(oTri.getDegreeFromYAxisToHypotenuse(i)<=90.0, "Actual="+oTri.getDegreeFromYAxisToHypotenuse(i));
      assertTrue(oTri.getDegreeFromYAxisToHypotenuse(-i)<=90.0, "Actual="+oTri.getDegreeFromYAxisToHypotenuse(-i));
    }
  }
}
