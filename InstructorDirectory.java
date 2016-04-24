package edu.gatech.cs6310.projectOne;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InstructorDirectory {
	
	private static final String PATH_INSTRUCTOR_PROFICIENCY = "instructor_pool_10.csv";
	private static final String PATH_USER_INFO = "users_10.csv";

	private List<Instructor> instructorList;
	private HashMap<Integer, Instructor> instructorMap;
	
	private static InstructorDirectory instance;
	
	public static InstructorDirectory getInstance() {
		if(instance == null) {
			try {
				instance = new InstructorDirectory();
			} catch (IOException e) {
				return null;
			}
		}
		
		return instance;
	}

    private InstructorDirectory() throws IOException {
    	List<String> instructorProficiencyLines = Files.readAllLines(Paths.get(PATH_INSTRUCTOR_PROFICIENCY));
    	List<String> infoFileLines = Files.readAllLines(Paths.get(PATH_USER_INFO));
        generateInstructors(instructorProficiencyLines, infoFileLines);
    }

    public List<Instructor> getInstructors() {
    	return instructorList;
    }

    public int getNumberInstructors() {
        return instructorList == null ? 0 : instructorList.size();
    }
    
    public void addInstructors(Collection<Instructor> instructors) {
    	for(Instructor instructor : instructors) {
    		instructor.index = instructorList.size();
    		instructorMap.put(instructor.getId(), instructor);
    		instructorList.add(instructor);
    	}
    }
    
    private void generateInstructors(List<String> input, List<String> info) {
    	CourseCatalog catalog = CourseCatalog.getInstance();
    	instructorList = new ArrayList<Instructor>();
    	instructorMap = new HashMap<Integer, Instructor>();
    	
    	//row 0 in the input file is the column labels
    	for(int i = 1; i < input.size(); i++) {
    		String[] courseStrings = input.get(i).split(",");
    		int instructorId = Integer.parseInt(courseStrings[0]) - 1;
    		int courseId = Integer.parseInt(courseStrings[1]) - 1;
    		Instructor mappedInstructor = instructorMap.get(instructorId);
    		Instructor instructor = mappedInstructor == null ? 
    					new Instructor(instructorId) :
    					mappedInstructor;
    		instructor.addProficientCourse(courseId, catalog.getCourse(courseId));
    		instructorMap.put(instructorId, instructor);
    	}
    	
    	int i = 0;
    	for(Instructor instructor : instructorMap.values()) {
    		instructor.index = i;
    		instructorList.add(instructor);
    		instructorMap.put(instructor.getId(), instructor);
    		i++;
    	}
    	
    	for(i = 1; i < info.size(); i++) {
    		String[] infoStrings = info.get(i).split(",");
    		String roleId = infoStrings[5];
    		if("1".equals(roleId) || "3".equals(roleId)) {
    			int id = Integer.parseInt(infoStrings[0]) - 1;
    			String firstName = infoStrings[1];
    			String lastName = infoStrings[2];
    			instructorMap.get(id).name = firstName + " " +  lastName;
    		}
    	}
    	
    }

    
}
