# Student Attendance Tracker

A console-based Java app for tracking daily attendance of a class — add students, mark them present or absent each day, and see who is falling behind on attendance.

**Status: Week 2 project, complete (Day 7 of 7 done).**

## Features
- Add a student with a name and roll number
- List all students
- Mark attendance (present / absent) for any date — blank means today
- View attendance for any date, with unmarked students shown as "Not marked"
- View one student's full attendance history, date-sorted with a present-days total
- Re-marking a student on the same date updates the record instead of duplicating it
- Students and attendance are saved to `students.csv` and `attendance.csv` and loaded again on the next run, so nothing is lost when the app exits
- Attendance percentage report per student — present days out of recorded days, sorted lowest first (students with no records yet come last), plus the class average
- Low-attendance warning (below 75%): a dedicated warnings list, a LOW flag in the percentage report, and an immediate warning when marking a student absent pushes them below 75%
- Input validation: a wrong date format or a future date is explained and re-asked, a wrong y/n answer is re-asked, and a duplicate roll number is rejected
- Search a student by name or roll number (partial, case-insensitive), shown with their attendance percentage
- Edit a student's name or roll number (Enter keeps the current value; duplicate roll numbers still rejected)
- Delete a student together with all of their attendance records, after confirmation
- Delete one attendance record (for a marking made by mistake), after confirmation
- The menu header shows today's date plus the current student and record counts, so the state of the data is visible at a glance

## Build plan for the week
| Day | Work |
| --- | --- |
| 1 | Project setup, Student model, add / list / mark attendance ✅ |
| 2 | Attendance records by date, view attendance for any student ✅ |
| 3 | Save and load students and attendance from CSV files ✅ |
| 4 | Attendance percentage report per student ✅ |
| 5 | Low-attendance warning (below 75%) and input validation ✅ |
| 6 | Search a student, edit and delete records ✅ |
| 7 | Final polish, sample data, complete README ✅ |

## Concepts used
Classes and objects, ArrayList, loops, switch, Scanner input, dates (LocalDate), file I/O (BufferedReader, FileReader, FileWriter, PrintWriter), sorting with Comparator and lambdas, removing items with `removeIf`, and input validation (try/catch, NumberFormatException, DateTimeParseException, re-asking loops).

## How to run
```bash
javac Student.java AttendanceRecord.java AttendanceStorage.java AttendanceTracker.java
java AttendanceTracker
```

Attendance is saved to `students.csv` and `attendance.csv` next to the program every time you add, edit or delete a student or mark attendance, and loaded again when the app starts. To start with an empty class, delete both CSV files.

## Sample data
The repo ships with sample data for 5 students across 5 class days (19, 20, 21, 23 and 24 November 2026 — 25 records in all), so every view works straight away:

| Student | Roll No | Present | Percentage |
| --- | --- | --- | --- |
| Aarav Sharma | CSE-101 | 5 of 5 | 100.0% |
| Ishita Verma | CSE-102 | 4 of 5 | 80.0% |
| Rohan Gupta | CSE-103 | 3 of 5 | 60.0% (LOW) |
| Priya Singh | CSE-104 | 5 of 5 | 100.0% |
| Kabir Mehta | CSE-105 | 2 of 5 | 40.0% (LOW) |

Class average: 76.0%. Try option 6 (percentage report) and option 7 (low attendance warnings) to see Rohan and Kabir flagged. Adding your own student or marking attendance saves over the sample files.

## Example session
```text
===== Student Attendance Tracker =====
Today: 2026-11-26  |  Students: 5  |  Records: 25
...
Choose an option: 6
Attendance percentage report:
  Kabir Mehta - 40.0% (2 present out of 5 days)  <-- LOW, below 75%!
  Rohan Gupta - 60.0% (3 present out of 5 days)  <-- LOW, below 75%!
  Ishita Verma - 80.0% (4 present out of 5 days)
  Aarav Sharma - 100.0% (5 present out of 5 days)
  Priya Singh - 100.0% (5 present out of 5 days)
  Class average: 76.0%
```
