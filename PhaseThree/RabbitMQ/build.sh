sudo docker compose build --no-cache
sudo kind load docker-image rabbitmq-rabbitmq:latest -n kind
sudo kubectl apply -f ./RabbitMQ.yaml