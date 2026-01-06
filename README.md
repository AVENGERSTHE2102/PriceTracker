# PricePulse 📈

PricePulse is a premium full-stack price tracking solution. It combines a robust **Java Spring Boot** backend with a modern **React + Vite** frontend to provide real-time monitoring, visualization, and alerts for products on Amazon and Flipkart.

## 🚀 Features

### Frontend (Dashboard)
-   **Premium UI**: Glassmorphism design with dark mode, backdrop blurs, and smooth animations.
-   **Predictive Pulse Charts**: Interactive Area Charts (Recharts) visualizing price trends over time.
-   **Real-time Inventory**: Searchable grid view of all tracked products with status indicators.
-   **Smart Wizard**: Step-by-step "Add Product" modal with validation.
-   **Responsive**: Fully optimized for desktop and tablet experiences.

### Backend (Engine)
-   **Multi-Site Support**: Scraping engine for **Amazon** and **Flipkart** (Jsoup).
-   **Automated Tracking**: Configurable scheduled jobs (Hourly/Daily) (`@Scheduled`).
-   **Smart Alerts**: Email notifications for price drops and target matches.
-   **Analytics**: Computes Min/Max/Avg prices and historical trends.
-   **REST API**: robust endpoints for management and data analysis.

## 🛠️ Tech Stack

### Frontend
-   **Framework**: React 18, Vite
-   **Styling**: Vanilla CSS (Variables, Glassmorphism, Animations)
-   **Visualization**: Recharts
-   **Icons**: Lucide React
-   **HTTP Client**: Axios

### Backend
-   **Core**: Spring Boot 3.5.9, Java 17
-   **Database**: MySQL 8.0 (JPA/Hibernate)
-   **Tools**: Lombok, Maven, Jsoup

## 📋 Prerequisites

-   Java 17+
-   Node.js 18+ (for frontend)
-   MySQL Server

## ⚙️ Configuration

1.  **Database Setup**:
    Create a MySQL database named `Pricetracker`.
    ```sql
    CREATE DATABASE Pricetracker;
    ```

2.  **Backend Config**:
    Update `src/main/resources/application.properties`:
    ```properties
    spring.datasource.username=root
    spring.datasource.password=your_password
    # Optional: Email Settings
    app.email.enabled=true
    spring.mail.host=smtp.example.com
    ```

## 🏃 Getting Started

### 1. Start the Backend
```bash
cd PriceTracker
./mvnw spring-boot:run
```
*Server runs on `http://localhost:8080`*

### 2. Start the Frontend
Open a new terminal:
```bash
cd PriceTracker/frontend
npm install
npm run dev
```
*Dashboard accessible at `http://localhost:3000`*

## 🔌 API Overview
The backend exposes a full REST API at `http://localhost:8080/api`:

-   `GET /products` - List inventory
-   `POST /products` - Track new URL
-   `GET /products/{id}/analytics` - Price analysis
-   `POST /products/{id}/scrape` - Force refresh

## 📂 Project Structure

```
PriceTracker/
├── src/main/java/com/PriceTracker/demo/  # Java Backend Source
│   ├── controller/   # REST Endpoints
│   ├── service/      # Business Logic
│   ├── scraper/      # Scraping Engine
│   └── models/       # Database Entities
└── frontend/         # React Frontend Source
    ├── src/components/ # UI Components (Card, Modal, Chart)
    └── src/services/   # API Integration
```

---
Built with ❤️ by the PricePulse Team.
