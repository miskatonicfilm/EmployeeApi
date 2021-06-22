package com.bscot7.EmployeeApi.controller;

import com.bscot7.EmployeeApi.model.Employee;
import com.bscot7.EmployeeApi.repo.EmployeeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@ExtendWith(SpringExtension.class)
class EmployeeControllerTest {

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private EmployeeRepo employeeRepo;

    @Autowired
    MockMvc mockMvc;


    @Test
    void testGetEmployeeById() throws Exception {
        String employeeId = UUID.randomUUID().toString();
        Employee employee = new Employee(employeeId, "Bob", "Test", 30L, "bob@test.com");

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", Matchers.is("Bob")));
    }

    @Test
    void testGetAllEmployees() throws Exception {
        Employee employee1 = new Employee(UUID.randomUUID().toString(), "Bob", "Test", 30L, "bob@test.com");
        Employee employee2 = new Employee(UUID.randomUUID().toString(), "Barb", "Test", 28L, "barb@test.com");
        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee2);

        when(employeeRepo.findAll()).thenReturn(employees);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", Matchers.is("Bob")))
                .andExpect(jsonPath("$[1].firstName", Matchers.is("Barb")));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        String employeeId = UUID.randomUUID().toString();
        Employee employee1 = new Employee(employeeId, "Bob", "Test", 30L, "bob@test.com");

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee1));

        Employee employeeUpdate = employee1;
        employeeUpdate.setFirstName("Mike");

        when(employeeRepo.save(employeeUpdate)).thenReturn(employeeUpdate);

        mockMvc.perform(put("/employees/" + employeeId).content(asJsonString(employeeUpdate)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", Matchers.is("Mike")));

    }

    @Test
    void testCreateEmployee() throws Exception {
        String employeeId = UUID.randomUUID().toString();
        Employee employee1 = new Employee(employeeId, "Bob", "Test", 30L, "bob@test.com");

        when(employeeRepo.insert(employee1)).thenReturn(employee1);

        mockMvc.perform(post("/employees").content(asJsonString(employee1)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void testDeleteEmployee() throws Exception {
        String employeeId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/employees/" + employeeId)).andExpect(status().isOk());
    }

    @Test
    void testGetEmployeeNotFound() throws Exception {
        String employeeId = UUID.randomUUID().toString();

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/employees/" + employeeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEmployeeFails() throws Exception {
        String employeeId = UUID.randomUUID().toString();
        Employee employee1 = new Employee(employeeId, "Bob", "Test", 30L, "bob@test.com");

        doThrow(Exception.class)
                .when(employeeRepo);

        MvcResult result = mockMvc.perform(post("/employees").content(asJsonString(employee1)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        Assertions.assertTrue(result.getResponse().getErrorMessage().equals("Something broke!"));
    }

    @Test
    void testUpdateEmployeeFails() throws Exception {
        String employeeId = UUID.randomUUID().toString();
        Employee employee1 = new Employee(employeeId, "Bob", "Test", 30L, "bob@test.com");

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/employees/" + employeeId).content(asJsonString(employee1)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEmployeeFails() throws Exception {
        String employeeId = UUID.randomUUID().toString();

        doThrow(Exception.class)
                .when(employeeRepo);

        MvcResult result =
                mockMvc.perform(delete("/employees/" + employeeId)).andExpect(status().isInternalServerError()).andReturn();

        Assertions.assertTrue(result.getResponse().getErrorMessage().equals("Hey, something broke!"));
    }

    /**
     * Helper method to turn object into a json string.
     * @param obj
     * @return Json String of object.
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}