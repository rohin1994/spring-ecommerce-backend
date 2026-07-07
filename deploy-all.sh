#!/usr/bin/env bash
set -euo pipefail
export JAVA_HOME="${JAVA_HOME:-/home/rohinr/.jdks/jdk-25.0.3+9}"
export PATH="$JAVA_HOME/bin:$PATH"
ROOT="$(cd "$(dirname "$0")" && pwd)"
NAMESPACE="${NAMESPACE:-personal}"

echo "=== Ensuring namespace $NAMESPACE ==="
kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

echo "=== Applying infrastructure (postgres, mongodb, mysql, redpanda) ==="
kubectl apply -f "$ROOT/k8s/personal/infra/" -n "$NAMESPACE"

echo "=== Waiting for infrastructure pods ==="
kubectl wait --for=condition=ready pod -l app=postgres -n "$NAMESPACE" --timeout=180s || true
kubectl wait --for=condition=ready pod -l app=mongodb -n "$NAMESPACE" --timeout=180s || true
kubectl wait --for=condition=ready pod -l app=mysql -n "$NAMESPACE" --timeout=300s || true
kubectl wait --for=condition=ready pod -l app=redpanda -n "$NAMESPACE" --timeout=180s || true

SERVICES=(
  service-discovery
  auth-service
  product-service
  user-service
  inventory-service
  order-service
  payment-service
  notification-service
  api-gateway
)

for SERVICE in "${SERVICES[@]}"; do
  echo ""
  echo "=========================================="
  echo "=== Deploying $SERVICE ==="
  echo "=========================================="
  "$ROOT/$SERVICE/deploy.sh" || { echo "FAILED: $SERVICE"; exit 1; }
done

echo ""
echo "=== All deployments complete ==="
kubectl get pods,svc -n "$NAMESPACE"
