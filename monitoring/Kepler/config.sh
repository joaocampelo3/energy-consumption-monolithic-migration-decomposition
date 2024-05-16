cd ../../KEPLER_TOOL/kepler
make build-manifest OPTS="PROMETHEUS_DEPLOY"
kubectl apply -f _output/generated-manifest/deployment.yaml
kubectl port-forward --address localhost -n kepler service/kepler-exporter 9102:9102 & kubectl port-forward --address localhost -n monitoring service/prometheus-k8s 9090:9090