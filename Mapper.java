package edu.gatech.cs6310.projectOne;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.List;

public class Mapper {
	
	private final static int NUMBER_YEARS = 4;
    private final static int NUMBER_SEMESTERS_PER_YEAR = 3;
    private final static int MAX_NUMBER_COURSES_PER_STUDENT_PER_SEMESTER = 2;
    
    public static void gurobiCompute() {
    	CourseCatalog catalog = CourseCatalog.getInstance();
    	StudentDirectory directory = StudentDirectory.getInstance();
    	GRBEnv env;
		try {
			env = new GRBEnv("mip1.log");
			env.set(GRB.IntParam.OutputFlag, 0);
			GRBModel model = new GRBModel(env);
			
			int numStudents = directory.getNumberStudents();
			int numCourses = catalog.getNumberCourses();
			int numSemesters = NUMBER_YEARS * NUMBER_SEMESTERS_PER_YEAR;
			
			//Each element of A maps whether student i takes course j in semester k
			GRBVar[][][] A = new GRBVar[numStudents][numCourses][numSemesters];
			
			//add all variables
			for(int i = 0; i < numStudents; i++) {
				for(int j = 0; j < numCourses; j++) {
					for(int k = 0; k < numSemesters; k++) {
	            		A[i][j][k] = model.addVar(0, 1, 0.0, GRB.BINARY, i + "_" + j + "_" + k);
					}
				}
			}
            
            GRBVar X = model.addVar(1, 1000, 0.0, GRB.INTEGER, "X");
			
			//Integrate new variables
			model.update();
			
			// Set the objective as minimization of X
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1, X);
            model.setObjective(expr, GRB.MINIMIZE);
            
            //Add Constraints that students can only take a given course once
            for(int i = 0; i < numStudents; i++) {
            	for(int j = 0; j < numCourses; j++) {
            		expr = new GRBLinExpr();
            		for(int k = 0; k < numSemesters; k++) {
            			expr.addTerm(1, A[i][j][k]);
            		}
                	model.addConstr(expr, GRB.LESS_EQUAL, 1, "Student " + i + " can take class " + j + "only once");
            	}
            }
                        
            //Add Constraints that students must take their requisite courses
            for(Student student : directory.getStudents()) {
            	int studentId = student.getId();
            	List<Course> requiredCourses = student.getRequiredCourses();
	            List<Course> prerequisitesForRequiredCourses = catalog.getPrerequisitesForCourseList(requiredCourses);
	            for(Course c : requiredCourses) {
            		int courseId = c.getId();
	            	expr = new GRBLinExpr();
	            	for(int k = 0; k < numSemesters; k++) {
	            		expr.addTerm(1, A[studentId][courseId][k]);
	            	}
                	model.addConstr(expr, GRB.EQUAL, 1, "Student " + studentId + " takes class " + courseId);
	            }
	            for(Course c : prerequisitesForRequiredCourses) {
            		int courseId = c.getId();
            		expr = new GRBLinExpr();
	            	for(int k = 0; k < numSemesters; k++) {
	            		expr.addTerm(1, A[studentId][courseId][k]);
	            	}
                	model.addConstr(expr, GRB.EQUAL, 1, "Student " + studentId + " takes class " + courseId);         
            	}
			}
            
            //Add Constraints for each student to only take up to the maximum number of
            //courses per semester
            
            for(int i = 0; i < numStudents; i++) {
            	for(int k = 0; k < numSemesters; k++) {
            		expr = new GRBLinExpr();
            		for(int j = 0; j < numCourses; j++) {
        				expr.addTerm(1, A[i][j][k]);
            		}
                	model.addConstr(expr, GRB.LESS_EQUAL, MAX_NUMBER_COURSES_PER_STUDENT_PER_SEMESTER, "Student " + i + " classes in semester" + k);
            	}
            }
            
            // Add Constraints for each class so that the sum of students taking
            // the course during each semester is less than or equal to the max
            // class size X, taking into account classes not offered
            
            for(int j = 0; j < numCourses; j++) {
            	for(int k = 0; k < numSemesters; k++) {
            		expr = new GRBLinExpr();
            		for(int i = 0; i < numStudents; i++) {
        				expr.addTerm(1, A[i][j][k]);
            		}
            		boolean offeredThisSemester = catalog.getCourse(j).seatsAvailable(k);
            		if(offeredThisSemester) {
            			model.addConstr(expr, GRB.LESS_EQUAL, X, "Course " + j + " seats in semester " + k);
            		} else {
            			model.addConstr(expr, GRB.EQUAL, 0, "Course " + j + " seats in semester " + k);
            		}
            	}
            }
            
            // Optimize the model
            model.optimize();
            
            // Display our results
            double objectiveValue = model.get(GRB.DoubleAttr.ObjVal);            
            System.out.printf("X=%.2f", objectiveValue);
		} catch (GRBException e) {
			e.printStackTrace();
		}
    }
    
}