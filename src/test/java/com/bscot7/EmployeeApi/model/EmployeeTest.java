package com.bscot7.EmployeeApi.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void testToString() {
        Employee employee = new Employee("123456", "Bob", "Test", 24L, "bob@test.com");
        Assertions.assertEquals(employee.toString(), "Employee{id=123456, firstName='Bob', lastName='Test', age=24, email='bob@test.com'}");
    }
}