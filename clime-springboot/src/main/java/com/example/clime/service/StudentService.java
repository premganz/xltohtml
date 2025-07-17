package com.example.clime.service;

import com.example.clime.model.Student;
import com.example.clime.model.Course;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private Map<String, Student> studentDatabase = new HashMap<>();

    public StudentService() {
        // Sample data matching the expected structure from the mini-language example
        Course course1 = new Course("CS101", "Computer Science");
        Course course2 = new Course("MATH201", "Advanced Mathematics");
        Course course3 = new Course("PHY301", "Quantum Physics");
        
        Student student1 = new Student("1", "John Doe", Arrays.asList(course1, course2));
        Student student2 = new Student("2", "Jane Smith", Arrays.asList(course2, course3));
        Student student3 = new Student("3", "Bob Johnson", Arrays.asList(course1, course3));
        
        studentDatabase.put("1", student1);
        studentDatabase.put("2", student2);
        studentDatabase.put("3", student3);
    }

    public Student findStudentById(String id) {
        return studentDatabase.get(id);
    }
}