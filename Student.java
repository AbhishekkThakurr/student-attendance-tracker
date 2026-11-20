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

    @Override
    public String toString() {
        return id + ". " + name + " (Roll No: " + rollNo + ")";
    }
}
