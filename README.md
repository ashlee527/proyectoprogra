# Práctica de Código — Programación 2, Fase 3

Cliente Android en Java que consume la API REST de la Fase 2 para abrir
lecciones, escribir código, compilarlo y ejecutarlo con `guali-dev.jar` y
JDK 25.

- **Estudiante:** Ashlee Stefani Ordóñez Pérez
- **Carné:** 3190-25-4272
- **Curso:** Programación 2
- **Fase:** 3
- **Universidad:** Universidad Mariano Gálvez de Guatemala

## Tutorial público

El tutorial completo en Markdown, con capturas reales del emulador, está en:

**https://github.com/ashlee527/proyectoprogra/blob/main/fase-3/tutorial.md**

## Contenido del repositorio

| Ruta | Descripción |
|------|-------------|
| `fase-3/tutorial.md` | Tutorial paso a paso con capturas |
| `fase-3/README.md` | Resumen del proyecto y resultados |
| `fase-3/android/` | Proyecto Android Studio (Java, Gradle 8.9) |
| `fase-3/docs/Manual-Tecnico-Fase-3.pdf` | Manual técnico en PDF |
| `fase-3/docs/img/` | Capturas reales y diagrama de arquitectura |

## Requisitos

- JDK 25 para el backend y `guali-dev.jar`
- Android Studio 2026.2.2 o superior
- Android SDK 35 y emulador API 35
- API de la Fase 2 en `http://10.0.2.2:8080`

## Compilación

```powershell
cd fase-3/android
.\gradlew.bat assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Verificación

- Compilación: `BUILD SUCCESSFUL`
- Pruebas unitarias: 6 pruebas, 0 fallos
- Emulador: Pixel_2_API_35, Android 15 (API 35)
- Compilación remota: `Compilacion exitosa. Clase generada: Calculadora.`
- Ejecución remota: `Suma: 23`, `Resta: 13`, `Producto: 90`, `Division: 3.6`
