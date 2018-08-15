#!/bin/bash


echo "removing old containers"
docker-compose down

echo "removing storages"


echo "Build"
mvn clean install -f ../pom.xml $1
[ $? -eq 0 ] || exit $?; 

docker-compose build
[ $? -eq 0 ] || exit $?; 


echo "Start"
docker-compose up
[ $? -eq 0 ] || exit $?; 


trap "docker-compose kill" INT
