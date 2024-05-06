docker-compose build
kubectl apply -f ./database_kubernets.yaml
sleep 7s
kubectl apply -f ./retailproject_kubernets.yaml