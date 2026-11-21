import java.time.LocalDate;

/**
 * One attendance entry: whether a student was present on a given date.
 * Day 2 adds this so attendance is no longer "today only" — every date
 * keeps its own set of records.
 */
public class AttendanceRecord {
    private int studentId;
    private LocalDate date;
    private boolean present;

    public AttendanceRecord(int studentId, LocalDate date, boolean present) {
        this.studentId = studentId;
        this.date = date;
        this.present = present;
    }

    public int getStudentId() {
        return studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    @Override
    public String toString() {
        return date + " — " + (present ? "Present" : "Absent");
    }
}
