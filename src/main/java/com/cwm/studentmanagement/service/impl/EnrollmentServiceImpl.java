package com.cwm.studentmanagement.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cwm.studentmanagement.dto.CourseDTO;
import com.cwm.studentmanagement.dto.EnrollmentDTO;
import com.cwm.studentmanagement.dto.EnrollmentSummaryDTO;
import com.cwm.studentmanagement.model.Courses;
import com.cwm.studentmanagement.model.Enrollment;
import com.cwm.studentmanagement.model.Students;
import com.cwm.studentmanagement.repository.CourseRepository;
import com.cwm.studentmanagement.repository.EnrollmentRepository;
import com.cwm.studentmanagement.repository.StudentRepository;
import com.cwm.studentmanagement.service.EnrollmentService;

/*
 * Copyright (c) 2026 Mahesh Shet
 * Licensed under the MIT License.
 */

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {
	private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);
	
	private final EnrollmentRepository enrollmentRepository;
	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;
	private final ModelMapper mapper;
	
	EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
			StudentRepository studentRepository,
			CourseRepository courseRepository,
			ModelMapper mapper) 
	{
		this.enrollmentRepository = enrollmentRepository;
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
		this.mapper = mapper;
	}
	

	@Override
	public void enrollStudentToCourses(EnrollmentDTO enrollmentDTO) {
		log.info("request from enrollStudentToCourses");
		
		Students student = studentRepository.findById(enrollmentDTO.getStudentId())
				.orElseThrow(() -> new RuntimeException("Student not found"));
		
		for(Long courseId : enrollmentDTO.getCourseIds()) {
			Courses course = courseRepository.findById(courseId)
					.orElseThrow(() -> new RuntimeException("course not found"));
			
			if(enrollmentRepository.existsByStudentIdAndCourseId(enrollmentDTO.getStudentId(),
					courseId)) {
				continue;
			}
			
			Enrollment enrollment = new Enrollment();
			enrollment.setStudent(student);
			enrollment.setCourse(course);
			enrollment.setEnrolledDate(java.time.LocalDateTime.now());
			
			student.getEnrollments().add(enrollment);
			course.getEnrollments().add(enrollment);
			
			enrollmentRepository.save(enrollment);
		}
		
	}


	@Override
	public Page<EnrollmentSummaryDTO> getEnrolledStudents(int page, int size) {
	log.info("list of enrolled students from: {}", page);
		
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));
		return studentRepository.findEnrolledStudents(pageRequest)
				.map(student -> {
					EnrollmentSummaryDTO dto = new EnrollmentSummaryDTO();
					dto.setStudentId(student.getId());
					dto.setStudentName(student.getFirstName() + " "+student.getLastName());
					dto.setEmail(student.getEmail());
					
					dto.setCourseCount(student.getEnrollments().size());
					BigDecimal totalFee = student.getEnrollments().stream()
							.map(enrollment -> enrollment.getCourse().getFee())
							.filter(fee -> fee != null)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					dto.setTotalFee(totalFee);
					
					return dto;
				});
	}


	@Override
	public EnrollmentSummaryDTO findEnrolledStudentCourseDetails(Long studentId) {
		// First check if student exists
		Students student = studentRepository.findById(studentId)
				.orElseThrow(() -> new RuntimeException("Student not found"));
		
		// Create enrollment summary DTO
		EnrollmentSummaryDTO dto = new EnrollmentSummaryDTO();
		dto.setStudentId(student.getId());
		dto.setStudentName(student.getFirstName() + " "+student.getLastName());
		dto.setEmail(student.getEmail());
		
		// Handle enrollments (could be empty)
		if (student.getEnrollments() != null && !student.getEnrollments().isEmpty()) {
			dto.setCourseCount(student.getEnrollments().size());
			BigDecimal totalFee = student.getEnrollments().stream()
					.map(enrollment -> enrollment.getCourse().getFee())
					.filter(fee -> fee != null)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			dto.setTotalFee(totalFee);
			
			List<CourseDTO> courseList = student.getEnrollments().stream()
					.map(enrollment -> {
						CourseDTO courseDTO = mapper.map(enrollment.getCourse(), CourseDTO.class);
						courseDTO.setEnrollmentId(enrollment.getId());
						return courseDTO;
					})
					.collect(Collectors.toList());
			
			dto.setCourseList(courseList);
		} else {
			// Student has no enrollments
			dto.setCourseCount(0);
			dto.setTotalFee(BigDecimal.ZERO);
			dto.setCourseList(new ArrayList<>());
		}
		
		return dto;
	}


	@Override
	public List<EnrollmentSummaryDTO> getRecentlyEnrolledStudents() {
		log.info("list of recently enrolled students");
		
		PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Direction.DESC, "id"));
		return studentRepository.findEnrolledStudents(pageRequest)
				.map(student -> {
					EnrollmentSummaryDTO dto = new EnrollmentSummaryDTO();
					dto.setStudentId(student.getId());
					dto.setStudentName(student.getFirstName() + " "+student.getLastName());
					dto.setEmail(student.getEmail());
					
					dto.setCourseCount(student.getEnrollments().size());
					BigDecimal totalFee = student.getEnrollments().stream()
							.map(enrollment -> enrollment.getCourse().getFee())
							.filter(fee -> fee != null)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					dto.setTotalFee(totalFee);
					
					return dto;
				})
				.getContent();
	}

	@Override
	public void deleteEnrollment(Long enrollmentId) {
		Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
				.orElseThrow(() -> new RuntimeException("No enrollment found"));
		
		enrollmentRepository.delete(enrollment);
	}

}
