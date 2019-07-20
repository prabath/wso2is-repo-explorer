package org.facilelogin.wso2is.repo.explorer.bean;

import java.util.Set;

public class Patch {

    private String name;
    private String jarVersion;
    private Set<String> prioducts;
    private int year;
    private String month;
    private String repoName;
    private String compName;

    public Patch(String name, String jarVersion, Set<String> prioducts) {
        super();
        this.name = name;
        this.jarVersion = jarVersion;
        this.prioducts = prioducts;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJarVersion() {
        return jarVersion;
    }

    public void setJarVersion(String jarVersion) {
        this.jarVersion = jarVersion;
    }

    public Set<String> getProductVersion() {
        return prioducts;
    }

    public void setProductVersion(Set<String> prioducts) {
        this.prioducts = prioducts;
    }

}
