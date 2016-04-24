package edu.gatech.cs6310.projectOne;

public class ProjectOne {

    public static void main(String[] args) {
    	CoreEngine coreEngine = CoreEngine.getInstance();
    	coreEngine.computeStudentMappingHardCoded(CoreEngine.SEMESTER_SPRING);
    	coreEngine.computeInstructorMappingHardCoded(CoreEngine.SEMESTER_SPRING);
	}

}