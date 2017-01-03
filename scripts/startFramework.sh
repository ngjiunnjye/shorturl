. ./env.sh

cd ${KAFKA_PATH} 
./zookeeper-server-start.sh ../config/zookeeper.properties &
./kafka-server-start.sh ../config/server.properties &
sleep 5
./kafka-topics.sh --zookeeper localhost:2181 --create --topic url.shortening.command --partitions 2 --replication-factor 1

cd ${H2_PATH} 
java -Xmx64M -Xms64M -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9101

