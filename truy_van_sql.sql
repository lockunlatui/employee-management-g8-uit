-- 1. Truy vấn SELECT để hiển thị các trường id và department_id ở định dạng UUID dễ đọc

SELECT 
    BIN_TO_UUID(id) AS id_uuid,
    employee_name,
    BIN_TO_UUID(department_id) AS department_id_uuid,
    status,
    join_date
FROM 
    Employees;

-- 2. Truy vấn UPDATE để cập nhật department_id cho từng nhân viên theo employee_name

-- a) Cập nhật nhân viên vào Phòng Nhân sự
UPDATE Employees e
JOIN Departments d ON d.department_name = 'Phòng Nhân sự'
SET e.department_id = d.id
WHERE e.employee_name LIKE '%Nguyễn%'
AND (e.department_id IS NULL OR LENGTH(e.department_id) = 0);

-- b) Cập nhật nhân viên vào Phòng Kỹ thuật
UPDATE Employees e
JOIN Departments d ON d.department_name = 'Phòng Kỹ thuật'
SET e.department_id = d.id
WHERE e.employee_name LIKE '%Trần%'
AND (e.department_id IS NULL OR LENGTH(e.department_id) = 0);

-- c) Cập nhật nhân viên vào Phòng Marketing
UPDATE Employees e
JOIN Departments d ON d.department_name = 'Phòng Marketing'
SET e.department_id = d.id
WHERE e.employee_name LIKE '%Lê%'
AND (e.department_id IS NULL OR LENGTH(e.department_id) = 0);

-- d) Truy vấn tổng hợp để cập nhật tất cả cùng lúc (cách khác)
UPDATE Employees e
LEFT JOIN Departments d ON 
    (e.employee_name LIKE '%Nguyễn%' AND d.department_name = 'Phòng Nhân sự') OR
    (e.employee_name LIKE '%Trần%' AND d.department_name = 'Phòng Kỹ thuật') OR
    (e.employee_name LIKE '%Lê%' AND d.department_name = 'Phòng Marketing')
SET e.department_id = d.id
WHERE (e.department_id IS NULL OR LENGTH(e.department_id) = 0) AND d.id IS NOT NULL;

-- 3. Truy vấn JOIN giữa bảng Employees và Departments để kiểm tra dữ liệu hiện tại

SELECT 
    BIN_TO_UUID(e.id) AS employee_uuid,
    e.employee_name,
    d.department_name,
    e.status
FROM 
    Employees e
LEFT JOIN 
    Departments d ON e.department_id = d.id
ORDER BY 
    d.department_name, e.employee_name;

-- Truy vấn bổ sung: Hiển thị tổng số nhân viên theo từng phòng ban

SELECT 
    d.department_name,
    COUNT(e.id) AS employee_count
FROM 
    Departments d
LEFT JOIN 
    Employees e ON d.id = e.department_id
GROUP BY 
    d.department_name
ORDER BY 
    employee_count DESC; 