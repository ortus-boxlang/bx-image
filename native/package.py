#!/usr/bin/env python3
"""Add a prebuilt MatchBox artifact to the existing Gradle-generated module package."""
import hashlib
import json
from pathlib import Path
import shutil
import subprocess
import sys

root = Path(__file__).resolve().parents[1]
package = Path(sys.argv[1]) if len(sys.argv) > 1 else root / "build/module"
target = sys.argv[2] if len(sys.argv) > 2 else next(
    line.split(": ", 1)[1] for line in subprocess.check_output(["rustc", "-vV"], text=True).splitlines() if line.startswith("host: ")
)
subprocess.run(["cargo", "build", "--release", "--target", target, "--target-dir", str(root / "native/target"), "--manifest-path", str(root / "native/Cargo.toml")], check=True)
filename = "bx_image_native.dll" if "windows" in target else "libbx_image_native.dylib" if "apple" in target else "libbx_image_native.so"
source = root / "native/target" / target / "release" / filename
destination = package / "native" / target / filename
destination.parent.mkdir(parents=True, exist_ok=True)
shutil.copyfile(source, destination)
manifest = package / "box.json"
metadata = json.loads(manifest.read_text())
metadata.setdefault("matchbox", {}).setdefault("artifacts", {})[target] = {
    "path": destination.relative_to(package).as_posix(),
    "sha256": hashlib.sha256(destination.read_bytes()).hexdigest(),
    "abi": 1,
}
manifest.write_text(json.dumps(metadata, indent=2) + "\n")
print(f"Added {target} native artifact to {package}")
