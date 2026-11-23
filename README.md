# Caso_DSY1105_005D

Aplicación móvil Android (Kotlin + Jetpack Compose) con microservicio propio en Spring Boot y consumo de API externa con Retrofit. Asignatura DSY1105 – Desarrollo de Aplicaciones Móviles (Evaluación Parcial 4).

## 1. Nombre del proyecto
<NOMBRE DE LA APP / CONTEXTO>

## 2. Integrantes
| Nombre y apellido | GitHub | Rol / aporte principal |
|---|---|---|
| <Integrante 1> | <usuario> | <p. ej. front-end, ViewModels> |
| <Integrante 2> | <usuario> | <p. ej. microservicio, pruebas> |

## 3. Funcionalidades
**Perfiles de usuario (cada uno con su pantalla tras el login):**
- **Cliente:** <p. ej. crear y consultar sus solicitudes>
- **Administrador:** <p. ej. gestionar usuarios, ver y editar todos los registros>
- **Técnico:** <p. ej. ver solicitudes asignadas y actualizar su estado>

**Generales:**
- Inicio de sesión con validación de formularios y redirección según perfil.
- CRUD completo (crear, listar, editar, eliminar) conectado al microservicio.
- Persistencia local con Room.
- Visualización de datos de una API externa dentro de la interfaz.
- Pruebas unitarias sobre ViewModel/Repository.
- APK firmado digitalmente.

## 4. Endpoints utilizados

### 4.1 Microservicio propio (Spring Boot)
Base URL: `http://<IP>:<PUERTO>/api` (emulador Android: `http://10.0.2.2:<PUERTO>/api`)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/<recurso>` | Lista todos los registros |
| GET | `/<recurso>/{id}` | Obtiene un registro por id |
| POST | `/<recurso>` | Crea un registro |
| PUT | `/<recurso>/{id}` | Actualiza un registro |
| DELETE | `/<recurso>/{id}` | Elimina un registro |

Base de datos: <MySQL / H2 / PostgreSQL> configurada en `application.properties`.

### 4.2 API externa (Retrofit)
- Nombre / proveedor: <API EXTERNA>
- Base URL: `<https://...>`
- Endpoint(s) consumido(s): `GET <ruta>` → <qué dato se muestra y en qué pantalla>

## 5. Pasos para ejecutar

### Requisitos
- Android Studio (última versión estable) y JDK 17
- IDE para Spring Boot (IntelliJ / STS) y la base de datos configurada
- Emulador o dispositivo Android con internet

### Microservicio
1. Clonar el repositorio: `git clone https://github.com/belouime-blip/Caso_DSY1105_005D.git`
2. Abrir la carpeta del microservicio (`<NOMBRE_CARPETA_MICROSERVICIO>`) en el IDE.
3. Configurar la base de datos en `src/main/resources/application.properties`.
4. Ejecutar la clase principal (`@SpringBootApplication`) o `./mvnw spring-boot:run`.
5. Verificar en `http://localhost:<PUERTO>/api/<recurso>`.

### App móvil
1. Abrir la carpeta raíz del repositorio en Android Studio y esperar la sincronización de Gradle.
2. Ajustar la URL base del microservicio en `<archivo Retrofit, p. ej. RetrofitClient.kt>`.
3. Ejecutar en emulador o dispositivo (`Run ▶`).
4. Credenciales de prueba:
   - Cliente: `<usuario>` / `<clave>`
   - Administrador: `<usuario>` / `<clave>`
   - Técnico: `<usuario>` / `<clave>`

### Pruebas unitarias
- Ejecutar: `./gradlew test` (y `./gradlew connectedAndroidTest` para las pruebas instrumentadas).
- Ubicación: `app/src/test` y `app/src/androidTest`.
- Herramientas: JUnit, MockK.

## 6. APK firmado y .jks
- APK firmado: `<ruta/al/app-release.apk>`
- Keystore: `<ruta/al/archivo.jks>`
- Configuración de firma: bloque `signingConfigs` en `app/build.gradle.kts`.

**Captura del APK firmado y .jks:**

![APK firmado y jks](docs/captura_apk_jks.png)

## 7. Planificación y trabajo en equipo
- Tablero Trello: <ENLACE AL TABLERO> (tareas distribuidas por integrante)
- Commits por integrante: <https://github.com/belouime-blip/Caso_DSY1105_005D/graphs/contributors>

![Evidencia de Trello](docs/trello.png)
