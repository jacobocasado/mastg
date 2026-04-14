#!/bin/bash

set -euo pipefail

rm -rf jadx-output extracted
mkdir -p extracted

jadx -d jadx-output app-release.apk || true
unzip -o -j app-release.apk 'lib/arm64-v8a/librootcheck.so' -d extracted > /dev/null

r2 -q -e scr.color=0 -e bin.relocs.apply=true \
  -c 'iS~dynsym,symtab,rodata,text' \
  -c 'izz~su' \
  -c 'afl~Java_org_owasp_mastestapp_MastgTest_findRootArtifactPath' \
  -c 'pdf @ sym.Java_org_owasp_mastestapp_MastgTest_findRootArtifactPath' \
  -A extracted/librootcheck.so > output.txt
