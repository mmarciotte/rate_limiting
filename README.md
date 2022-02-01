# Rate Limit Exercise (L2 Coding Challenge)

[Reference](https://thorn-paperback-665.notion.site/L2-Coding-Challenge-f55f26875e1c4871b528f07e109c0e52)

## Design

![image info](docs/architecture.png)

## ADRs

We use micrometer for the metrics because of the easy integration with spring framework

Use redis to store rate limit data to share information between services

Use JUnit 4 to easy mock service components

## Usage

#### Requirements

* java SDK v11
* Docker and docker-compose
* gnu Makefile

#### Run

```
make build
```

#### Get Message:

```
curl 'localhost:8080/message'
```

## Metrics

Access to prometheus panel:

```
http://localhost:9090/
```

Search for metrics such as

```
http_server_requests_seconds_max
endpoint_message_time_seconds_max
service_foass_message_time_seconds_max
service.rate-limiter.allowed.*
```

## Contact

[Matias Marciotte](mailto:mmarciotte@gmail.com)