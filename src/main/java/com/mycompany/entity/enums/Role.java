package com.mycompany.entity.enums;

public enum Role {
    EMPLOYEE("Employee", 1),
    MANAGER("Manager", 2);

    private final int id;
    private final String name;

    Role(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
