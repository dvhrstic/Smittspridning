#!/bin/bash
# Basic while loop
S=(0.04 0.042 0.044 0.046 0.048 0.05 0.052 0.054 0.056 0.058 0.06 0.062 0.064 0.066 0.068 0.7)

i=1
while [ $i -le 16 ]
do
	j=1
	echo ${S[i-1]}
	output=""
	fileName="${S[i-1]}.csv"
	RANDOM=57
	while [ $j -le 100 ]
	do	
		seed=$RANDOM
		#echo ${S[i]}
		NEWLINE=$'\n'
		curr_output=$(java Contagion 40 ${S[i-1]} 0.0 6 9 1 20,20 $seed)
		output+=$curr_output
		#echo $curr_output
		#echo $seed
		#echo $j
		output+=${NEWLINE}
		((j+=1))
	done
	echo "$output" > $fileName
	((i+=1))
done