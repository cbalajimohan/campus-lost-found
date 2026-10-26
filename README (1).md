# Campus Lost and Found Management System

A beginner-friendly Java Swing mini project inspired by the CS25C09 Java Programming Laboratory minor-project theme.

## Features
- Add Lost and Found item reports
- Edit or delete selected records
- Search item name, category, location, date, contact, status, and description
- Filter by Lost or Found
- Track status as Open or Claimed/Returned
- Validate required fields and date format
- Automatically save records to a CSV file
- Load saved records on startup
- Includes fictional demo records on first run

## Requirements
- JDK 8 or later
- Windows, macOS, or Linux
- No external libraries or database required

## Run on Windows
1. Install a JDK if Java is not installed.
2. Open the folder containing `CampusLostFoundSystem.java`.
3. Click the File Explorer address bar, type `cmd`, and press Enter.
4. Compile:
   `javac CampusLostFoundSystem.java`
5. Run:
   `java CampusLostFoundSystem`

Alternatively, open the folder in VS Code, install the Java Extension Pack, and run the `main` method.

## Data storage
The app creates `lost_found_records.csv` in the current working directory. Back up this file to keep records. Do not upload real contact details or sensitive item data to a public repository.

## Files
- `CampusLostFoundSystem.java` — complete application
- `PROJECT_REPORT_OUTLINE.md` — report structure and explanation

## Limitation
This is a desktop application. It does not connect students across devices, provide user accounts, or use a central database. A multi-user campus version would need a backend and shared database.
