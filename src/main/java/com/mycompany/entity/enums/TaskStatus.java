package com.mycompany.entity.enums;

import java.util.Arrays;
import java.util.List;

public enum TaskStatus {
    NEW_TASK(1, "New task"),
    BUG(2, "Bug"),
    IN_PROCESS(3, "In process"),
    REOPEN(4, "Reopen"),
    RESOLVED(5, ("Resolved")),
    DONE(6, ("Done"));

    private final int id;
    private final String name;

    TaskStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<TaskStatus> getEmployeeStatuses() {
        List<TaskStatus> taskStatusList = Arrays.asList(IN_PROCESS, RESOLVED);
        return taskStatusList;
    }

}
