#!/bin/bash
runfile="profiling-run.sh"
rm -rf $runfile
touch $runfile
folder="profile"
pwd
for d in * ; do
    if [ -d $d ]; then
		echo "d=$d"
        rm -rf $d/$folder
        mkdir $d/$folder
        cd $d
        echo "Going into ..."
        pwd

		echo "cd $d" >> ../$runfile
		i=0
		while read -r line
		do
#			if [ $i -lt 20 ]; then
		        echo "mkdir $folder/$i/" >> ../$runfile
                echo "./$d $line " >> ../$runfile
		        echo "gcov -b -c $d" >> ../$runfile
		        echo "mv $d.c.gcov $folder/$i/" >> ../$runfile
		        echo "rm $d.gcda $folder/$i/" >> ../$runfile
                let i++
#			fi
		done < ./universe.txt
		echo "cd .." >> ../$runfile
        cd ..
	fi
done
chmod +x $runfile
./$runfile
