
# Ultra Safe (JavaFX)
A minimal, emoji-friendly JavaFX app to store entries of (username, password, context) with an optional attached file.
Data is encrypted with AES-GCM using a key derived from your master password (PBKDF2-HMAC-SHA256). Saves to `.safe` files.

## Features
- Create/Open/Save/Save As `.safe` stores ("safes")
- Master password required to create/open; lock to clear it from memory
- Add / Edit / Delete entries
- Attach any file (txt, pdf, jpg, png, doc, etc.) to an entry; export attachment later
- Filterable/sortable table view
- All-in-one fat jar build via Shadow

## Build
You need Java 17+ and JavaFX is fetched automatically by Gradle.
If `gradle/wrapper/gradle-wrapper.jar` is missing, run `gradle wrapper` once (requires Gradle installed),
then you can use the wrapper.

```bash
# Linux/macOS
./gradlew shadowJar
# Windows (cmd)
gradlew.bat shadowJar
```

This will produce a runnable jar:
```
build/libs/ultra-safe-all.jar
```

## Run
```bash
java -jar build/libs/ultra-safe-all.jar
# or
mvn javafx:run
```

## Notes
- Encryption: PBKDF2 (SHA-256) with 200_000 iterations, 16-byte salt, AES-256-GCM with 12-byte IV and 128-bit tag.
- The safe is only unlocked in-memory while the app is "unlocked".
- Emoji toolbar to keep it simple: 🆕 📂 💾 🔒 ➕ ✏️ 🗑️ 📄
