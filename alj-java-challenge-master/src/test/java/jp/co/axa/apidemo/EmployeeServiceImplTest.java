package jp.co.axa.apidemo;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.exception.EmployeeNotFoundException;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import jp.co.axa.apidemo.services.EmployeeServiceImpl;

/**
 *  Unit test for service layer through Mockito
 *
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class EmployeeServiceImplTest {

	private static final long EMP_ID = 1L;

	private Employee emp;

	@InjectMocks
	private EmployeeServiceImpl employeeServiceImpl;

	@Mock
	private EmployeeRepository employeeRepository;

	@Before
	public void setup() {
		// @formatter:off
		emp = Employee.builder()
				.id(EMP_ID)
				.name("test")
				.department("IT")
				.salary(1000000).build();
		// @formatter:off
	}
	

	@Test
	public void testRetrieveEmployees() {
		// @formatter:off
		Employee emp1  = Employee.builder()
				.id(EMP_ID)
				.name("test")
				.department("IT")
				.salary(1000000).build();
		// @formatter:off
		List<Employee> list = new ArrayList<Employee>();
		list.add(emp);
		list.add(emp1);
		when(employeeRepository.findAll()).thenReturn(list);
		assertTrue(list.size() == 2);
		assertTrue(employeeServiceImpl.retrieveEmployees().equals(list));		
		verify(employeeRepository, times(1)).findAll();
	}
	
	@Test
	public void testSaveEmployee() {		
		when(employeeRepository.save(emp)).thenReturn(emp);
		Employee actualEmp = employeeServiceImpl.saveEmployee(emp);
		assertTrue(actualEmp.getId() == emp.getId());
		assertTrue(actualEmp.getName().equals(emp.getName()));
		assertTrue(actualEmp.getDepartment().equals(emp.getDepartment()));
		assertTrue(actualEmp.getSalary().equals(emp.getSalary()));		
		verify(employeeRepository, times(1)).save(emp);
	}

	@Test
	public void testGetEmployee() {						
		when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
		assertTrue(Optional.of(emp).isPresent());
		Employee actualEmp = employeeServiceImpl.getEmployee(EMP_ID);
		assertTrue(actualEmp.getId() == emp.getId());
		assertTrue(actualEmp.getName().equals(emp.getName()));
		assertTrue(actualEmp.getDepartment().equals(emp.getDepartment()));
		assertTrue(actualEmp.getSalary().equals(emp.getSalary()));		
		verify(employeeRepository, times(1)).findById(EMP_ID);
	}

	@Test(expected = EmployeeNotFoundException.class)
    public void testGetEmployeeNotFound() {
			when(employeeRepository.findById(200L)).thenThrow(new EmployeeNotFoundException());
			Employee actualEmp = employeeServiceImpl.getEmployee(200L);
			assertTrue("".equals(actualEmp.getName()));
			verify(employeeRepository, times(1)).findById(EMP_ID);
    }
	
	@Test
	public void testDeleteEmployee() {
		String expectedDelMessage = "Deleted employee with ID 1 Successfully";
		when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
		assertTrue(Optional.of(emp).isPresent());
		String actualDelMessage = employeeServiceImpl.deleteEmployee(1L);
		assertTrue(expectedDelMessage.equals(actualDelMessage));		
		verify(employeeRepository, times(1)).delete(emp);		
		verify(employeeRepository, times(1)).deleteById(1L);
	}
	
	@Test(expected = EmployeeNotFoundException.class)
    public void testDeleteEmployeeNotFound() {
		when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
		assertTrue(Optional.of(emp).isPresent());
		when(employeeServiceImpl.deleteEmployee(1L)).thenThrow(new EmployeeNotFoundException());
		String delMessage = employeeServiceImpl.deleteEmployee(200L);
		assertTrue("".equals(delMessage));		
		verify(employeeRepository, times(1)).delete(emp);		
		verify(employeeRepository, times(1)).deleteById(1L);
    }
	
	@Test
	public void testUpdateEmployee() {
		when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
		assertTrue(Optional.of(emp).isPresent());
		emp.setDepartment("Sales");
		when(employeeRepository.save(emp)).thenReturn(emp);
		Employee actualEmp = employeeServiceImpl.updateEmployee(emp, EMP_ID );
		assertTrue(actualEmp.getId().equals(emp.getId()));
		assertTrue(actualEmp.getDepartment().equals(emp.getDepartment()));
		verify(employeeRepository, times(1)).save(emp);
	}
	
	@Test(expected = EmployeeNotFoundException.class)
	public void testUpdateEmployeeNotFound() {
		when(employeeRepository.findById(EMP_ID)).thenReturn(Optional.of(emp));
		assertTrue(Optional.of(emp).isPresent());
		emp.setDepartment("Sales");
		when(employeeRepository.save(emp)).thenThrow(new EmployeeNotFoundException());
		Employee actualEmp = employeeServiceImpl.updateEmployee(emp, EMP_ID );
		assertTrue("".equals(actualEmp.getName()));
		verify(employeeRepository, times(1)).save(emp);
	}
	
	@Test
	public final void testGetPaginatedEmployees() throws Exception {
		List<Employee> expectedList = new ArrayList<Employee>();
		expectedList.add(emp);

		int pageNo = 0;
		int pageSize = 2;
		Pageable pageable = PageRequest.of(pageNo, 2);
		Page<Employee> employeePage = new PageImpl<Employee>(expectedList);
		
        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
		
		assertTrue(expectedList.size() == 1);
		List<Employee> actualEmployeeList = employeeServiceImpl.retrievePaginatedEmployees(pageNo, pageSize);		
		assertTrue(actualEmployeeList.size() == 1);
		assertTrue(expectedList.get(0).getName().equals(actualEmployeeList.get(0).getName()));
		verify(employeeRepository, times(1)).findAll(pageable);
	}
	
}
