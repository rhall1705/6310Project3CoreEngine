package edu.gatech.cs6310.projectOne;

import java.util.List;

public class InstructorCourseMapping {
	public Instructor instructor;
	public List<Course> courses;
	
	public InstructorCourseMapping(Instructor instructor, List<Course> courses) {
		this.instructor = instructor;
		this.courses = courses;
	}
}
