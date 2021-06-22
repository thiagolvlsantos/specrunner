#!/bin/sh
ls -la
for x in core core-excel core-text core-spring
do
  echo "Copiando $x"
  cp -R userguide/specrunner-userguide-${x}/src/test/java/* specrunner-${x}/src/test/java/
  find specrunner-${x}/src/test/java/
  cp -R userguide/specrunner-userguide-${x}/src/test/resources/* specrunner-${x}/src/test/resources/
  find specrunner-${x}/src/test/resources/
done;
