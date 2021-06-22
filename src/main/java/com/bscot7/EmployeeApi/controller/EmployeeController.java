package com.bscot7.EmployeeApi.controller;

import com.bscot7.EmployeeApi.model.Employee;
import com.bscot7.EmployeeApi.repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class EmployeeController {


    @Autowired
    EmployeeRepo employeeRepo;

    /**
     * Get an employee by id.
     * @param id
     * @return Employee
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(value = "id") String id) {
        //Check database for id. Return employee object.
        Optional<Employee> employee = employeeRepo.findById(id);
        if(employee.isPresent()) {
            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<>(employeeRepo.findAll(), HttpStatus.OK);
    }

    /**
     * Create an employee. Randomly generate an id.
     * @param employee
     * @return Employee
     */
    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        try {
            Employee emp = employeeRepo.insert(new Employee(UUID.randomUUID().toString(), employee.getFirstName(), employee.getLastName(), employee.getAge(), employee.getEmail()));
            return new ResponseEntity<>(emp, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something broke!");
        }
    }

    /**
     * Update an employee.
     * @param employee
     * @return
     */
    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployeeById(@PathVariable("id") String id, @RequestBody Employee employee) {
        Optional<Employee> employeeData = employeeRepo.findById(id);
        if(employeeData.isPresent()) {
            Employee emp = employeeData.get();
            emp.setFirstName(employee.getFirstName());
            emp.setLastName(employee.getLastName());
            emp.setEmail(employee.getEmail());
            emp.setAge(employee.getAge());

            emp = employeeRepo.save(emp);

            return new ResponseEntity<>(emp, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Delete an employee by id.
     * @param id
     * @return true, if successful, false if not.
     */
    @DeleteMapping("employees/{id}")
    public ResponseEntity<HttpStatus> deleteEmployeeById(@PathVariable String id) {
        try {
            employeeRepo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Hey, something broke!");
        }
    }
}
