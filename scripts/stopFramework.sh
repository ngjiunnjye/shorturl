. ./env.sh

cd ${KAFKA_PATH} 
./kafka-server-stop.sh
sleep 10
./zookeeper-server-stop.sh

#stop h2 
h2pid=`ps -ef|grep h2 | grep java | awk '{print $2}'`
kill -9  $h2pid
