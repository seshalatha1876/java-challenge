package jp.co.axa.apidemo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.exception.EmployeeNotFoundException;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * 1. Implemented caching with @Cacheable annotation
 * 2. Implemented EmployeeNotFound logic on deleteEmployee method
 * 3. Implemented EmployeeNotFound logic on updateEmployee method
 * 4. Implemented EmployeeNotFound logic on getEmployee method
 * 5. Implemented log4j
 * 6. Implemented retrievePaginatedEmployees method to show case pagination
 */

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Cacheable("employees")
    public List<Employee> retrieveEmployees() {
    	log.info(EmployeeServiceImpl.class +" :: retrieveEmployees");
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    @Cacheable("employee")
    public Employee getEmployee(Long employeeId) {
    	log.info(EmployeeServiceImpl.class +" :: getEmployee");
		return employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException());    	
    }

    @Caching(evict = {
            @CacheEvict(value="employee", allEntries=true),
            @CacheEvict(value="employees", allEntries=true),
            @CacheEvict(value="employeePagination", allEntries=true)})
    public Employee saveEmployee(Employee employee){
    	log.info(EmployeeServiceImpl.class +" :: saveEmployee");
    	Employee emp = employee; 
    	if(employee.getName() == null) {
    		emp.setName("");
    	}
    	if(employee.getSalary() == null) {
    		emp.setSalary(0);
    	}
    	if(employee.getDepartment() == null) {
    		emp.setDepartment("");
    	}    	
    	emp = employeeRepository.save(employee);
    	return emp;
    }

    @Caching(evict = {
            @CacheEvict(value="employee", allEntries=true),
            @CacheEvict(value="employees", allEntries=true),
            @CacheEvict(value="employeePagination", allEntries=true)})
    public String deleteEmployee(Long employeeId){
    	log.info(EmployeeServiceImpl.class +" :: deleteEmployee");
		return employeeRepository.findById(employeeId).map(employee -> {
			employeeRepository.deleteById(employeeId);
			String delMessage = "Deleted employee with ID " + employeeId.toString() + " Successfully";
			employeeRepository.delete(employee);
			return delMessage;
		}).orElseThrow(() -> {
			return new EmployeeNotFoundException();
		});
    }

    @Caching(evict = {
            @CacheEvict(value="employee", allEntries=true),
            @CacheEvict(value="employees", allEntries=true),
            @CacheEvict(value="employeePagination", allEntries=true)})
    public Employee updateEmployee(Employee employee, Long employeeId) {
    	log.info(EmployeeServiceImpl.class +" :: updateEmployee");
    	Employee emp = getEmployee(employeeId);
    	if(employee.getName() != null) {
    		emp.setName(employee.getName());
    	}
    	if(employee.getSalary() != null) {
    		emp.setSalary(employee.getSalary());
    	}
    	if(employee.getDepartment() != null) {
    		emp.setDepartment(employee.getDepartment());
    	}    	 
        Employee savedEmp = employeeRepository.save(emp);
        return savedEmp;
    }
    
    @Cacheable("employeePagination")
    public List<Employee> retrievePaginatedEmployees(int pageNo, int pageSize) {
    	log.info(EmployeeServiceImpl.class +" :: retrievePaginatedEmployees");
    	Pageable paging = PageRequest.of(pageNo, pageSize);
    	Page<Employee> pagedResult = employeeRepository.findAll(paging);
        List<Employee> employees = pagedResult.getContent();
        return employees;
    }
}