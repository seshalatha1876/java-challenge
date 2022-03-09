package jp.co.axa.apidemo;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.exception.EmployeeNotFoundException;
import jp.co.axa.apidemo.services.EmployeeService;

/**
 *  Unit test for controller layer through MockMvc
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureJsonTesters
@WithMockUser
public class EmployeeControllerTest {
	private static final long EMP_ID = 1L;
	private Employee emp;

	private MockMvc mockMvc;

	@Autowired
	private JacksonTester<List<Employee>> jsonEmpList;

	@Autowired
	private JacksonTester<Employee> jsonEmp;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private EmployeeService employeeService;

	@Before
	public void setup() {
		// @formatter:off
		 emp = Employee.builder()
					.id(EMP_ID)
					.name("test")
					.department("IT")
					.salary(1000000).build();
		// @formatter:off
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}
	
	@Test	
	public final void testGetEmployee()  throws Exception {
		when(employeeService.getEmployee(1L)).thenReturn(emp);
		Employee expectedEmp = emp;
		MockHttpServletResponse empResponse = mockMvc.perform(get("/api/v1/employees/1")
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		assertTrue(empResponse.getStatus() == HttpStatus.OK.value());
		Employee actualEmp = jsonEmp.parseObject(empResponse.getContentAsString());
		assertTrue(actualEmp.getId() == expectedEmp.getId());
		assertTrue(actualEmp.getName().equals(expectedEmp.getName()));
		assertTrue(actualEmp.getDepartment().equals(expectedEmp.getDepartment()));
		assertTrue(actualEmp.getSalary().equals(expectedEmp.getSalary()));		
	}
	
	@Test
	public final void testGetPaginatedEmployees() throws Exception {
		List<Employee> empList = new ArrayList<Employee>();
		empList.add(emp);
		when(employeeService.retrievePaginatedEmployees(1, 3)).thenReturn(empList);
		
		MockHttpServletResponse empResponse = mockMvc.perform(get("/api/v1/employees/1/3")
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();		

		assertTrue(empResponse.getStatus() == HttpStatus.OK.value());		
		assertTrue(!empResponse.getContentAsString().isEmpty());		
		assertTrue(empResponse.getContentAsString().equals(jsonEmpList.write(empList).getJson()));
	}
	
	@Test	
	public final void testGetEmployeeNotFound()  throws Exception {
		when(employeeService.getEmployee(1L)).thenThrow(new EmployeeNotFoundException());
		MockHttpServletResponse empResponse = mockMvc.perform(get("/api/v1/employees/1")
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		assertTrue(empResponse.getStatus() == HttpStatus.NOT_FOUND.value());
		assertTrue(empResponse.getContentAsString().equals(""));	
		
	}
	
	@Test	
	public final void testGetEmployees()  throws Exception {
		List<Employee> empList = new ArrayList<Employee>();
		empList.add(emp);
		when(employeeService.retrieveEmployees()).thenReturn(empList);  
			
		MockHttpServletResponse empResponse = mockMvc.perform(get("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		assertTrue(empResponse.getStatus() == HttpStatus.OK.value());
		List<Employee> actualEmpList = jsonEmpList.parseObject(empResponse.getContentAsString());
		
		assertTrue(actualEmpList.size() == empList.size());
		assertTrue(empResponse.getContentAsString().equals(jsonEmpList.write(empList).getJson()));
	}

	@Test	
	public final void testSaveEmployee()  throws Exception {
		when(employeeService.saveEmployee(emp)).thenReturn((emp));   	   
		Employee expectedEmp = emp;
			
		MockHttpServletResponse empResponse = mockMvc.perform(post("/api/v1/employees").flashAttr("emp", emp)
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		Employee actualEmp = jsonEmp.parseObject(empResponse.getContentAsString());
		assertTrue(empResponse.getStatus() == HttpStatus.OK.value());
		assertTrue(actualEmp.getId() == expectedEmp.getId());
		assertTrue(actualEmp.getName().equals(expectedEmp.getName()));
		assertTrue(actualEmp.getDepartment().equals(expectedEmp.getDepartment()));
		assertTrue(actualEmp.getSalary().equals(expectedEmp.getSalary()));		
		
	}

	@Test
	public final void testDeleteEmployee() throws Exception {
		String delMessage = "Deleted employee with ID 1 Successfully"; 
		when(employeeService.deleteEmployee(1L)).thenReturn((delMessage));   	   
		
		
		MockHttpServletResponse empResponse = mockMvc.perform(delete("/api/v1/employees/1")
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		
		assertTrue(empResponse.getStatus() == HttpStatus.OK.value());
		assertTrue(empResponse.getContentAsString().equals(delMessage));
	}
	
	@Test
	public final void testDeleteEmployeeNotFound() throws Exception {
		when(employeeService.deleteEmployee(1L)).thenThrow(new EmployeeNotFoundException());
		
		MockHttpServletResponse empResponse = mockMvc.perform(delete("/api/v1/employees/1")
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		
		assertTrue(empResponse.getStatus() == HttpStatus.NOT_FOUND.value());
		assertTrue(empResponse.getContentAsString().equals(""));
	}
	
	
		
	@Test
	public final void testUpdateEmployee()  throws Exception {
		when(employeeService.updateEmployee(emp, 1L)).thenReturn((emp));   	   
		Employee expectedEmp = emp;
		expectedEmp.setName("testuser");	
		MockHttpServletResponse empResponse = mockMvc.perform(put("/api/v1/employees/1").flashAttr("emp", expectedEmp)
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		Employee actualEmp = jsonEmp.parseObject(empResponse.getContentAsString());
		assertTrue(empResponse.getStatus() == HttpStatus.OK.value());
		assertTrue(actualEmp.getId() == expectedEmp.getId());
		assertTrue(actualEmp.getName().equals(expectedEmp.getName()));
	}
	
	@Test
	public final void testUpdateEmployeeNotFound()  throws Exception {			
		
		when(employeeService.getEmployee(1L)).thenReturn(emp);
		MockHttpServletResponse getEmpResponse = mockMvc.perform(get("/api/v1/employees/1")
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

		Employee expectedEmp = jsonEmp.parseObject(getEmpResponse.getContentAsString());
		expectedEmp.setName("testuser");
		
		when(employeeService.updateEmployee(expectedEmp, 1L)).thenThrow(new EmployeeNotFoundException());
		
		MockHttpServletResponse empResponse = mockMvc.perform(put("/api/v1/employees/1").flashAttr("emp", expectedEmp)
				.contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		
		assertTrue(empResponse.getStatus() == HttpStatus.NOT_FOUND.value());
		assertTrue(empResponse.getContentAsString().equals(""));
	}
	
	
}
