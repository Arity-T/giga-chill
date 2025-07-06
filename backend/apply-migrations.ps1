param(
    [string]$DB_HOST = $env:DB_HOST ?? "localhost",
    [string]$DB_PORT = $env:DB_PORT ?? "5432",
    [string]$DB_NAME = $env:DB_NAME ?? "gigachill",
    [string]$DB_USER = $env:DB_USER ?? "postgres",
    [string]$DB_PASSWORD = $env:DB_PASSWORD ?? "postgres"
)

Write-Host "Applying migrations to ${DB_HOST}:${DB_PORT}/${DB_NAME} as ${DB_USER}"

# Получаем и сортируем миграции
$migrations = Get-ChildItem "src/main/resources/db/migration/V*.sql" | Sort-Object Name

foreach ($file in $migrations) {
    Write-Host "Applying: $($file.Name)"
    $env:PGPASSWORD = $DB_PASSWORD
    & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f $file.FullName -v ON_ERROR_STOP=1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Migration failed: $($file.Name)" -ForegroundColor Red
        exit 1
    }
}

Write-Host "All migrations applied successfully!" -ForegroundColor Green