# Project: Architecture and performance of computer systems
To launch the project first you have to make sure that the __data__ folder exists in the __Server__ directory:
```
mkdir -p Server/data
cp path_to_database_file/dbdata.txt Server/data
```
Then spin up all the virtual machines:
```
vagrant up
```
Now you can connect to the Server through ssh and launch the application:
```
vagrant ssh server
#In the virtual machine
nohup dstat -tpcdrmgln --time --cpu --mem --load --output /home/Server/serverCPUUsage.csv &
nohup dstat -tcmsn -N eth1  --output /home/Server/serverNetworkUsage.csv &
export _JAVA_OPTIONS="-Xms512m -Xmx1024m"
cp -rf ../Server/* /home/vagrant
javac ReadFile.java 
javac Server.java 
java Server 4999
```

And after the database is loaded(You should see "Database Loaded!!!" in the output of the server), you can run the clients:

```
vagrant ssh client1 #There are 2 clients so you have to connect also to the client2
nohup dstat -tpcdrmgln --time --cpu --mem --load --output /home/Client/$(hostname)CPUUsage.csv &
nohup dstat -tcmsn -N eth1  --output /home/Client/$(hostname)NetworkUsage.csv &
export _JAVA_OPTIONS="-Xms512m -Xmx1024m"
cp -rf ../Client/* /home/vagrant
javac Client.java 
java Client 10.0.0.10 4999 /home/Client/serviceTime$(hostname).csv
```
If you want to destroy all the virtual machines:
```
#In the host machine
vagrant destroy up
```