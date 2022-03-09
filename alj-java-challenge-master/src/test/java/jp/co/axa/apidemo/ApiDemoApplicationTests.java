package jp.co.axa.apidemo;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jp.co.axa.apidemo.entities.Employee;

/**
 *  Integration test for rest service end points  through TestRestTemplate
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/test-data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD) 

public class ApiDemoApplicationTests {

	/*@Test
	public void contextLoads() {
	}*/
	
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Value("${login.security.user.name}")
	private String username;

	@Value("${login.security.user.password}")
	private String password;

	@Test
	public final void testGetEmployees() throws Exception {
		ResponseEntity<List<Employee>> empResponse = testRestTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/employees", HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {
				});

		assertTrue(empResponse.getStatusCode() == HttpStatus.OK);
		assertTrue(!empResponse.getBody().isEmpty());
		assertTrue(empResponse.getBody().size() >= 1);
	}

	@Test
	public final void testGetPaginatedEmployees() throws Exception {
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("pageNo", 1);
		paramMap.put("pageSize", 3);
		ResponseEntity<List<Employee>> empResponse = testRestTemplate.withBasicAuth(username, password).exchange(
				"/api/v1/employees/{pageNo}/{pageSize}", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Employee>>() {
				}, paramMap);

		assertTrue(empResponse.getStatusCode() == HttpStatus.OK);
		assertTrue(!empResponse.getBody().isEmpty());

		assertTrue(empResponse.getBody().size() == 3);
	}

	@Test
	public final void testSaveEmployee() throws Exception {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("name", "testuser");
		map.add("department", "sales");

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map,
				new HttpHeaders());

		ResponseEntity<Employee> empResponse = testRestTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/employees", HttpMethod.POST, request, Employee.class);
		assertTrue(empResponse.getStatusCode() == HttpStatus.OK);
		assertTrue(empResponse.getBody().getName().equals("testuser"));
	}

	@Test
	public final void testDeleteEmployee() throws Exception {
		String delMessage = "Deleted employee with ID 1 Successfully";
		Map<String, Long> paramMap = new HashMap<String, Long>();
		paramMap.put("employeeId", 1L);
		ResponseEntity<String> empResponse = testRestTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/employees/{employeeId}", HttpMethod.DELETE, null, String.class, paramMap);
		assertTrue(empResponse.getStatusCode() == HttpStatus.OK);
		assertTrue(!empResponse.getBody().isEmpty());
		assertTrue(empResponse.getBody().equals(delMessage));
	}

	@Test
	public final void testDeleteEmployeeNotFound() throws Exception {
		Map<String, Long> paramMap = new HashMap<String, Long>();
		paramMap.put("employeeId", 300L);
		ResponseEntity<String> empResponse = testRestTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/employees/{employeeId}", HttpMethod.DELETE, null, String.class, paramMap);
		assertTrue(empResponse.getStatusCode() == HttpStatus.NOT_FOUND);
	}

	@Test
	public void testGetEmployeeById() {
		Map<String, Long> paramMap = new HashMap<String, Long>();
		paramMap.put("employeeId", 3L);
		ResponseEntity<Employee> empResponse = testRestTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/employees/{employeeId}", HttpMethod.GET, null, Employee.class, paramMap);
		assertTrue(empResponse.getStatusCode() == HttpStatus.OK);
		assertTrue(empResponse.getBody().getId() == 3L);
	}

	@Test
	public void testGetEmployeeByIdNotFound() {
		Map<String, Long> paramMap = new HashMap<String, Long>();
		paramMap.put("employeeId", 300L);
		ResponseEntity<Employee> empResponse = testRestTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/employees/{employeeId}", HttpMethod.GET, null, Employee.class, paramMap);
		assertTrue(empResponse.getStatusCode() == HttpStatus.NOT_FOUND);
	}

	@Test
	public final void testUpdateEmployee() throws Exception {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("name", "updatetestuser");
		map.add("department", "sales");
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map,
				new HttpHeaders());

		ResponseEntity<Employee> empSaveResponse = testRestTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/employees/{employeeId}", HttpMethod.PUT, request, Employee.class, 3L);
		assertTrue(empSaveResponse.getStatusCode() == HttpStatus.OK);
		assertTrue(empSaveResponse.getBody().getName().equals("updatetestuser"));
	}

	@Test
	public final void testUpdateEmployeeNotFound() throws Exception {
		Map<String, Long> paramMap = new HashMap<String, Long>();
		paramMap.put("employeeId", 300L);
		ResponseEntity<Employee> empSaveResponse = testRestTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/employees/{employeeId}", HttpMethod.PUT, null, Employee.class, paramMap);
		assertTrue(empSaveResponse.getStatusCode() == HttpStatus.NOT_FOUND);
	}
}
