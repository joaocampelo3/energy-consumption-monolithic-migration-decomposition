sudo docker compose build --no-cache
sudo kind load docker-image rabbitmq-service:latest -n kind
sudo kubectl apply -f ./RabbitMQ.yaml