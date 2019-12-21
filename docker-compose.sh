#!/bin/bash

echo "Remove old containers"
docker-compose down

echo "Maven Build"
#mvn clean install $1
[ $? -eq 0 ] || exit $?;

echo "Docker build and start"
if [ -z $2 ];
then docker-compose up --build;
else docker-compose --file $2 up --build;
fi;

[ $? -eq 0 ] || exit $?;


trap "docker-compose kill" INT
