#!/bin/sh
if [ "$1" = "-c" ] || [ "$1" = "-C" ] || [ "$1" = "--help" ] || [ "$1" = "--HELP" ]
  then if [ "$JAVA_HOME" != "" ]
  then "$JAVA_HOME/bin/java" -Xms8m -Xmx32m -jar `dirname $0`/cyclesmod.jar $1 $2 $3
  else java -Xms8m -Xmx32m -jar `dirname $0`/cyclesmod.jar $1 $2 $3
  fi
else
  if [ "$JAVA_HOME" != "" ]
  then "$JAVA_HOME/bin/java" -XstartOnFirstThread -Xms8m -Xmx32m -jar `dirname $0`/cyclesmod.jar $1 >/dev/null 2>&1 &
  else java -XstartOnFirstThread -Xms8m -Xmx32m -jar `dirname $0`/cyclesmod.jar $1 >/dev/null 2>&1 &
  fi
  osascript -e 'tell application "Terminal" to quit' &
  exit
fi
