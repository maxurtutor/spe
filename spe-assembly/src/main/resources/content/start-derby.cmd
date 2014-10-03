set JAVA_OPTS=-Xmx1024M -Xms1024M -Xss1M -XX:+UseParallelGC

set SPE_CLASSPATH=.\derby\*;

"%JAVA_HOME%"\bin\java  -cp "%SPE_CLASSPATH%" org.apache.derby.drda.NetworkServerControl