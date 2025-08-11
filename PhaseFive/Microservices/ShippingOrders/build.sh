sudo docker compose build --no-cache
sudo kind load docker-image shippingorder-retailproject_service:latest -n kind
sudo kubectl apply -f ./database_kubernetes.yaml
sleep 7s
sudo kubectl apply -f ./retailproject_kubernetes.yaml