docker-compose build
kubectl apply -f ./database_kubernetes.yaml
sleep 7s
kubectl apply -f ./retailproject_kubernetes.yaml