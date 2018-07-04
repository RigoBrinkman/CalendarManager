package com.brinkbros;

public class CalmanAssignment {
    
    private int id;
    private int eventId;
    private CalmanUser user;
    private int assignmentType;
    
    public CalmanAssignment(int id, int eventId, CalmanUser user, int assignmentType){
        this.id = id;
        this.eventId = eventId;
        this.user = user;
        this.assignmentType = assignmentType;
    }

    public int getId() {
        return id;
    }

    public int getEventId() {
        return eventId;
    }

    public CalmanUser getUser() {
        return user;
    }

    public int getAssignmentType() {
        return assignmentType;
    }
    
    public String getTypeString(){
        switch(assignmentType){
            case 0:
                return "Hoofdverantwoordelijke";
            case 1:
                return "MT-verantwoordelijke";
            case 2:
                return "Tracker";
            case 3:
                return "Overig";
            default:
                throw new IllegalArgumentException("onbekend assignmenttype");
        }
    }
    
}
