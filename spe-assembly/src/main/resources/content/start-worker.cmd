set JAVA_OPTS=-Xmx1024M -Xms1024M -Xss1M -XX:+UseParallelGC
set SPE_CLASSPATH=.\lib\*;.\conf
set JMX_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=2609 -Djava.rmi.server.hostname=localhost

"%JAVA_HOME%"\bin\java %JAVA_OPTS% %JMX_OPTS% -cp "%SPE_CLASSPATH%" org.maxur.spe.Launcher