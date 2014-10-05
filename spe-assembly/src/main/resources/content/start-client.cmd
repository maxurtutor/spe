set JAVA_OPTS=-Xmx1024M -Xms1024M -Xss1M -XX:+UseParallelGC
set SPE_CLASSPATH=.\lib\*;.\conf

"%JAVA_HOME%"\bin\java %JAVA_OPTS% %JMX_OPTS% -cp "%SPE_CLASSPATH%" org.maxur.spe.client.TestClient