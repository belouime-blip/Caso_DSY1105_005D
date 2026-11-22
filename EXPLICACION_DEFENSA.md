# Explicación del proyecto para la defensa

**Gestión de Solicitudes de Servicios Digitales** · Kotlin · Jetpack Compose · MVVM · Room · Retrofit · CameraX

## 1. Qué hace la app (en 30 segundos)
Un cliente crea una solicitud de servicio (título, descripción, categoría, servicio, foto de evidencia). La solicitud recibe un **código único** y queda en estado *Recibida*. Un administrador la revisa, define prioridad y asigna un técnico. El técnico la trabaja y la marca como resuelta. El cliente confirma (se cierra) o pide reapertura. Todo queda guardado en **Room (SQLite)**: usuarios, solicitudes, historial/comentarios/fotos de avance y escaneos QR.

## 2. Los 3 perfiles y su login
Hay **un solo login** y la app decide la página según el **rol** del usuario guardado en Room.

| Perfil | Correo (clave `123456`) | Página | Qué puede hacer |
|---|---|---|---|
| Cliente | cliente@duoc.cl | `cliente_home` | Crear solicitud con foto, ver solo las suyas, comentar, compartir por WhatsApp, confirmar o pedir reapertura, ver catálogo, escanear QR |
| Administrador | admin@duoc.cl | `admin_home` | Ver todas, panel resumen por estado, definir prioridad, asignar técnico, cerrar/cancelar |
| Técnico | tecnico@duoc.cl | `tecnico_home` | Ver solo las asignadas, pasar a *En proceso* / *Resuelta*, comentar y subir foto de avance |

**Flujo del login:** `HomeScreen` → `LoginViewModel.submit()` → `AuthRepository.login()` (consulta `UsuarioDao`) → si existe, `SesionManager.iniciar(usuario)` y se navega a `rol.rutaInicio`. Si no, muestra "Credenciales inválidas". Los 3 usuarios demo se crean solos la primera vez (`sembrarUsuariosDemo`). Un usuario nuevo desde "Regístrate" **siempre** queda como Cliente (no se puede auto-registrar como admin).

## 3. Arquitectura MVVM (te preguntarán esto)
- **View (ui/):** pantallas Compose. Solo dibujan y llaman al ViewModel. No tocan la base de datos.
- **ViewModel (viewmodel/):** guarda el estado (`uiState`, `StateFlow`) y la lógica de pantalla. Sobrevive a rotaciones.
- **Model / Repository (data/):** `Repository` es el intermediario; accede a `DAO` (Room) o a `ApiService` (Retrofit).
- **Flujo de datos:** Pantalla → ViewModel → Repository → DAO/API → de vuelta como `Flow/StateFlow` → la pantalla se redibuja sola.
- `AppViewModelFactory` crea los ViewModels que reciben un repositorio por constructor.
- `util/SolicitudFlujo.kt` concentra las reglas de negocio (quién ve qué, qué cambios de estado permite cada perfil) en Kotlin puro, por eso es fácil de probar.

## 4. Room (base de datos local)
Entidades (`AppDatabase`, versión 3): `SolicitudEntity`, `UsuarioEntity` (nombre, **apellido**, correo, clave, dirección, rol), `HistorialEntity` (cambios, comentarios y fotos de avance), `EscaneoEntity` (cada QR leído).
**Se guarda al crear la solicitud** (`SolicitudViewModel.guardar()` → `repo.guardar()` → `SolicitudDao`), junto con la foto (URI) y una línea en el historial. Los QR escaneados se guardan al leerse. **Decisión de diseño:** no se creó microservicio; se sigue trabajando con Room (indicado por el profesor para esta entrega).

## 5. API externa (Retrofit)
Catálogo de servicios: `ApiService` (`@GET("posts")`) → `RetrofitInstance` (base `https://jsonplaceholder.typicode.com/`, conversor Gson) → `PostRepository` → `PostViewModel` (`postList`, `cargando`, `error`) → `PostScreen`. Es la misma API del ejemplo del profe. Si no hay internet muestra el error y un botón "Reintentar".

## 6. Cámara, fotos y QR
- **Foto:** `rememberTomarFoto` pide el permiso de cámara, crea un archivo en `filesDir/evidencias` y lo expone con `FileProvider`; la URI se guarda en Room. El formulario la usa como evidencia y el técnico como foto de avance.
- **QR (CameraX + ML Kit):** `QrScanner` analiza la cámara; `LectorQrScreen` y `QrScannerScreen` guardan cada lectura en `escaneos`. Al escanear un código de solicitud solo se abre si el perfil actual tiene permiso para verla.
- **WhatsApp:** botón "Compartir referencia" envía "código + título + estado" con un Intent a WhatsApp (o selector si no está instalado).

