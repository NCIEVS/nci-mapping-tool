set OCP=%CLASSPATH%
set lib0=.
set lib1=lib

set lib2=extlib

set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;%lib0%\*
set CLASSPATH=%CLASSPATH%;%lib1%\*
set CLASSPATH=%CLASSPATH%;%lib2%\* 

C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% gov.nih.nci.evs.service.MappingPanel https://sparql-evs-dev.nci.nih.gov/sparql 

set CLASSPATH=%OCP%
