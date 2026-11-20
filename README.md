# Student Attendance Tracker

A console-based Java app for tracking daily attendance of a class — add students, mark them present or absent each day, and see who is falling behind on attendance.

**Status: Week 2 project, Day 1 of 7 done.**

## Features
- Add a student with a name and roll number
- List all students
- Mark today's attendance (present / absent) per student
- View today's attendance

## Build plan for the week
| Day | Work |
| --- | --- |
| 1 | Project setup, Student model, add / list / mark attendance ✅ |
| 2 | Attendance records by date, view attendance for any student |
| 3 | Save and load students and attendance from CSV files |
| 4 | Attendance percentage report per student |
| 5 | Low-attendance warning (below 75%) and input validation |
| 6 | Search a student, edit and delete records |
| 7 | Final polish, sample data, complete README |

## Concepts used
Classes and objects, ArrayList, HashMap, loops, switch, Scanner input, dates (LocalDate), and input validation (try/catch, NumberFormatException, re-asking loops).

## How to run
```bash
javac Student.java AttendanceTracker.java
java AttendanceTracker
```

Attendance is kept in memory for now — it is lost when the app exits. Saving to a file arrives on Day 3.
