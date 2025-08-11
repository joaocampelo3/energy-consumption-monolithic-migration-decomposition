kind delete cluster -n kind
export CLUSTER_NAME="kepler-cluster"
kind create cluster --name=$CLUSTER_NAME --config=./local-cluster-config.yaml