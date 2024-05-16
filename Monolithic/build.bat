docker-compose build
kubectl apply -f .\database_kubernetes.yaml
timeout 7 > nul
kubectl apply -f .\retailproject_kubernetes.yaml