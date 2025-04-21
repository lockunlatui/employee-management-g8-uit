package service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import model.User;

public interface UserService {
    List<User> getAllUsers() throws SQLException;
    User getUserById(UUID id) throws SQLException;
    void addUser(User user) throws SQLException;
    void updateUser(User user) throws SQLException;
    void deleteUser(UUID id) throws SQLException;
    List<User> searchUsers(String keyword, String searchType) throws SQLException;
} 