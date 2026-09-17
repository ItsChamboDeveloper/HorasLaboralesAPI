# Horas Laborales API

API REST desarrollada con **Spring Boot** para la gestión de horas laborales.
La aplicación utiliza **Oracle Database** como base de datos y está preparada para ejecutarse mediante **Docker**.

## Tecnologías utilizadas

* Java 17
* Spring Boot 4
* Spring Data JPA
* Hibernate
* Oracle Database
* Maven
* Docker
* Docker Compose
* Oracle Wallet

## Requisitos

Para ejecutar el proyecto se necesita tener instalado:

* Docker Desktop
* Acceso a la base de datos Oracle
* Oracle Wallet correspondiente a la base de datos

No es necesario instalar Java ni Maven en el equipo si se utiliza Docker para ejecutar la aplicación.

## Estructura del proyecto

```text
HorasLaboralesAPI/
├── src/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env
├── .gitignore
├── Wallet_SGMA/
└── README.md
```

## Configuración del Oracle Wallet

El proyecto utiliza un Oracle Wallet para establecer la conexión con Oracle Database.

La carpeta del Wallet debe encontrarse en la raíz del proyecto con el siguiente nombre:

```text
Wallet_SGMA/
```

Por ejemplo:

```text
HorasLaboralesAPI/
└── Wallet_SGMA/
    ├── cwallet.sso
    ├── tnsnames.ora
    ├── sqlnet.ora
    └── ...
```

El `docker-compose.yml` monta esta carpeta dentro del contenedor en:

```text
/app/wallet
```

y configura:

```text
TNS_ADMIN=/app/wallet
```

## Variables de entorno

La configuración de la aplicación utiliza un archivo `.env`.

Este archivo debe contener las variables necesarias para la conexión y configuración de la aplicación.

**Importante:** el archivo `.env` contiene información sensible y no debe publicarse en repositorios públicos.

## Ejecutar el proyecto con Docker

Abrir PowerShell dentro de la carpeta del proyecto:

```powershell
cd "C:\ruta\HorasLaboralesAPI"
```

Construir y levantar la aplicación:

```powershell
docker compose up -d --build
```

Para comprobar que el contenedor está ejecutándose:

```powershell
docker ps
```

El contenedor utilizado por la aplicación se llama:

```text
horas-laborales-api
```

La API queda disponible en:

```text
http://localhost:8080
```

## Ver los logs

Para consultar los logs de la aplicación:

```powershell
docker logs horas-laborales-api
```

Una ejecución correcta debe mostrar mensajes similares a:

```text
HikariPool-1 - Start completed.
Tomcat started on port 8080
Started HorasLaboralesAutomotrizApplication
```

## Comprobar la conexión con Oracle

En los logs debe aparecer una conexión exitosa mediante HikariCP, por ejemplo:

```text
HikariPool-1 - Added connection oracle.jdbc.driver.T4CConnection
HikariPool-1 - Start completed.
```

También debe aparecer información relacionada con Oracle Database.

## Detener la aplicación

Para detener los servicios:

```powershell
docker compose down
```

## Reconstruir la aplicación

Si se realizan cambios en el código y se necesita reconstruir la imagen:

```powershell
docker compose down
docker compose up -d --build
```

## Autenticación

La API utiliza autenticación mediante cookies.

Para iniciar sesión como instructor se utiliza:

```http
POST /api/instructorsAuth/instructorLogin
```

Ejemplo:

```http
POST http://localhost:8080/api/instructorsAuth/instructorLogin
```

El cuerpo de la solicitud debe enviarse en formato JSON:

```json
{
    "email": "correo_del_instructor",
    "password": "contraseña"
}
```

Después de iniciar sesión correctamente se genera una cookie de autenticación llamada:

```text
authToken
```

Para consultar el instructor autenticado:

```http
GET /api/instructorsAuth/meInstructor
```

## Ejemplo de respuesta autenticada

```json
{
    "authenticated": true,
    "instructor": {
        "id": 1,
        "names": "...",
        "lastNames": "...",
        "email": "...",
        "role": "...",
        "level": "...",
        "instructorImage": "...",
        "authorities": []
    }
}
```

## Puerto utilizado

La aplicación utiliza el puerto:

```text
8080
```

Por lo tanto:

```text
http://localhost:8080
```

## Seguridad

Los siguientes archivos o carpetas contienen información que puede ser sensible y no deben publicarse en GitHub:

```text
.env
Wallet_SGMA/
```

Estos elementos se encuentran incluidos en `.gitignore`.

## Comandos principales

### Iniciar

```powershell
docker compose up -d --build
```

### Ver contenedores

```powershell
docker ps
```

### Ver logs

```powershell
docker logs horas-laborales-api
```

### Detener

```powershell
docker compose down
```

### Reiniciar

```powershell
docker compose restart
```

## Estado del proyecto

La aplicación está preparada para ejecutarse mediante Docker y conectarse a Oracle Database utilizando Oracle Wallet.

Componentes comprobados:

* Spring Boot funcionando
* Java 17
* Tomcat en puerto 8080
* Docker funcionando
* Docker Compose funcionando
* Oracle Database conectado
* Oracle Wallet funcionando
* Hibernate/JPA funcionando
* Autenticación de instructores funcionando
