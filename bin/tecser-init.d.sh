#!/bin/sh
# tecser.sh
# Created Wed Apr 22 2009 by S Luz luzs@acm.org
# Debian/Ubuntu style init script.
# 

## edit these for your server
TEC_BIN=/disk2/gok/software/modnlp-tecser
TEC_SERVER=/usr/bin/java
ARGS="-Xms400m -Xmx1000m -jar tecser.jar"
PIDFILE=/var/run/tecser.pid

export PATH=/disk3/contrib/jre1.6.0_07/bin:$PATH

cd $TEC_BIN

case "${1}" in
("start")
        echo "Stating TEC server"
        start-stop-daemon --start  --pidfile $PIDFILE --make-pidfile --user luzs --chuid luzs:luzs --chdir ${TEC_BIN} --startas "${TEC_SERVER}" -- ${ARGS} &
        exit $?
        ;;
("stop")
        echo "Stopping TEC server"
        start-stop-daemon --stop --retry=1 --pidfile $PIDFILE 
#>/dev/null 2>&1
        exit $?
        ;;
(*)
        echo "Usage: /etc/init.d/tec {start|stop}" >&2
        exit 3
        ;;
esac
