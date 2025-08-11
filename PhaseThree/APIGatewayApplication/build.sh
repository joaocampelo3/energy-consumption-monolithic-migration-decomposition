sudo docker compose build --no-cache
sudo kind load docker-image apigatewayapplication-retailproject_service:latest -n kind
sudo kubectl apply -f ./retailproject_apigateway_kubernetes.yaml