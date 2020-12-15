FROM java:8

WORKDIR /var/www/java

COPY . /var/www/java

RUN javac Client.java

EXPOSE 4999

CMD [ "java", "Client" ]