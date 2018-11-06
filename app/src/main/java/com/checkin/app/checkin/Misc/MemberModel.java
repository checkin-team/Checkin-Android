package com.checkin.app.checkin.Misc;

public class MemberModel {
    private String profilePic;
    private String fullName;

    private String role;

    public String getFullName() {
        return fullName;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public MemberModel(String name,String roleMember)
    {
        this.fullName=name;
        this.role=roleMember;
    }
}
