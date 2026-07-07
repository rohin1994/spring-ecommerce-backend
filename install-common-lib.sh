#!/usr/bin/env bash
set -euo pipefail
export JAVA_HOME="${JAVA_HOME:-/home/rohinr/.jdks/jdk-25.0.3+9}"
export PATH="$JAVA_HOME/bin:$PATH"
cd "$(dirname "$0")/common-lib"
../service-discovery/mvnw -q install -DskipTests
