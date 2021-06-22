package com.bscot7.EmployeeApi.repo;

import com.bscot7.EmployeeApi.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepo extends MongoRepository<Employee, String> {

}
