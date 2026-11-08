<#
.SYNOPSIS
    Prepara y publica el proyecto de la Fase 3 en GitHub.

.DESCRIPTION
    Copia el contenido de esta carpeta a una carpeta llamada "fase-3" dentro de
    un repositorio local, excluye archivos generados (build, .gradle, .idea,
    local.properties y archivos zip), inicializa Git si es necesario, crea el
    commit y configura el remoto del repositorio indicado.

    Por omision no hace push: muestra el comando que debe ejecutar con sus
    credenciales de GitHub. Use -Push para intentar la publicacion directamente.

.PARAMETER Usuario
    Usuario de GitHub. Por omision: ashlee527.

.PARAMETER Repositorio
    Nombre del repositorio. Por omision: proyectoprogra.

.PARAMETER CarpetaDestino
    Carpeta local del repositorio. Por omision:
    $env:USERPROFILE\Downloads\proyectoprogra.

.PARAMETER Push
    Si se indica, ejecuta git push -u origin main al final.

.EXAMPLE
    .\publicar-github.ps1

.EXAMPLE
    .\publicar-github.ps1 -Push
#>
[CmdletBinding()]
param(
    [string]$Usuario = "ashlee527",
    [string]$Repositorio = "proyectoprogra",
    [string]$CarpetaDestino = (Join-Path $env:USERPROFILE "Downloads\proyectoprogra"),
    [switch]$Push
)

$ErrorActionPreference = "Stop"

function Escribir([string]$Texto, [string]$Color = "Gray") {
    Write-Host $Texto -ForegroundColor $Color
}

function Verificar-Programa([string]$Nombre) {
    $comando = Get-Command $Nombre -ErrorAction SilentlyContinue
    if (-not $comando) {
        throw "No se encontro '$Nombre' en el PATH. Instalelo y vuelva a intentar."
    }
    return $comando.Source
}

Escribir "== Publicacion de la Fase 3 en GitHub ==" "Cyan"
Escribir ""

$git = Verificar-Programa "git"
Escribir "Git: $git" "Green"
& $git --version | ForEach-Object { Escribir "  $_" }

$origen = $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($origen)) {
    $origen = (Get-Location).Path
}
$origen = (Resolve-Path $origen).Path

if (-not (Test-Path (Join-Path $origen "tutorial.md"))) {
    throw "No se encontro tutorial.md en '$origen'. Ejecute el script desde la carpeta 'fase 3'."
}

Escribir "Origen:  $origen"
Escribir "Destino: $CarpetaDestino"
Escribir ""

# ---------------------------------------------------------------------------
# 1. Crear la carpeta del repositorio y sincronizar el remoto
# ---------------------------------------------------------------------------
$remoto = "https://github.com/$Usuario/$Repositorio.git"
if (-not (Test-Path $CarpetaDestino)) {
    New-Item -ItemType Directory -Force -Path $CarpetaDestino | Out-Null
    Escribir "Carpeta del repositorio creada." "Green"
} else {
    Escribir "La carpeta del repositorio ya existe; se actualizara el contenido." "Yellow"
}

$gitDir = Join-Path $CarpetaDestino ".git"
if (-not (Test-Path $gitDir)) {
    & $git -C $CarpetaDestino init | Out-Null
    & $git -C $CarpetaDestino remote add origin $remoto | Out-Null
    $anterior = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & $git -C $CarpetaDestino fetch origin --quiet 2>&1 | Out-Null
    $fetchOk = ($LASTEXITCODE -eq 0)
    if ($fetchOk) {
        & $git -C $CarpetaDestino checkout -B main origin/main --quiet 2>&1 | Out-Null
        if ($LASTEXITCODE -ne 0) {
            & $git -C $CarpetaDestino checkout -B main --quiet 2>&1 | Out-Null
        }
    } else {
        & $git -C $CarpetaDestino checkout -B main --quiet 2>&1 | Out-Null
    }
    $ErrorActionPreference = $anterior
    Escribir "Repositorio Git preparado con el contenido remoto." "Green"
}

$destinoFase = Join-Path $CarpetaDestino "fase-3"
New-Item -ItemType Directory -Force -Path $destinoFase | Out-Null

