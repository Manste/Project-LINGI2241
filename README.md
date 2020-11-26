# Project: Architecture and performance of computer systems
To launch the project first you have to spin up the containers:
```
docker-compose up -d
```
and then add the latencies(100ms) to outbound traffic on the containers.
```
$ docker exec client tc qdisc add dev eth0 root netem delay 100ms
$ docker exec server tc qdisc add dev eth0 root netem delay 100ms
```