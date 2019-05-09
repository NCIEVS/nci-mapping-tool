set OCP=%CLASSPATH%
set lib1=lib
set lib2=extlib



set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;%lib1%\*
set CLASSPATH=%CLASSPATH%;%lib2%\*
C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% gov.nih.nci.evs.restapi.util.CSVFileConverter GO_to_NCIt_Mapping_1.1.csv
C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% util.Mapping2TTLRunner GO_to_NCIt_Mapping_1.1.txt

set CLASSPATH=%OCP%
