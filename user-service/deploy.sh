#!/usr/bin/env bash
set -euo pipefail
SERVICE=user-service
IMAGE_TAG="${1:-0.0.1-SNAPSHOT}"
NAMESPACE="${NAMESPACE:-personal}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$(dirname "$0")"
export JAVA_HOME="${JAVA_HOME:-/home/rohinr/.jdks/jdk-25.0.3+9}"
"$ROOT/install-common-lib.sh" || true
./mvnw clean package -DskipTests
eval "$(minikube docker-env)"
docker build -t "${SERVICE}:${IMAGE_TAG}" .
helm upgrade --install "${SERVICE}" "$ROOT/helm/${SERVICE}" -n "${NAMESPACE}" --create-namespace -f "$ROOT/helm/${SERVICE}/values-personal.yaml" --set image.tag="${IMAGE_TAG}"
kubectl rollout restart "deployment/${SERVICE}" -n "${NAMESPACE}" || true
kubectl rollout status "deployment/${SERVICE}" -n "${NAMESPACE}" --timeout=300s
