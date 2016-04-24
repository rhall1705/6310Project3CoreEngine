package edu.gatech.cs6310.projectOne;

import java.util.HashMap;

public class Instructor {

	public String name;
	private int id;
	// unlike course and student, instructor ids do not function as indices
	public int index;
	private HashMap<Integer, Course> proficientCourses = new HashMap<Integer, Course>();

    public Instructor(int id) {
    	this.id = id;
    }
    
    public int getId() {
    	return id;
    }
    
    public void addProficientCourse(Integer index, Course course) {
    	proficientCourses.put(index, course);
    }
    
    public Course getProficientCourse(Integer index) {
    	return proficientCourses.get(index);
    }

}
