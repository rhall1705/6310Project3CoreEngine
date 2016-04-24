package edu.gatech.cs6310.projectOne;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CourseCatalog {
	
	private static final String PATH_COURSE_DATA = "courses.csv";
	private static final String PATH_COURSE_DEPENDENCIES = "course_dependencies.csv";

    private Course[] courses;
    
    private static CourseCatalog instance;
    
    public static CourseCatalog getInstance() {
    	if(instance == null) {
    		try {
				instance = new CourseCatalog();
			} catch (IOException e) {
				return null;
			}
    	}
    	
    	return instance;
    }
    
    private CourseCatalog() throws IOException{
    	List<String> dataFileLines = Files.readAllLines(Paths.get(PATH_COURSE_DATA));
    	List<String> dependenciesFileLines = Files.readAllLines(Paths.get(PATH_COURSE_DEPENDENCIES));
        generateCourses(dataFileLines, dependenciesFileLines);
    }

    public Course getCourse(int index) {
        if(index < courses.length) {
            return courses[index];
        }
        return null;
    }
    
    public Course[] getCourses() {
    	return courses;
    }

    public int getNumberCourses() {
        return courses.length;
    }
    
    public List<Course> getPrerequisitesForCourseList(List<Course> courseList) {
        List<Course> prerequisiteCourses = new ArrayList<Course>();
        for(Course course : courseList) {
        	prerequisiteCourses.addAll(course.getPrerequisiteCourses());
        }
        return prerequisiteCourses;
    }

    private void generateCourses(List<String> data, List<String> dependencies) {
    	courses = new Course[data.size() - 1];
    	for(int i = 1; i < data.size(); i++) {
    		String[] dataStrings = data.get(i).split(",");
    		int id = Integer.parseInt(dataStrings[0]) - 1;
    		String name = dataStrings[1];
    		String number = dataStrings[2];
    		boolean fall = "1".equals(dataStrings[3]);
    		boolean spring = "1".equals(dataStrings[4]);
    		boolean summer = "1".equals(dataStrings[5]);
 
    		courses[id] = new Course.Builder(id, new boolean[] {fall, spring, summer})
    			.setName(name)
    			.setNumber(number)
    			.build();
    	}
    	
    	for(int i = 1; i < dependencies.size(); i++) {
    		String[] dependencyStrings = dependencies.get(i).split(",");
    		int dependentId = Integer.parseInt(dependencyStrings[1]) - 1;
    		int preReqId = Integer.parseInt(dependencyStrings[0]) - 1;
    		courses[dependentId].addPrerequisiteCourse(courses[preReqId]);
    	}

    }

}