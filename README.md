Levanta docker.
Genera las imagenes a partir del docker-compose y levanta las:docker compose up -d --build
la consola de kafka-ui estara en: http://localhost:8080/
Las peticiones a los micros iran por el: http://localhost:8089

## Arquitectura y Flujo de Eventos

```mermaid
flowchart LR
    Cliente["Cliente / Swagger UI<br/>(Puerto 8089)"] -->|HTTP| Gateway["ms-api-gateway<br/>(:8089)"]

    subgraph Microservicios["Microservicios REST"]
        Pedidos["ms-pedidos<br/>(:8083) /api/orders"]
        Inventario["ms-inventario<br/>(:8084) /api/inventory"]
        Productor["servicio-pedidos<br/>(:8081) /pedidos"]
    end

    Gateway -->|/api/orders/**| Pedidos
    Gateway -->|/api/inventory/**| Inventario
    Gateway -->|/pedidos/**| Productor

    Pedidos -.->|"OpenFeign: GET /{code}<br/>(Check Stock)"| Inventario

    subgraph Mensajeria["Broker y Esquemas"]
        Kafka[("Apache Kafka +<br/>Schema Registry")]
    end

    Pedidos -->|Publica Evento| Kafka
    Productor -->|Publica Evento| Kafka

    subgraph Consumidores["Consumidores (Grupo de Consumo)"]
        Notif1["servicio-notificaciones<br/>(Instancia 1)"]
        Notif2["servicio-notificaciones<br/>(Instancia 2)"]
    end

    Kafka -->|Consume Evento| Notif1
    Kafka -->|Consume Evento| Notif2

    Notif1 -.->|"OpenFeign: POST /{code}/decrease<br/>(Descontar Stock)"| Inventario
    Notif2 -.->|"OpenFeign: POST /{code}/decrease<br/>(Descontar Stock)"| Inventario
```