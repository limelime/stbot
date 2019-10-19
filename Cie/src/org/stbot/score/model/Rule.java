package org.stbot.score.model;

import java.sql.Date;

public class Rule
{
  private int id;
  private Date date;
  private double weight;
  private String description;
  
  public int getId()
  {
    return id;
  }
  public void setId(int id)
  {
    this.id = id;
  }
  public Date getDate()
  {
    return date;
  }
  public void setDate(Date date)
  {
    this.date = date;
  }
  public double getWeight()
  {
    return weight;
  }
  public void setWeight(double weight)
  {
    this.weight = weight;
  }
  public String getDescription()
  {
    return description;
  }
  public void setDescription(String description)
  {
    this.description = description;
  }


}
