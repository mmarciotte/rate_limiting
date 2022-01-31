build:
	mvn -f message-server/pom.xml -q -DoutputDirectory=message-server/target clean package
	docker-compose up --build
clean:
	mvn -q clean
down:
	docker-composer down
