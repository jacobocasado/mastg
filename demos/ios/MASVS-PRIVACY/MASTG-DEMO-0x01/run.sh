#!/bin/bash
# Extract and display purpose strings from Info.plist
grep -E "Usage.*Description|NS.*UsageDescription" Info.plist | grep -v "^--" > output.txt
# Alternative using plutil if available:
# plutil -p Info.plist | grep -i "usage" >> output.txt
