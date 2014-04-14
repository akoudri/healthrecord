#! /bin/bash

convert -resize 144x144 $1.png $1-xxhdpi.png
convert -resize 96x96 $1.png $1-xhdpi.png
convert -resize 72x72 $1.png $1-hdpi.png
convert -resize 48x48 $1.png $1-mdpi.png


mv $1-xxhdpi.png /home/koudri/AndroidStudioProjects/HealthRecord/app/src/main/res/drawable-xxhdpi/$1.png
mv $1-xhdpi.png /home/koudri/AndroidStudioProjects/HealthRecord/app/src/main/res/drawable-xhdpi/$1.png
mv $1-mdpi.png /home/koudri/AndroidStudioProjects/HealthRecord/app/src/main/res/drawable-mdpi/$1.png
mv $1-hdpi.png /home/koudri/AndroidStudioProjects/HealthRecord/app/src/main/res/drawable-hdpi/$1.png

