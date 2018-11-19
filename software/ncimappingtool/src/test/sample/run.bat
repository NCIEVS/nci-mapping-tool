set OCP=%CLASSPATH%
set lib1=..\lib

set lib2=..\extlib

set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;%lib1%\*
set CLASSPATH=%CLASSPATH%;%lib2%\* 

rem C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% MappingUtils https://sparql-evs-dev.nci.nih.gov/sparql   http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl DOID_Term.txt


rem C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% MappingUtils https://sparql-evs-dev.nci.nih.gov/sparql http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl DOID_Term.txt


rem C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% TestMappingUtils https://sparql-evs-dev.nci.nih.gov/sparql   http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl DOID_Term.txt
rem C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% MappingHelper https://sparql-evs-dev.nci.nih.gov/sparql http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl test.txt

rem C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% MappingUtils https://sparql-evs-dev.nci.nih.gov/sparql   http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl ICD10PreferredTerm.txt
rem C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% MappingUtils ICD10PreferredTerm.txt
rem C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% MappingHelper 

C:\jdk1.8.0_45\bin\java -d64 -Xms512m -Xmx4g -classpath %CLASSPATH% gov.nih.nci.evs.mapping.util.MappingUtils DOID_Term.txt
set CLASSPATH=%OCP%
