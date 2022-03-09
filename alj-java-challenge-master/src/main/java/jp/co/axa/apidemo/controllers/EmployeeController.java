package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 
 *  1. Changed the return type of end points to ResponseEntity
 *  2. Added getPaginatedEmployees method to implement pagination
 *  3. Modified the logic slightly inside updateEmployee to avoid nulls getting inserted in case
 *     employee fields are empty  
 *  4. Implemented log4j
 **/
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getEmployees() {
    	log.info(EmployeeController.log + " :: getEmployees");
        List<Employee> employees = employeeService.retrieveEmployees();
    	log.info(EmployeeController.log + "Employeees retrieved Successfully");
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployee(@PathVariable(name="employeeId")Long employeeId) {
    	log.info(EmployeeController.log + " :: getEmployee "+employeeId);
    	log.info(EmployeeController.log + "Employeee retrieved Successfully");
    	Employee emp = employeeService.getEmployee(employeeId);
        return ResponseEntity.ok(emp);
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> saveEmployee(@ModelAttribute("emp") Employee employee){
    	log.info(EmployeeController.log + " :: saveEmployee "+employee.toString());
        Employee emp = employeeService.saveEmployee(employee);
    	log.info(EmployeeController.log + "Employee Saved Successfully");
        return ResponseEntity.ok(emp);
    }
    
    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable(name="employeeId")Long employeeId){
    	log.info(EmployeeController.log + " :: deleteEmployee " +employeeId);
        String delMessage = employeeService.deleteEmployee(employeeId);
    	log.info(EmployeeController.log + "Employee deleted Successfully");
    	return ResponseEntity.ok(delMessage);
    }

    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(@ModelAttribute("emp") Employee employee,
                               @PathVariable(name="employeeId")Long employeeId){
    	log.info(EmployeeController.log + " :: updateEmployee " +employeeId);
        Employee savedEmp =  employeeService.updateEmployee(employee, employeeId);
    	log.info(EmployeeController.log + "Employee Updated Successfully");
        return ResponseEntity.ok(savedEmp); 
    }
    
    @GetMapping("/employees/{pageNo}/{pageSize}")
    public  ResponseEntity<List<Employee>> getPaginatedEmployees(@PathVariable int pageNo, 
            @PathVariable int pageSize) {
    	log.info(EmployeeController.log + " :: getPaginatedEmployees");
    	List<Employee> employees = employeeService.retrievePaginatedEmployees(pageNo, pageSize);
    	log.info(EmployeeController.log + "Paginated Employees retrieved Successfully");
        return ResponseEntity.ok(employees);
    }
}
