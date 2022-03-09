package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;

import java.util.List;

/**
 * Changed the return types to cross check the results
 *
 */

public interface EmployeeService {

    public List<Employee> retrieveEmployees();
    
    public List<Employee> retrievePaginatedEmployees(int pageNo, int pageSize);

    public Employee getEmployee(Long employeeId);

    public Employee saveEmployee(Employee employee);

    public String deleteEmployee(Long employeeId);

    public Employee updateEmployee(Employee employee, Long employeeId);
   
}