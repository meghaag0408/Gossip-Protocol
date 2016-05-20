#!/bin/bash
if [ $# -eq 6 ]
then
 	type1=$2
	type2=$4
	filename=$6

elif  [ $# -eq 4 ]
then
 	
	if [ "$1" == "-p" ]
	then
		type1=$2
		type2=10
		filename=$4

	elif [ "$1" == "-n" ]
	then
		type1=5
		type2=$2
		filename=$4
	fi

elif [ $# -eq 2 ]
then
 	type1=5
	type2=10
	filename=$2

fi

if test -e serialise
	then
		x=1
else
	mkdir serialise
fi

if test -e serialise/1.txt
	then
		x=1
else
	touch serialise/{1..20}.txt
fi



for i in `seq 1 $type2`;
        do
	   if [ $i -le $type1 ]; 
		then
               	./MyServer.sh $i $type2 "-i" $filename &
            else
          	./MyServer.sh $i $type2 &
	   fi
        done 



