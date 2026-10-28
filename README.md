# Simple Shop - Backend API 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)

API RESTful robusta desarrollada para el e-commerce **Simple Shop**. Gestiona la lógica de negocio, autenticación segura, persistencia de datos y control de accesos.

---

## 🛠️ Tecnologías Principales

- **Lenguaje:** Java 17+
- **Framework:** Spring Boot
- **Seguridad:** Spring Security (JWT y Cookies)
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** MySQL

---

## ✨ Funcionalidades Principales

- **Sistema de Roles y Permisos:** Control de acceso basado en roles (Usuario, Administrador).
- **Autenticación Segura:** Implementación de JSON Web Tokens (JWT) y cookies.
- **Seguridad de Datos:** Encriptación de IDs y contraseñas.
- **CRUD Completo:** Gestión de Productos, Usuarios y Listados.
- **Gestión de Archivos:** Subida, almacenamiento y eliminación de imágenes de productos.
- **Optimización:** Paginación implementada en todas las entidades principales.

---

## 📁 Estructura del Proyecto

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/com/simpleshop/   # Controladores, servicios, entidades y repositorios
│   │   └── resources/            # Configuración de la aplicación (application.properties / yml)
│   └── test/                     # Pruebas unitarias e integración
├── Dockerfile                    # Configuración de Docker para el backend
└── pom.xml                       # Dependencias de Maven