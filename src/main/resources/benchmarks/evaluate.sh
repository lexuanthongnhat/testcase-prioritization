#!/bin/bash
output="evaluation.csv"
rm -f $output
touch $output
echo "#program, criteria, method, #faulty detected" >> $output
for d in * ; do
    if [ -d $d ]; then
		for criteria in STATEMENT BRANCH COMBINATION; do
			for method in RANDOM TOTAL ADDITIONAL; do
				filename=$criteria-$method-output.txt
				origin=$d/origin/$filename
				count=0
				i=1
				while [ -d $d/v$i ]; do
					version=$d/v$i/$filename
					if ! cmp -s "$origin" "$version"; then
						let count++
					fi
					
					let i++
				done
				echo "$d,$criteria,$method,$count" >> $output
			done
		done
	fi
done

