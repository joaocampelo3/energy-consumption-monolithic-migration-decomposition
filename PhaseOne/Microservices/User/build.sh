sudo docker compose build --no-cache
sudo kind load docker-image .* -n kind
sudo kubectl apply -f .*
sleep 7s
sudo kubectl apply -f ./retailproject_kubernetes.yaml