## 6b. Avisos locales
`util/Notificador.kt` muestra notificaciones del propio teléfono (sin servidor) cuando: se recibe una nueva solicitud, se asigna a un técnico, cambia el estado, o queda resuelta (pendiente de confirmación del cliente). En Android 13 o superior `MainActivity` pide el permiso `POST_NOTIFICATIONS`; si se niega, la app sigue funcionando sin avisos.

## 7. Las 4 pruebas unitarias (`app/src/test`)
| # | Clase | Qué prueba | Qué se simula (MockK) |
|---|---|---|---|
| 1 | `SolicitudFlujoTest` | Cada perfil ve solo lo suyo; cambios de estado permitidos por perfil; cuándo se puede asignar | nada (lógica pura) |
| 2 | `AuthRepositoryTest` | Login normaliza correo; credenciales malas → null; correo duplicado; registro fuerza rol Cliente; usuarios demo | `UsuarioDao` |
| 3 | `PostViewModelTest` | La lista se llena tras `fetchPosts()`; si falla la API queda vacía con mensaje de error | `PostRepository` |
| 4 | `LoginViewModelTest` | Login correcto entrega el rol y guarda sesión; inválido muestra error; campos vacíos no consultan la BD | `AuthRepository` |

Herramientas: **Kotest** (`StringSpec`, `shouldBe`), **MockK** (`mockk`, `coEvery`, `coVerify`), **coroutines-test** (`setMain`, `runTest`). Ejecutar: `./gradlew testDebugUnitTest`. Hay además una prueba de UI en `androidTest/PostScreenTest.kt` (necesita emulador).

## 8. Modificación típica en la evaluación: "agrega/cambia el campo apellido"
Ya existe `apellido`. Si piden otro campo (ej. `telefono`), toca estos 6 puntos en orden:
1. `UsuarioEntity`: agregar `val telefono: String = ""`.
2. `AppDatabase`: subir `version = 3` → `4` (la app borra y recrea la BD con `fallbackToDestructiveMigration`; desinstala o limpia datos si falla).
3. `RegistroViewModel` / `RegistroUiState`: agregar el campo y su `onTelefonoChange`.
4. `RegistroScreen`: agregar el `OutlinedTextField`.
5. `PerfilViewModel` / `PerfilScreen`: igual para poder editarlo.
6. `AuthRepository.sembrarUsuariosDemo` (datos demo) y las pruebas que construyen `UsuarioEntity` (tienen valor por defecto, normalmente no se rompen).

## 9. Preguntas probables y respuestas cortas
- **¿Por qué MVVM?** Separa UI, lógica y datos; el estado sobrevive a rotaciones; es más fácil de probar.
- **¿Qué es un `StateFlow`?** Un flujo que guarda el último valor; Compose lo observa con `collectAsState()` y se redibuja al cambiar.
- **¿Para qué sirve el Repository?** Oculta de dónde vienen los datos (Room o API); el ViewModel no lo sabe.
- **¿Por qué `suspend` y `viewModelScope`?** Room y Retrofit no deben bloquear el hilo principal; las corrutinas lo evitan.
- **¿Por qué mockear en las pruebas?** Para probar solo la lógica, sin base de datos ni internet, y que sean rápidas y repetibles.
- **¿Cómo se restringe lo que ve cada perfil?** `SolicitudFlujo.visiblesPara()` filtra por correo del cliente / técnico asignado / todo para el admin.
- **¿Por qué la cámara pide permiso?** CAMERA es un permiso peligroso; se solicita en ejecución.
- **¿Qué pasa si no hay internet?** El catálogo muestra error con "Reintentar"; el resto de la app funciona porque usa Room.

## 10. Pendientes que debes hacer tú (no se pueden hacer desde aquí)
1. **Abrir el proyecto en Android Studio, sincronizar Gradle y ejecutar una vez.** Yo no pude compilar (sin Android SDK ni red), así que revisa que no aparezca algún error de compilación y avísame con el texto exacto para corregirlo.
2. **APK firmado (release):** *Build → Generate Signed App Bundle / APK → APK → Create new keystore* (guarda el `.jks` y la clave) → variante **release** → Finish. El APK queda en `app/release/`.
3. Documentación técnica final, repositorio (GitHub) y tablero (Trello) que pida la pauta: usa este documento como base.
4. **Confirmar con el profesor:** la pauta (PDF) menciona microservicios; tus apuntes dicen "no crear microservicio y seguir con ROOM". Esta entrega sigue tus apuntes.
