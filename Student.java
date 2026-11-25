/**
 * One student in the class.
 * id is assigned by the tracker, rollNo is the student's college roll number.
 */
public class Student {
    private int id;
    private String name;
    private String rollNo;

    public Student(int id, String name, String rollNo) {
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRollNo() {
        return rollNo;
    }

    // Day 6: setters so a student's name or roll number can be corrected
    // after they have been added, instead of deleting and re-adding them.
    public void setName(String name) {
        this.name = name;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    @Override
    public String toString() {
        return id + ". " + name + " (Roll No: " + rollNo + ")";
    }
}
