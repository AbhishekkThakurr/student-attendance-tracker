# Student Attendance Tracker

A console-based Java app for tracking daily attendance of a class — add students, mark them present or absent each day, and see who is falling behind on attendance.

**Status: Week 2 project, Day 5 of 7 done.**

## Features
- Add a student with a name and roll number
- List all students
- Mark attendance (present / absent) for any date — blank means today
- View attendance for any date, with unmarked students shown as "Not marked"
- View one student's full attendance history, date-sorted with a present-days total
- Re-marking a student on the same date updates the record instead of duplicating it
- Students and attendance are saved to `students.csv` and `attendance.csv` and loaded again on the next run, so nothing is lost when the app exits
- Attendance percentage report per student — present days out of recorded days, sorted lowest first, plus the class average
- Low-attendance warning (below 75%): a dedicated warnings list, a LOW flag in the percentage report, and an immediate warning when marking a student absent pushes them below 75%
- Input validation: a wrong date format or a future date is explained and re-asked, a wrong y/n answer is re-asked, and a duplicate roll number is rejected

## Build plan for the week
| Day | Work |
| --- | --- |
| 1 | Project setup, Student model, add / list / mark attendance ✅ |
| 2 | Attendance records by date, view attendance for any student ✅ |
| 3 | Save and load students and attendance from CSV files ✅ |
| 4 | Attendance percentage report per student ✅ |
| 5 | Low-attendance warning (below 75%) and input validation ✅ |
| 6 | Search a student, edit and delete records |
| 7 | Final polish, sample data, complete README |

## Concepts used
Classes and objects, ArrayList, HashMap, loops, switch, Scanner input, dates (LocalDate), file I/O (BufferedReader, FileWriter, PrintWriter), and input validation (try/catch, NumberFormatException, DateTimeParseException, re-asking loops).

## How to run
```bash
javac Student.java AttendanceRecord.java AttendanceTracker.java
java AttendanceTracker
```

Attendance is saved to `students.csv` and `attendance.csv` next to the program every time you add a student or mark attendance, and loaded again when the app starts. The repo ships with sample data for 3 students across 2 days, so you can try the views straight away.
