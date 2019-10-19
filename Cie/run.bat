pause
pause
pause

SET Unique=%date%_%time%
SET Unique=%Unique:/=.%
SET Unique=%Unique::=.%
SET Unique=%Unique: =%
SET Unique=%Unique:Mon=%
SET Unique=%Unique:Tue=%
SET Unique=%Unique:Wed=%
SET Unique=%Unique:Thu=%
SET Unique=%Unique:Fri=%
SET Unique=%Unique:Sat=%
SET Unique=%Unique:Sun=%
 
echo %Unique%

REM ant Scheduler >> stock_%Unique%.log

CALL run_Exchange.bat
CALL run_Prices.bat
CALL run_Finances.bat
CALL run_Cronjobs.bat
CALL run_Cronjobs.bat
CALL run_Scores.bat

 
SET Unique=&::


