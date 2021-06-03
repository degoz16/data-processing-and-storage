package xml1;


import java.util.Set;
import java.util.TreeSet;

public class Person {
    private String id = null;
    private String firstName = null;
    private String secondName = null;
    private Set<String> parents = new TreeSet<>();
    private String mother = null;
    private String father = null;
    private Set<String> siblings = new TreeSet<>();
    private Set<String> sisters = new TreeSet<>();
    private Set<String> brothers = new TreeSet<>();
    private String spouse = null;
    private String spouseGender = null;
    private String spouseName = null;
    private String gender = null;
    private Set<String> children = new TreeSet<>();
    private Set<String> daughters = new TreeSet<>();
    private Set<String> sons = new TreeSet<>();
    private Integer childrenNum = null;
    private Integer siblingsNum = null;

    public Person(String id) {
        this.id = id;
    }

    public String getSpouseGender() {
        return spouseGender;
    }

    public void setSpouseGender(String spouseGender) {
        this.spouseGender = spouseGender;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public Set<String> getChildren() {
        return children;
    }

    public Set<String> getDaughters() {
        return daughters;
    }


    public Set<String> getSons() {
        return sons;
    }

    public Integer getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(Integer childrenNum) {
        this.childrenNum = childrenNum;
    }

    public Integer getSiblingsNum() {
        return siblingsNum;
    }

    public void setSiblingsNum(Integer siblingsNum) {
        this.siblingsNum = siblingsNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Set<String> getParents() {
        return parents;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public Set<String> getSiblings() {
        return siblings;
    }

    public Set<String> getSisters() {
        return sisters;
    }

    public Set<String> getBrothers() {
        return brothers;
    }

    public String getSpouse() {
        return spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }

    public void setChildren(Set<String> children) {
        this.children = children;
    }
}
