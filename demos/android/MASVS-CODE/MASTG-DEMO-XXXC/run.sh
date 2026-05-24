#!/bin/bash
# SUMMARY: This script uses semgrep to detect implicit intents that leak sensitive data via extras.
NO_COLOR=true semgrep --config rule.yaml ../MASTG-DEMO-XXXA/MastgTest_reversed.java --text -o output.txt
