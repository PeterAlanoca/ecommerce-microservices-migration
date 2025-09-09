# 🏗️ Sistema de Microservicios - Migración desde Monolito Transaccional

Este proyecto implementa una arquitectura de microservicios que migra la funcionalidad de un monolito transaccional a servicios independientes, manteniendo la integridad de las transacciones distribuidas.

## 📋 Tabla de Contenidos

- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Servicios](#servicios)
- [Bases de Datos](#bases-de-datos)
- [Configuración](#configuración)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [APIs Disponibles](#apis-disponibles)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Monitoreo](#monitoreo)
- [Troubleshooting](#troubleshooting)

## 🏗️ Arquitectura del Sistema

### Diagrama de Arquitectura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Cliente       │    │   API Gateway   │    │  Discovery      │
│   (Frontend)    │◄──►│   (Port 8080)   │◄──►│  Service        │
└─────────────────┘    └─────────────────┘    │  (Port 8761)    │
                              │                └─────────────────┘
                              │
                    ┌─────────┼─────────┐
                    │         │         │
            ┌───────▼───┐ ┌───▼───┐ ┌───▼────┐
            │   Sales   │ │Product│ │Accounting│
            │ Service   │ │Service│ │ Service  │
            └───────┬───┘ └───┬───┘ └───┬────┘
                    │         │         │
            ┌───────▼───┐ ┌───▼───┐ ┌───▼────┐
            │PostgreSQL │ │ MySQL │ │PostgreSQL│
            │  (Sales)  │ │(Warehouse)│ │(Accounting)│
            └───────────┘ └───────┘ └─────────┘
```

### Patrones Implementados

- **Strangler Fig Pattern**: Migración gradual del monolito
- **Database per Service**: Cada microservicio tiene su propia base de datos
- **API Gateway Pattern**: Punto de entrada único
- **Service Discovery**: Registro automático de servicios
- **Saga Pattern**: Transacciones distribuidas

## 🚀 Servicios

### 1. Discovery Service (Eureka Server)
- **Puerto**: 8761
- **Función**: Registro y descubrimiento de servicios
- **URL**: http://localhost:8761

### 2. API Gateway
- **Puerto**: 8080
- **Función**: Punto de entrada único, enrutamiento y balanceo de carga
- **URL**: http://localhost:8080

### 3. Sales Service
- **Puerto**: Dinámico (registrado en Eureka)
- **Función**: Gestión de ventas y orquestación de transacciones
- **Base de datos**: PostgreSQL (sales)

### 4. Product Service (Warehouse)
- **Puerto**: Dinámico (registrado en Eureka)
- **Función**: Gestión de inventarios y productos
- **Base de datos**: MySQL (warehouse)

### 5. Accounting Service
- **Puerto**: Dinámico (registrado en Eureka)
- **Función**: Diario contable y registros contables
- **Base de datos**: PostgreSQL (accounting)

## 🗄️ Bases de Datos

### MySQL - Warehouse (Inventarios)
- **Puerto**: 13306
- **Base de datos**: warehouse
- **Usuario**: root
- **Contraseña**: 123456

### PostgreSQL - Sales (Ventas)
- **Puerto**: 15432
- **Base de datos**: sales
- **Usuario**: postgres
- **Contraseña**: 123456

### PostgreSQL - Accounting (Contabilidad)
- **Puerto**: 15432
- **Base de datos**: accounting
- **Usuario**: postgres
- **Contraseña**: 123456

## ⚙️ Configuración

### Variables de Entorno Requeridas

```bash
# MySQL (Warehouse)
MYSQL_HOST=localhost
MYSQL_PORT=13306
MYSQL_DATABASE=warehouse
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456

# PostgreSQL (Sales & Accounting)
POSTGRES_HOST=localhost
POSTGRES_PORT=15432
POSTGRES_SALES_DATABASE=sales
POSTGRES_ACCOUNTING_DATABASE=accounting
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=123456
```

### Configuración de Eureka

Todos los microservicios están configurados para registrarse automáticamente en Eureka:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```

## 🚀 Instalación y Ejecución

### Prerrequisitos

- Java 17+
- Maven 3.6+
- Docker (para las bases de datos)
- Git

### 1. Configurar Bases de Datos

#### MySQL (Warehouse)
```bash
docker run --name mysql-warehouse -e MYSQL_ROOT_PASSWORD=123456 -p 13306:3306 -d mysql:9
```

#### PostgreSQL (Sales & Accounting)
```bash
docker run --name postgres-db -e POSTGRES_PASSWORD=123456 -p 15432:5432 -d postgres
```

### 2. Crear Bases de Datos

#### MySQL - Warehouse
```sql
CREATE DATABASE warehouse;
USE warehouse;

CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2),
    sku VARCHAR(50) UNIQUE,
    stock_quantity INT NOT NULL DEFAULT 0,
    min_stock_level INT DEFAULT 0,
    max_stock_level INT DEFAULT 1000,
    supplier VARCHAR(255),
    brand VARCHAR(100),
    weight DECIMAL(8,2),
    dimensions VARCHAR(50),
    status ENUM('active', 'inactive', 'discontinued') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### PostgreSQL - Sales
```sql
CREATE DATABASE sales;
\c sales

CREATE TABLE Sale (
    id SERIAL PRIMARY KEY,
    sale_number VARCHAR(20) UNIQUE NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_amount DECIMAL(12,2),
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(12,2) GENERATED ALWAYS AS (total_amount - discount_amount) STORED,
    sale_date DATE NOT NULL DEFAULT CURRENT_DATE,
    customer_id INT,
    customer_name VARCHAR(255),
    salesperson VARCHAR(100),
    payment_method VARCHAR(50) DEFAULT 'cash',
    payment_status VARCHAR(20) DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### PostgreSQL - Accounting
```sql
CREATE DATABASE accounting;
\c accounting

CREATE TABLE JOURNAL (
    id SERIAL PRIMARY KEY,
    journal_entry_number VARCHAR(20) UNIQUE NOT NULL,
    transaction_date DATE NOT NULL,
    posting_date DATE DEFAULT CURRENT_DATE,
    account_code VARCHAR(20) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    reference_number VARCHAR(50),
    debit_amount DECIMAL(15,2) DEFAULT 0.00,
    credit_amount DECIMAL(15,2) DEFAULT 0.00,
    balance_type CHAR(1) CHECK (balance_type IN ('D', 'C')),
    department VARCHAR(100),
    cost_center VARCHAR(50),
    project_code VARCHAR(50),
    currency_code CHAR(3) DEFAULT 'USD',
    exchange_rate DECIMAL(10,6) DEFAULT 1.000000,
    source_document VARCHAR(100),
    created_by VARCHAR(100) NOT NULL,
    approved_by VARCHAR(100),
    approval_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'draft',
    reversed_by_entry VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. Ejecutar Servicios

**Orden de ejecución recomendado:**

```bash
# Terminal 1: Discovery Service
cd discovery
mvn spring-boot:run

# Terminal 2: API Gateway
cd gateway
mvn spring-boot:run

# Terminal 3: Accounting Service
cd accounting
mvn spring-boot:run

# Terminal 4: Product Service
cd producto
mvn spring-boot:run

# Terminal 5: Sales Service
cd sales
mvn spring-boot:run
```

## 📡 API Principal

### Sales Service - Crear Venta

```bash
curl --location 'http://localhost:8080/ms-sales/api/sales?quantity=1' \
--header 'Content-Type: application/json' \
--data '{
  "id": 1,
  "name": "Laptop con procesador Intel i5, 8GB RAM, 256GB SSD",
  "price": 899.99,
  "stockQuantity": 25
}'
```

## 💡 Ejemplo de Uso

### Respuesta Esperada

Al ejecutar el comando de la API de sales, recibirás una respuesta como esta:

```json
{
  "id": 1,
  "saleNumber": "SALE-375C19D0",
  "productId": 1,
  "quantity": 1,
  "unitPrice": 899.99,
  "totalAmount": 899.99,
  "discountPercentage": 0,
  "discountAmount": 0,
  "finalAmount": 899.99,
  "saleDate": "2025-09-08",
  "customerId": null,
  "customerName": null,
  "salesperson": null,
  "paymentMethod": "cash",
  "paymentStatus": "pending",
  "notes": null,
  "createdAt": "2025-09-08T20:57:03.8441349",
  "updatedAt": "2025-09-08T20:57:03.8441349"
}
```

### Flujo de Transacción

1. **Cliente** envía petición de venta al Gateway
2. **Gateway** enruta la petición al Sales Service
3. **Sales Service** valida el producto con Product Service
4. **Sales Service** actualiza el stock en Product Service
5. **Sales Service** crea registros contables en Accounting Service
6. **Sales Service** guarda la venta en su base de datos
7. **Respuesta** se devuelve al cliente

## 📊 Monitoreo

### Eureka Dashboard
- **URL**: http://localhost:8761
- **Función**: Ver servicios registrados y su estado

### Health Checks
- **Gateway**: http://localhost:8080/actuator/health
- **Sales**: http://localhost:8080/ms-sales/actuator/health
- **Product**: http://localhost:8080/ms-producto/actuator/health
- **Accounting**: http://localhost:8080/ms-accounting/actuator/health

## 🔧 Troubleshooting

### Problemas Comunes

#### 1. Error de Conexión a Base de Datos
```bash
# Verificar que los contenedores estén ejecutándose
docker ps

# Verificar conectividad
docker exec -it mysql-warehouse mysql -u root -p
docker exec -it postgres-db psql -U postgres
```

#### 2. Servicios No se Registran en Eureka
```bash
# Verificar que Discovery Service esté ejecutándose
curl http://localhost:8761/eureka/apps

# Verificar logs de los microservicios
tail -f logs/application.log
```

#### 3. Error de Duplicado en Journal
El sistema incluye lógica de reintento automático para manejar números de asiento duplicados.

#### 4. Error de Longitud en Journal Entry Number
Los números de asiento están limitados a 20 caracteres máximo.

### Logs Importantes

```bash
# Ver logs de un servicio específico
tail -f logs/sales-service.log
tail -f logs/accounting-service.log
tail -f logs/product-service.log
```

### Verificar Estado de Transacciones

```sql
-- Verificar ventas
SELECT * FROM sale ORDER BY created_at DESC LIMIT 10;

-- Verificar asientos contables
SELECT journal_entry_number, account_name, debit_amount, credit_amount, created_at 
FROM journal 
ORDER BY created_at DESC 
LIMIT 10;

-- Verificar productos
SELECT id, name, price, stock_quantity FROM product;
```

## 🏗️ Arquitectura vs Monolito

### Ventajas de la Migración

1. **Escalabilidad Independiente**: Cada servicio puede escalarse según demanda
2. **Despliegue Independiente**: Cambios en un servicio no afectan otros
3. **Tecnología Independiente**: Cada servicio puede usar diferentes tecnologías
4. **Equipos Independientes**: Diferentes equipos pueden trabajar en diferentes servicios
5. **Resiliencia**: Fallo de un servicio no afecta completamente el sistema

### Desafíos

1. **Complejidad de Transacciones**: Las transacciones distribuidas son más complejas
2. **Latencia de Red**: Comunicación entre servicios añade latencia
3. **Manejo de Errores**: Más complejo en arquitectura distribuida
4. **Consistencia Eventual**: Los datos pueden no estar inmediatamente consistentes

## 📝 Notas de Desarrollo

- **Java Version**: 17
- **Spring Boot Version**: 3.5.5
- **Spring Cloud Version**: 2025.0.0
- **Maven**: Gestión de dependencias
- **Docker**: Contenedores para bases de datos

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

**Desarrollado como parte del curso de Arquitectura de Microservicios - Universidad Católica Boliviana**
