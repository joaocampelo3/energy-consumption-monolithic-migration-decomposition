kubectl delete namespace monitoring --ignore-not-found
kubectl delete namespace kepler --ignore-not-found
kubectl create namespace monitoring
kubectl create namespace kepler

Grafana\config.bat
Kepler\config.bat

kubectl port-forward --address localhost -n kepler service/kepler-exporter 9102:9102 &
kubectl port-forward --address localhost -n monitoring service/prometheus-k8s 9091:9091 &