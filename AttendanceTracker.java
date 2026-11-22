import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Student Attendance Tracker — Day 3.
 * Attendance is stored as one record per student per date, and students
 * and records are now saved to students.csv and attendance.csv, so the
 * data is still there the next time the app starts.
 */
public class AttendanceTracker {

    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<AttendanceRecord> records = new ArrayList<>();
    private int nextId = 1;

    public static void main(String[] args) {
        AttendanceTracker tracker = new AttendanceTracker();
        tracker.run();
    }

    private void run() {
        loadData();
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
                    viewAttendanceForDate(scanner);
                    break;
                case 5:
                    viewStudentHistory(scanner);
                    break;
                case 6:
                    saveData();
                    System.out.println("Goodbye! Your data is saved in students.csv and attendance.csv.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please choose 1-6.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== Student Attendance Tracker =====");
        System.out.println("Today: " + LocalDate.now());
        System.out.println("1. Add a student");
        System.out.println("2. List all students");
        System.out.println("3. Mark attendance (for any date)");
        System.out.println("4. View attendance for a date");
        System.out.println("5. View one student's attendance history");
        System.out.println("6. Exit");
    }

    // Loads saved students and records at startup, and works out the
    // next free student id from the highest id already saved.
    private void loadData() {
        students = AttendanceStorage.loadStudents();
        records = AttendanceStorage.loadRecords();
        for (Student student : students) {
            if (student.getId() >= nextId) {
                nextId = student.getId() + 1;
            }
        }
        if (!students.isEmpty() || !records.isEmpty()) {
            System.out.println("Loaded " + students.size() + " student(s) and "
                    + records.size() + " attendance record(s) from file.");
        }
    }

    // Writes the current students and records to the CSV files.
    private void saveData() {
        AttendanceStorage.saveStudents(students);
        AttendanceStorage.saveRecords(records);
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

    // Asks for a date in yyyy-MM-dd; blank means today.
    // Returns null if the date could not be understood.
    private LocalDate askDate(Scanner scanner) {
        System.out.print("Enter date (yyyy-MM-dd) or press Enter for today: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("That date is not in yyyy-MM-dd format, e.g. 2026-11-20.");
            return null;
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
        saveData();
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

    // Finds the record for one student on one date, or null if none exists.
    private AttendanceRecord findRecord(int studentId, LocalDate date) {
        for (AttendanceRecord record : records) {
            if (record.getStudentId() == studentId && record.getDate().equals(date)) {
                return record;
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
        LocalDate date = askDate(scanner);
        if (date == null) {
            return;
        }
        System.out.print("Present on " + date + "? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        boolean present;
        if (answer.equals("y") || answer.equals("yes")) {
            present = true;
        } else if (answer.equals("n") || answer.equals("no")) {
            present = false;
        } else {
            System.out.println("Please answer y or n.");
            return;
        }
        AttendanceRecord existing = findRecord(id, date);
        if (existing != null) {
            existing.setPresent(present);
            System.out.println("Updated: " + student.getName() + " is now "
                    + (present ? "PRESENT" : "ABSENT") + " on " + date + ".");
        } else {
            records.add(new AttendanceRecord(id, date, present));
            System.out.println(student.getName() + " marked "
                    + (present ? "PRESENT" : "ABSENT") + " for " + date + ".");
        }
        saveData();
    }

    private void viewAttendanceForDate(Scanner scanner) {
        LocalDate date = askDate(scanner);
        if (date == null) {
            return;
        }
        if (students.isEmpty()) {
            System.out.println("No students added yet.");
            return;
        }
        System.out.println("Attendance for " + date + ":");
        for (Student student : students) {
            AttendanceRecord record = findRecord(student.getId(), date);
            if (record == null) {
                System.out.println("  " + student.getName() + " — Not marked");
            } else {
                System.out.println("  " + student.getName() + " — "
                        + (record.isPresent() ? "Present" : "Absent"));
            }
        }
    }

    private void viewStudentHistory(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("Add a student first.");
            return;
        }
        listStudents();
        System.out.print("Enter student id: ");
        int id = readInt(scanner);
        Student student = findById(id);
        if (student == null) {
            System.out.println("No student with id " + id + ".");
            return;
        }
        ArrayList<AttendanceRecord> history = new ArrayList<>();
        for (AttendanceRecord record : records) {
            if (record.getStudentId() == id) {
                history.add(record);
            }
        }
        if (history.isEmpty()) {
            System.out.println("No attendance recorded for " + student.getName() + " yet.");
            return;
        }
        history.sort(Comparator.comparing(AttendanceRecord::getDate));
        System.out.println("Attendance history for " + student.getName() + ":");
        int presentCount = 0;
        for (AttendanceRecord record : history) {
            System.out.println("  " + record);
            if (record.isPresent()) {
                presentCount++;
            }
        }
        System.out.println("  Total: " + presentCount + " present out of "
                + history.size() + " days recorded.");
    }
}
