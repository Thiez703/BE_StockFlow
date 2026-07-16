<div align="center">
  <h1>📦 STOCKFLOW</h1>
  <p><b>Hệ Thống Quản Lý Kho Hàng - Doanh Nghiệp Phân Phối Bia & Nước Giải Khát</b></p>
  
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
  [![Java](https://img.shields.io/badge/Java-21-blue.svg?logo=java)](#)
  [![MySQL](https://img.shields.io/badge/MySQL-8.4-blue.svg?logo=mysql)](#)
  [![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg?logo=docker)](#)
</div>

---

## 📖 Giới thiệu (Về StockFlow)

**StockFlow** là hệ thống Backend cung cấp RESTful API để quản lý luồng hàng hóa ra vào kho dành riêng cho doanh nghiệp phân phối bia và nước giải khát. Hệ thống giải quyết 3 bài toán cốt lõi:
1. **Tồn kho cập nhật tức thời:** Số liệu không còn bị trễ đến cuối ngày hay cuối tuần. Mọi biến động được cập nhật theo thời gian thực.
2. **Kiểm soát bán lẻ tại kho:** Ghi nhận xuất kho nhanh chóng theo nguyên tắc FEFO, chống rò rỉ số liệu.
3. **Phân tách trách nhiệm:** Tích hợp quy trình kiểm soát chéo, người lập và người duyệt phải khác nhau (đối với kiểm kê và hàng bất thường).

---

## 🚀 Các Chức Năng Chính (Modules)

1. **M1 — Xác thực & Phân quyền:** JWT Authentication, phân quyền Role-Based (ADMIN, MANAGER, ACCOUNTANT, STAFF).
2. **M2 — Quản lý hệ thống:** Quản lý User và xem Nhật ký hoạt động (Audit Logs) cho các thao tác nhạy cảm.
3. **M3 — Dữ liệu nền (Master Data):** Quản lý danh mục, sản phẩm, đơn vị tính quy đổi, lô hàng (theo FEFO), nhà cung cấp, khách hàng và vị trí lưu trữ.
4. **M4 — Nhập kho:** Quản lý phiếu nhập, cập nhật tồn kho tức thời (POSTED) theo đơn vị cơ sở.
5. **M5 — Xuất kho & Bán lẻ:** Tối ưu hóa bán lẻ tại kho (<15s), gợi ý xuất lô theo FEFO, chặn xuất vượt tồn (Optimistic Locking).
6. **M6 — Kiểm kê & Hàng bất thường:** Quy trình phê duyệt phân cấp chặt chẽ (Người lập ≠ Người duyệt).
7. **M7 — Tồn kho & Báo cáo:** Xem thẻ kho (lịch sử biến động), cảnh báo tồn thấp/hết hạn, xuất báo cáo Excel.

---

## 👥 Ma Trận Phân Quyền

| Chức năng | `ADMIN` (Chủ) | `MANAGER` (QL Kho) | `ACCOUNTANT` (Kế toán) | `STAFF` (NV Kho) |
| :--- | :---: | :---: | :---: | :---: |
| Đăng nhập, đổi mật khẩu | ✅ | ✅ | ✅ | ✅ |
| Quản lý user & Xem log | ✅ | ❌ | ❌ | ❌ |
| Quản lý dữ liệu nền | ❌ | ✅ | ❌ | ❌ |
| Lập phiếu nhập/xuất/bán lẻ | ❌ | ✅ | ❌ | ✅ |
| Lập phiếu kiểm kê/bất thường | ❌ | ✅ | ✅ | ✅ |
| Duyệt kiểm kê/bất thường | ✅ | ✅ | ✅ | ❌ |
| Xem thẻ kho, tồn kho, báo cáo | ✅ | ✅ | ✅ | ✅ (Chỉ xem tồn) |

> **Quy tắc Duyệt (BR-04, BR-05):** Người duyệt phải ở cấp bậc cao hơn và **không được trùng** với người lập phiếu. Cấp bậc: `STAFF` < `ACCOUNTANT` < `MANAGER` < `ADMIN`.

---

## 🛠 Công Nghệ Sử Dụng

- **Ngôn ngữ:** Java 21
- **Framework:** Spring Boot 4.1 (Spring Security, Spring Data JPA, Spring Web)
- **Database:** MySQL 8.4
- **Authentication:** JWT (JSON Web Tokens)
- **Build Tool:** Maven 3.9+
- **Deployment:** Docker & Docker Compose

---

## 📂 Cấu Trúc Thư Mục (Directory Structure)

```text
📦 be-stockflow
 ├── 📂 src
 │    └── 📂 main
 │         ├── 📂 java
 │         │    └── 📂 com.vertex.stockflow
 │         │         ├── 📂 annotation   # Các custom annotation của dự án
 │         │         ├── 📂 common       # Các hằng số, helper, class dùng chung
 │         │         ├── 📂 config       # Cấu hình Spring (Security, Swagger,...)
 │         │         ├── 📂 controller   # Nơi tiếp nhận Request và trả về Response (REST API)
 │         │         ├── 📂 dto          # Data Transfer Object (Request, Response)
 │         │         ├── 📂 entity       # Lớp thực thể ánh xạ với bảng trong Database (JPA)
 │         │         ├── 📂 exception    # Xử lý Exception toàn cục (Global Exception Handler)
 │         │         ├── 📂 mapper       # Chuyển đổi dữ liệu giữa Entity và DTO
 │         │         ├── 📂 repository   # Lớp tương tác với Database (Spring Data JPA)
 │         │         ├── 📂 security     # Xử lý xác thực và phân quyền (JWT Filter)
 │         │         ├── 📂 service      # Nơi chứa Logic nghiệp vụ (Business Logic)
 │         │         └── 📄 BeStockflowApplication.java
 │         │
 │         └── 📂 resources
 │              ├── 📂 static            # Chứa các file tĩnh (nếu có)
 │              └── 📄 application.yml   # File cấu hình chính của ứng dụng
 ├── 📄 .env.example       # File mẫu cấu hình biến môi trường (Database, Secret keys...)
 ├── 📄 docker-compose.yml # File cấu hình Docker để chạy DB và các service
 ├── 📄 pom.xml            # File cấu hình và quản lý thư viện của Maven
 ├── 📄 .gitignore         # File cấu hình bỏ qua các file không cần commit lên Git
 └── 📄 README.md          # Tài liệu hướng dẫn chi tiết của dự án
```

---

## 🔐 Cấu Hình Biến Môi Trường (.env)

Trong quá trình phát triển, việc hardcode (gắn cứng) các thông tin nhạy cảm như *tài khoản/mật khẩu Database, chuỗi secret JWT, port chạy ứng dụng...* trực tiếp vào file code (`application.yml` hay `docker-compose.yml`) là cực kỳ nguy hiểm. Nếu sơ ý commit các file này lên Github, dự án có thể bị lộ thông tin bảo mật. 

Đó là lý do chúng ta cần file `.env`:
1. **Bảo mật:** File `.env` sẽ được đưa vào `.gitignore` để không bao giờ bị push lên Git. Các thông tin quan trọng luôn được giữ an toàn ở local của bạn.
2. **Linh hoạt:** Mỗi thành viên trong team có thể tùy chỉnh môi trường Dev của mình (VD: dùng mật khẩu DB khác nhau, chạy Port khác nhau) mà không làm ảnh hưởng đến code chung của người khác.

**Cách tạo và sử dụng file `.env`:**
1. Tại thư mục gốc của dự án (ngang hàng với `pom.xml`), tạo một file mới có tên chính xác là `.env`.
2. Copy toàn bộ nội dung từ file `.env.example` (nếu có) và dán sang file `.env`.
3. Thay đổi các giá trị trong `.env` cho phù hợp với môi trường local của bạn. Ví dụ:
   ```env
   DB_URL=jdbc:mysql://localhost:3309/stockflow
   DB_USER=root
   DB_PASSWORD=123456
   JWT_SECRET=your_super_secret_key_here
   ```
4. Đảm bảo cấu hình trong `application.yml` và `docker-compose.yml` đang đọc dữ liệu từ biến môi trường thay vì chuỗi cố định (VD: `${DB_PASSWORD}`).

---

## ⚙️ Hướng Dẫn Cài Đặt & Chạy Dự Án (Dành cho Dev)

### 1. Yêu cầu môi trường
- Java JDK 21
- Apache Maven 3.9+
- Docker & Docker Compose (Để chạy MySQL dễ nhất)
- IDE khuyên dùng: IntelliJ IDEA

### 2. Clone mã nguồn
```bash
git clone https://github.com/your-org/be-stockflow.git
cd be-stockflow
```

### 3. Khởi động Database (MySQL 8.4)
Hệ thống yêu cầu MySQL đang chạy ở port `3309` (hoặc cấu hình lại trong `application.yml`). Chạy lệnh sau để dùng Docker:

```bash
# Khởi chạy bằng Docker Compose (nếu đã có file docker-compose.yml)
docker compose up -d

# Hoặc khởi chạy thủ công bằng Docker run:
docker run --name stockflow-db \
  -p 3309:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=stockflow \
  -d mysql:8.4
```

### 4. Cấu hình ứng dụng
Mở file `src/main/resources/application.yml` và chắc chắn config database khớp với môi trường của bạn:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3309/stockflow?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
    username: root
    password: 123456
```

### 5. Chạy ứng dụng
Dùng Maven wrapper có sẵn trong project:
```bash
# Trên Windows
mvnw.cmd spring-boot:run

# Trên Linux/Mac
./mvnw spring-boot:run
```
> App chạy mặc định tại: `http://localhost:8080/api`

---

## 🌿 Quy Trình Làm Việc Với Git (Dành Cho Team)

Để dự án được quản lý tốt, các thành viên tuân thủ luồng Git Flow sau:

### 1. Phân chia Branch
- `main`: Chứa code Production (Ổn định nhất). **Tuyệt đối không push trực tiếp lên main.**
- `develop`: Nhánh chính để ghép code từ các feature. Chứa code cho môi trường dev.
- `feature/<tên-chức-năng>`: Nhánh làm tính năng mới (Tạo ra từ `develop`). Ví dụ: `feature/auth-jwt`, `feature/inbound-api`.
- `hotfix/<tên-lỗi>`: Nhánh sửa lỗi khẩn cấp trên production (Tạo ra từ `main`).

### 2. Quy trình Commit & Push
1. Kéo code mới nhất từ develop về trước khi bắt đầu làm: 
   ```bash
   git checkout develop
   git pull origin develop
   ```
2. Tạo nhánh feature mới:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Code và Commit (Viết message rõ ràng):
   ```bash
   git add .
   git commit -m "feat: thêm API tạo phiếu nhập kho" 
   ```
   > **Quy ước Commit Message:** 
   > - `feat:` Tính năng mới
   > - `fix:` Sửa lỗi
   > - `refactor:` Tối ưu code (không làm thay đổi logic)
   > - `docs:` Bổ sung tài liệu
4. Push nhánh lên Github và tạo **Pull Request (PR)** vào nhánh `develop`.
5. Đợi Code Review và Merge.

### 3. Coding Convention (Quy ước Code)
- Tên Class: `PascalCase` (VD: `ProductController`)
- Tên biến & method: `camelCase` (VD: `findById`, `totalQuantity`)
- Hằng số: `UPPER_SNAKE_CASE` (VD: `MAX_STOCK_LIMIT`)
- Tuân thủ cấu trúc thư mục (Controller -> Service -> Repository).
- API thiết kế chuẩn RESTful (`GET /api/products`, `POST /api/inbounds`).
- **Không** commit các file cấu hình cá nhân (`.idea/`, `.vscode/`, `target/`). Đã có sẵn `.gitignore`.

---

<p align="center"><i>Tài liệu được xây dựng dựa trên bản Đặc tả SRS v1.0. Chúc team code ít bug! 🚀</i></p>