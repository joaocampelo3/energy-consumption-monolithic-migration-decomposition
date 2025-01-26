docker compose build --no-cache
kind load docker-image retailproject_user_service:latest -n kepler-cluster
kubectl apply -f ./database_kubernetes.yaml
sleep 7s
kubectl apply -f ./retailproject_kubernetes.yaml