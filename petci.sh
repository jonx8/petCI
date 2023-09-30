#!/bin/bash
# shellcheck disable=SC2046
exec java -jar $(dirname "$(realpath "$0")")/target/petci-1.1.0.jar "$@"
