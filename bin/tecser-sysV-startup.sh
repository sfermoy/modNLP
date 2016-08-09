#!/bin/sh
# tecser-sysV-startup.sh
# Created Wed Apr 22 2016 by S Luz luzs@acm.org
#
# Unix System V-style initialization script
# Place it in /etc/init.d and add symlinks to it from the appropriate runlevel 
# directories (e.g. 
# ln -s /etc/init.d/tecser-sysV-startup.sh /etc/rc3.d/K05tecser and
# ln -s /etc/init.d/tecser-sysV-startup.sh /etc/rc3.d/S97tecser

. /etc/init.d/functions
## edit these variable to your server's configuration
TEC_BIN=/disk2/TECv2/software/modnlp-tecser
TEC_SERVER=/usr/bin/java
ARGS="-Xms400m -Xmx450m -jar tecser.jar"
PIDFILE=/var/run/tecser.pid
SYSLOGFILE="/var/log/tec/tecsys.log"
ERRLOG="/var/log/tec/tecsys-error.log"

export PATH=/usr/bin:$PATH

cd $TEC_BIN

case "${1}" in
("start")
        echo "Stating TEC server"
	daemonize -u luzs -p $PIDFILE -o $SYSLOGFILE -e $ERRLOG -c ${TEC_BIN} ${TEC_SERVER} ${ARGS} && success || failure
        #start-stop-daemon --start  --pidfile $PIDFILE --make-pidfile --user luzs --chuid luzs:luzs --chdir ${TEC_BIN} --startas "${TEC_SERVER}" -- ${ARGS} &
        exit $?
        ;;
("stop")
        echo "Stopping TEC server"
	killproc -p $PIDFILE 
#        start-stop-daemon --stop --retry=1 --pidfile $PIDFILE  >/dev/null 2>&1
        exit $?
        ;;
(*)
        echo "Usage: /etc/init.d/tec {start|stop}" >&2
        exit 3
        ;;
esac
