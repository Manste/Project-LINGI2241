#!/bin/bash
add-apt-repository ppa:openjdk-r/ppa -y
apt-get update

echo "\n----- Installing Java 8 ------\n"
apt-get -y install openjdk-8-jdk
update-alternatives --config java

echo "\n------- Installing monitoring tools ----\n"
sudo apt-get install dstat -y

if $(hostname) == "server":
then
    mkdir -p /home/Server/
else
    mkdir -p /home/Client/
fi