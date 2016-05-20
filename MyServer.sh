#!/bin/bash
filename="output.txt_"$1
>$filename

if [ $# -eq 2 ]
then
echo "Server Registered...!!"
java -cp $PWD/protobuf-java-2.5.0.jar:bin server.GossipServer $1 $2 >> $filename
else
echo "Gossiping!!"
java -cp $PWD/protobuf-java-2.5.0.jar:bin server.GossipServer $1 $2 $3 $4 >> $filename
fi



