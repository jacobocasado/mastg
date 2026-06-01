#!/bin/bash
# Print the purpose strings and their user-facing explanations from Info.plist.
# plutil -p renders the plist in a readable form for inspection (see @MASTG-TECH-0154).
plutil -p Info.plist | grep -i "UsageDescription" > output.txt
