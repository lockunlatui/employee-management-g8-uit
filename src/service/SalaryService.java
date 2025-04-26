package service;

import model.Salary;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface SalaryService {
    List<Salary> getAllSalaries() throws SQLException;

    Salary getSalaryById(UUID id) throws SQLException;

    void insertSalary(Salary salary) throws SQLException;

    void updateSalary(Salary salary) throws SQLException;

    void deleteSalary(UUID id) throws SQLException;

}
