kubectl delete namespace monitoring --ignore-not-found
kubectl delete namespace kepler --ignore-not-found
sudo kind delete kepler-cluster
export CLUSTER_NAME="kepler-cluster"
sudo kind create cluster --name=$CLUSTER_NAME --config=./local-cluster-config.yaml