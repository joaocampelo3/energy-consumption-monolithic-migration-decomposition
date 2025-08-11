sudo docker compose build --no-cache
sudo kind load docker-image loadbalancerapplication-retailproject_service:latest -n kind
sudo kubectl apply -f ./retailproject_loadbalancer_kubernetes.yaml