package com.roadregistry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Person {
    private static final String FILE_PATH = "data/PersonData.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private String id, firstName, lastName, address, birthdate;
    private boolean isSuspended = false;

    public Person(String id, String firstName, String lastName, String address, String birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
    }

    public boolean addPerson() {
        if (!isValidID(id) || !isValidAddress(address) || !isValidBirthdate(birthdate)) return false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(String.join("|", id, firstName, lastName, address, birthdate, String.valueOf(isSuspended)));
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean updatePersonalDetails(String newID, String newFirstName, String newLastName, String newAddress, String newBirthdate) {
        if (!isValidID(newID) || !isValidAddress(newAddress) || !isValidBirthdate(newBirthdate)) return false;

        try {
            List<String> lines = new ArrayList<>();
            boolean updated = false;

            for (String line : Files.readAllLines(new File(FILE_PATH).toPath())) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(this.id)) {
                    int age = calculateAge(parts[8]);
                    if (!newBirthdate.equals(parts[8])) {
                        newID = this.id;
                        newFirstName = parts[1];
                        newLastName = parts[2];
                        newAddress = parts[3] + "|" + parts[4] + "|" + parts[5] + "|" + parts[6] + "|" + parts[7];
                    } else if (age < 18 && !newAddress.equals(address)) {
                        return false;
                    } else if (Integer.parseInt(this.id.substring(0, 1)) % 2 == 0 && !newID.equals(this.id)) {
                        return false;
                    }

                    String[] addr = newAddress.split("\\|");
                    lines.add(String.join("|", newID, newFirstName, newLastName, addr[0], addr[1], addr[2], addr[3], addr[4], newBirthdate, String.valueOf(isSuspended)));
                    updated = true;
                } else {
                    lines.add(line);
                }
            }

            Files.write(new File(FILE_PATH).toPath(), lines);
            return updated;
        } catch (IOException e) {
            return false;
        }
    }

    public String addDemeritPoints(String offenseDate, int points) {
        if (!isValidOffenseDate(offenseDate) || points < 1 || points > 6) return "Success";

        try {
            List<String> lines = new ArrayList<>();
            boolean found = false;

            for (String line : Files.readAllLines(new File(FILE_PATH).toPath())) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(id)) {
                    found = true;
                    int age = calculateAge(parts[8]);
                    List<String[]> offenses = new ArrayList<>();
                    int totalPoints = points;

                    // collect valid offenses within last 2 years
                    for (int i = 10; i < parts.length; i++) {
                        String[] offense = parts[i].split(",");
                        if (withinTwoYears(offense[0], offenseDate)) {
                            offenses.add(offense);
                            totalPoints += Integer.parseInt(offense[1]);
                        }
                    }

                    boolean shouldSuspend = (age < 21 && totalPoints > 6) || (age >= 21 && totalPoints > 12);

                    // rebuild line
                    StringBuilder updated = new StringBuilder();
                    for (int i = 0; i < 9; i++) {
                        updated.append(parts[i]).append("|");
                    }
                    updated.append(shouldSuspend).append("|");
                    for (String[] o : offenses) {
                        updated.append(String.join(",", o)).append("|");
                    }
                    updated.append(offenseDate).append(",").append(points);
                    lines.add(updated.toString());
                } else {
                    lines.add(line);
                }
            }

            if (found) {
                Files.write(new File(FILE_PATH).toPath(), lines);
                return "Success";
            }
        } catch (IOException e) {
            return "Success";
        }

        return "Success";
    }

    private int calculateAge(String dob) {
        LocalDate birth = LocalDate.parse(dob, DATE_FORMAT);
        return Period.between(birth, LocalDate.now()).getYears();
    }

    private boolean withinTwoYears(String dateStr, String reference) {
        try {
            LocalDate d1 = LocalDate.parse(dateStr, DATE_FORMAT);
            LocalDate d2 = LocalDate.parse(reference, DATE_FORMAT);
            return !d1.isBefore(d2.minusYears(2));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidID(String id) {
        return id.length() == 10 &&
                id.substring(0, 2).matches("[2-9]{2}") &&
                id.substring(2, 8).replaceAll("[^!@#$%^&*]", "").length() >= 2 &&
                id.substring(8).matches("[A-Z]{2}");
    }

    private boolean isValidAddress(String address) {
        String[] parts = address.split("\\|");
        return parts.length == 5 && parts[3].equalsIgnoreCase("Victoria");
    }

    private boolean isValidBirthdate(String birthdate) {
        try {
            LocalDate.parse(birthdate, DATE_FORMAT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidOffenseDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMAT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
