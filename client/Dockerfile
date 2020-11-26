FROM ubuntu:18.04

WORKDIR /work

RUN apt-get update && \
    apt-get install iproute2 iputils-ping software-properties-common -y 

# Add the JDK 8 and accept licenses (mandatory)
RUN add-apt-repository ppa:webupd8team/java && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections

# Install Java 8
RUN apt-get update && \
    apt-get --yes --no-install-recommends install oracle-java8-installer

ADD ./Client.java .

RUN javac Client.java

CMD ["java", "Client"]