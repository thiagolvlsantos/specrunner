#!/bin/sh

ls -la

mkdir specrunner-ant/build
cp -R userguide/specrunner-userguide-ant/build/* specrunner-ant/build/


for x in core ant 
do
  echo "Copiando $x"
  export y=${x}/src/test/
  mkdir specrunner-${x}/src
  mkdir specrunner-${x}/src/test
  mkdir specrunner-${x}/src/test/java
  cp -R userguide/specrunner-userguide-${y}java/* specrunner-${y}java/
  find specrunner-${y}java/
  mkdir specrunner-${x}/src
  mkdir specrunner-${x}/src/test
  mkdir specrunner-${x}/src/test/resources
  cp -R userguide/specrunner-userguide-${y}resources/* specrunner-${y}resources/
  find specrunner-${y}resources/
done;
