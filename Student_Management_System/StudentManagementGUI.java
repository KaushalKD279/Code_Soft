import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Student class
class Student implements Serializable {
    private String name;
    private int rollNumber;
    private String grade;

    public Student(String name, int rollNumber, String grade) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.grade = grade;
    }

    public String getName() { return name; }
    public int getRollNumber() { return rollNumber; }
    public String getGrade() { return grade; }

    public void setName(String name) { this.name = name; }
    public void setGrade(String grade) { this.grade = grade; }

    @Override
    public String toString() {
        return rollNumber + " - " + name + " (" + grade + ")";
    }
}

// Student Management System
class StudentManagementSystem {
    private List<Student> students = new ArrayList<>();

    public void addStudent(Student student) { students.add(student); }
    public boolean removeStudent(int roll) {
        return students.removeIf(s -> s.getRollNumber() == roll);
    }
    public Student searchStudent(int roll) {
        for (Student s : students) {
            if (s.getRollNumber() == roll) return s;
        }
        return null;
    }
    public List<Student> getAllStudents() { return students; }

    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(students);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            students = (List<Student>) ois.readObject();
        } catch (Exception e) {
            students = new ArrayList<>();
        }
    }
}

// GUI Class
public class StudentManagementGUI extends JFrame {
    private StudentManagementSystem sms = new StudentManagementSystem();
    private DefaultTableModel tableModel;
    private JTable table;

    public StudentManagementGUI() {
        sms.loadFromFile("students.dat");

        setTitle("Student Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Roll No", "Name", "Grade"}, 0);
        table = new JTable(tableModel);
        refreshTable();

        // Buttons
        JButton addBtn = new JButton("Add Student");
        JButton removeBtn = new JButton("Remove Student");
        JButton searchBtn = new JButton("Search Student");
        JButton editBtn = new JButton("Edit Student");
        JButton saveBtn = new JButton("Save & Exit");

        // Panel for buttons
        JPanel panel = new JPanel();
        panel.add(addBtn);
        panel.add(removeBtn);
        panel.add(searchBtn);
        panel.add(editBtn);
        panel.add(saveBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        // Button Actions
        addBtn.addActionListener(e -> addStudent());
        removeBtn.addActionListener(e -> removeStudent());
        searchBtn.addActionListener(e -> searchStudent());
        editBtn.addActionListener(e -> editStudent());
        saveBtn.addActionListener(e -> {
            sms.saveToFile("students.dat");
            JOptionPane.showMessageDialog(this, "Data saved! Exiting...");
            System.exit(0);
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student s : sms.getAllStudents()) {
            tableModel.addRow(new Object[]{s.getRollNumber(), s.getName(), s.getGrade()});
        }
    }

    private void addStudent() {
        JTextField rollField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField gradeField = new JTextField();

        Object[] message = {
                "Roll No:", rollField,
                "Name:", nameField,
                "Grade:", gradeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Student", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int roll = Integer.parseInt(rollField.getText());
                String name = nameField.getText().trim();
                String grade = gradeField.getText().trim();
                if (name.isEmpty() || grade.isEmpty()) throw new Exception("Fields cannot be empty");
                sms.addStudent(new Student(name, roll, grade));
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void removeStudent() {
        String input = JOptionPane.showInputDialog(this, "Enter Roll No to Remove:");
        if (input != null) {
            try {
                int roll = Integer.parseInt(input);
                if (sms.removeStudent(roll)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Student removed successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    private void searchStudent() {
        String input = JOptionPane.showInputDialog(this, "Enter Roll No to Search:");
        if (input != null) {
            try {
                int roll = Integer.parseInt(input);
                Student s = sms.searchStudent(roll);
                if (s != null) {
                    JOptionPane.showMessageDialog(this, "Found: " + s);
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    private void editStudent() {
        String input = JOptionPane.showInputDialog(this, "Enter Roll No to Edit:");
        if (input != null) {
            try {
                int roll = Integer.parseInt(input);
                Student s = sms.searchStudent(roll);
                if (s != null) {
                    JTextField nameField = new JTextField(s.getName());
                    JTextField gradeField = new JTextField(s.getGrade());

                    Object[] message = {
                            "Name:", nameField,
                            "Grade:", gradeField
                    };

                    int option = JOptionPane.showConfirmDialog(this, message, "Edit Student", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        s.setName(nameField.getText().trim());
                        s.setGrade(gradeField.getText().trim());
                        refreshTable();
                        JOptionPane.showMessageDialog(this, "Student updated successfully.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementGUI().setVisible(true));
    }
}
