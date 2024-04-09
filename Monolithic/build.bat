docker-compose build
kubectl apply -f .\database_kubernets.yaml
timeout 7 > nul
kubectl apply -f .\retailproject_kubernets.yaml