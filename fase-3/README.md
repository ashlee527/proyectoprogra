# Práctica de Código · Fase 3 (cliente Android)

Aplicación Android desarrollada en **Java** para la Fase 3 del proyecto de
**Programación 2** de la Universidad Mariano Gálvez de Guatemala.

El cliente consume la **API REST de la Fase 2** y permite abrir lecciones,
leer su contenido, escribir código Java, compilarlo y ejecutarlo en el servidor
con `guali-dev.jar` y el **JDK 25**.

- **Estudiante:** Ashlee Stefani Ordóñez Pérez
- **Carné:** 3190-25-4272
- **Curso:** Programación 2
- **Fase:** 3

## Tutorial publicado

El tutorial completo en Markdown, con capturas de pantalla, está en:

```text
https://github.com/ashlee527/proyectoprogra/blob/main/fase-3/tutorial.md
```

> El repositorio debe ser **público** y contener la carpeta `fase-3` en la rama
> `main` para que la URL funcione. Use el script `publicar-github.ps1` para
> preparar y publicar el contenido.

## Funcionalidad

| Requisito | Implementación |
|-----------|----------------|
| Barra de menú | `Lección`, `Configuración`, `Historial`, `Ayuda` con `PopupMenu` |
| Título de lección activa | `tituloLeccionActiva`, `subtituloLeccion` y chips de objetivo/estado |
| Panel de lección activa | Listado, botón Abrir, título, objetivo y contenido Markdown |
| Editor de código | `EditorCodigoView` con numeración de líneas y fuente monoespaciada |
| Consola | `Consola` con hora, colores por tipo de mensaje y autodesplazamiento |
| Compilar | `POST /api/v1/ejecucion/compilar` |
| Ejecutar | `POST /api/v1/ejecucion/ejecutar` (solo tras compilar correctamente) |
| Historial | `HistorialActivity` con lista, resumen y estados |
| Configuración | Dirección de la API y ruta del JDK del servidor |
| Modo sin conexión | Catálogo local de cuatro lecciones en `assets/demo` |

## Estructura

```text
fase 3/
├── android/                    Proyecto Android Studio (Gradle)
│   ├── app/
│   │   ├── build.gradle
│   │   └── src/
│   │       ├── main/
│   │       │   ├── AndroidManifest.xml
│   │       │   ├── assets/demo/lecciones.json
│   │       │   ├── java/gt/edu/umg/practicacodigo/
│   │       │   │   ├── MainActivity.java
│   │       │   │   ├── HistorialActivity.java
│   │       │   │   ├── AyudaActivity.java
│   │       │   │   ├── api/          ApiCliente, ApiExcepcion
│   │       │   │   ├── datos/        RepositorioDemo
│   │       │   │   ├── modelo/       POJO y enums del dominio
│   │       │   │   ├── util/         Preferencias, Formato, Markdown, tareas
│   │       │   │   └── vista/        Editor, consola, adaptador y diálogo
│   │       │   └── res/              Layouts, drawables, valores, mipmaps
│   │       └── test/                 Pruebas unitarias de EvaluadorObjetivos
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew / gradlew.bat
│   └── gradle/wrapper/
├── docs/
│   ├── img/                    Capturas y diagrama de arquitectura
│   └── Manual-Tecnico-Fase-3.pdf
├── tutorial.md                 Tutorial paso a paso
├── publicar-github.ps1         Script de publicación
└── README.md
```

## Requisitos

- **Android Studio** reciente con Android SDK **35**.
- **JDK 25** para el servidor de la Fase 2.
- **API REST de la Fase 2** en ejecución (`http://localhost:8080`).
- Emulador con API 35 o dispositivo físico con depuración USB.
- Gradle **8.9** (lo descarga el wrapper incluido).

## Compilar y ejecutar

1. Inicie la API de la Fase 2:

   ```powershell
   cd "C:\ruta\al\proyecto\fase-2\backend"
   java -jar target\proyecto-fase-2-backend.jar
   ```

2. Abra la carpeta `android` en Android Studio y espere la sincronización.
3. Cree o inicie un emulador con API 35.
4. Ejecute la aplicación con **Run ▶** o `Shift + F10`.
5. Abra **Configuración > Configurar JDK** y guarde la ruta del JDK 25 del
   servidor. En el emulador la API debe apuntar a `http://10.0.2.2:8080`.
6. Seleccione una lección, presione **Abrir**, luego **Compilar** y, si fue
   correcta, **Ejecutar**.

Desde la terminal:

```powershell
cd android
.\gradlew.bat assembleDebug
.\gradlew.bat test
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Dirección de la API

| Entorno | Dirección |
|---------|-----------|
| Emulador de Android | `http://10.0.2.2:8080` |
| Dispositivo físico en la misma red | `http://IP_DEL_EQUIPO:8080` |

`10.0.2.2` es la dirección con la que el emulador ve al equipo anfitrión.
`localhost` dentro del emulador apunta al propio emulador.

## Capturas de pantalla

Las imágenes de `docs/img` son **capturas reales** tomadas de la aplicación
ejecutándose en un emulador Android 15 (API 35, 1080 × 1920), conectado a la
API de la Fase 2 en `http://10.0.2.2:8080`. Muestran las cuatro lecciones
importadas, la compilación con `guali-dev.jar` y la ejecución de la clase
`Calculadora` en el servidor.

| Archivo | Contenido |
|---------|-----------|
| `01-pantalla-principal.png` | Pantalla principal en vertical |
| `02-menu-leccion.png` | Menú Lección desplegado |
| `03-compilar-exitoso.png` | Compilación correcta en la consola |
| `04-ejecutar-salida.png` | Ejecución y salida del programa |
| `05-historial.png` | Historial de sesión |
| `06-configuracion-jdk.png` | Configuración de la API y del JDK |
| `07-ayuda.png` | Pantalla de ayuda |
| `08-pantalla-horizontal.png` | Diseño en orientación horizontal |
| `09-arquitectura.png` | Arquitectura del cliente Android |

## Pruebas

Las pruebas unitarias de `EvaluadorObjetivos` no dependen del SDK de Android:

```powershell
cd android
.\gradlew.bat test
```

El reporte queda en `app/build/reports/tests/test/index.html`.

## Publicación en GitHub

```powershell
cd "C:\Users\asop2\Downloads\fase 3"
.\publicar-github.ps1
```

El script verifica Git, copia el proyecto a una carpeta `fase-3`, inicializa el
repositorio y muestra el comando de publicación. Si el repositorio ya existe en
GitHub, ejecute el `git push` que indica el script.

## Nota académica

El cliente **no compila código en el teléfono**: envía el texto a la API de la
Fase 2, que ejecuta `guali-dev.jar` con el JDK 25 instalado en el servidor. Por
eso la compilación y la ejecución requieren que la API esté disponible.
