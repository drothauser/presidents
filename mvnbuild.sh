#!/bin/bash
mvn clean install site -DreuseForks=false -Pcoverage-merge -Pbuild-standalone -U
docker build -t presidents .
