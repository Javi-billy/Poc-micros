com.tuempresa.inventario
├── domain
│   ├── exception          <-- ProductNotFoundException, InsufficientStockException
│   ├── model              <-- ProductStock (Reglas puras de negocio)
│   ├── port
│   │   ├── in             <-- CheckStockUseCase, DecreaseStockUseCase
│   │   └── out            <-- ProductStockRepositoryPort
│   └── service            <-- InventoryService (Implementa ambos casos de uso)
└── infrastructure
    ├── adapter
    │   ├── in
    │   │   └── web        <-- InventoryController y GlobalExceptionHandler
    │   └── out
    │       └── jpa        <-- ProductStockJpaEntity, SpringData y Adaptador
    └── config             <-- BeanConfiguration y datos iniciales para pruebas