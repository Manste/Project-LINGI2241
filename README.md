# Project: Architecture and performance of computer systems
To launch the project first you have to spin up all the virtual machines:
```
vagrant up
```
Then connect to the Server through ssh and launch the application:
```
vagrant ssh server
#In the host machine
export _JAVA_OPTIONS="-Xms512m -Xmx1024m"
cp -rf ../Server/* /home/vagrant
javac ReadFile.java 
javac Server.java 
java Server 4999
```

And after the database is loaded, run the clients:

```
vagrant ssh client1 #There is 2 clients
export _JAVA_OPTIONS="-Xms512m -Xmx1024m"
cp -rf ../Client/* /home/vagrant
javac Client.java 
java Client 4999
```