package com.ermile.payamres.item;

import java.util.List;

public class school {

    String name,sex;
    techer techers;
    List<arraySchool> arraySchoolList;

    public school(String name, String sex, techer techers) {
        this.name = name;
        this.sex = sex;
        this.techers = techers;
        this.arraySchoolList = arraySchoolList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public techer getTechers() {
        return techers;
    }

    public void setTechers(techer techers) {
        this.techers = techers;
    }

    public List<arraySchool> getArraySchoolList() {
        return arraySchoolList;
    }

    public void setArraySchoolList(List<arraySchool> arraySchoolList) {
        this.arraySchoolList = arraySchoolList;
    }
}
