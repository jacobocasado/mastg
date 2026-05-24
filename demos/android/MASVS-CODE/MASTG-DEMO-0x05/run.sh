#!/bin/bash
# SUMMARY: This script uses semgrep to detect registration for the custom INTERNAL_ACTION intent.
NO_COLOR=true semgrep --config rule.yaml AndroidManifest.xml --text -o output.txt
