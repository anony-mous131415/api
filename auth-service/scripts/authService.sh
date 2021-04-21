#!/bin/bash
#


# Source function library.
. /etc/rc.d/init.d/functions

PROG="io.revx.auth.AuthServiceMainApplication"

if [ -z $JAVA_HOME ];then
	echo -e "\nJAVA_HOME is not set using as JAVA_HOME\n"
	export JAVA_HOME=/usr/java/jdk1.8.0_144
fi

SERVICE_HOME=/atom/auth-service
PID_FILE=$SERVICE_HOME/authService.pid
LOCKFILE=$SERVICE_HOME/authService.lock
STOP_TIMEOUT=3
STATUS_FILE=$SERVICE_HOME/status/ztstatus.txt
SLEEP_SECONDS=5
RETVAL=0
JAVA_OPTS="-server -Xms128m -Xmx512m -XX:+UseG1GC"

start() {
        echo -n $"Starting $PROG: "
        PID=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}")
        PROGSTATUS=`echo $?`
        PROCESS_COUNT=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}"|wc -l)
        if [[ $PID -ne 0 ]] || [[ $PROCESS_COUNT -ge 1 ]];then 
                echo "is already running..."
         else
                export APP_HOME=$SERVICE_HOME/current
                cd $APP_HOME
                CP=./conf
                FILES=./lib/*.jar
                for file in $FILES;do
                        CP=$CP:$file
                done
                FILES=./conf/*.*
                for file in $FILES;do
                        CP=$CP:$file
                done
                #echo $CP
                sudo -u atomex  $JAVA_HOME/bin/java  $JAVA_OPTS -cp $CP -Djasypt.encryptor.password=mySecretKey@123 $PROG > $SERVICE_HOME/logs/authServiceConsoleOutput.log 2>&1&
                sleep 5;
                PID=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}")
                RETVAL=$?
                sleep 3
                echo "$PROG started with pid $PID"
                echo $PID > $PID_FILE
                [ $RETVAL = 0 ] && touch ${LOCKFILE}
                return $RETVAL
        fi
}

addlb() {
SANITY_STATUS_CHK=0
while [[ $SANITY_STATUS_CHK -lt 5 ]];
do
        STATUS_CODE=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}")
        if [[ -n $STATUS_CODE ]];
        then
                echo "Api  SERVICE is UP, adding to LB";
                SANITY_STATUS_CHK=5;
                if [ -f ${STATUS_FILE} ];
                then
                        echo "Status file ${STATUS_FILE} already exist"
                        exit
                else
                        touch ${STATUS_FILE}
                if [ -f ${STATUS_FILE} ];
                then
                        echo "Added in LB... Created status file ${STATUS_FILE}"
                fi
        fi
        else
                echo "$SANITY_STATUS_CHK) Service Check Failed Re Trying in 10 Sec..."
                sleep 10
                SANITY_STATUS_CHK=$((SANITY_STATUS_CHK+1));
        fi
done

}

removelb() {
if [ ! -f ${STATUS_FILE} ];
then
        echo "Status ${STATUS_FILE} file does not exist"
else
        echo "Removing from LB ${STATUS_FILE}"
        rm -vf ${STATUS_FILE}
        if [ "$?" -ne "0" ]; then
        echo "Sorry, cannot remove from LB"
        exit 1
        fi
fi
}


stop() {

echo -n $"Stopping $PROG: "
PID=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}")
PROGSTATUS=`echo $?`
PROCESS_COUNT=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}" |wc -l)
if [[ $PID -ne 0 ]] || [[ $PROCESS_COUNT -ge 1 ]];

then
        echo "Waiting for ${SLEEP_SECONDS} seconds"
        sleep ${SLEEP_SECONDS}
        kill $PID
        RETVAL=$?
        sleep 5
        PID=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}")
        while [[ $PID -ne 0 ]]; do
        sleep 5
        PID=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}")
        done
else
        echo "already stopped"
fi
[ $RETVAL = 0 ] && rm -f ${LOCKFILE} && rm -f ${PID_FILE}
        [ $RETVAL = 0 ]
sleep 1
}

stopforcefully() {

echo -n $"Stopping $PROG forcefully:"
killproc -p $PID_FILE ${STOP_TIMEOUT}
RETVAL=$?
        echo
        [ $RETVAL = 0 ]
sleep 1
}

# See how we were called.
case "$1" in
    start)
        start
        ;;
    stop)
        removelb
        stop
        ;;
    stopforcefully)
        removelb
        stopforcefully
        ;;

     status)
        echo "The status of $PROG:"

        if [ -f $PID_FILE ];then status -p $PID_FILE

        else
          PROCESS_COUNT=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}"|wc -l)
          PID=$(${JAVA_HOME}/bin/jps -lV | awk "/$PROG/{print \$1}")
          if [[ $PROCESS_COUNT -ge 1 ]];then echo "is running  $PID";
            else
                echo "$PROG is not running"
          fi

        fi
        ;;
    restart)
        removelb
        stop
        start
        ;;
    restartforcefully)
        removelb
        stopforcefully
        start
        ;;
    removelb)
        removelb
        ;;      
    addlb)
        addlb
        ;;
    *)
        echo $"Usage: $0 {start|stop|stopforcefully|restart|restartforcefully|status|removelb|addlb}"
        RETVAL=3
esac

exit $RETVAL