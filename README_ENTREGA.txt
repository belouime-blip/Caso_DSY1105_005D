GESTIÓN DE SOLICITUDES DE SERVICIOS DIGITALES  ·  DSY1105
App móvil · Jetpack Compose · MVVM · Room · Retrofit

CÓMO ABRIR
1. Descomprime el ZIP y abre la carpeta que contiene settings.gradle.kts con Android Studio (File > Open).
2. Espera la sincronización de Gradle (necesita internet la primera vez).
3. Elige un emulador y pulsa Run.

CUENTAS DE PRUEBA (clave 123456)
- cliente@duoc.cl   -> perfil Cliente
- admin@duoc.cl     -> perfil Administrador
- tecnico@duoc.cl   -> perfil Técnico
(se crean solas en Room la primera vez que se abre la app; también existe "Regístrate" para crear clientes)

PRUEBAS UNITARIAS (carpeta app/src/test)
- Desde Android Studio: clic derecho sobre la carpeta "test" > Run Tests
- Por consola: ./gradlew testDebugUnitTest
Prueba de UI (necesita emulador): app/src/androidTest/.../PostScreenTest.kt

API EXTERNA: https://jsonplaceholder.typicode.com/posts (catálogo de servicios, vía Retrofit)

Para la explicación completa del proyecto (arquitectura, flujo, pruebas, qué modificar en la defensa)
lee EXPLICACION_DEFENSA.md
<<<<<<< HEAD

NUEVO EN ESTA VERSION
- Acceso biométrico con BiometricPrompt (si el dispositivo lo soporta).
- Galería de evidencias e historial QR accesible desde cliente, administrador y técnico.
- Adjuntar una imagen existente además de tomar una foto con la cámara.
- Botón Salir de la aplicación con confirmación.
- La barra Android Atrás/Inicio/Recientes permanece visible.
- Ubicación de Room y fotos explicada en EXPLICACION_DEFENSA.md.

IMPORTANTE SOBRE SPRING BOOT
La app ya consume la API pública de ejemplo por Retrofit. Para conectar el microservicio
Spring Boot del equipo se debe configurar la URL y los endpoints reales del backend.
No se inventó una dirección ni una estructura de respuesta que no venían en el proyecto.
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
