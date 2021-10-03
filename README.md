## Queuing-Service

### How to use
**Start the application** ```sbt run```

**Publish queries to the queueing service**
```sh
curl -X POST -H "Content-Type: application/json" -d '{"uris": ["http://localhost:4242/order?q=123456789,987654321,123456783", "http://localhost:4242/order?q=44444444", "http://localhost:4242/product?q=123456789,987654321,123456783", "http://localhost:4242/product?q=44444444"]}' http://localhost:4242/queue/publish
```

**Subscribe to the queing service to get results**
```sh
curl http://localhost:4242/queue/subscribe
```
If the queue is not full at time of subscribing, will wait 5 seconds before returning a response, and then wipe the queue.

#### Other Endpoints
```sh
curl http://localhost:4242/order?q=$CustomerNumbers
```
```sh
curl http://localhost:4242/product?q=$CustomerNumbers
```
Takes a list of **CustomerNumbers** as a parameter, which is a 9 digit number. Will simply ignore invalid numbers.
