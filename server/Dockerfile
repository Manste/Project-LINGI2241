FROM java:8

WORKDIR /var/www/java

COPY . /var/www/java

RUN javac Server.java

EXPOSE 4999

CMD ["java", "Server"]