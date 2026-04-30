#!/usr/bin/env bash
# 在仓库根目录: bash deploy/build-web.sh
set -euo pipefail
cd "$(dirname "$0")/../weblog-vue3"
if command -v pnpm >/dev/null 2>&1; then
  pnpm run build
else
  npm run build
fi
echo "[OK] 静态资源已输出: weblog-vue3/dist"
