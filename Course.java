package edu.gatech.cs6310.projectOne;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Course {

	private String name;
	private String number;
	
    private int id;
    private boolean[] seatsAvailablePerSemester;
    private List<Course> prerequisiteCourses;

    private Course(int id, boolean[] seatsAvailablePerSemester) {
        this.id = id;
        this.seatsAvailablePerSemester = seatsAvailablePerSemester;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getNumber() {
    	return number;
    }

    public boolean seatsAvailable(int semesterIndex) {
        if(seatsAvailablePerSemester != null && seatsAvailablePerSemester.length > semesterIndex) {
            return seatsAvailablePerSemester[semesterIndex];
        }
        return false;
    }

    public void addPrerequisiteCourse(Course prerequisiteCourse) {
        this.prerequisiteCourses.add(prerequisiteCourse);
    }

    public int getId() {
        return id;
    }

    public List<Course> getPrerequisiteCourses() {
        return prerequisiteCourses;
    }
    
    public static class Builder {
    	private String name;
    	private String number;
    	
    	private int id;
        private boolean[] seatsAvailablePerSemester;
        private List<Course> prerequisiteCourses = new ArrayList<Course>();
        
        public Builder(int id, boolean[] seatsAvailablePerSemester) {
            this.id = id;
            this.seatsAvailablePerSemester = seatsAvailablePerSemester;
        }
        
        public Builder addPrerequisiteCourses(Collection<Course> courses) {
        	this.prerequisiteCourses.addAll(courses);
        	return this;
        }
        
        public Builder setName(String name) {
        	this.name = name;
        	return this;
        }
        
        public Builder setNumber(String number) {
        	this.number = number;
        	return this;
        }
        
        public Course build() {
        	Course course = new Course(id, seatsAvailablePerSemester);
        	course.prerequisiteCourses = prerequisiteCourses;
        	course.name = name;
        	course.number = number;
        	return course;
        }
    }

}