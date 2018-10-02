# Redis client optimization

## Prerequisite to run the project 

* maven installed
* docker installed 
* redis installed and started

## How to run the project

Issue the commend `./docker_test` in current directory. After the printout stop rolling and `Started RedisApplication in * seconds` is displayed in the console, the application is start successfully on `localhost:8080`. 

#### Change Redis server address

If we are using a remote redis instance or redis cluster, please go to the `docker_test.sh` file, change the content of the `REDIS_HOST`.

For example,

```
...
docker run \
    -e REDIS_HOST=<your redis url goes here> \
    -p 8080:8080 redis-demo
```

## APIs 

`/bulk-slow ` This is for challenge A. Using the origianl method to load 100K record to redis. After you call this, it will give the collapse time.

`/bulk-fast` For challenge A. Using pipeline to load 100K record to redis. After this is called, it will give the collapse time.

`/pair/{key}/{value}` For challenge B. Using the original method to set key-value to redis.

`/fast/pair/{key}/{value}` For challenge B. Using async method to set key-value to redis. 

#### Example

Try to run ` wrk -c 20 -d 5 -t 2 http://localhost:8080/pair/key/value` and ` wrk -c 20 -d 5 -t 2 http://localhost:8080/fast/pair/key/value` multipule times to see the difference.

## Explanation 

Generally, redis follows a request/response protocal, which mean a client send a request and wait for the response from server, in a blocking way. 

So in challenge A, if we send the `set key` request one by one. The second request won't execute unitl the we get the response from redis for the first `set key` request. And the third request will wait for the second one, so on so forth. To avoid that kind of latency and network delay between each two request, we could use pipeline. When a requet is emitted by the client, instead of pushing the request immediately to server, we queue the request in a pipeline. After all requests get queued in the pipeline, we flush the pipeline to redis server as a big request. Thus we only suffer one RTT here.

Same principle is applied to chanllenge B. In a blocking model, a web server send a `set` request to redis, it will wait for the redis response before the web server  returns to its client, which is unnecessary, so we apply the async non-blocking task here, which means after the web server emitted the request to redis, it will return immediately without waiting for the response from redis. 

## Performace

The nonoptimal solution for bulk insert 100K records to redis takes 3s.

The optimal solution for bulk insert 100K records takes less then 1s.

If we issue this commend `wrk -c 20 -d 5 -t 2 http://host:8080/pair/key/value`

The throughput of nonoptimal solution is around 2K. However the throghput of optimal soluation is 3K+.