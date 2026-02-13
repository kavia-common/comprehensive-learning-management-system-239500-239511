#!/bin/bash
cd /home/kavia/workspace/code-generation/comprehensive-learning-management-system-239500-239511/lms_backend
./gradlew checkstyleMain
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

