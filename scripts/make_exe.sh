clear
echo removing old exe
rm -rf exe/UltraSafe
echo creating new exe
jpackage \
  --input target/ \
  --main-jar ../target/ultra-safe-all.jar \
  --main-class com.ultrasafe.UltraSafeApp \
  --name UltraSafe \
  --type app-image \
  --icon safe.ico \
  --dest exe
echo "done"