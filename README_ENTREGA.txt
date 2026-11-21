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
