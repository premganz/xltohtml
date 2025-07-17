package com.example.clime.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Student {
    private String studentId;
    private String name;
    private String course;
    private String grade;
    private List<Student> students; // For supporting the "all students" feature

    public Student() {}

    public Student(String studentId, String name, String course, String grade) {
        this.studentId = studentId;
        this.name = name;
        this.course = course;
        this.grade = grade;
    }

    @JsonProperty("studentId")
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}