# Migration to Microservices: Effects on Energy Consumption
## About this Project
The prototype is developed using Java Spring Boot.
The focus of the project was to explore the Monolithic migration and decomposition effects the energy consumption, performance and maintainability.  

A **reference monolithic Java retail application** (``/Monolithic``) was created from scratch using Spring Boot, offering full control and comparability — a rare advantage over proprietary systems. Its simplified design enables focused analysis of migration effects.

---

## Project Context
A digital retail platform that expands needs an integrated operational system that handles user enrollment and product administration and order processing, and shipping logistics. The system needs centralized management because growing customer numbers and merchant involvement requires streamlined operations for better efficiency.

The system provides a complete retail management platform that enables users to register and explore products by categories, make orders, and monitor their purchase delivery status.
The system enables merchants to handle their product catalog through item management and order listing retrieval and new item creation, and order status updates.
The system administrator controls all platform operations through their ability to handle user sign-ups and category management and item administration and merchant oversight, and complete order and order and shipping management.

The platform supports actual retail operations through user registration and category and item management and order processing, and shipping coordination, which users and merchants, and administrators can monitor.
The system began as a single unified application to simplify its initial development process and deployment. The system transitioned to a microservices architecture because it needed to handle growing ecosystem requirements for scalability and maintenance, and system resilience.
The system serves two main functions by operating as a commercial platform while demonstrating a step-by-step transition from monolithic to microservices architecture, which shows architectural methods and business continuity maintenance during the transformation process.

---

## Architecture & Project Structure
This backend-only application follows a **Domain-Driven Design (DDD)** and **n-tiered architecture**:

- Presentation Layer: Spring Boot controllers for REST endpoints  
- Business Layer: Core domain logic  
- Persistence Layer: Data interaction via Spring Data JPA  
- Model Layer: Domain objects and DTOs  
- Security Layer: Spring Security with JWT authentication  

Key characteristics include:

- Stateless RESTful API
- SOLID design principles for modularity and maintainability  
- ACID-compliant MySQL backend  
- No caching or data denormalisation—to ensure consistency across migration phases  

---

## Tech Stack & Prerequisites

| Component                   | Version / Notes                                           |
|-----------------------------|-----------------------------------------------------------|
| Java                        | 17 or higher                                              |
| Spring Boot                 | 3.2.2                                                     |
| Apache Maven                | 3.9.9 or higher                                           |
| MySQL                       | 8.0                                                       |
| Apache JMeter               | 5.6.3 (benchmarking)                                      |
| Go                          | 1.21 (needed for hardware sensor integration)             |
| Kubernetes + `kubectl`      | v1.20+ with proper cluster access                         |

---

## Setup & Installation

### Jenkins Pipelines
Each migration/decomposition stage includes its own Jenkins pipeline under `/jenkins/jenkins` subfolder on each Phase.

On after every pipeline run, expose the services with the following commands:
```bash
sudo kubectl port-forward -n kepler svc/kepler 28282:28282 &
sudo kubectl port-forward --address localhost -n monitoring service/prometheus-k8s 9090:9090 &
sudo kubectl port-forward --address localhost -n monitoring service/grafana 3000:3000 &
sudo kubectl port-forward --address localhost -n retailproject-namespace service/retailproject-loadbalancer-service 8080:8080
```
### Grafana Dashboard
Import the dashboard located at `/Grafana Dashboard`.

---

## Design
The application’s architecture will be illustrated here. The diagrams created for this project do not cover every level of abstraction, as some are not essential for understanding the project.

