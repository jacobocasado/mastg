#!/bin/bash
# SUMMARY: This script uses semgrep to find all exported activities in the manifest.
NO_COLOR=true semgrep --config rule.yaml ../MASTG-DEMO-XXXA/AndroidManifest_reversed.xml --text -o output.txt
