package service;

import dao.SalaryDAO;

import model.Salary;

import java.sql.SQLException;

import java.util.List;
import java.util.UUID;

public class SalaryServiceImpl implements SalaryService {
    private SalaryDAO salaryDAO;

    public SalaryServiceImpl() {
        this.salaryDAO = new SalaryDAO();

    }

    @Override
    public List<Salary> getAllSalaries() throws SQLException {
        return salaryDAO.getAllSalaries();
    }

    @Override
    public Salary getSalaryById(UUID id) throws SQLException {
        return salaryDAO.getOne(id);
    }

    @Override
    public void insertSalary(Salary salary) throws SQLException {
        salaryDAO.insertSalary(salary);
    }

    @Override
    public void updateSalary(Salary salary) throws SQLException {
        salaryDAO.updateSalary(salary);
    }

    @Override
    public void deleteSalary(UUID id) throws SQLException {
        salaryDAO.deleteSalary(id);
    }

}
