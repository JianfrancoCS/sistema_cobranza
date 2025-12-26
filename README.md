# TaxiHub - Sistema de Gestión de Cobranza

Sistema web desarrollado en Spring Boot para la gestión integral de operaciones de una flota de taxis, incluyendo control de empleados, vehículos, deudas, pagos y marcaciones.

## Descripción

TaxiHub es una aplicación web completa que permite gestionar todas las operaciones relacionadas con una flota de taxis, desde el control de empleados y vehículos hasta el seguimiento de deudas, pagos y marcaciones de entrada/salida. El sistema incluye roles de usuario (Administrador, Supervisor, Conductor) con permisos específicos para cada uno.

## Características Principales

### Gestión de Empleados
- Registro y gestión de conductores y supervisores
- Control de licencias de conducir
- Asignación de cargos y grupos de trabajo
- Validación de documentos (DNI)

### Gestión de Vehículos
- Registro de autos con información completa (placa, marca, modelo, estado)
- Asignación de vehículos a conductores
- Control de disponibilidad de vehículos

### Sistema de Deudas y Pagos
- Generación automática de deudas basadas en comisiones
- Registro de pagos físicos (efectivo) y virtuales (billetera virtual)
- Gestión de estados de pago (Por revisar, En revisión, Aprobado, Rechazado)
- Cálculo automático de saldos pendientes
- Visualización de vouchers para pagos virtuales

### Sistema de Marcaciones
- Registro de entrada y salida de conductores
- Integración con API REST para marcaciones externas
- Historial de marcaciones por empleado

### Dashboard y Reportes
- KPIs en tiempo real
- Visualización de métricas del día
- Estadísticas de pagos y deudas

### Seguridad
- Autenticación basada en Spring Security
- Sistema de roles y permisos (RBAC)
- Grupos de trabajo con permisos específicos
- Auditoría de acciones críticas

## Tecnologías Utilizadas

### Backend
- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **MySQL** - Base de datos
- **Flyway** - Migraciones de base de datos
- **Lombok** - Reducción de código boilerplate
- **MapStruct** - Mapeo de objetos
- **Thymeleaf** - Motor de plantillas

### Frontend
- **Bootstrap 5.3.8** - Framework CSS
- **Bootstrap Icons 1.13.1** - Iconografía
- **JavaScript** - Interactividad
- **HTML5/CSS3**

### Herramientas
- **Maven** - Gestión de dependencias
- **Spring DevTools** - Desarrollo ágil

## Estructura del Proyecto

```
sistema_cobranza/
├── taxihub/                    # Aplicación principal (Web)
│   ├── src/main/java/
│   │   └── edu/cibertec/taxihub/
│   │       ├── config/         # Configuraciones (Seguridad, REST, etc.)
│   │       ├── constantes/      # Enumeraciones
│   │       ├── controller/     # Controladores MVC
│   │       ├── dao/            # Capa de acceso a datos
│   │       │   ├── entity/    # Entidades JPA
│   │       │   ├── repository/ # Repositorios
│   │       │   └── specification/ # Especificaciones JPA
│   │       ├── dto/            # Objetos de transferencia de datos
│   │       ├── exception/      # Manejo de excepciones
│   │       ├── security/       # Configuración de seguridad
│   │       ├── services/       # Lógica de negocio
│   │       ├── usecase/        # Casos de uso
│   │       └── utils/          # Utilidades
│   └── src/main/resources/
│       ├── db/migration/      # Scripts de migración Flyway
│       ├── static/               # Recursos estáticos (CSS, JS, imágenes)
│       └── templates/             # Plantillas Thymeleaf
│
└── taxihub-rest/                 # API REST para marcaciones
    └── src/main/java/
        └── edu/cibertec/taxihub_rest/
```

## Requisitos Previos

- **Java 21** o superior
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git** (opcional)

## Instalación

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd sistema_cobranza
```

### 2. Configurar la base de datos

Crear una base de datos MySQL:
```sql
CREATE DATABASE taxihub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar las credenciales

Editar `taxihub/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taxihub?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 4. Ejecutar las migraciones

Las migraciones de Flyway se ejecutarán automáticamente al iniciar la aplicación.

### 5. Compilar y ejecutar

#### Usando Maven Wrapper:
```bash
# Compilar
cd taxihub
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run
```

#### O usando Maven directamente:
```bash
cd taxihub
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## Credenciales por Defecto

Al iniciar la aplicación por primera vez, se crea automáticamente un usuario administrador:

- **Email:** `sanpablosegundo@gmail.com`
- **Contraseña:** `admin123`

**Importante:** Cambiar estas credenciales en producción.

## Roles y Permisos

### Administrador
- Gestión completa de usuarios
- Gestión de grupos y autoridades
- Gestión de empleados y vehículos
- Visualización de todos los pagos
- Gestión de pagos (aprobar/rechazar)
- Acceso completo al dashboard

### Supervisor
- Gestión de empleados
- Registro de pagos físicos
- Gestión de vehículos
- Visualización de deudas y pagos
- Acceso al dashboard

### Conductor
- Visualización de sus propias deudas
- Registro de pagos virtuales (con voucher)
- Visualización de sus propios pagos
- Registro de marcaciones

## API REST

El proyecto incluye un módulo REST (`taxihub-rest`) para la integración con sistemas externos, especialmente para el registro de marcaciones.

### Endpoints disponibles:
- `POST /api/marcaciones` - Registrar marcación

## Base de Datos

El sistema utiliza Flyway para la gestión de migraciones. Las tablas principales incluyen:

- `tbl_personas` - Información de personas
- `tbl_empleados` - Empleados (conductores, supervisores)
- `tbl_autos` - Vehículos
- `tbl_deudas` - Deudas de empleados
- `tbl_pagos` - Pagos realizados
- `tbl_marcaciones` - Registro de entrada/salida
- `tbl_usuarios` - Usuarios del sistema
- `tbl_grupos` - Grupos de trabajo
- `tbl_autoridades` - Permisos del sistema

## Integraciones Externas

El sistema se integra con APIs externas:

- **Factiliza API** - Para validaciones de documentos
- **PeruDevs API** - Para consultas adicionales
- **API de Marcaciones** - Para registro externo de marcaciones

## Desarrollo

### Compilar el proyecto
```bash
# Proyecto principal
cd taxihub
./mvnw clean compile

# API REST
cd ../taxihub-rest
./mvnw clean compile
```

### Ejecutar tests
```bash
./mvnw test
```

## Notas Adicionales

- El sistema utiliza soft delete (eliminación lógica) para mantener el historial
- Todas las entidades incluyen campos de auditoría (fecha_creacion, fecha_actualizacion, etc.)
- Los pagos físicos se aprueban automáticamente
- Los pagos virtuales requieren revisión y aprobación manual
- El sistema calcula automáticamente las deudas basadas en comisiones y salidas

## Contribución

Este es un proyecto académico desarrollado para Cibertec.

## Licencia

Este proyecto es de uso educativo.

## Autor

Desarrollado para el sistema de gestión de taxis TaxiHub.

---

**Versión:** 0.0.1-SNAPSHOT  
**Última actualización:** 2025

