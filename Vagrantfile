
# -*- mode: ruby -*-
# vi: set ft=ruby :

BOX_IMAGE = "ubuntu/trusty64"
NODE_COUNT = 3

Vagrant.configure("2") do |config|

  config.vm.define "server" do |server|
    server.vm.box = BOX_IMAGE
    server.vm.hostname = "server"
    server.vm.network :private_network, ip: "10.0.0.10"
    server.vm.provider "virtualbox" do |vb|
      vb.memory = "2048"
      vb.cpus =  "2"
    end
    server.vm.synced_folder "./Server", "/home/Server", :owner => "vagrant"
  end
  
  (1..NODE_COUNT).each do |i|
    config.vm.define "client#{i}" do |client|
      client.vm.box = BOX_IMAGE
      client.vm.hostname = "client#{i}"
      client.vm.network :private_network, ip: "10.0.0.#{i + 10}"
      client.vm.provider "virtualbox" do |vb|
        vb.memory =  "1024"
        vb.cpus = "2"
      end
      client.vm.synced_folder "./Client", "/home/Client", :owner => "vagrant"
    end
  end

  config.vm.provision "shell", path: "provisioning.sh"
end