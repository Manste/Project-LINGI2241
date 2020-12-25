#!/bin/bash
add-apt-repository ppa:openjdk-r/ppa -y
apt-get update

echo "\n----- Installing Java 8 ------\n"
apt-get -y install openjdk-8-jdk
update-alternatives --config java

echo "\n------- Installing monitoring tools ----\n"
sudo apt-get install dstat -y
sudo add-apt-repository ppa:wireshark-dev/stable 
sudo apt update
sudo apt -y install wireshark
sudo apt-get install -y tshark

if $(hostname) == "server":
then
    mkdir -p /home/Server/
    dstat --output /home/Server/serverCPUUsage.csv -cdn &
    tshark -i eth1 -r test.pcap -Y "tcp.port == 4999 || udp.port == 4999" -T fields -e frame.number -e frame.len -e frame.time -e eth.src -e eth.dst -e ip.src -e ip.dst -e ip.proto -E header=y -E separator=, -E quote=d -E occurrence=f > /home/Server/serverNetworkUsage.csv &
else
    mkdir -p /home/Client/
    dstat --output /home/Client/$(hostname)CPUUsage.csv -cdn
    tshark -i eth1 -r test.pcap -Y "tcp.port == 4999 || udp.port == 4999" -T fields -e frame.number -e frame.len -e frame.time -e eth.src -e eth.dst -e ip.src -e ip.dst -e ip.proto -E header=y -E separator=, -E quote=d -E occurrence=f > /home/Client/$(hostname)NetworkUsage.csv &
fi

#

