package com.example.clime.service;

import com.example.clime.model.Student;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private Map<String, Student> studentDatabase = new HashMap<>();
    private List<Student> allStudents;

    public StudentService() {
        // Sample data matching the MHT file format
        Student student1 = new Student("1", "John Doe", "Computer Science", "A");
        Student student2 = new Student("2", "Jane Smith", "Mathematics", "B");
        Student student3 = new Student("3", "Bob Johnson", "Physics", "A-");
        
        studentDatabase.put("1", student1);
        studentDatabase.put("2", student2);
        studentDatabase.put("3", student3);
        
        allStudents = Arrays.asList(student1, student2, student3);
    }

    public Student findStudentById(String id) {
        Student student = studentDatabase.get(id);
        if (student != null) {
            // Include the list of all students for the iteration feature
            Student result = new Student(student.getStudentId(), student.getName(), 
                                       student.getCourse(), student.getGrade());
            result.setStudents(allStudents);
            return result;
        }
        return null;
    }
}