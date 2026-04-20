package com.cwm.studentmanagement.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cwm.studentmanagement.model.Courses;
import com.cwm.studentmanagement.model.Students;
import com.cwm.studentmanagement.model.Users;
import com.cwm.studentmanagement.repository.CourseRepository;
import com.cwm.studentmanagement.repository.StudentRepository;
import com.cwm.studentmanagement.repository.UsersRepository;

/*
 * Copyright (c) 2026 Mahesh Shet
 * Licensed under the MIT License.
 */

@Configuration
public class DataIntializer {
	
	@Bean
	CommandLineRunner loadSampleData(UsersRepository usersRepository,
			StudentRepository studentRepository,
			CourseRepository courseRepository,
			PasswordEncoder passwordEncoder) {
		
		return args -> {
			// Create admin user
			if(!usersRepository.existsByUsername("Admin")) {
				Users users = new Users();
				users.setUsername("Admin");
				users.setPassword(passwordEncoder.encode("admin@123"));
				users.setActive(true);
				usersRepository.save(users);
			}
			
			// Create sample courses
			if(courseRepository.count() == 0) {
				CourseRepository finalCourseRepository = courseRepository;
				
				Courses javaCourse = new Courses();
				javaCourse.setCourseName("Java Programming");
				javaCourse.setCourseCode("JAVA101");
				javaCourse.setDuration("3 Months");
				javaCourse.setFee(new BigDecimal("9999.00"));
				javaCourse.setDescription("Learn Java programming from basics to advanced concepts");
				javaCourse.setActive(true);
				finalCourseRepository.save(javaCourse);
				
				Courses webCourse = new Courses();
				webCourse.setCourseName("Web Development");
				webCourse.setCourseCode("WEB201");
				webCourse.setDuration("4 Months");
				webCourse.setFee(new BigDecimal("12999.00"));
				webCourse.setDescription("Full stack web development with modern technologies");
				webCourse.setActive(true);
				finalCourseRepository.save(webCourse);
				
				Courses pythonCourse = new Courses();
				pythonCourse.setCourseName("Python Programming");
				pythonCourse.setCourseCode("PYT301");
				pythonCourse.setDuration("2 Months");
				pythonCourse.setFee(new BigDecimal("7999.00"));
				pythonCourse.setDescription("Python programming for data science and web development");
				pythonCourse.setActive(true);
				finalCourseRepository.save(pythonCourse);
			}
			
			// Create sample students
			if(studentRepository.count() == 0) {
				StudentRepository finalStudentRepository = studentRepository;
				
				Students student1 = new Students();
				student1.setFirstName("John");
				student1.setLastName("Doe");
				student1.setEmail("john.doe@example.com");
				student1.setPhoneNumber("9876543210");
				student1.setAddress("123 Main St, City, State");
				student1.setActive(true);
				student1.setCreatedAt(LocalDateTime.now());
				finalStudentRepository.save(student1);
				
				Students student2 = new Students();
				student2.setFirstName("Jane");
				student2.setLastName("Smith");
				student2.setEmail("jane.smith@example.com");
				student2.setPhoneNumber("9876543211");
				student2.setAddress("456 Oak Ave, City, State");
				student2.setActive(true);
				student2.setCreatedAt(LocalDateTime.now());
				finalStudentRepository.save(student2);
				
				Students student3 = new Students();
				student3.setFirstName("Mike");
				student3.setLastName("Johnson");
				student3.setEmail("mike.johnson@example.com");
				student3.setPhoneNumber("9876543212");
				student3.setAddress("789 Pine Rd, City, State");
				student3.setActive(true);
				student3.setCreatedAt(LocalDateTime.now());
				finalStudentRepository.save(student3);
			}
		};
	}

}
