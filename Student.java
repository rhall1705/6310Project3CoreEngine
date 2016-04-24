package edu.gatech.cs6310.projectOne;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;

public class Student {
	
	// Details not necessary for computation
	private String name;
	private String concentration;
	private long enrollDateInMillis;
	private long expectedGraduationInMillis;
	
	// Details necessary for computation
	private int id;
	private List<Course> requiredCourses;
	private HashMap<Integer, Course> takenCourses;

    private Student(int id) {
    	this.id = id;
    }
    
    public int getId() {
    	return id;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getConcentration() {
    	return concentration;
    }
    
    public long getEnrollDateInMillis() {
    	return enrollDateInMillis;
    }
    
    public long getExpectedGraduationInMillis() {
    	return expectedGraduationInMillis;
    }
    
    public void addRequiredCourse(Course course) {
    	requiredCourses.add(course);
    }
    
    public List<Course> getRequiredCourses() {
    	return requiredCourses;
    }
    
    public void addTakenCourse(Integer index, Course course) {
    	takenCourses.put(index, course);
    }
    
    public Course getTakenCourse(Integer index) {
    	return takenCourses.get(index);
    }
    
    public boolean hasTakenCourses(Collection<Course> courses) {
    	for(Course course : courses) {
    		if(takenCourses.get(course.getId()) == null) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public static class Builder {
    	private String name;
    	private String concentration;
    	private long enrollDateInMillis;
    	private long expectedGraduationInMillis;
    	
    	// Details necessary for computation
    	private int id;
    	private List<Course> requiredCourses = new ArrayList<Course>();
    	private HashMap<Integer, Course> takenCourses = new HashMap<Integer, Course>();

    	public Builder(int id) {
    		this.id = id;
    	}
    	
    	public Builder addRequiredCourses(Collection<Course> courses) {
    		requiredCourses.addAll(courses);
    		return this;
    	}
    	
    	public Builder addTakenCourses(Collection<Course> courses) {
    		for(Course course : courses) {
    			takenCourses.put(course.getId(), course);
    		}
    		return this;
    	}
    	
    	public Builder setName(String name) {
    		this.name = name;
    		return this;
    	}
    	
    	public Builder setConcentration(String concentration) {
    		this.concentration = concentration;
    		return this;
    	}
    	
    	public Builder setEnrollDate(long enrollDate) {
    		this.enrollDateInMillis = enrollDate;
    		return this;
    	}
    	
    	public Builder setExpectedGraduation(long expectedGraduation) {
    		this.expectedGraduationInMillis = expectedGraduation;
    		return this;
    	}
    	
    	public Student build() {
    		Student student = new Student(id);
    		student.requiredCourses = requiredCourses;
    		student.takenCourses = takenCourses;
    		student.name = name;
    		student.concentration = concentration;
    		student.enrollDateInMillis = enrollDateInMillis;
    		student.expectedGraduationInMillis = expectedGraduationInMillis;
    		return student;
    	}
    	
    }
    
}