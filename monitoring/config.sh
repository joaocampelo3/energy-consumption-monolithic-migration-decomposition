kubectl delete namespace monitoring --ignore-not-found
kubectl delete namespace kepler --ignore-not-found
kubectl create namespace monitoring
kubectl create namespace kepler

./Grafana/config.sh
./Kepler/config.sh