### Domain Model
![Domain Model](/Monolithic/retailproject/doc/domain_model/domain_model.png)
### Use Cases
| Functional Requirement | Description |
|------------------------|-------------|
| UC-001 | The user must be able to register itself. |
| UC-002 | The admin user must be able to register an admin user. |
| UC-003 | The admin user must be able to register as a merchant user. |
| UC-004 | The admin user must be able to get a list of categories. |
| UC-005 | The admin user must be able to create a category. |
| UC-006 | The admin user must be able to update a category. |
| UC-007 | The admin user must be able to delete a category. |
| UC-008 | The admin user must be able to get a list of all items. |
| UC-009 | The merchant user must be able to get a list of his items. |
| UC-010 | The admin user must be able to get an item using its identifier. |
| UC-011 | The merchant user must be able to get his item using its identifier. |
| UC-012 | The merchant user must be able to create an item. |
| UC-013 | The admin user must be able to update an item. |
| UC-014 | The admin user must be able to delete an item. |
| UC-015 | The admin user must be able to get a list of merchants. |
| UC-016 | The admin user must be able to get a merchant using its identifier. |
| UC-017 | The admin user must be able to create a merchant. |
| UC-018 | The admin user must be able to update a merchant. |
| UC-019 | The admin user must be able to delete a merchant. |
| UC-020 | The admin user must be able to get a list of all merchant orders. |
| UC-021 | The merchant user must be able to get a list of his merchant orders. |
| UC-022 | The admin user must be able to get a merchant order using its identifier. |
| UC-023 | The merchant user must be able to get his merchant order using its identifier. |
| UC-024 | The merchant user must be able to update his merchant order. |
| UC-025 | The admin user must be able to get a list of orders. |
| UC-026 | The user must be able to get a list of his orders. |
| UC-027 | The admin user must be able to get an order using its identifier. |
| UC-028 | The user must be able to get his order using its identifier. |
| UC-029 | The user must be able to create his order. |
| UC-030 | The admin user must be able to delete an order. |
| UC-031 | The admin user must be able to cancel an order using its identifier. |
| UC-032 | The user must be able to cancel his order using its identifier. |
| UC-033 | The admin user must be able to update an order. |
| UC-034 | The admin user must be able to get a list of shipping orders. |
| UC-035 | The user must be able to get a list of his shipping orders. |
| UC-036 | The admin user must be able to get a shipping order using its identifier. |
| UC-037 | The user must be able to get his shipping order using its identifier. |
| UC-038 | The admin user must be able to update a shipping order. |

### Bounded Contexts
![Domain Model With Bounded Contexts](/Monolithic/retailproject/doc/domain_model/domain_model_with_bounded_contexts.png)

### Monolithic Layer Diagram
![MonolithicLayerDiagram](/Monolithic/retailproject/doc/MonolithicLayerDiagram.png)

### Monolithic Layer Diagram
![Monolithic Layer Diagram](/Monolithic/retailproject/doc/MonolithicLayerDiagram.png)

### Microservices Architecture
![Microservices Architecture](/Monolithic/retailproject/doc/MicroservicesArchitecture.png)
---

## Repository Layout

