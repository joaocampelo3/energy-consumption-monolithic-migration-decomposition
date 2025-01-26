docker compose build --no-cache
kind load docker-image retailproject_loadbalancer_service:latest -n kepler-cluster
kubectl apply -f ./retailproject_loadbalancer_kubernetes.yaml