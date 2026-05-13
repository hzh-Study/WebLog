param(
    [Parameter(Mandatory=$true)]
    [string[]]$SkillNames
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$skillsDir = $scriptDir

$allNames = @()
foreach ($name in $SkillNames) {
    if ($name -match ',') {
        $allNames += $name -split ',' | ForEach-Object { $_.Trim() }
    } else {
        $allNames += $name.Trim()
    }
}

foreach ($name in $allNames) {
    if ([string]::IsNullOrWhiteSpace($name)) { continue }
    $skillDir = Join-Path $skillsDir $name
    $skillPath = Join-Path $skillDir "SKILL.md"
    if (Test-Path $skillPath) {
        Write-Output "--- SKILL: $name ---"
        Write-Output "Base directory: $skillDir"
        Write-Output ""
        Get-Content $skillPath -Raw -Encoding UTF8
        Write-Output ""
        Write-Output "--- END SKILL: $name ---"
    } else {
        Write-Output "Skill not found: $name"
        Write-Output "Available skills:"
        Get-ChildItem -Path $skillsDir -Directory | ForEach-Object { Write-Output "  $($_.Name)" }
    }
}
