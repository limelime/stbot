package org.stbot.common.utils;

import org.hibernate.*;
import org.hibernate.cfg.*;

public class HibernateUtil {

  private static final SessionFactory sessionFactory;

  static
  {
    try 
    {
      //File f = new File("E:\\Eclipse_WorkspaceTPTP-4.5.0\\HibernateTest\\hibernate.cfg.xml"); 
      // Create the SessionFactory from hibernate.cfg.xml
      sessionFactory = new Configuration().configure(/*f*/).buildSessionFactory();
    } 
    catch (Throwable ex)
    {
      // Make sure you log the exception, as it might be swallowed
      System.err.println("Initial SessionFactory creation failed." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static SessionFactory getSessionFactory()
  {
    return sessionFactory;
  }

}
