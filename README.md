
# 🧩 Employee Management System - Java Swing

## 📌 Mô tả dự án
Đây là hệ thống quản lý nhân viên được xây dựng bằng Java Swing, kết nối cơ sở dữ liệu MySQL thông qua JDBC và vận hành bằng Docker. Dự án hỗ trợ đầy đủ các chức năng quản lý nhân viên, phòng ban, người dùng, chấm công và lương.

---

## 🏗️ Kiến trúc hệ thống

```
[Người dùng]
     ↓
[Ứng dụng Java Swing GUI (App.java)]
     ↓
[JDBC]
     ↓
[MySQL DB - Docker Container (Port 3307)]
```

- Java Swing quản lý giao diện người dùng (UI)
- JDBC xử lý tương tác cơ sở dữ liệu
- MySQL chạy trong Docker Compose để dễ dàng triển khai

---

## 🖼️ Giao diện và chức năng

| STT | Chức năng               | File giao diện (Java Swing)           |
|-----|--------------------------|----------------------------------------|
| 1   | Trang chủ                | `HomePagePanel.java`                  |
| 2   | Quản lý nhân viên        | `EmployeeManagementPanel.java`        |
| 3   | Thêm nhân viên           | `AddEmployeeDialog.java`              |
| 4   | Sửa nhân viên            | `EditEmployeeDialog.java`             |
| 5   | Xoá nhân viên            | `DeleteEmployeeDialog.java`           |
| 6   | Quản lý phòng ban        | `DepartmentManagementPanel.java`      |
| 7   | Thêm phòng ban           | `AddDepartmentDialog.java`            |
| 8   | Sửa phòng ban            | `EditDepartmentDialog.java`           |
| 9   | Xoá phòng ban            | `DeleteDepartmentDialog.java`         |
| 10  | Quản lý người dùng       | `UserManagementPanel.java`            |
| 11  | Thêm người dùng          | `AddUserDialog.java`                  |
| 12  | Sửa người dùng           | `EditUserDialog.java`                 |
| 13  | Xoá người dùng           | `DeleteUserDialog.java`               |
| 14  | Quản lý chấm công        | `AttendanceManagementPanel.java`      |
| 15  | Quản lý lương            | `SalaryManagementPanel.java`          |
| 16  | Menu bên trái            | `LeftMenu.java`                       |
| 17  | Giao diện chính toàn bộ | `MainFrame.java`                      |

---

## ✅ Yêu cầu hệ thống
- Docker và Docker Compose
- Java Runtime Environment (JRE) 8 trở lên

---

## 🚀 Các bước khởi động ứng dụng

### Bước 1: Khởi động cơ sở dữ liệu MySQL bằng Docker

```bash
cd /đường/dẫn/đến/thư/mục/employee-management-g8-uit
docker-compose up -d
docker ps
```

Bạn sẽ thấy container có tên `employee_management_db` đang chạy.

---

### Bước 2: Kiểm tra kết nối cơ sở dữ liệu (tuỳ chọn)

```bash
java -cp "lib/mysql-connector-j-8.0.33.jar:bin:." database.TestDatabaseConnection
```

Kết quả mong đợi: `"Database connection test successful!"`

---

### Bước 3: Chạy ứng dụng Java Swing

```bash
java -cp "lib/mysql-connector-j-8.0.33.jar:bin:." gui.App
```

---

## 🛑 Cách dừng ứng dụng

```bash
docker-compose down
```

---

## 🧯 Xử lý sự cố

### 1. Nếu container không khởi động
```bash
netstat -an | grep 3307
```
→ Nếu cổng 3307 bị chiếm, sửa lại trong `docker-compose.yml` và `ConnectionManager.java`

### 2. Không kết nối được DB?
- Kiểm tra container: `docker ps`
- Xem log: `docker logs employee_management_db`
- Kiểm tra thông tin trong `ConnectionManager.java`

### 3. Muốn xóa sạch dữ liệu và chạy lại?
```bash
docker-compose down -v
rm -rf mysql-data
docker-compose up -d
```

---

## 🗄️ Thông tin kết nối cơ sở dữ liệu

- Host: `localhost`
- Port: `3307`
- Database: `employee_management_database`
- Username: `root`
- Password: `root`

---

## 📊 Cấu trúc cơ sở dữ liệu

| Bảng              | Chức năng             |
|------------------|------------------------|
| `Departments`     | Quản lý phòng ban       |
| `Employees`       | Quản lý thông tin nhân viên |
| `AttendanceLogs`  | Ghi nhận lịch sử chấm công |
| `Salaries`        | Lưu thông tin lương     |
| `Users`           | Quản trị hệ thống       |

---

## 🗂️ Cấu trúc thư mục chính

- `src/gui/` – Giao diện người dùng
- `src/dao/` – Lớp truy xuất dữ liệu JDBC
- `src/dto/` – Các đối tượng truyền dữ liệu
- `src/model/` – Lớp đối tượng chính
- `src/service/` – Lớp xử lý nghiệp vụ
- `src/database/` – Cấu hình kết nối MySQL
- `lib/` – Thư viện MySQL JDBC

---

## 👨‍💻 Tác giả

Nhóm 8 – UIT  
Môn: 2425HK2 - IE303.E22.LT.CNTT - Công nghệ Java - ThS. Sử Nhật Hạ
