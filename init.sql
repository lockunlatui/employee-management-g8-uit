-- Bảng Departments
CREATE TABLE Departments (
    id BINARY(16) PRIMARY KEY,
    department_name VARCHAR(255) NOT NULL,
    created_at DATE,
    updated_at DATE
);

-- Bảng Employees
CREATE TABLE Employees (
    id BINARY(16) PRIMARY KEY,
    department_id BINARY(16),
    employee_name VARCHAR(255) NOT NULL,
    status VARCHAR(100),
    join_date DATE,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (department_id) REFERENCES Departments(id)
);

-- Bảng AttendanceLogs
CREATE TABLE AttendanceLogs (
    id BINARY(16) PRIMARY KEY,
    employee_id BINARY(16),
    check_in DATETIME,
    check_out DATETIME,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (employee_id) REFERENCES Employees(id)
);

-- Bảng Salaries
CREATE TABLE Salaries (
    id BINARY(16) PRIMARY KEY,
    employee_id BINARY(16),
    base_salary DECIMAL(15,2),
    deductions DECIMAL(15,2),
    status VARCHAR(100),
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (employee_id) REFERENCES Employees(id)
);

-- Bảng Users
CREATE TABLE Users (
    id BINARY(16) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(100),
    employee_id BINARY(16),
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (employee_id) REFERENCES Employees(id)
);

-- Thêm một số dữ liệu mẫu
INSERT INTO Departments (id, department_name, created_at, updated_at)
VALUES (UUID_TO_BIN(UUID()), 'Phòng Nhân sự', CURDATE(), CURDATE()),
       (UUID_TO_BIN(UUID()), 'Phòng Kỹ thuật', CURDATE(), CURDATE()),
       (UUID_TO_BIN(UUID()), 'Phòng Marketing', CURDATE(), CURDATE()); 