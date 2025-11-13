Clear-Host
Write-Host "🧹 Removing old exe..."
Remove-Item -Recurse -Force "exe\UltraSafe" -ErrorAction SilentlyContinue

Write-Host "⚙️  Creating new exe..."
jpackage `
  --input "target" `
  --main-jar "ultra-safe-all.jar" `
  --main-class "com.ultrasafe.UltraSafeApp" `
  --name "UltraSafe" `
  --type "app-image" `
  --icon "safe.ico" `
  --dest "exe" `
  --add-modules "javafx.controls,javafx.fxml"

Write-Host "✅ Done."
