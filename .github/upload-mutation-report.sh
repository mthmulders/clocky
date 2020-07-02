#!/usr/bin/env bash
set -Euo pipefail

trap "rm mutation-testing-report.json" ERR

reportJsLocation=$(find . -name "report.js")
echo Found report.js at ${reportJsLocation}
reportJsContent=$(<${reportJsLocation})
report="${reportJsContent:60}"
echo "${report}" > mutation-testing-report.json

BASE_URL="https://dashboard.stryker-mutator.io"
PROJECT="github.com/${GITHUB_REPOSITORY}"
VERSION=${GITHUB_REF#refs/heads/}

echo Uploading mutation-testing-report.json to ${BASE_URL}/api/reports/${PROJECT}/${VERSION}
curl -X PUT \
  ${BASE_URL}/api/reports/${PROJECT}/${VERSION} \
  -H "Content-Type: application/json" \
  -H "Host: dashboard.stryker-mutator.io" \
  -H "X-Api-Key: ${API_KEY}" \
  -d @mutation-testing-report.json

rm mutation-testing-report.json