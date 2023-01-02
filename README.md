
---
### Start HTTP Backend service at http://localhost:9002

```
$ gradle build
$ gradle run
$ ...
$ Server started on port: 9001
```
OR with Docker desktop,
```
docker compose -f docker-compose-local.yaml up
```

---
### Local Testing
Test your service by hitting the `/docs` HTTP GET endpoint.
