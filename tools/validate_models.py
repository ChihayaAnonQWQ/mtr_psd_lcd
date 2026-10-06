#!/usr/bin/env python3
"""Pre-build validation for this port's block models.

Checks (all of these have caused real in-game failures at least once):
  1. every blockstate-referenced model resolves along its parent chain;
  2. every face has a `texture` and any `#var` it uses is defined somewhere in the chain;
  3. `from`/`to` are 3 numbers and `uv` is exactly 4 numbers;
  4. every texture path actually exists (in this mod or in MTR's jar);
  5. audit: which models differ from the upstream 1.19.2 jar (informational).

Usage: python validate_models.py [path-to-mtr-jar]
Exit code 1 when a hard check fails.
"""
import json
import sys
import zipfile
from pathlib import Path

HERE = Path(__file__).resolve()
RES = HERE.parents[1] / "mtr-psd-lcd-forge" / "src" / "main" / "resources" / "assets" / "mtr_psd_lcd"
UPSTREAM = HERE.parents[1] / "mtr_psd_lcd-fabric-1.19.2-1.2.8.jar"
DEFAULT_MTR = Path(r"D:\Minecraft\HZYMTR\versions\HZYMTR v26.10\mods\MTR-forge-1.20.1-3.6.3.jar")
MTR_JAR = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_MTR

errors = []
notes = []


def jar_json(jar, entry):
    try:
        with zipfile.ZipFile(jar) as z:
            return json.loads(z.read(entry).decode("utf-8-sig"))
    except (KeyError, FileNotFoundError, json.JSONDecodeError):
        return None


def load_model(ns, path):
    """Return (model_dict, texture_png_exists) for a namespaced model path."""
    rel = f"{path}.json"
    if ns == "mtr_psd_lcd":
        f = RES / "models" / rel
        if not f.exists():
            return None, False
        try:
            return json.loads(f.read_text(encoding="utf-8-sig")), True
        except json.JSONDecodeError as exc:
            errors.append(f"{f.name}: JSON 解析失败 {exc}")
            return None, False
    if ns == "mtr":
        d = jar_json(MTR_JAR, f"assets/mtr/models/{rel}")
        return d, d is not None
    return None, True          # vanilla models: assume fine


def resolve(model_ref):
    """Walk the parent chain and return (merged_textures, elements)."""
    ns, path = model_ref.split(":", 1) if ":" in model_ref else ("mtr_psd_lcd", model_ref)
    chain, seen = [], set()
    while (ns, path) not in seen:
        seen.add((ns, path))
        data, ok = load_model(ns, path)
        if data is None:
            return None, None, f"模型缺失: {ns}:{path}" if ok else f"模型缺失: {ns}:{path}"
        chain.append(data)
        parent = data.get("parent")
        if not parent:
            break
        ns, path = parent.split(":", 1) if ":" in parent else ("minecraft", parent)
        if ns == "minecraft":
            break
    textures = {}
    for data in reversed(chain):
        textures.update(data.get("textures") or {})
    elements = next((d.get("elements") for d in chain if d.get("elements")), None)
    return textures, elements, None


def texture_exists(ref, ns_hint="mtr_psd_lcd"):
    if not isinstance(ref, str):
        return True
    ns, path = ref.split(":", 1) if ":" in ref else ("minecraft", ref)
    if ns == "minecraft":
        return True
    png = f"textures/{path}.png"
    if ns == "mtr_psd_lcd":
        return (RES / png).exists()
    with zipfile.ZipFile(MTR_JAR) as z:
        return f"assets/{ns}/{png}" in z.namelist()


# 1..4: walk every blockstate reference
bs_dir = RES / "blockstates"
for bs in sorted(bs_dir.glob("*.json")):
    data = json.loads(bs.read_text(encoding="utf-8-sig"))
    refs = []
    for v in (data.get("variants") or {}).values():
        refs += v if isinstance(v, list) else [v]
    for part in data.get("multipart") or []:
        a = part.get("apply")
        refs += a if isinstance(a, list) else [a]
    for ref in refs:
        m = ref.get("model", "")
        if m.startswith("minecraft") or not m:
            continue
        textures, elements, err = resolve(m)
        if err:
            errors.append(f"{bs.name}: {m} -> {err}")
            continue
        for var, val in textures.items():
            if not texture_exists(val):
                errors.append(f"{bs.name}: {m} 贴图不存在 {var}={val}")
        for e in elements or []:
            frm, to = e.get("from"), e.get("to")
            if not (isinstance(frm, list) and len(frm) == 3 and isinstance(to, list) and len(to) == 3):
                errors.append(f"{bs.name}: {m} 元素 from/to 不是 3 个数: {frm} -> {to}")
            for face, fd in (e.get("faces") or {}).items():
                tref = fd.get("texture", "")
                if not tref:
                    errors.append(f"{bs.name}: {m} 面 {face} 缺少 texture")
                elif tref.startswith("#") and tref[1:] not in textures:
                    errors.append(f"{bs.name}: {m} 面 {face} 引用未定义变量 {tref}")
                uv = fd.get("uv")
                if uv is not None and (not isinstance(uv, list) or len(uv) != 4):
                    errors.append(f"{bs.name}: {m} 面 {face} uv 必须是 4 个数，当前 {uv}")

# 5: informational upstream diff
upstream_names = set()
with zipfile.ZipFile(UPSTREAM) as z:
    upstream_names = set(z.namelist())
for p in sorted((RES / "models" / "block").glob("*.json")):
    entry = f"assets/mtr_psd_lcd/models/block/{p.name}"
    cur = json.loads(p.read_text(encoding="utf-8-sig"))
    if entry not in upstream_names:
        notes.append(f"[新增] {p.name}")
        continue
    orig = jar_json(UPSTREAM, entry)
    if json.dumps(orig, sort_keys=True) != json.dumps(cur, sort_keys=True):
        notes.append(f"[改动] {p.name}")

print(f"硬性检查失败: {len(errors)}")
for e in errors:
    print("   ✗", e)
print(f"\n与上游差异（仅供确认，共 {len(notes)} 项）:")
for n in notes:
    print("   ", n)
sys.exit(1 if errors else 0)
