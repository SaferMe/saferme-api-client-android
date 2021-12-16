#!/bin/bash

# Define output colours
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Extract information
jacocoReportPath='app/build/reports/jacocoTestReport/jacocoTestReport.xml'

readValues() {
  missed=$(xmllint --xpath "string(/report/counter[@type=\"$1\"]/@missed)" "$2")
  covered=$(xmllint --xpath "string(/report/counter[@type=\"$1\"]/@covered)" "$2")
  # shellcheck disable=SC2154
  numerator=$((covered*100 | bc))
  denominator=$((covered + missed))
  result=$((numerator / denominator ))
}

getCoverage() {
  readValues "INSTRUCTION" $jacocoReportPath
  instructionCoverage=$result

  readValues "BRANCH" $jacocoReportPath
  branchCoverage=$result

  coverage=$((instructionCoverage > branchCoverage ? instructionCoverage : branchCoverage))
}

getCoverage

# Output coverage
# shellcheck disable=SC2059
printf "${BLUE}=== SaferMe Api code coverage: "
printf %.1f $coverage
printf "%% === \n${NC}"

if [ $coverage -lt $MIN_COVERAGE ]
then
    # shellcheck disable=SC2059
    printf "${RED}Insufficient code coverage. Failing CI verification${NC}"
    exit 1
else
    # shellcheck disable=SC2059
    printf "${GREEN}Sufficient code coverage. Passed CI verification${NC}"
fi
