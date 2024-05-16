kubectl delete -f grafana.yaml --ignore-not-found
kubectl apply -f grafana.yaml --namespace=monitoring