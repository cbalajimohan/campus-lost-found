# Project Report Outline
## Campus Lost and Found Management System

### Cover Page
- Project title
- Student name and register number
- Department and institution
- Course: CS25C09 – Java Programming Laboratory
- Faculty name and academic year

### 1. Abstract
The Campus Lost and Found Management System is a Java desktop application that helps campus users record and search for lost and found items. It supports adding, editing, deleting, searching, filtering, and tracking item status. Records are stored in a local CSV file so they remain available after the application closes.

### 2. Introduction
Explain the difficulty of reporting and finding misplaced belongings on a campus and how a central record list can help.

### 3. Problem Statement
Manual notices and informal messages can be missed or duplicated. A structured application makes item details searchable and records whether an item is still open or has been claimed/returned.

### 4. Objectives
- Record lost and found items.
- Store item name, category, location, date, contact and description.
- Search records and filter by record type.
- Update or remove incorrect records.
- Track open and claimed/returned status.
- Save and reload records from a local CSV file.

### 5. Requirements
Software: JDK 8+, Java Swing, text editor or IDE. No external library required.
Hardware: a computer capable of running a modern JDK.

### 6. Technologies
- Java: object-oriented logic and data handling.
- Swing: graphical user interface.
- JTable and DefaultTableModel: display item records.
- File I/O: persist data to CSV.
- Local file storage: save records on the same computer.

### 7. Modules
1. Item entry and validation
2. Record listing
3. Search and type filtering
4. Update and delete
5. Status tracking
6. CSV persistence

### 8. Algorithm / Workflow
1. Start the application.
2. Load records from `lost_found_records.csv`; create sample records if no file exists.
3. Display records in the table.
4. Accept user input to add, update, delete, search or filter.
5. Validate required fields and date format.
6. Save changed records to CSV.
7. Refresh the table.
8. Exit the application.

### 9. Testing Plan
| Test | Input/action | Expected result |
|---|---|---|
| Add valid record | Fill required fields and click Add Record | Record appears and is saved |
| Missing required field | Leave item name or location blank | Validation message appears |
| Invalid date | Enter a non-date value | Validation message appears |
| Search | Search for a known item or location | Matching rows appear |
| Filter | Select Lost or Found | Only matching record types appear |
| Update | Select a row, edit a field, click Update Selected | Record is updated |
| Delete | Select a row and confirm deletion | Record is removed |
| Persistence | Close and reopen app | Saved records reload |

### 10. Advantages
- Simple desktop interface
- No external dependencies
- Searchable records
- Data persists between application restarts
- Demonstrates Java Swing, collections, event handling and file I/O

### 11. Limitations
- Records are stored on one computer only.
- No user login or role permissions.
- No central database or real-time updates.
- Contact details in demo records are fictional.
- CSV file should not be committed publicly if it contains personal data.

### 12. Future Enhancements
- Connect a MySQL database using JDBC.
- Add login and administrator roles.
- Add item photos.
- Add email/SMS notifications.
- Add a web interface and central API.
- Add date-range filters and export tools.

### 13. Conclusion
The project demonstrates how Java Swing and file handling can create a basic campus lost-and-found application. It provides a foundation for learning GUI programming, event handling, object-oriented design and persistent storage.

### References
- Java Documentation: https://docs.oracle.com/en/java/
- Java Swing Tutorial: https://docs.oracle.com/javase/tutorial/uiswing/
