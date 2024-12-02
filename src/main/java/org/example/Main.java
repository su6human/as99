package org.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static class Employee {
        private int id;
        private String name;
        private String position;
        private double salary;
        private String hireDate;

        public Employee(int id, String name, String position, double salary, String hireDate) {
            this.id = id;
            this.name = name;
            this.position = position;
            this.salary = salary;
            this.hireDate = hireDate;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getPosition() { return position; }
        public double getSalary() { return salary; }
        public String getHireDate() { return hireDate; }
    }

    static class EmployeeData {
        private static final String DB_URL = "jdbc:sqlite:employees.db";

        public void createTable() {
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS Employees (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        position TEXT NOT NULL,
                        salary REAL NOT NULL,
                        hire_date TEXT NOT NULL  
                    );
                """;
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void addEmployee(Employee employee) {
            String sql = "INSERT INTO Employees (name, position, salary, hire_date) VALUES (?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, employee.getName());
                pstmt.setString(2, employee.getPosition());
                pstmt.setDouble(3, employee.getSalary());
                pstmt.setString(4, employee.getHireDate());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void deleteEmployee(int id) {
            String sql = "DELETE FROM Employees WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void updateEmployee(Employee employee) {
            String sql = "UPDATE Employees SET name = ?, position = ?, salary = ?, hire_date = ? WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, employee.getName());
                pstmt.setString(2, employee.getPosition());
                pstmt.setDouble(3, employee.getSalary());
                pstmt.setString(4, employee.getHireDate());
                pstmt.setInt(5, employee.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public List<Employee> getAllEmployees() {
            List<Employee> employees = new ArrayList<>();
            String sql = "SELECT * FROM Employees";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    employees.add(new Employee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("position"),
                            rs.getDouble("salary"),
                            rs.getString("hire_date")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return employees;
        }
    }

    public static void main(String[] args) {
        EmployeeData employeeData = new EmployeeData();
        employeeData.createTable();

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("Menu:");
            System.out.println("1. Create Employee");
            System.out.println("2. Delete Employee");
            System.out.println("3. Update Employee");
            System.out.println("4. Read Employees");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    System.out.println("Enter employee name: ");
                    String name = scanner.nextLine();
                    System.out.println("Enter employee position: ");
                    String position = scanner.nextLine();
                    System.out.println("Enter employee salary: ");
                    double salary = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter employee birth date (yyyy-MM-dd): ");
                    String hireDate = scanner.nextLine();

                    Employee newEmployee = new Employee(0, name, position, salary, hireDate);
                    employeeData.addEmployee(newEmployee);
                }
                case 2 -> {
                    System.out.println("Enter employee ID to delete: ");
                    int idToDelete = Integer.parseInt(scanner.nextLine());
                    employeeData.deleteEmployee(idToDelete);
                }
                case 3 -> {
                    System.out.println("Enter employee ID to update: ");
                    int idToUpdate = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter new name: ");
                    String newName = scanner.nextLine();
                    System.out.println("Enter new position: ");
                    String newPosition = scanner.nextLine();
                    System.out.println("Enter new salary: ");
                    double newSalary = Double.parseDouble(scanner.nextLine());
                    System.out.println("Enter new birth date (yyyy-MM-dd): ");
                    String newHireDate = scanner.nextLine();

                    Employee updatedEmployee = new Employee(idToUpdate, newName, newPosition, newSalary, newHireDate);
                    employeeData.updateEmployee(updatedEmployee);
                }
                case 4 -> {
                    List<Employee> employees = employeeData.getAllEmployees();
                    if (employees.isEmpty()) {
                        System.out.println("No employees found.");
                    } else {
                        System.out.println("All employees:");
                        for (Employee emp : employees) {
                            System.out.println("ID: " + emp.getId() + ", Name: " + emp.getName() +
                                    ", Position: " + emp.getPosition() + ", Salary: " + emp.getSalary() +
                                    ", Birth Date: " + emp.getHireDate());
                        }
                    }
                }
                case 5 -> System.out.println("Exiting program...");
                default -> System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 5);
    }
}
