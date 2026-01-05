# PricePulse 📈

PricePulse is a robust Java-based product price tracker and analytics dashboard. It automatically monitors prices across multiple e-commerce platforms (like Amazon and Flipkart), stores historical data, and triggers alerts when targeted prices are met or significant price drops occur.

## 🚀 Features

-   **Multi-Site Support**: Built-in scrapers for Amazon and Flipkart using Jsoup.
-   **Automated Tracking**: Scheduled hourly and daily scraping jobs.
-   **Price History**: Detailed logging of every price fluctuation.
-   **Smart Alerts**: Instant email notifications for:
    -   Target price reached.
    -   Significant price drops (>5%).
-   **Analytics**: Computes min, max, and average prices for every product.
-   **RESTful API**: Comprehensive endpoints for product and alert management.
-   **Global Error Handling**: Standardized API error responses.

## 🛠️ Tech Stack

-   **Backend**: Spring Boot 3.5.9
-   **Language**: Java 17
-   **Data Access**: Spring Data JPA (Hibernate)
-   **Database**: MySQL (Production/Dev), H2 (Testing)
-   **Scraping**: Jsoup
-   **Mailing**: Spring Boot Starter Mail
-   **Scheduling**: Spring Task Scheduling
-   **Build Tool**: Maven

## 📋 Prerequisites

-   Java 17 or higher
-   MySQL Server
-   Maven (or use included `./mvnw`)

## ⚙️ Configuration

1.  Create a MySQL database named `Pricetracker`.
2.  Update `src/main/resources/application.properties` with your database credentials:
    ```properties
    spring.datasource.username=root
    spring.datasource.password=your_password
    ```
3.  (Optional) Enable email notifications:
    ```properties
    app.email.enabled=true
    spring.mail.host=smtp.example.com
    # ... other mail settings
    ```

## 🏃 Running the Application

Clone the repository and run:
```bash
cd PriceTracker
./mvnw spring-boot:run
```
The server will start at `http://localhost:8080`.

## 🔌 API Endpoints

### Product Management
- `POST /api/products`: Add a product to track.
- `GET /api/products`: List all tracked products.
- `GET /api/products/{id}`: Detailed product info and analytics.
- `DELETE /api/products/{id}`: Remove a product.
- `POST /api/products/{id}/scrape`: Manually trigger a price update.

### Analysis & Alerts
- `GET /api/products/{id}/prices`: Get historical price records.
- `GET /api/products/{id}/analytics`: Get min/max/avg price analysis.
- `GET /api/alerts/pending`: See triggered alerts that haven't been notified.

## 📂 Project Structure

-   `com.PriceTracker.demo.models`: JPA Entities (Product, PriceHistory, Alert)
-   `com.PriceTracker.demo.repositories`: Data Access Layer
-   `com.PriceTracker.demo.service`: Business Logic Layer
-   `com.PriceTracker.demo.scraper`: Web Scraping Strategy Implementations
-   `com.PriceTracker.demo.controller`: REST Controllers
-   `com.PriceTracker.demo.scheduler`: Automated background tasks

## 🧪 Testing

Run the test suite:
```bash
./mvnw test
```
*Note: Tests use an in-memory H2 database and do not require MySQL.*

---
Built with ❤️ by PricePulse Team.
