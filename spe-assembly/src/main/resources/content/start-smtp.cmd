set JAVA_OPTS=-Xmx1024M -Xms1024M -Xss1M -XX:+UseParallelGC

rem start the SMTP server automatically at launch (-s argument)
rem on a different port (-p argument)
rem with no gui (-b argument),
rem -o output_directory_name
"%JAVA_HOME%"\bin\java -jar ./lib/fakeSMTP-1.11-SNAPSHOT.jar -s -p 25 -a 127.0.0.1 -o mailbox