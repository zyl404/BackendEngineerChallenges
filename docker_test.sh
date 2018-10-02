if [ -d target ]; then
    rm -rf target
fi

mvn package

docker build -t redis-demo .
docker run \
    -e REDIS_HOST=host.docker.internal \
    -p 8080:8080 redis-demo