# ---------------------------------------------------------------------------
# 2. Copiar el proyecto excluyendo archivos generados
# ---------------------------------------------------------------------------
Escribir "Copiando el proyecto a fase-3 ..."
$robocopy = Get-Command "robocopy.exe" -ErrorAction SilentlyContinue
if ($robocopy) {
    $argumentos = @(
        $origen, $destinoFase, "/E", "/NFL", "/NDL", "/NJH", "/NJS", "/NP",
        "/XD", "build", ".gradle", ".idea", ".git",
        "/XF", "local.properties", "*.zip"
    )
    & $robocopy.Source @argumentos | Out-Null
    if ($LASTEXITCODE -ge 8) {
        throw "Robocopy fallo con el codigo $LASTEXITCODE."
    }
} else {
    Get-ChildItem -Path $origen -Force | Where-Object {
        $_.Name -notin @("build", ".gradle", ".idea", ".git", "local.properties") -and
        $_.Extension -ne ".zip"
    } | ForEach-Object {
        Copy-Item $_.FullName -Destination $destinoFase -Recurse -Force
    }
}
Escribir "Copia terminada." "Green"
Escribir ""

# ---------------------------------------------------------------------------
# 3. Inicializar el repositorio y crear el commit
# ---------------------------------------------------------------------------
$gitDir = Join-Path $CarpetaDestino ".git"
if (-not (Test-Path $gitDir)) {
    & $git -C $CarpetaDestino init | Out-Null
    Escribir "Repositorio Git inicializado." "Green"
}

$gitignore = Join-Path $CarpetaDestino ".gitignore"
if (-not (Test-Path $gitignore)) {
    $lineasGitignore = @(
        "# Archivos generados por Android Studio y Gradle",
        "build/",
        ".gradle/",
        ".idea/",
        "local.properties",
        "*.iml",
        "*.apk",
        "*.zip",
        "captures/",
        ""
    )
    $utf8SinBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllLines($gitignore, $lineasGitignore, $utf8SinBom)
}

& $git -C $CarpetaDestino add --all
if ($LASTEXITCODE -ne 0) {
    throw "git add fallo."
}

$hayCambios = (& $git -C $CarpetaDestino status --porcelain) -join ""
if ([string]::IsNullOrWhiteSpace($hayCambios)) {
    Escribir "No hay cambios nuevos para confirmar." "Yellow"
} else {
    & $git -C $CarpetaDestino -c user.name="$Usuario" `
        -c user.email="$Usuario@users.noreply.github.com" `
        commit -m "Fase 3: cliente Android y tutorial" | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "git commit fallo. Verifique su nombre y correo de Git."
    }
    Escribir "Commit creado." "Green"
}

& $git -C $CarpetaDestino branch -M main
if ($LASTEXITCODE -ne 0) {
    throw "No se pudo crear o renombrar la rama main."
}

$remotos = (& $git -C $CarpetaDestino remote) -join " "
if ($remotos -notmatch "(^|\s)origin(\s|$)") {
    & $git -C $CarpetaDestino remote add origin $remoto
    Escribir "Remoto origin agregado: $remoto" "Green"
} else {
    & $git -C $CarpetaDestino remote set-url origin $remoto
    Escribir "Remoto origin actualizado: $remoto" "Green"
}

Escribir ""
Escribir "== Repositorio preparado ==" "Cyan"
Escribir "Carpeta local: $CarpetaDestino"
Escribir "Rama:          main"
Escribir "Remoto:        $remoto"
Escribir ""
Escribir "URL del tutorial:" "Cyan"
Escribir "  https://github.com/$Usuario/$Repositorio/blob/main/fase-3/tutorial.md"
Escribir ""

if ($Push) {
    Escribir "Publicando en GitHub ..." "Cyan"
    & $git -C $CarpetaDestino push -u origin main
    if ($LASTEXITCODE -ne 0) {
        throw "git push fallo. Inicie sesion en GitHub o configure un token de acceso."
    }
    Escribir "Publicacion completada." "Green"
} else {
    Escribir "Para publicar, ejecute:" "Yellow"
    Escribir "  cd `"$CarpetaDestino`""
    Escribir "  git push -u origin main"
    Escribir ""
    Escribir "O vuelva a ejecutar este script con -Push." "Yellow"
}

Escribir ""
Escribir "Recuerde que el repositorio debe ser PUBLICO para que la URL del" "Yellow"
Escribir "tutorial sea accesible sin iniciar sesion." "Yellow"
