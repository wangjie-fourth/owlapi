#!/usr/bin/env bash

echo ">>> git pull"
git clone https://github.com/wangjie-fourth/owlapi.git

PORT=8081

echo "kill old service"
kill -9 $(lsof -n -P -t -i:$PORT)

echo ">>> mvn clean package"
mvn clean package -Dmaven.test.skip=true

echo "cd target"
cd target

echo ">>> start new service"
java -jar demo-0.0.1-SNAPSHOT.jar > app.log &