```bash
energy-consumption-monolithic-migration-decomposition/
├── LICENSE
├── README.md
├── Grafana Dashboard
│   └── Kepler Grafana Dashboard.json  # Grafana Dashboard configuration
├── Monolithic/                        # Phase 0 - Baseline monolithic retail application
│   ├── build.sh
│   ├── database_kubernetes.yaml
│   ├── docker-compose.yml
│   ├── retailproject_kubernetes.yaml
│   ├── jenkins/                         # Jenkins pipeline configs
│   ├── jmeter/                          # JMeter performance testing files
│   └── retailproject/
├── PhaseOne/                          # Phase 1 – microservices migration and decomposition
│   ├── APIGatewayApplication/           # API Gateway Spring Boot service
│   ├── LoadBalancerApplication/         # Load balancer Spring Boot service
│   ├── jenkins/                         # Jenkins pipeline for Phase One
│   └── jmeter/                          # JMeter test plans for Phase One
│   └── Microservices/                   # Microservices
│   │   └── User/                          # User Microservices
│   └── Monolithic/                      # Monolithic application for Phase One
├── PhaseTwo/                          # Phase 2 – microservices migration and decomposition
│   ├── APIGatewayApplication/           # API Gateway Spring Boot service
│   ├── LoadBalancerApplication/         # Load balancer Spring Boot service
│   ├── jenkins/                         # Jenkins pipeline for Phase Two
│   └── jmeter/                          # JMeter test plans for Phase Two
│   └── Microservices/                   # Microservices
│   │   ├── Items/                         # Items Microservices
│   │   └── User/                          # User Microservices
│   └── Monolithic/                      # Monolithic application for Phase Two
├── PhaseThree/                        # Phase 3 – microservices migration and decomposition
│   ├── APIGatewayApplication/           # API Gateway Spring Boot service
│   ├── LoadBalancerApplication/         # Load balancer Spring Boot service
│   ├── jenkins/                         # Jenkins pipeline for Phase Three
│   └── jmeter/                          # JMeter test plans for Phase Three
│   └── Microservices/                   # Microservices
│   │   ├── Items/                         # Items Microservices
│   │   ├── Orders/                        # Orders Microservices 
│   │   └── User/                          # User Microservices
│   └── Monolithic/                      # Monolithic application for Phase Four
├── PhaseFour/                         # Phase 4 – microservices migration and decomposition
│   ├── APIGatewayApplication/           # API Gateway Spring Boot service
│   ├── LoadBalancerApplication/         # Load balancer Spring Boot service
│   ├── jenkins/                         # Jenkins pipeline for Phase Five
│   └── jmeter/                          # JMeter test plans for Phase Five
│   └── Microservices/                   # Microservices
│       ├── Items/                         # Items Microservices
│       ├── MerchantOrders/                # MerchantOrders Microservices
│       ├── Orders/                        # Orders Microservices 
│       ├── ShippingOrders/                # Shipping Orders Microservices
│       └── User/                        # User Microservices
├── PhaseFive/                         # Phase 5 – Final phase microservices migration and decomposition
│   ├── APIGatewayApplication/           # API Gateway Spring Boot service
│   ├── LoadBalancerApplication/         # Load balancer Spring Boot service
│   ├── jenkins/                         # Jenkins pipeline for Phase Five
│   └── jmeter/                          # JMeter test plans for Phase Five
│   └── Microservices/                   # Microservices
│       ├── Items/                       # Items Write Microservices
│       ├── MerchantOrders/              # MerchantOrders Write Microservices
│       ├── Orders/                      # Orders Write Microservices 
│       ├── Reads/                       # Microservices Read
│       │   ├── Items/                     # Items Read Microservices
│       │   ├── MerchantOrders/            # Merchant Orders Read Microservices
│       │   ├── Orders/                    # Orders Read Microservices 
│       │   └── ShippingOrders/            # Shipping Orders Read Microservices
│       ├── ShippingOrders/              # Shipping Orders Write Microservices
│       └── User/                        # User Microservices
└── Results                            # Results of energy consumption, mantainability, performance
    ├── JMeter                         # Apache JMeter Result about performance
    │   ├── ThreadGroup                  # Performance of Thread Group tests
    │   ├── BZM                          # Performance of BZM tests
    │   └── JPGC                         # Performance of JPGC tests
    └── Kepler                         # Kepler Result about energy consumption
    |   ├── ThreadGroup                  # Energy consumption of Thread Group tests
    |   ├── BZM                          # Energy consumption of BZM tests
    |   └── JPGC                         # Energy consumption of JPGC tests
    └── Sonargraph                     # Sonargraph Result about maintainability
        ├── Monolithic                   # Maintainability of Monolithic
        ├── PhaseOne                     # Maintainability of Phase One
        ├── PhaseTwo                     # Maintainability of Phase Two
        ├── PhaseThree                   # Maintainability of Phase Three
        ├── PhaseFour                    # Maintainability of Phase Four
        └── PhaseFive                    # Maintainability of Phase Five
```
