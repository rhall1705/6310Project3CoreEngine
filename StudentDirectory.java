package edu.gatech.cs6310.projectOne;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class StudentDirectory {
	
	private static final String PATH_STUDENT_DEMAND = "student_demand_10.csv";
	private static final String PATH_STUDENT_HISTORY = "student_history_10.csv";
	private static final String PATH_USER_INFO = "users_10.csv";
	
    private List<Student> students;
    
    private static StudentDirectory instance;
    
    public static StudentDirectory getInstance() {
    	if(instance == null) {
    		try {
				instance = new StudentDirectory();
			} catch (IOException e) {
				return null;
			}
    	}
    	
    	return instance;
    }

    private StudentDirectory() throws IOException {
    	List<String> demandFileLines = Files.readAllLines(Paths.get(PATH_STUDENT_DEMAND));
    	List<String> historyFileLines = Files.readAllLines(Paths.get(PATH_STUDENT_HISTORY));
    	List<String> infoFileLines = Files.readAllLines(Paths.get(PATH_USER_INFO));
        generateStudents(demandFileLines, historyFileLines, infoFileLines);
    }

    public List<Student> getStudents() {
    	return students;
    }

    public int getNumberStudents() {
        return students == null ? 0 : students.size();
    }
    
    public void addStudents(Collection<Student> students) {
    	int oldSize = this.students.size();
    	this.students.addAll(students);
    	reapplyHistory(oldSize - 1); 
    }
    
    private void generateStudents(List<String> demand, List<String> history, List<String> info) {
    	CourseCatalog catalog = CourseCatalog.getInstance();
    	students = new ArrayList<Student>();
    	HashMap<Integer, Student> studentMap = new HashMap<Integer, Student>();
    	
    	//row 0 in the input file is the column labels
    	for(int i = 1; i < demand.size(); i++) {
    		String[] courseStrings = demand.get(i).split(",");
    		int studentId = Integer.parseInt(courseStrings[0]) - 1;
    		int courseId = Integer.parseInt(courseStrings[1]) - 1;
    		Student mappedStudent = studentMap.get(studentId);
    		Student student = mappedStudent == null ?
    				new Student.Builder(studentId).build() :
    					mappedStudent;
    		student.addRequiredCourse(catalog.getCourse(courseId));
    		studentMap.put(studentId, student);
    	}
    	
    	for(Student student : studentMap.values()) {
    		students.add(student.getId(), student);
    	}
    	
    	for(int i = 1; i < history.size(); i++) {
    		String[] courseStrings = history.get(i).split(",");
    		int studentNumber = Integer.parseInt(courseStrings[0]) - 1;
    		if(studentNumber < students.size()) {
    			int courseNumber = Integer.parseInt(courseStrings[1]) - 1;
    			students.get(studentNumber).addTakenCourse(courseNumber, catalog.getCourse(courseNumber));
    		}
    	}
    	
    	for(int i = 1; i < info.size(); i++) {
    		String[] infoStrings = info.get(i).split(",");
    		String roleId = infoStrings[5];
    		if("5".equals(roleId)) {
    			int id = Integer.parseInt(infoStrings[0]) - 1;
    			if(id < students.size()) {
    				String firstName = infoStrings[1];
    				String lastName = infoStrings[2];
    				students.get(id).setName(firstName + " " +  lastName);
    			}
    		}
    	}
    }
    
    private void reapplyHistory(int oldSize) {
    	try {
			List<String> history = Files.readAllLines(Paths.get(PATH_STUDENT_HISTORY));
			CourseCatalog catalog = CourseCatalog.getInstance();
			for(int i = 1; i < history.size() && i < students.size(); i++) {
	    		String[] courseStrings = history.get(i).split(",");
	    		int studentNumber = Integer.parseInt(courseStrings[0]) - 1;
	    		if(studentNumber > oldSize) {
	    			int courseNumber = Integer.parseInt(courseStrings[1]) - 1;
	    			students.get(studentNumber).addTakenCourse(courseNumber, catalog.getCourse(courseNumber));
	    		}
	    	}
		} catch (IOException ignored) {}
    }
    
}