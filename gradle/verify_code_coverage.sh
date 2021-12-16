#!/bin/bash

# Define output colours
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Extract information
jacocoReportPath='app/build/reports/jacocoTestReport/jacocoTestReport.xml'

readValues() {
  missed=$(xmllint --xpath "string(/report/counter[@type=\"$1\"]/@missed)" $2)
  covered=$(xmllint --xpath "string(/report/counter[@type=\"$1\"]/@covered)" $2)
  numerator=$(expr $covered*100 | bc)
  denominator=$(expr $covered + $missed)
  result=$(expr $numerator / $denominator)
}

getCoverage() {
  readValues "INSTRUCTION" $jacocoReportPath
  instructionCoverage=$result

  readValues "BRANCH" $jacocoReportPath
  branchCoverage=$result

  coverage=$(($instructionCoverage > $branchCoverage ? $instructionCoverage : $branchCoverage))
}

getCoverage

# Output coverage
printf "${BLUE}=== SaferMe API code coverage: "
printf %.1f $coverage
printf "%% === \n${NC}"

## Validate code coverage
if [ $coverage < $MIN_COVERAGE ]
then
    printf "${RED}Insufficent code coverage. Failing CI verification${NC}"
    exit 1
else
    printf "${GREEN}Sufficent code coverage. Passed CI verification${NC}"
fi
