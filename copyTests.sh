#!/bin/sh

ls -la
for x in core core-excel core-text core-spring
do
  echo "Copiando $x"
  export y=${x}/src/test/
  cp -R userguide/specrunner-userguide-${y}java/* specrunner-${y}java/
  find specrunner-${y}java/
  cp -R userguide/specrunner-userguide-${y}resources/* specrunner-${y}resources/
  find specrunner-${y}resources/
done;
