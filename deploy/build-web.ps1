# 在仓库根目录执行，生成 weblog-vue3/dist 供部署到 Nginx root
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot\..\weblog-vue3
if (-not (Get-Command pnpm -ErrorAction SilentlyContinue)) {
    if (-not (Get-Command npm -ErrorAction SilentlyContinue)) { throw "需要安装 pnpm 或 npm" }
    npm run build
} else {
    pnpm run build
}
Write-Host "[OK] 静态资源已输出: weblog-vue3/dist" -ForegroundColor Green
