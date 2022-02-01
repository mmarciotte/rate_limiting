build:
	mvn -q clean package
	docker-compose up --build
clean:
	mvn -q clean
down:
	docker-compose down
