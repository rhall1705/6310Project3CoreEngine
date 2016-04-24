package edu.gatech.cs6310.projectOne;

import java.util.List;

public class StudentCourseMapping {
	
	public Student student;
	public List<Course> courses;
	
	public StudentCourseMapping(Student student, List<Course> courses) {
		this.student = student;
		this.courses = courses;
	}

}
