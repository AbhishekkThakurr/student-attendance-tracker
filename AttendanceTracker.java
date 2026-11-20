import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Student Attendance Tracker — Day 1.
 * Add students, list them, and mark today's attendance (present / absent).
 * Attendance is kept in memory for now; saving to a file comes on Day 3.
 */
public class AttendanceTracker {

    private ArrayList<Student> students = new ArrayList<>();
    // student id -> true means present today, false means absent today
    private Map<Integer, Boolean> todayAttendance = new HashMap<>();
    private int nextId = 1;

    public static void main(String[] args) {
        AttendanceTracker tracker = new AttendanceTracker();
        tracker.run();
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            System.out.print("Choose an option: ");
            int choice = readInt(scanner);
            if (choice == -1) {
                System.out.println("Please enter a number from the menu.");
                continue;
            }
            switch (choice) {
                case 1:
                    addStudent(scanner);
                    break;
                case 2:
                    listStudents();
                    break;
                case 3:
                    markAttendance(scanner);
                    break;
                case 4:
                    viewTodayAttendance();
                    break;
                case 5:
                    System.out.println("Goodbye! Attendance for today is kept only until you exit.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== Student Attendance Tracker =====");
        System.out.println("Today: " + LocalDate.now());
        System.out.println("1. Add a student");
        System.out.println("2. List all students");
        System.out.println("3. Mark today's attendance");
        System.out.println("4. View today's attendance");
        System.out.println("5. Exit");
    }

    // Reads one integer; returns -1 if the input was not a number.
    private int readInt(Scanner scanner) {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void addStudent(Scanner scanner) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        System.out.print("Enter roll number: ");
        String rollNo = scanner.nextLine().trim();
        if (rollNo.isEmpty()) {
            System.out.println("Roll number cannot be empty.");
            return;
        }
        Student student = new Student(nextId, name, rollNo);
        students.add(student);
        System.out.println("Added: " + student);
        nextId++;
    }

    private void listStudents() {
        if (students.isEmpty()) {
            System.out.println("No students added yet.");
            return;
        }
        System.out.println("Students (" + students.size() + "):");
        for (Student student : students) {
            System.out.println("  " + student);
        }
    }

    private Student findById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    private void markAttendance(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("Add a student first.");
            return;
        }
        listStudents();
        System.out.print("Enter student id to mark: ");
        int id = readInt(scanner);
        Student student = findById(id);
        if (student == null) {
            System.out.println("No student with id " + id + ".");
            return;
        }
        System.out.print("Present today? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (answer.equals("y") || answer.equals("yes")) {
            todayAttendance.put(id, true);
            System.out.println(student.getName() + " marked PRESENT for " + LocalDate.now() + ".");
        } else if (answer.equals("n") || answer.equals("no")) {
            todayAttendance.put(id, false);
            System.out.println(student.getName() + " marked ABSENT for " + LocalDate.now() + ".");
        } else {
            System.out.println("Please answer y or n.");
        }
    }

    private void viewTodayAttendance() {
        if (todayAttendance.isEmpty()) {
            System.out.println("No attendance marked for " + LocalDate.now() + " yet.");
            return;
        }
        System.out.println("Attendance for " + LocalDate.now() + ":");
        for (Student student : students) {
            if (todayAttendance.containsKey(student.getId())) {
                String status = todayAttendance.get(student.getId()) ? "Present" : "Absent";
                System.out.println("  " + student.getName() + " — " + status);
            }
        }
    }
}
