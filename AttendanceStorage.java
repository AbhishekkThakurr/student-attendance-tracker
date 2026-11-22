import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Saves and loads the tracker data as plain CSV files, so students and
 * attendance survive closing the app. Day 3.
 *
 * Files (created next to the program):
 *   students.csv   — one line per student: id,name,rollNo
 *   attendance.csv — one line per record: studentId,date,present
 *
 * Names and roll numbers are stored as-is; a comma inside one is replaced
 * with a space on save, because commas separate the columns.
 * A missing file simply means "nothing saved yet" — it is not an error.
 * A malformed line is skipped with a warning instead of crashing the load.
 */
public class AttendanceStorage {

    public static final String STUDENTS_FILE = "students.csv";
    public static final String ATTENDANCE_FILE = "attendance.csv";

    // --- Students ---

    public static void saveStudents(ArrayList<Student> students) {
        try (PrintWriter out = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
            out.println("id,name,rollNo");
            for (Student student : students) {
                out.println(student.getId() + ","
                        + safe(student.getName()) + ","
                        + safe(student.getRollNo()));
            }
        } catch (IOException e) {
            System.out.println("Could not save students: " + e.getMessage());
        }
    }

    public static ArrayList<Student> loadStudents() {
        ArrayList<Student> students = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line = in.readLine(); // header line
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 3) {
                    System.out.println("Skipping bad student line: " + line);
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    students.add(new Student(id, parts[1].trim(), parts[2].trim()));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping bad student line: " + line);
                }
            }
        } catch (IOException e) {
            // No saved file yet — start with an empty class.
        }
        return students;
    }

    // --- Attendance records ---

    public static void saveRecords(ArrayList<AttendanceRecord> records) {
        try (PrintWriter out = new PrintWriter(new FileWriter(ATTENDANCE_FILE))) {
            out.println("studentId,date,present");
            for (AttendanceRecord record : records) {
                out.println(record.getStudentId() + ","
                        + record.getDate() + ","
                        + record.isPresent());
            }
        } catch (IOException e) {
            System.out.println("Could not save attendance: " + e.getMessage());
        }
    }

    public static ArrayList<AttendanceRecord> loadRecords() {
        ArrayList<AttendanceRecord> records = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line = in.readLine(); // header line
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 3) {
                    System.out.println("Skipping bad attendance line: " + line);
                    continue;
                }
                try {
                    int studentId = Integer.parseInt(parts[0].trim());
                    LocalDate date = LocalDate.parse(parts[1].trim());
                    boolean present = Boolean.parseBoolean(parts[2].trim());
                    records.add(new AttendanceRecord(studentId, date, present));
                } catch (NumberFormatException | DateTimeParseException e) {
                    System.out.println("Skipping bad attendance line: " + line);
                }
            }
        } catch (IOException e) {
            // No saved file yet — start with no records.
        }
        return records;
    }

    // Commas would break the CSV columns, so replace them with spaces.
    private static String safe(String value) {
        return value.replace(",", " ");
    }
}
