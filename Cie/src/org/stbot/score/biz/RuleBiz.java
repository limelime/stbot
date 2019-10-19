package org.stbot.score.biz;

import org.hibernate.Session;
import org.stbot.common.utils.HibernateUtil;
import org.stbot.score.model.Rule;

public class RuleBiz
{
  public Rule load(final int iRuleId)
  {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Rule oRule = (Rule)session.get(Rule.class, iRuleId);
    if( oRule == null )
      oRule = new Rule();
    
    return oRule;
  }
}
