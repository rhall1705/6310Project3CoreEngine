package edu.gatech.cs6310.projectOne;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CoreEngine {

    private final static int MAX_NUMBER_COURSES_PER_STUDENT_PER_SEMESTER = 10;
    public final static int SEMESTER_FALL = 0;
    public final static int SEMESTER_SPRING = 1;
    public final static int SEMESTER_SUMMER = 2;
    
    private static CoreEngine instance;
    
    public static CoreEngine getInstance() {
    	if(instance == null) {
    		instance = new CoreEngine();
    	}
    	
    	return instance;
    }
    
    private CoreEngine() {}
    
    public List<StudentCourseMapping> computeStudentMappingHardCoded(int semester) {
    	return computeStudentMappingWithAddedData(null, semester);
    }
    
    public List<StudentCourseMapping> computeStudentMappingWithAddedData(Collection<Student> students, int semester) {
    	CourseCatalog catalog = CourseCatalog.getInstance();
    	StudentDirectory studentDirectory = StudentDirectory.getInstance();
    	
    	if(catalog == null || studentDirectory == null ) {
    		System.out.println("Error reading files");
    		return null;
    	}
    	
    	if(students != null) {
    		studentDirectory.addStudents(students);
    	}
    	return computeStudentMapping(catalog, studentDirectory, semester);
	}
    
    public List<InstructorCourseMapping> computeInstructorMappingHardCoded(int semester) {
    	return computeInstructorMappingWithAddedData(null, semester);
    }
    
    public List<InstructorCourseMapping> computeInstructorMappingWithAddedData(Collection<Instructor> instructors, int semester) {
    	CourseCatalog catalog = CourseCatalog.getInstance();
    	InstructorDirectory instructorDirectory = InstructorDirectory.getInstance();
    	
    	if(catalog == null || instructorDirectory == null ) {
    		System.out.println("Error reading files");
    		return null;
    	}
    	
    	if(instructors != null) {
    		instructorDirectory.addInstructors(instructors);
    	}
    	return computeInstructorMapping(catalog, instructorDirectory, semester);
    }
    
    private List<StudentCourseMapping> computeStudentMapping(CourseCatalog catalog, StudentDirectory studentDirectory, int semester) {
    	GRBEnv env;
		try {
			env = new GRBEnv("mip1.log");
			env.set(GRB.IntParam.OutputFlag, 0);
			GRBModel model = new GRBModel(env);
			
			int numStudents = studentDirectory.getNumberStudents();
			int numCourses = catalog.getNumberCourses();
			
			//Each element of A maps whether student i takes course j 
			GRBVar[][] A = new GRBVar[numStudents][numCourses];
			
			//add all variables
			for(int i = 0; i < numStudents; i++) {
				for(int j = 0; j < numCourses; j++) {
	            	A[i][j] = model.addVar(0, 1, 0.0, GRB.BINARY, i + "_" + j);
				}
			}
            
            GRBVar X = model.addVar(1, 1000, 0.0, GRB.INTEGER, "X");
			
			//Integrate new variables
			model.update();
			
			// Set the objective as minimization of X
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1, X);
            model.setObjective(expr, GRB.MINIMIZE);
                        
            //Add Constraints that students must take their desired courses
            //Only add these constraints if the student has already taken the prereqs
            //and if the course is offered this semester
            for(Student student : studentDirectory.getStudents()) {
            	int studentId = student.getId();
            	List<Course> requiredCourses = student.getRequiredCourses();
	            for(Course c : requiredCourses) {
	            	if(!c.seatsAvailable(semester)) {
	            		continue;
	            	}
	            	List<Course> preReqs = c.getPrerequisiteCourses();
            		if(student.hasTakenCourses(preReqs)) {
            			int courseId = c.getId();
            			expr = new GRBLinExpr();
            			expr.addTerm(1, A[studentId][courseId]);
            			model.addConstr(expr, GRB.EQUAL, 1, "Student " + studentId + " takes class " + courseId);
            		}
	            }
			}
            
            //Add Constraints for each student to only take up to the maximum number of
            //courses per semester
            for(int i = 0; i < numStudents; i++) {
        		expr = new GRBLinExpr();
        		for(int j = 0; j < numCourses; j++) {
    				expr.addTerm(1, A[i][j]);
        		}
            	model.addConstr(expr, GRB.LESS_EQUAL, MAX_NUMBER_COURSES_PER_STUDENT_PER_SEMESTER, "Student " + i + " classes");
            }
            
            // Add Constraints for each class so that the sum of students taking
            // the course during each semester is less than or equal to the max
            // class size X, taking into account classes not offered
            for(int j = 0; j < numCourses; j++) {
        		expr = new GRBLinExpr();
        		for(int i = 0; i < numStudents; i++) {
    				expr.addTerm(1, A[i][j]);
        		}
        		boolean offeredThisSemester = catalog.getCourse(j).seatsAvailable(semester);
        		if(offeredThisSemester) {
        			model.addConstr(expr, GRB.LESS_EQUAL, X, "Course " + j + " seats");
        		} else {
        			model.addConstr(expr, GRB.EQUAL, 0, "Course " + j + " seats");
        		}
            }
            
            // Optimize the model
            model.optimize();
            
            // Display our results
            return mapStudentResults(A, studentDirectory, catalog);
		} catch (GRBException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    private List<StudentCourseMapping> mapStudentResults(GRBVar[][] A, StudentDirectory directory, CourseCatalog catalog) throws GRBException {
    	List<StudentCourseMapping> results = new ArrayList<StudentCourseMapping>();
    	List<Student> students = directory.getStudents();
    	for(int i = 0; i < A.length; i++) {
    		Student student = students.get(i);
    		List<Course> courses = new ArrayList<Course>();
    		for(int j = 0; j < A[0].length; j++) {
    			double value = A[i][j].get(GRB.DoubleAttr.X);
    			if(1.0 == value) {
    				Course course = catalog.getCourse(j);
    				courses.add(course);
    				System.out.println("Student " + student.getName() + " takes course " + course.getName());
    			}
    		}
    		results.add(new StudentCourseMapping(student, courses));
    	}
    	return results;
    }
    
    private List<InstructorCourseMapping> computeInstructorMapping(CourseCatalog catalog, InstructorDirectory instructorDirectory, int semester) {
    	GRBEnv env;
		try {
			env = new GRBEnv("mip2.log");
			env.set(GRB.IntParam.OutputFlag, 0);
			GRBModel model = new GRBModel(env);
			
			int numInstructors = instructorDirectory.getNumberInstructors();
			int numCourses = catalog.getNumberCourses();
			
			//Each element of A maps whether instructor i teaches course j 
			GRBVar[][] A = new GRBVar[numInstructors][numCourses];
			
			//add all variables
			for(int i = 0; i < numInstructors; i++) {
				for(int j = 0; j < numCourses; j++) {
	            	A[i][j] = model.addVar(0, 1, 0.0, GRB.BINARY, i + "_" + j);
				}
			}
            
            GRBVar X = model.addVar(1, 1000, 0.0, GRB.INTEGER, "X");
			
			//Integrate new variables
			model.update();
			
			// Set the objective as minimization of X
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1, X);
            model.setObjective(expr, GRB.MINIMIZE);
            
            //Add Constraints that each course must have exactly one instructor
            //Alternatively, if they're not offered that semester, they must have no instructors
            for(int i = 0; i < numCourses; i++) {
            	expr = new GRBLinExpr();
            	for(int j = 0; j < numInstructors; j++) {
            		expr.addTerm(1, A[j][i]);
            	}
            	boolean hasSeats = catalog.getCourse(i).seatsAvailable(semester);
            	model.addConstr(expr, GRB.EQUAL, hasSeats ? 1 : 0, "Course " + i);
            }
            
            //Add Constraints that instructors can't teach a course
            //if they are not proficient in it
            for(Instructor instructor : instructorDirectory.getInstructors()) {
            	for(Course course : catalog.getCourses()) {
            		int courseId = course.getId();
            		if(instructor.getProficientCourse(course.getId()) == null) {
            			expr = new GRBLinExpr();
            			expr.addTerm(1, A[instructor.index][courseId]);
            			model.addConstr(expr, GRB.EQUAL, 0, "Instructor " + instructor.getId() + " can't teach course " + courseId);
            		}
            	}
            }
            
            //Add Constraints for each instructor to only teach up 
            //to the maximum number of courses X per semester
            for(int i = 0; i < numInstructors; i++) {
        		expr = new GRBLinExpr();
        		for(int j = 0; j < numCourses; j++) {
    				expr.addTerm(1, A[i][j]);
        		}
        		
            	model.addConstr(expr, GRB.LESS_EQUAL, X, "Instructor  " + i + " classes");
            }
            
            // Optimize the model
            model.optimize();
            //model.dispose();
            
            // Display our results
            return mapInstructorResults(A, instructorDirectory, catalog);
		} catch (GRBException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    private List<InstructorCourseMapping> mapInstructorResults(GRBVar[][] A, InstructorDirectory directory, CourseCatalog catalog) throws GRBException {
    	List<InstructorCourseMapping> results = new ArrayList<InstructorCourseMapping>();
    	List<Instructor> instructors = directory.getInstructors();
    	for(int i = 0; i < A.length; i++) {
    		Instructor instructor = instructors.get(i);
    		List<Course> courses = new ArrayList<Course>();
    		for(int j = 0; j < A[0].length; j++) {
    			double value = A[i][j].get(GRB.DoubleAttr.X);
    			if(1.0 == value) {
    				Course course = catalog.getCourse(j);
    				courses.add(course);
    				System.out.println("Instructor " + instructor.name + " teaches course " + course.getName());
    			}
    		}
    		results.add(new InstructorCourseMapping(instructor, courses));
    	}
    	return results;
    }

}
