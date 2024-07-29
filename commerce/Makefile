all:
	@echo "make up-full        		- Start docker-compose with profile 'full' -> All services including MAIN APP."
	@echo "make up-full build       - Start docker-compose with profile 'full' and build -> All services including MAIN APP."
	@echo "make up-infra       		- Start docker-compose with profile 'infra' -> All services except for MAIN APP."
	@echo "make test-unit      		- Start unit tests"
	@echo "make test-api      		- Start api tests (before must be started: make up-full"
	@echo "make test-integration    - Start integration tests (before must be started: make up-infra"

down:
	docker-compose down;

up-full:
	docker-compose --profile full up

up-infra:
	docker-compose --profile infra up

test-unit:
	docker-compose --profile unit up

test-api:
	docker-compose exec app /bin/sh -c "./mvnw test -Dspring.profiles.active=docker -Dtest=**api.**"


