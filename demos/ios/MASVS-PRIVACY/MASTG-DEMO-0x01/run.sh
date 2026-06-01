#!/bin/bash
plutil -p Info.plist | grep -i "UsageDescription" > output.txt
