#!/bin/bash
# d - program
runfile="program-run.sh"
rm -rf $runfile
touch $runfile
origin="origin"
for d in * ; do
	if [ -d $d ]; then
		echo "cd $d" >> $runfile
		cd $d
# now in tcas, printtoken, ...
		rm -rf $origin
		mkdir $origin
		for criteria in STATEMENT BRANCH COMBINATION; do
			for method in RANDOM TOTAL ADDITIONAL; do
				# prepare output files, one per (version, criteria, method)
				touch $origin/$criteria-$method-output.txt
				i=0
				while [ -d v$i ]; do
					rm -f ./v$i/$criteria-$method-output.txt
					touch ./v$i/$criteria-$method-output.txt
					let i++
				done

				while read line; do
					# original version
					echo "./$d $line >> ./$origin/$criteria-$method-output.txt" >> ../$runfile

					# faulty versions
					j=1
					while [ -d v$j ]; do
						echo "./v$j/$d $line >> ./v$j/$criteria-$method-output.txt" >> ../$runfile
						let j++
					done
				done < ./testsuite/$criteria\_$method.txt
			done
		done
# leaving folder
		cd ..
		echo "cd .." >> $runfile
	fi
done
chmod +x $runfile
./$runfile
