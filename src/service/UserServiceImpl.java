package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dao.UserDAO;
import dao.EmployeeDAO;
import model.User;
import model.Employee;

public class UserServiceImpl implements UserService {
    private UserDAO userDAO;
    private EmployeeDAO employeeDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    @Override
    public User getUserById(UUID id) throws SQLException {
        return userDAO.getUserById(id);
    }

    @Override
    public void addUser(User user) throws SQLException {
        // Kiểm tra username đã tồn tại chưa
        if (userDAO.isUsernameExists(user.getUsername(), null)) {
            throw new SQLException("Tên đăng nhập đã tồn tại");
        }

        // Kiểm tra nhân viên đã có tài khoản chưa
        if (user.getEmployeeId() != null && userDAO.isEmployeeHasAccount(user.getEmployeeId())) {
            throw new SQLException("Nhân viên đã có tài khoản");
        }

        userDAO.addUser(user);
    }

    @Override
    public void updateUser(User user) throws SQLException {
        // Kiểm tra username đã tồn tại chưa (trừ user hiện tại)
        if (userDAO.isUsernameExists(user.getUsername(), user.getId())) {
            throw new SQLException("Tên đăng nhập đã tồn tại");
        }

        // Kiểm tra nhân viên đã có tài khoản khác chưa
        if (user.getEmployeeId() != null && userDAO.isEmployeeHasAccount(user.getEmployeeId(), user.getId())) {
            throw new SQLException("Nhân viên đã có tài khoản khác");
        }

        userDAO.updateUser(user);
    }

    @Override
    public void deleteUser(UUID id) throws SQLException {
        userDAO.deleteUser(id);
    }

    @Override
    public List<User> searchUsers(String keyword, String searchType) throws SQLException {
        List<User> users = userDAO.getAllUsers();
        List<User> result = new ArrayList<>();

        for (User user : users) {
            boolean match = false;
            String employeeName = (user.getEmployee() != null) ? user.getEmployee().getEmployeeName() : "N/A";

            switch(searchType) {
                case "Tên đăng nhập":
                    match = user.getUsername().toLowerCase().contains(keyword.toLowerCase());
                    break;
                case "Vai trò":
                    match = user.getRole().toLowerCase().contains(keyword.toLowerCase());
                    break;
                case "Tên nhân viên":
                    match = employeeName.toLowerCase().contains(keyword.toLowerCase());
                    break;
            }

            if (match || keyword.isEmpty()) {
                result.add(user);
            }
        }

        return result;
    }
} 