package com.roadregistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;

public class PersonTest {

    @BeforeEach
    public void clearPersonDataFile() {
        try {
            PrintWriter writer = new PrintWriter("data/PersonData.txt");
            writer.print(""); // Clear file content before each test
            writer.close();
        } catch (Exception e) {
            System.out.println("Failed to clear file: " + e.getMessage());
        }
    }

    // ======== Test Cases for addPerson() ========

    @Test
    public void testAddValidPerson() {
        Person person = new Person("26ab@#CDXY", "John", "Doe", "10|Main St|Melbourne|Victoria|Australia", "15-08-2000");
        assertTrue(person.addPerson());
    }

    @Test
    public void testAddPerson_InvalidID() {
        Person person = new Person("12abc!!XYZ", "Test", "User", "32|Highland Street|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(person.addPerson()); // ID doesn't start with 2–9
    }

    @Test
    public void testAddPerson_InsufficientSpecialChars() {
        Person person = new Person("34abcdEFGH", "Test", "User", "32|Highland Street|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(person.addPerson()); // Not enough special characters in middle
    }

    @Test
    public void testAddPerson_WrongStateInAddress() {
        Person person = new Person("34@#bcEFGH", "Test", "User", "32|Highland Street|Melbourne|NSW|Australia", "01-01-2000");
        assertFalse(person.addPerson()); // State must be Victoria
    }

    @Test
    public void testAddPerson_InvalidBirthdateFormat() {
        Person person = new Person("34@#bcEFGH", "Test", "User", "32|Highland Street|Melbourne|Victoria|Australia", "2000-01-01");
        assertFalse(person.addPerson()); // Wrong birthdate format
    }

    // ======== Test Cases for updatePersonalDetails() ========

    @Test
    public void testUpdateValidDetails() {
        Person person = new Person("23ab!!GHXY", "Alice", "Smith", "23|Some St|Melbourne|Victoria|Australia", "10-02-2003");
        person.addPerson();
        boolean updated = person.updatePersonalDetails("23ab!!GHXY", "Alice", "Smith", "23|Some St|Melbourne|Victoria|Australia", "10-02-2003");
        assertTrue(updated);
    }

    @Test
    public void testUpdateInvalidDetails() {
        Person person = new Person("24ab!!GHXY", "Bob", "Brown", "99|High St|Melbourne|Victoria|Australia", "01-01-2010");
        person.addPerson();
        boolean updated = person.updatePersonalDetails("24ab!!GHXY", "Bob", "Brown", "88|New St|Melbourne|Victoria|Australia", "01-01-2010");
        assertFalse(updated); // Update with invalid address for under-18
    }

    @Test
    public void testUpdateDetails_Under18CannotChangeAddress() {
        Person person = new Person("34@#bcEFGH", "Young", "User", "12|Main Rd|Melbourne|Victoria|Australia", "01-01-2010");
        person.addPerson();
        boolean result = person.updatePersonalDetails("34@#bcEFGH", "Young", "User", "99|New St|Melbourne|Victoria|Australia", "01-01-2010");
        assertFalse(result);
    }

    @Test
    public void testUpdateDetails_BirthdayChangeOnly() {
        Person person = new Person("45@#deEFGH", "Old", "User", "23|Oak St|Melbourne|Victoria|Australia", "01-01-1990");
        person.addPerson();
        boolean result = person.updatePersonalDetails("45@#deEFGH", "Old", "User", "23|Oak St|Melbourne|Victoria|Australia", "01-01-1991");
        assertTrue(result);
    }

    @Test
    public void testUpdateDetails_EvenIDCannotBeChanged() {
        Person person = new Person("28!!aaEFGH", "Static", "ID", "10|Grove St|Melbourne|Victoria|Australia", "01-01-2000");
        person.addPerson();
        boolean result = person.updatePersonalDetails("88!!aaEFGH", "Static", "ID", "10|Grove St|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(result);
    }

    // ======== Test Cases for addDemeritPoints() ========

    @Test
    public void testAddDemeritPointsValid() {
        Person person = new Person("25ab!!GHXY", "Charlie", "Davis", "100|Station St|Melbourne|Victoria|Australia", "10-05-1990");
        person.addPerson();
        String result = person.addDemeritPoints("01-01-2024", 4);
        assertEquals("Success", result);
    }

    @Test
    public void testAddDemeritPoints_OverLimitUnder21() {
        Person person = new Person("60@@xxEFGH", "Tim", "Junior", "50|River St|Melbourne|Victoria|Australia", "01-01-2005");
        person.addPerson();
        person.addDemeritPoints("01-01-2024", 4);
        String result = person.addDemeritPoints("01-02-2024", 4); // total = 8
        assertEquals("Success", result);
    }

    @Test
    public void testAddDemeritPoints_OverLimitOver21() {
        Person person = new Person("61@@xxEFGH", "Adult", "Driver", "12|Main St|Melbourne|Victoria|Australia", "01-01-1990");
        person.addPerson();
        person.addDemeritPoints("01-01-2024", 6);
        String result = person.addDemeritPoints("01-02-2024", 7); // total = 13
        assertEquals("Success", result);
    }

    @Test
    public void testAddDemeritPoints_InvalidDateFormat() {
        Person person = new Person("62@@xxEFGH", "Invalid", "Date", "11|Ash St|Melbourne|Victoria|Australia", "01-01-1990");
        person.addPerson();
        String result = person.addDemeritPoints("2024-01-01", 3);
        assertEquals("Success", result);
    }
}






// test