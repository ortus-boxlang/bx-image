#!/usr/bin/env bash
# Fails when a library needs a newer glibc than the baseline. Usage: check-glibc.sh LIBRARY BASELINE (e.g. 2.28)
set -euo pipefail
lib="${1:?library}"; base="${2:?glibc baseline}"
need="$(objdump -T "$lib" | grep -oE 'GLIBC_[0-9]+(\.[0-9]+)+' | sed 's/GLIBC_//' | sort -Vu | tail -1)"
echo "$lib needs glibc ${need:-none} (baseline $base)"
[[ -z "$need" || "$(printf '%s\n%s\n' "$need" "$base" | sort -V | tail -1)" == "$base" ]] || { echo "error: needs glibc $need, newer than $base" >&2; exit 1; }
