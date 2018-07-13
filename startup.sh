#!/bin/sh

java -Xms512m -Xmx512m -XX:NewSize=170m -XX:MaxNewSize=170m -jar ./build/libs/KakaopayTodoListService-0.0.1.jar
