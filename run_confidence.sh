#!/bin/bash
# Basic while loop
S=(0.01 0.015 0.02 0.025 0.03 0.035 0.04 0.045 0.05 0.055 0.06 0.065 0.07 0.075 0.08 0.085 0.09 0.095 0.1)

i=1

while [ $i -le 19 ]
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