package edu.gatech.cs6310.projectOne;

import java.util.ArrayList;
import java.util.List;

public class ProjectOne {

    public static void main(String[] args) {
    	CoreEngine coreEngine = CoreEngine.getInstance();
    	
    	List<Course> requiredCourses = new ArrayList<Course>();
    	requiredCourses.add(CourseCatalog.getInstance().getCourse(12));
    	Student student = new Student.Builder(10).addRequiredCourses(requiredCourses).setName("Sample Student Look Here Trevor").build();
    	List<Student> students = new ArrayList<Student>();
    	students.add(student);
    	
    	coreEngine.computeStudentMappingWithAddedData(students, CoreEngine.SEMESTER_SPRING);
    	coreEngine.computeInstructorMappingHardCoded(CoreEngine.SEMESTER_SPRING);
	}

}