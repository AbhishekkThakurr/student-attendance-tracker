import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Student Attendance Tracker — Day 6.
 * Attendance is stored as one record per student per date, and students
 * and records are now saved to students.csv and attendance.csv, so the
 * data is still there the next time the app starts. Day 4 added the
 * attendance percentage report: present days out of recorded days,
 * per student. Day 5 adds the low-attendance warning — any student below
 * 75% is flagged in the report, in a dedicated warnings list, and right
 * after being marked absent — and tightens input validation: bad dates
 * and bad y/n answers are re-asked instead of cancelling the action,
 * future dates are refused, and duplicate roll numbers are rejected.
 * Day 6 adds managing records: search a student by name or roll number,
 * edit a student's name or roll number, delete a student together with
 * all of their attendance records, and delete one attendance record
 * (for a marking made by mistake). Every change is saved to CSV.
 */
public class AttendanceTracker {

    // The usual minimum attendance for exams; below this, warn.
    private static final double LOW_ATTENDANCE_LIMIT = 75.0;

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
                    viewPercentageReport();
                    break;
                case 7:
                    viewLowAttendanceWarnings();
                    break;
                case 8:
                    searchStudent(scanner);
                    break;
                case 9:
                    editStudent(scanner);
                    break;
                case 10:
                    deleteStudent(scanner);
                    break;
                case 11:
                    deleteAttendanceRecord(scanner);
                    break;
                case 12:
                    saveData();
                    System.out.println("Goodbye! Your data is saved in students.csv and attendance.csv.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please choose 1-12.");
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
        System.out.println("6. Attendance percentage report");
        System.out.println("7. Low attendance warnings (below 75%)");
        System.out.println("8. Search a student (by name or roll number)");
        System.out.println("9. Edit a student (name or roll number)");
        System.out.println("10. Delete a student");
        System.out.println("11. Delete one attendance record");
        System.out.println("12. Exit");
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
    // Day 5: keeps asking until a usable date is given. A wrong format
    // is explained and re-asked, and a future date is refused — you can
    // only mark attendance up to today.
    private LocalDate askDate(Scanner scanner) {
        while (true) {
            System.out.print("Enter date (yyyy-MM-dd) or press Enter for today: ");
            String input = scanner.nextLine().trim();
            LocalDate date;
            if (input.isEmpty()) {
                date = LocalDate.now();
            } else {
                try {
                    date = LocalDate.parse(input);
                } catch (DateTimeParseException e) {
                    System.out.println("That date is not in yyyy-MM-dd format, e.g. 2026-11-20. Try again.");
                    continue;
                }
            }
            if (date.isAfter(LocalDate.now())) {
                System.out.println("That date is in the future — attendance can only be marked up to today. Try again.");
                continue;
            }
            return date;
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
        // Day 5: one roll number belongs to one student — a duplicate
        // would make two students impossible to tell apart.
        for (Student existing : students) {
            if (existing.getRollNo().equalsIgnoreCase(rollNo)) {
                System.out.println("Roll number " + rollNo + " already belongs to "
                        + existing.getName() + ".");
                return;
            }
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
        // Day 5: a wrong y/n answer is re-asked instead of cancelling
        // the marking, so one typo does not waste the whole step.
        boolean present;
        while (true) {
            System.out.print("Present on " + date + "? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                present = true;
                break;
            } else if (answer.equals("n") || answer.equals("no")) {
                present = false;
                break;
            }
            System.out.println("Please answer y or n.");
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
        // Day 5: warn immediately if this marking pushed the student
        // below 75% attendance.
        double percentage = attendancePercentage(student);
        if (percentage >= 0 && percentage < LOW_ATTENDANCE_LIMIT) {
            System.out.printf("Warning: %s's attendance is now %.1f%% — below 75%%.%n",
                    student.getName(), percentage);
        }
    }

    private void viewAttendanceForDate(Scanner scanner) {
        LocalDate date = askDate(scanner);
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

    // Day 4: one percentage per student — present days out of the days
    // actually recorded for that student. A student with no records yet
    // has no percentage, so the report says so instead of dividing by 0.
    // Sorted lowest first, so the students who need attention are on top.
    // Day 5: students below 75% are flagged right in the report.
    private void viewPercentageReport() {
        if (students.isEmpty()) {
            System.out.println("No students added yet.");
            return;
        }
        ArrayList<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingDouble(this::attendancePercentage));
        System.out.println("Attendance percentage report:");
        int totalPresent = 0;
        int totalRecorded = 0;
        for (Student student : sorted) {
            int presentCount = 0;
            int recordedCount = 0;
            for (AttendanceRecord record : records) {
                if (record.getStudentId() == student.getId()) {
                    recordedCount++;
                    if (record.isPresent()) {
                        presentCount++;
                    }
                }
            }
            if (recordedCount == 0) {
                System.out.println("  " + student.getName() + " — No attendance recorded yet");
            } else {
                double percentage = 100.0 * presentCount / recordedCount;
                String warning = percentage < LOW_ATTENDANCE_LIMIT
                        ? "  <-- LOW, below 75%!" : "";
                System.out.printf("  %s - %.1f%% (%d present out of %d days)%s%n",
                        student.getName(), percentage, presentCount, recordedCount, warning);
                totalPresent += presentCount;
                totalRecorded += recordedCount;
            }
        }
        if (totalRecorded > 0) {
            System.out.printf("  Class average: %.1f%%%n",
                    100.0 * totalPresent / totalRecorded);
        }
    }

    // Day 5: lists every student whose attendance is below 75% — the
    // usual minimum for exams — with their percentage and absent days,
    // so the teacher knows who needs attention. Students with no records
    // yet are skipped, since there is nothing to warn about.
    private void viewLowAttendanceWarnings() {
        if (students.isEmpty()) {
            System.out.println("No students added yet.");
            return;
        }
        System.out.println("Low attendance warnings (below "
                + (int) LOW_ATTENDANCE_LIMIT + "%):");
        boolean anyWarning = false;
        for (Student student : students) {
            double percentage = attendancePercentage(student);
            if (percentage < 0 || percentage >= LOW_ATTENDANCE_LIMIT) {
                continue;
            }
            int absentCount = 0;
            int recordedCount = 0;
            for (AttendanceRecord record : records) {
                if (record.getStudentId() == student.getId()) {
                    recordedCount++;
                    if (!record.isPresent()) {
                        absentCount++;
                    }
                }
            }
            System.out.printf("  WARNING: %s — %.1f%% (%d absent out of %d days)%n",
                    student.getName(), percentage, absentCount, recordedCount);
            anyWarning = true;
        }
        if (!anyWarning) {
            System.out.println("  No warnings — every student with recorded attendance is at or above 75%.");
        }
    }

    // Day 6: finds students whose name or roll number contains the
    // search text (case-insensitive), and shows each match with their
    // attendance percentage, so you can find a student without scrolling
    // through the full list.
    private void searchStudent(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("No students added yet.");
            return;
        }
        System.out.print("Enter name or roll number to search: ");
        String query = scanner.nextLine().trim().toLowerCase();
        if (query.isEmpty()) {
            System.out.println("Search text cannot be empty.");
            return;
        }
        boolean found = false;
        for (Student student : students) {
            if (student.getName().toLowerCase().contains(query)
                    || student.getRollNo().toLowerCase().contains(query)) {
                double percentage = attendancePercentage(student);
                String extra = percentage < 0
                        ? " — no attendance recorded yet"
                        : String.format(" — %.1f%% attendance", percentage);
                System.out.println("  " + student + extra);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No student matches \"" + query + "\".");
        }
    }

    // Day 6: corrects a student's name or roll number. Pressing Enter
    // keeps the current value. A new roll number is still checked
    // against every other student, so duplicates cannot sneak in.
    private void editStudent(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("Add a student first.");
            return;
        }
        listStudents();
        System.out.print("Enter student id to edit: ");
        int id = readInt(scanner);
        Student student = findById(id);
        if (student == null) {
            System.out.println("No student with id " + id + ".");
            return;
        }
        System.out.print("New name (Enter keeps \"" + student.getName() + "\"): ");
        String newName = scanner.nextLine().trim();
        System.out.print("New roll number (Enter keeps \"" + student.getRollNo() + "\"): ");
        String newRollNo = scanner.nextLine().trim();
        if (newName.isEmpty() && newRollNo.isEmpty()) {
            System.out.println("Nothing changed.");
            return;
        }
        if (!newRollNo.isEmpty()) {
            for (Student existing : students) {
                if (existing.getId() != id
                        && existing.getRollNo().equalsIgnoreCase(newRollNo)) {
                    System.out.println("Roll number " + newRollNo + " already belongs to "
                            + existing.getName() + ".");
                    return;
                }
            }
        }
        if (!newName.isEmpty()) {
            student.setName(newName);
        }
        if (!newRollNo.isEmpty()) {
            student.setRollNo(newRollNo);
        }
        System.out.println("Updated: " + student);
        saveData();
    }

    // Day 6: removes a student and every attendance record they have.
    // Asks for confirmation first, because there is no undo — the CSV
    // files are rewritten right away.
    private void deleteStudent(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("Add a student first.");
            return;
        }
        listStudents();
        System.out.print("Enter student id to delete: ");
        int id = readInt(scanner);
        Student student = findById(id);
        if (student == null) {
            System.out.println("No student with id " + id + ".");
            return;
        }
        int recordCount = 0;
        for (AttendanceRecord record : records) {
            if (record.getStudentId() == id) {
                recordCount++;
            }
        }
        System.out.print("Delete " + student.getName() + " and their "
                + recordCount + " attendance record(s)? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (!answer.equals("y") && !answer.equals("yes")) {
            System.out.println("Cancelled — nothing deleted.");
            return;
        }
        records.removeIf(record -> record.getStudentId() == id);
        students.remove(student);
        System.out.println("Deleted " + student.getName() + " and "
                + recordCount + " attendance record(s).");
        saveData();
    }

