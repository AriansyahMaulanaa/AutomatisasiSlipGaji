#!/usr/bin/env bash
# Run SlipGaji Pro (fat JAR)
# Prasyarat: `java` (JDK 17+) tersedia di PATH,
# dan `mvn package -DskipTests` sudah dijalankan sekali.

set -e
cd "$(dirname "$0")"

JAR="target/SlipGajiPro-1.1.0.jar"

if [ ! -f "$JAR" ]; then
    echo "[run.sh] JAR belum ada. Menjalankan 'mvn package -DskipTests'..."
    mvn -q package -DskipTests
fi

# Prefer JAVA_HOME jika di-set, fallback ke `java` di PATH
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_BIN="$JAVA_HOME/bin/java"
else
    JAVA_BIN="java"
fi

exec "$JAVA_BIN" -jar "$JAR" "$@"
