#!/bin/bash
# compile faulty versions
for d in * ; do
	if [ -d $d ]; then
		cd $d
		# ogininal versions
		# fixing replace.c
		if [ $d = replace ]; then
			sed -i 's/getline/getoneline/g' $d.c
		fi			
		if [ $d = totinfo -o $d = replace ]; then
			gcc -g -o $d $d.c -lm -fprofile-arcs -ftest-coverage
		else
			gcc -g -o $d $d.c -fprofile-arcs -ftest-coverage
		fi
		
		# faulty versions
		i=1
		while [ -d v$i ]; do
			cd v$i
			# fixing replace.c
			if [ $d = replace ]; then
				sed -i 's/getline/getoneline/g' $d.c
			fi			
			if [ $d = totinfo -o $d = replace ]; then
				gcc -g -o $d $d.c -lm
			else
				gcc -g -o $d $d.c	
			fi
			cd ..
			let i++
		done
		cd ..
	fi
done