    // Day 6: removes one attendance record — for a day marked by
    // mistake. The student and their other records stay untouched.
    private void deleteAttendanceRecord(Scanner scanner) {
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
        System.out.println("Records for " + student.getName() + ":");
        for (int i = 0; i < history.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + history.get(i));
        }
        System.out.print("Enter record number to delete: ");
        int choice = readInt(scanner);
        if (choice < 1 || choice > history.size()) {
            System.out.println("Invalid record number. Nothing deleted.");
            return;
        }
        AttendanceRecord toDelete = history.get(choice - 1);
        System.out.print("Delete " + toDelete + " for " + student.getName() + "? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (!answer.equals("y") && !answer.equals("yes")) {
            System.out.println("Cancelled — nothing deleted.");
            return;
        }
        records.remove(toDelete);
        System.out.println("Deleted record: " + toDelete + " for " + student.getName() + ".");
        saveData();
    }

    // Percentage for sorting the report; -1 means "no records", so those
    // students sort above the 0% ones instead of being treated as 0%.
    private double attendancePercentage(Student student) {
        int presentCount = 0;
        int recordedCount = 0;
        for (AttendanceRecord record : records) {
            if (record.getStudentId() == student.getId()) {
                recordedCount++;
                if (record.isPresent()) {
                    presentCount++;
                }
            }
        }
        if (recordedCount == 0) {
            return -1;
        }
        return 100.0 * presentCount / recordedCount;
    }
}
