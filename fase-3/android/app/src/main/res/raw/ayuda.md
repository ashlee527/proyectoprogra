# Guía rápida de Práctica de Código

Aplicación Android de la **Fase 3** del proyecto de Programación 2. Consume la
API REST de la Fase 2 y permite abrir lecciones, escribir código Java,
compilarlo y ejecutarlo en el servidor con `guali-dev.jar` y el JDK 25.

## Requisitos

- API REST de la Fase 2 iniciada y accesible desde el teléfono o emulador.
- JDK 25 configurado en el servidor (Configuración > Configurar JDK).
- Conexión de red entre la aplicación y el equipo donde corre la API.

## Uso de la interfaz

1. **Lección**: abra el menú y elija *Abrir lección* para cargar la lección
   seleccionada en el listado. También puede cerrarla o copiar su código de
   ejemplo al editor.
2. **Panel de lección activa**: muestra el título, el objetivo y el contenido
   Markdown de la lección abierta.
3. **Editor de código**: área editable con numeración de líneas. Escriba su
   código Java manualmente o copie el ejemplo de la lección.
4. **Compilar**: envía el código a la API. Los errores o el éxito se muestran
   en la consola. Si la compilación es correcta se habilita *Ejecutar*.
5. **Ejecutar**: corre la clase compilada en el servidor y muestra la salida y
   los errores en la consola.
6. **Consola**: registra cada acción con hora, comandos, salida y resultados.

## Objetivos de las lecciones

- **Compilar sin errores**: se completa al compilar correctamente.
- **Compilar con error**: se completa cuando la compilación reporta errores.
- **Ejecutar con éxito**: se completa cuando el programa termina sin errores.
- **Ejecutar con error**: se completa cuando el programa falla o excede el
  tiempo límite.

Al cumplirse el objetivo, la lección se marca como **COMPLETADA** y el estado
se guarda en el historial de la API.

## Configuración

- **Dirección de la API**: por omisión `http://10.0.2.2:8080`, que es el
  equipo anfitrión visto desde el emulador de Android. En un dispositivo
  físico use `http://IP_DEL_EQUIPO:8080`.
- **Ruta del JDK**: ruta absoluta del JDK 25 en el servidor. Se guarda en la
  API (clave `jdk.ruta`) y la usa `guali-dev.jar`. El teléfono no puede
  explorar el sistema de archivos del servidor, por eso la ruta se escribe
  manualmente.

## Historial

El menú **Historial** muestra todos los registros de la API: lección, fecha de
apertura, objetivo, detalle y estado. Use *Actualizar* para recargar la lista.

## Modo demostración

Si la API no responde, la aplicación carga un catálogo local de cuatro
lecciones de ejemplo para recorrer la interfaz. En ese modo la compilación y la
ejecución están deshabilitadas: siempre requieren el servidor.

## Solución de problemas

- **No se pudo conectar con la API**: verifique que el backend esté iniciado y
  que la dirección configurada sea la correcta.
- **JDK no configurado**: abra Configuración > Configurar JDK y guarde la ruta
  del JDK 25 del servidor.
- **Compile de nuevo antes de ejecutar**: el código cambió después de la
  última compilación; vuelva a compilar.
- **Error de red en un dispositivo físico**: use la IP de la red local del
  equipo, no `localhost` ni `10.0.2.2`.
