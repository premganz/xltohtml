package com.example.clime.service;

import com.example.clime.model.Course;
import com.example.clime.model.Student;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudentService {

    private Map<String, Student> studentDatabase = new HashMap<>();

    public StudentService() {
        // Sample data with courses
        Course course1 = new Course("CS101", "Computer Science Fundamentals", 40);
        Course course2 = new Course("MATH201", "Advanced Mathematics", 35);
        Course course3 = new Course("ENG101", "English Literature", 30);
        
        studentDatabase.put("S001", new Student("S001", "John Doe", Arrays.asList(course1, course2)));
        studentDatabase.put("S002", new Student("S002", "Jane Smith", Arrays.asList(course2, course3)));
        studentDatabase.put("S003", new Student("S003", "Bob Johnson", Arrays.asList(course1, course3)));
    }

    public Student findStudentById(String id) {
        return studentDatabase.get(id);
    }
}