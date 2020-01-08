#!/bin/bash
#  Variables for the test
numSub=1
numPub=1
delPub=100
qos=0

for i in {0..2}
do
qos=$i
echo "######## First test starting (qos=$qos) ########"
java -jar Subscriber.jar $numSub $qos &
echo "Waiting for subscribers to start correctly..."
sleep 2
java -jar Publisher.jar $numPub $delPub $qos
done

# L0ultimo stabile era 50 pub e 500 ms di delay
numSub=1
numPub=200
delPub=200
qos=0

for i in {0..2}
do
qos=$i
echo "######## Second test starting (qos=$qos) ########"
java -jar Subscriber.jar $numSub $qos &
echo "Waiting for subscribers to start correctly..."
sleep 2
java -jar Publisher.jar $numPub $delPub $qos
sleep 3
done

echo "Waiting for termination..."
wait

# Dell'ultima ci va doppia se no non stampa
