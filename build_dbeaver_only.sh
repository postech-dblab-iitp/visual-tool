#!/usr/bin/env bash
##########################
# download ant from :
#   https://ant.apache.org
SHELL_PATH="$( cd "$( dirname "$0" )" && pwd -P )"
MVN="`which mvn`" 
if [ ! -z "${MAVEN_HOME}" ]; then
  echo MAVEN_HOME: ${MAVEN_HOME}
  MVN="${MAVEN_HOME}/bin/mvn" 
fi 

if [ -z "$MVN" ]; then
  echo maven not found.
  exit 1 
else
  $MVN -f $SHELL_PATH/pom.xml clean package
fi
