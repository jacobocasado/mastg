#!/bin/bash
# SUMMARY: This script uses semgrep to find all exported activities in the manifest.
NO_COLOR=true semgrep --lang=xml --pattern='<activity ... android:exported="true" ... />' AndroidManifest_reversed.xml --text -o output.txt
