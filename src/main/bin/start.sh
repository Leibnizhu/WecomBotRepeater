#!/bin/sh
export LC_ALL=zh_CN.utf8
export LANG=zh_CN.utf8
export LC_CTYPE=zh_CN.UTF-8

if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi

LOG_PATH=./logs
CONFIG_FILE=./config.json
JAVA=${JAVA_HOME}/bin/java
JAR=$(ls | grep WecomBotRepeater | grep 'fat.jar' | grep -v original)
CLASS=io.github.leibnizhu.repeater.MainLauncher
JAVA_OPTS="$JAVA_OPTS -server -Xms128m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=500 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError "

echo $JAVA $JAVA_OPTS -XX:OnOutOfMemoryError='chmod 644 *.hprof' -DLOG_HOME=$LOG_PATH -jar $PWD/$JAR $CONFIG_FILE
$JAVA $JAVA_OPTS -XX:OnOutOfMemoryError='chmod 644 *.hprof' -DLOG_HOME=$LOG_PATH -jar $PWD/$JAR $CONFIG_FILE
