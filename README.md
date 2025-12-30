## Estructura del proyecto

```
main/src/com.techlab.store/   
 ├── controller      # Gestiona las peticiones HTTP y la API REST.
 ├── dto             # Clases de transferia de datos DTO.
 ├── entity/         # Clases que mapean las tablas de la base de datos (JPA).
 ├── repository/     # Interfaz para el acceso y manipulación de datos (CRUD).
 ├── service/        # Contiene la lógica de negocio y las transacciones.
 ├── utils/          # Clases de ayuda.
 └── ProyectoFinalApplication.java # Punto de entrada de la aplicación Spring Boot.
```

#### RECURSOS Y CONFIGURACIÓN:
```
└── src/main/resources/
    ├── application.yml         # Configuración principal de Spring (DB, puertos, etc.).
    ├── clients.sql             # SQL para la inicialización de datos de clientes de prueba.
    └── products.sql            # SQL para la inicialización de datos de productos de prueba.
```


#### ESTADO DEL PROYECTO (SPRING BOOT):

- La __API__ se ejecuta correctamente y gestiona las entidades (User, Client, Order, OrderDetails, Products).
- __BASE__ del proyecto: [ejemplo-proyecto-final](https://github.com/AvilaEducation/clases-java-25254-tt-) ❤️
- __ORIGEN__ de la migración:  [tp-java](https://github.com/dmydna/tp-java)
- __IMPORTANTE__: 💀~~Ninguna de las clases del proyecto ha sido testeada~~.

---

#### EndPoints (funcionales)

root `\api`:
- `\products`
- `\products\{id}`
- `\orders`
- `\orders\{id}`
- `\orders\client\id`
- `\client`
- `\client\{id}`

---

#### Diagrama ER.

![](ER_graph.png)


<details>
<summary>Ver Estructuras</summary>
<hr>

```
// Pseudocodigo

Product {
    Long id,
    String name,
    String description,
    Double price,
    String category,
    Integer stock,
    Boolean deleted,
    Date deletedDate
}

Order {
   Long id,
   Int state,
   Client client,
   List<OrderDetail> details 
}

OrderDetails {
   Long id,
   Product producto,
   Int quantitty
}

Client {
   Long id,
   String name,
   String email,
   List<Orders> orders,
   Boolean deleted
}
```

<hr>
</details>



#### PENDIENTES (MAS CRITICOS):
-    **Manejo de Errores:** Hacen falta implementaciones de **Excepciones** a nivel global y métodos para devolver **ResponseEntity** (códigos HTTP correctos como 404, 400, 201) en los controladores.
-   **Validaciones:** Se requieren validaciones completas (ej. @NotNull, @Size) en las entidades y validaciones de negocio en la capa Service.
-   **Testing:** ~~Ninguna de las clases principales ha sido testeada~~

