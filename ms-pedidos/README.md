com.example.pedidos
├── domain
│   ├── exception          <-- Excepciones de negocio puras
│   ├── model              <-- Entidad de dominio Order
│   ├── port
│   │   ├── in             <-- CreateOrderUseCase
│   │   └── out            <-- OrderRepositoryPort, InventoryPort, EventPublisherPort
│   └── service            <-- CreateOrderService (Lógica pura sin Spring)
└── infrastructure
    ├── adapter
    │   ├── in
    │   │   └── web        <-- OrderController y GlobalExceptionHandler
    │   └── out
    │       ├── feign      <-- Clientes Feign (Inventario y Kafka Producer)
    │       └── jpa        <-- Entidad JPA, SpringData y Adaptador
    └── config             <-- BeanConfiguration (@EnableFeignClients)