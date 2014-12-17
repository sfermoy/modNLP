#!/bin/sh
# tecser.sh
# Created Wed Apr 22 2009 by S Luz luzs@cs.tcd.ie
# $Id$
# $Log$

## edit these for your server
TEC_BIN=/home/administrador/concECPC/EPEN-2.0/bin/modnlp-tecser-0.0.5-bin
TEC_SERVER=/usr/bin/java
ARGS="-Xms400m -Xmx450m -jar tecser.jar"
PIDFILE=/var/run/concECPCenser.pid
#export PATH=/disk3/contrib/jre1.6.0_07/bin:$PATH

cd $TEC_BIN

case "${1}" in
("start")
        echo "Stating concECPC EN server"
        start-stop-daemon --start  --pidfile $PIDFILE --make-pidfile --user administrador --chuid administrador:administrador --chdir ${TEC_BIN} --startas "${TEC_SERVER}" -- ${ARGS} &
        exit $?
        ;;
("stop")
        echo "Stopping concECPC EN server"
        start-stop-daemon --stop --retry=1 --pidfile $PIDFILE 
#>/dev/null 2>&1
        exit $?
        ;;
(*)
        echo "Usage: /etc/init.d/concECPCen {start|stop}" >&2
        exit 3
        ;;
esac
