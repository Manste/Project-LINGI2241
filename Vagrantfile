BOX_IMAGE = "ubuntu/trusty64"
NODE_COUNT = 2

Vagrant.configure("2") do |config|
  config.vm.define "server" do |subconfig|
    subconfig.vm.box = BOX_IMAGE
    subconfig.vm.hostname = "server"
    subconfig.vm.network :private_network, ip: "10.0.0.10"
    subconfig.vm.provision "file", source: "./Server", destination: "$HOME/Server"
  end
  
  (1..NODE_COUNT).each do |i|
    config.vm.define "client#{i}" do |subconfig|
      subconfig.vm.box = BOX_IMAGE
      subconfig.vm.hostname = "client#{i}"
      subconfig.vm.network :private_network, ip: "10.0.0.#{i + 10}"
      subconfig.vm.provision "file", source: "./Client", destination: "$HOME/Client"
    end
  end

  config.vm.provision "shell", path: "provisioning.sh"
  config.vm.network :forwarded_port, host: 5000, guest: 5000
end