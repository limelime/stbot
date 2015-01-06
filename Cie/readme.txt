1)
Restore database from db/.(~ 1 hrs)
	-In build.xml, change the path of the mysql server.
	-cd db/
	-restore.bat stock_20XX-XX-XX_XX.XX.XX

2)
run.bat

Developer:
===========
Model package contains table models using Hibernate.
Biz package contains all process logic.



General Javadoc from Eclipse and at the same time save settings in Ant script.


Add another Exchange:
=====================
Add org.stbot.stock.biz.[Exchange name] (e.g Copy Amex.java)
Create an object of the Exchange in org.stbot.stock.scheduler.Exchange->updateCiesFromExchanges()


Dealing with Proxy connection:
=====================
Add the following code before you code:
      // Use Proxy if applicable.
      ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();
      ProxySelector myProxySelector = proxySearch.getProxySelector();
      ProxySelector.setDefault(myProxySelector);
      