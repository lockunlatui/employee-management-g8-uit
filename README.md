# Hướng dẫn quản lý nhân viên - Employee Management

## Yêu cầu hệ thống
- Docker và Docker Compose
- Java Runtime Environment (JRE) 8 trở lên

## Các bước khởi động ứng dụng

### Bước 1: Khởi động cơ sở dữ liệu MySQL bằng Docker

1. Mở Terminal hoặc Command Prompt
2. Di chuyển đến thư mục gốc của dự án:
   ```
   cd /đường/dẫn/đến/thư/mục/employee-management-g8-uit
   ```
3. Khởi động container MySQL với Docker Compose:
   ```
   docker-compose up -d
   ```
4. Kiểm tra xem container đã chạy thành công chưa:
   ```
   docker ps
   ```
   Bạn sẽ thấy container có tên `employee_management_db` đang chạy

### Bước 2: Kiểm tra kết nối cơ sở dữ liệu (tùy chọn)

Để kiểm tra kết nối đến cơ sở dữ liệu, chạy:
```
java -cp "lib/mysql-connector-j-8.0.33.jar:bin:." database.TestDatabaseConnection
```

Nếu thấy thông báo "Database connection test successful!", tức là kết nối thành công.

### Bước 3: Chạy ứng dụng

```
java -cp "lib/mysql-connector-j-8.0.33.jar:bin:." gui.App
```

Ứng dụng sẽ hiển thị giao diện người dùng nếu kết nối cơ sở dữ liệu thành công.

## Cách dừng ứng dụng

1. Đóng cửa sổ ứng dụng Java
2. Dừng container MySQL khi không sử dụng để tiết kiệm tài nguyên:
   ```
   docker-compose down
   ```

## Xử lý sự cố

### 1. Nếu container không khởi động
Kiểm tra xem cổng 3307 có đang được sử dụng không:
```
netstat -an | grep 3307
```
Nếu cổng đã được sử dụng, hãy sửa file `docker-compose.yml` và `src/database/ConnectionManager.java` để sử dụng cổng khác.

### 2. Nếu không kết nối được đến cơ sở dữ liệu
- Kiểm tra container MySQL đang chạy chưa: `docker ps`
- Xem log của container: `docker logs employee_management_db`
- Đảm bảo thông tin kết nối trong `src/database/ConnectionManager.java` chính xác

### 3. Nếu muốn xóa toàn bộ dữ liệu và khởi động lại
```
docker-compose down -v
rm -rf mysql-data
docker-compose up -d
```

## Thông tin kết nối cơ sở dữ liệu

- Host: localhost
- Port: 3307
- Database: employee_management_database
- Username: root
- Password: root

## Cấu trúc cơ sở dữ liệu

Cơ sở dữ liệu gồm các bảng:
- Departments: Phòng ban
- Employees: Nhân viên
- AttendanceLogs: Lịch sử chấm công
- Salaries: Lương
- Users: Người dùng hệ thống 