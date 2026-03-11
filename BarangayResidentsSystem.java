import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// ==================== COLOR CONSTANTS ====================
// Class containing all color constants used throughout the application
// This implements abstraction by centralizing color management
class BarangayColors {
    // Primary brand colors - getter methods implicitly through public static final fields
    public static final Color PRIMARY_BLUE = new Color(47, 93, 138);      // Main theme color
    public static final Color ACCENT_ORANGE = new Color(230, 126, 34);    // Accent color for highlights
    public static final Color LIGHT_BACKGROUND = new Color(245, 246, 247); // Background color for panels
    public static final Color SIDEBAR_GRAY = new Color(240, 242, 245);    // Sidebar background
    public static final Color TEXT_COLOR = new Color(46, 46, 46);         // Main text color
    public static final Color HEADER_BLUE = new Color(41, 82, 122);       // Header background
    public static final Color TABLE_HEADER = new Color(240, 240, 245);    // Table header background
    public static final Color BORDER_COLOR = new Color(210, 210, 210);    // Border color for components
    public static final Color BUTTON_BLACK = new Color(50, 50, 50);       // Default button color
    public static final Color BUTTON_HOVER = new Color(80, 80, 80);       // Button hover state
    public static final Color BUTTON_ACTIVE = new Color(100, 100, 100);   // Button active/pressed state
    public static final Color DECEASED_COLOR = new Color(220, 220, 220);  // Color for deceased records
    public static final Color ARCHIVE_COLOR = new Color(255, 240, 240);   // Color for archived records
    public static final Color LOGOUT_RED = new Color(220, 53, 69);        // Logout button color
    public static final Color LOGOUT_HOVER = new Color(200, 35, 51);      // Logout button hover state
}

// ==================== DATE UTILITIES ====================
// Utility class for date formatting and parsing operations
// Implements abstraction by hiding date conversion complexities
class DateUtils {
    // Formatter for display purposes (MM-DD-YYYY) - getter through public access
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    // Formatter for display with time - getter through public access
    private static final DateTimeFormatter DISPLAY_FORMAT_TIME = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
    // Formatter for storage (ISO format) - getter through public access
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    
    // Method to format LocalDate for display
    public static String formatDisplay(LocalDate date) {
        return date != null ? date.format(DISPLAY_FORMAT) : "";
    }
    
    // Method to format LocalDateTime for display
    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMAT_TIME) : "";
    }
    
    // Method to parse date string from display format
    // Includes error handling and fallback to storage format
    public static LocalDate parseDisplay(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr, DISPLAY_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr, STORAGE_FORMAT);
            } catch (DateTimeParseException ex) {
                throw new DateTimeParseException("Invalid date format. Please use MM-DD-YYYY", dateStr, 0);
            }
        }
    }
    
    // Method to format LocalDate for storage
    public static String formatStorage(LocalDate date) {
        return date != null ? date.format(STORAGE_FORMAT) : "";
    }
}

// ==================== ROUNDED BORDER CLASS ====================
// Custom border class for rounded corners on components
// Implements polymorphism by extending AbstractBorder
class RoundedBorder extends AbstractBorder {
    // Encapsulation: private fields with implicit getters/setters through constructor
    private int radius;        // Border radius size
    private Color borderColor; // Border color
    
    // Constructor with default border color - setter method
    public RoundedBorder(int radius) {
        this.radius = radius;
        this.borderColor = BarangayColors.BORDER_COLOR;
    }
    
    // Constructor with custom border color - setter method
    public RoundedBorder(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = borderColor;
    }
    
    // Override paintBorder method - polymorphism (runtime polymorphism)
    // Draws rounded rectangle border
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }
    
    // Override getBorderInsets method - polymorphism
    // Returns border insets based on radius
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius/2, radius, radius/2, radius);
    }
}

// ==================== STYLED COMPONENTS ====================
// Custom JButton with hover effects and styling
// Inheritance: extends JButton, polymorphism: overrides paintComponent
class StyledButton extends JButton {
    // Encapsulation: private fields for button states
    private Color normalColor;  // Normal state color
    private Color hoverColor;   // Hover state color
    private Color activeColor;  // Active/pressed state color
    private Color textColor;    // Text color
    
    // Constructor with default black styling - setter method
    public StyledButton(String text) {
        super(text);
        this.normalColor = BarangayColors.BUTTON_BLACK;
        this.hoverColor = BarangayColors.BUTTON_HOVER;
        this.activeColor = BarangayColors.BUTTON_ACTIVE;
        this.textColor = Color.WHITE;
        setupButton();
    }
    
    // Constructor with custom colors - setter method
    public StyledButton(String text, Color bgColor, Color fgColor) {
        super(text);
        this.normalColor = bgColor;
        this.hoverColor = bgColor.brighter();
        this.activeColor = bgColor.darker();
        this.textColor = fgColor;
        setupButton();
    }
    
    // Private setup method - encapsulates button configuration
    private void setupButton() {
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new RoundedBorder(5));
        setContentAreaFilled(false);
        setOpaque(true);
        setBackground(normalColor);
        setForeground(textColor);
        
        // MouseListener for hover effects - event handling
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(normalColor);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(activeColor);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }
        });
    }
    
    // Override paintComponent for custom rendering - polymorphism
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Set color based on button state
        if (getModel().isPressed()) {
            g2.setColor(activeColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(getBackground());
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.dispose();
        super.paintComponent(g);
    }
}

// Custom JTextField with placeholder support
// Inheritance: extends JTextField, polymorphism: overrides paintComponent
class StyledTextField extends JTextField {
    // Constructor
    public StyledTextField(int columns) {
        super(columns);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BarangayColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        setBackground(Color.WHITE);
    }
    
    // Override paintComponent to draw placeholder text - polymorphism
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw placeholder if field is empty and not focused
        if (getText().isEmpty() && !isFocusOwner()) {
            String placeholder = (String) getClientProperty("JTextField.placeholderText");
            if (placeholder != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int x = insets.left;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, x, y);
                g2.dispose();
            }
        }
    }
}

// Custom JPasswordField with placeholder support
// Inheritance: extends JPasswordField, polymorphism: overrides paintComponent
class StyledPasswordField extends JPasswordField {
    // Constructor
    public StyledPasswordField(int columns) {
        super(columns);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BarangayColors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        setBackground(Color.WHITE);
    }
    
    // Override paintComponent to draw placeholder - polymorphism
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw placeholder if password field is empty and not focused
        if (getPassword().length == 0 && !isFocusOwner()) {
            String placeholder = (String) getClientProperty("JTextField.placeholderText");
            if (placeholder != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int x = insets.left;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, x, y);
                g2.dispose();
            }
        }
    }
}

// Custom JComboBox with styling
// Inheritance: extends JComboBox
class StyledComboBox<T> extends JComboBox<T> {
    // Constructor
    public StyledComboBox(T[] items) {
        super(items);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setBackground(Color.WHITE);
        setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
        // Custom renderer for list items
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
                return c;
            }
        });
    }
}

// Custom JTable with styling and status-based coloring
// Inheritance: extends JTable, polymorphism: overrides constructor and uses custom renderer
class StyledTable extends JTable {
    // Constructor - sets up table styling and custom cell renderer
    public StyledTable(DefaultTableModel model) {
        super(model);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setRowHeight(28);
        setShowGrid(true);
        setGridColor(new Color(230, 230, 230));
        setSelectionBackground(new Color(47, 93, 138, 50));
        setSelectionForeground(BarangayColors.TEXT_COLOR);
        
        // Style table header
        getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        getTableHeader().setBackground(BarangayColors.TABLE_HEADER);
        getTableHeader().setForeground(BarangayColors.TEXT_COLOR);
        getTableHeader().setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
        getTableHeader().setReorderingAllowed(false);
        
        // Custom cell renderer for row coloring based on status
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Center align ID and age columns
                if (column == 0 || column == 2) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }
                
                // Apply status-based background color if not selected
                if (!isSelected) {
                    int modelRow = convertRowIndexToModel(row);
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    if (modelRow >= 0 && modelRow < model.getRowCount()) {
                        // Find status column index
                        int statusColumn = -1;
                        for (int i = 0; i < model.getColumnCount(); i++) {
                            if (model.getColumnName(i).equals("Status")) {
                                statusColumn = i;
                                break;
                            }
                        }
                        // Apply deceased color for DECEASED status
                        if (statusColumn >= 0) {
                            Object statusObj = model.getValueAt(modelRow, statusColumn);
                            if (statusObj != null && statusObj.toString().equals("DECEASED")) {
                                c.setBackground(BarangayColors.ARCHIVE_COLOR);
                            } else if (row % 2 == 0) {
                                c.setBackground(Color.WHITE);
                            } else {
                                c.setBackground(new Color(250, 250, 250));
                            }
                        }
                    } else if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(250, 250, 250));
                    }
                }
                
                ((JComponent)c).setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return c;
            }
        });
    }
}

// ==================== ARCHIVE RECORD CLASS ====================
// Class representing an archived resident record
// Implements Serializable for file storage
// Encapsulation: private fields with getter methods
class ArchiveRecord implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization version control
    private Resident resident;      // The archived resident - getter below
    private LocalDateTime archivedDate; // Date when archived - getter below
    private String finalStatus;     // Final status (DECEASED/TRANSFERRED) - getter below
    
    // Constructor - setter method
    public ArchiveRecord(Resident resident, String finalStatus) {
        this.resident = resident;
        this.archivedDate = LocalDateTime.now();
        this.finalStatus = finalStatus;
    }
    
    // Getter methods - encapsulation
    public Resident getResident() { return resident; }
    public LocalDateTime getArchivedDate() { return archivedDate; }
    public String getFinalStatus() { return finalStatus; }
}

// ==================== HOUSEHOLD MEMBER CLASS (UPDATED) ====================
// Class representing a household member (non-head resident)
// Implements Serializable for file storage
// Encapsulation: private fields with getter methods
class HouseholdMember implements Serializable {
    private static final long serialVersionUID = 2L; // For serialization version control
    
    // Encapsulation: private fields
    private String lastName;
    private String firstName;
    private String qualifier;      // Jr., Sr., III, etc.
    private int age;
    private String birthday;
    private String civilStatus;
    private String sex;
    private String relationship;    // Relationship to household head
    
    // Constructor - setter method
    public HouseholdMember(String lastName, String firstName, String qualifier, int age, 
                          String birthday, String civilStatus, String sex, String relationship) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.qualifier = qualifier;
        this.age = age;
        this.birthday = birthday;
        this.civilStatus = civilStatus;
        this.sex = sex;
        this.relationship = relationship;
    }
    
    // Getter methods - encapsulation
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getQualifier() { return qualifier; }
    public int getAge() { return age; }
    public String getBirthday() { return birthday; }
    public String getCivilStatus() { return civilStatus; }
    public String getSex() { return sex; }
    public String getRelationship() { return relationship; }
    
    // Method to get full name with qualifier
    public String getFullName() {
        String name = firstName + " " + lastName;
        if (qualifier != null && !qualifier.trim().isEmpty()) {
            name += " " + qualifier;
        }
        return name;
    }
}

// ==================== RESIDENT CLASS ====================
// Main resident entity class
// Implements Serializable for file storage
// Encapsulation: private fields with getter/setter methods
class Resident implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization version control
    
    // Enum for resident status - encapsulation
    public enum ResidentStatus {
        ACTIVE, DECEASED, TRANSFERRED
    }
    
    // Encapsulation: private fields
    private int residentID;
    private String firstName;
    private String mInitial;           // Middle initial
    private String lastName;
    private String qualifier;           // Jr., Sr., III, etc.
    private int age;
    private String birthday;
    private String sex;
    private String medicalCondition;
    private int incomeBracket;
    private String motherTongue;
    private String religion;
    private String employment;
    private String maritalStatus;
    private String address;
    private String position;            // Position in household
    private String contactNumber;
    private String occupation;
    private int householdHeadID;        // ID of household head (0 if self is head)
    private boolean isHouseholdHead;
    private ResidentStatus status;
    private LocalDateTime createdAt;    // Timestamp when record created
    private LocalDateTime updatedAt;    // Timestamp when last updated
    private LocalDateTime deceasedAt;   // Timestamp when marked deceased
    private List<HouseholdMember> householdMembers; // List of other household members
    
    // Constructor for household head
    public Resident(int residentID, String firstName, String mInitial, String lastName, String qualifier,
                   int age, String birthday, String sex, String medicalCondition,
                   int incomeBracket, String motherTongue, String religion,
                   String employment, String maritalStatus, String address,
                   String position, String contactNumber, String occupation) {
        this.residentID = residentID;
        this.firstName = firstName;
        this.mInitial = mInitial;
        this.lastName = lastName;
        this.qualifier = qualifier;
        this.age = age;
        this.birthday = birthday;
        this.sex = sex;
        this.medicalCondition = medicalCondition;
        this.incomeBracket = incomeBracket;
        this.motherTongue = motherTongue;
        this.religion = religion;
        this.employment = employment;
        this.maritalStatus = maritalStatus;
        this.address = address;
        this.position = position;
        this.contactNumber = contactNumber;
        this.occupation = occupation;
        this.householdHeadID = 0;
        this.isHouseholdHead = true;
        this.status = ResidentStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.householdMembers = new ArrayList<>();
    }
    
    // Constructor for household member
    public Resident(int residentID, String firstName, String mInitial, String lastName, String qualifier,
                   int age, String birthday, String sex, String medicalCondition,
                   int incomeBracket, String motherTongue, String religion,
                   String employment, String maritalStatus, String address,
                   String position, String contactNumber, String occupation, int householdHeadID) {
        this(residentID, firstName, mInitial, lastName, qualifier, age, birthday, sex, medicalCondition,
             incomeBracket, motherTongue, religion, employment, maritalStatus, address,
             position, contactNumber, occupation);
        this.householdHeadID = householdHeadID;
        this.isHouseholdHead = false;
    }
    
    // Getters and Setters - encapsulation
    public int getResidentID() { return residentID; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; this.updatedAt = LocalDateTime.now(); }
    public String getMInitial() { return mInitial; }
    public void setMInitial(String mInitial) { this.mInitial = mInitial; this.updatedAt = LocalDateTime.now(); }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; this.updatedAt = LocalDateTime.now(); }
    public String getQualifier() { return qualifier; }
    public void setQualifier(String qualifier) { this.qualifier = qualifier; this.updatedAt = LocalDateTime.now(); }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; this.updatedAt = LocalDateTime.now(); }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { 
        this.birthday = birthday; 
        this.updatedAt = LocalDateTime.now();
        calculateAgeFromBirthday(); // Auto-calculate age from birthday
    }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; this.updatedAt = LocalDateTime.now(); }
    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; this.updatedAt = LocalDateTime.now(); }
    public int getIncomeBracket() { return incomeBracket; }
    public void setIncomeBracket(int incomeBracket) { this.incomeBracket = incomeBracket; this.updatedAt = LocalDateTime.now(); }
    public String getMotherTongue() { return motherTongue; }
    public void setMotherTongue(String motherTongue) { this.motherTongue = motherTongue; this.updatedAt = LocalDateTime.now(); }
    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; this.updatedAt = LocalDateTime.now(); }
    public String getEmployment() { return employment; }
    public void setEmployment(String employment) { this.employment = employment; this.updatedAt = LocalDateTime.now(); }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; this.updatedAt = LocalDateTime.now(); }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; this.updatedAt = LocalDateTime.now(); }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; this.updatedAt = LocalDateTime.now(); }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; this.updatedAt = LocalDateTime.now(); }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; this.updatedAt = LocalDateTime.now(); }
    public int getHouseholdHeadID() { return householdHeadID; }
    public void setHouseholdHeadID(int householdHeadID) { this.householdHeadID = householdHeadID; this.updatedAt = LocalDateTime.now(); }
    public boolean isHouseholdHead() { return isHouseholdHead; }
    public void setHouseholdHead(boolean isHouseholdHead) { this.isHouseholdHead = isHouseholdHead; this.updatedAt = LocalDateTime.now(); }
    public ResidentStatus getStatus() { return status; }
    public void setStatus(ResidentStatus status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    public LocalDateTime getDeceasedAt() { return deceasedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<HouseholdMember> getHouseholdMembers() { return householdMembers; }
    public void setHouseholdMembers(List<HouseholdMember> members) { this.householdMembers = members; this.updatedAt = LocalDateTime.now(); }
    
    // Method to add household member
    public void addHouseholdMember(HouseholdMember member) {
        this.householdMembers.add(member);
        this.updatedAt = LocalDateTime.now();
    }
    
    // Private method to calculate age from birthday
    private void calculateAgeFromBirthday() {
        if (birthday != null && !birthday.isEmpty()) {
            try {
                LocalDate birthDate = DateUtils.parseDisplay(birthday);
                LocalDate currentDate = LocalDate.now();
                this.age = Period.between(birthDate, currentDate).getYears();
            } catch (DateTimeParseException e) {
                // If parsing fails, keep the existing age
            }
        }
    }
    
    // Method to get full name with middle initial and qualifier
    public String getFullName() {
        String name = firstName + " " + lastName;
        if (mInitial != null && !mInitial.isEmpty()) {
            name = firstName + " " + mInitial + ". " + lastName;
        }
        if (qualifier != null && !qualifier.trim().isEmpty()) {
            name += " " + qualifier;
        }
        return name;
    }
    
    // Method to get household size (head + members)
    public int getHouseholdSize() {
        return householdMembers.size() + 1;
    }
    
    // Method to check if resident is active
    public boolean isActive() {
        return status == ResidentStatus.ACTIVE;
    }
    
    // Method to get total population (head + members)
    public int getTotalPopulation() {
        return 1 + householdMembers.size();
    }
}

// ==================== USER CLASSES ====================
// Abstract base class for all user types
// Abstraction: defines abstract methods for permissions
// Inheritance: base class for Admin, ResidentUser, Staff
abstract class User implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization version control
    
    // Encapsulation: protected fields (accessible to subclasses)
    protected String username;
    protected String encryptedPassword;
    protected String role;
    protected String email;
    protected String phone;
    protected boolean isActive;
    
    // Constructor - setter method
    public User(String username, String password, String role, String email, String phone) {
        this.username = username;
        this.encryptedPassword = encryptPassword(password);
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.isActive = true;
    }
    
    // Abstract methods - abstraction (to be implemented by subclasses)
    public abstract boolean canAccessAdminPanel();
    public abstract boolean canManageUsers();
    public abstract boolean canViewAllResidents();
    
    // Method to encrypt password using SHA-256
    protected String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return password; // Fallback if encryption fails
        }
    }
    
    // Method to verify password against stored hash
    public boolean verifyPassword(String password) {
        return this.encryptedPassword.equals(encryptPassword(password));
    }
    
    // Getter methods - encapsulation
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}

// Admin user class
// Inheritance: extends User
class Admin extends User {
    private static final long serialVersionUID = 1L; // For serialization version control
    
    // Encapsulation: private fields
    private int otpAttempts;            // Number of OTP attempts
    private String currentOTP;          // Current OTP code
    private LocalDateTime otpExpiry;    // OTP expiry time
    private static final int MAX_OTP_ATTEMPTS = 3;      // Max OTP attempts
    private static final int OTP_VALID_MINUTES = 5;     // OTP validity duration
    private int loginCount;              // Number of logins
    
    // Constructor
    public Admin(String username, String password, String email, String phone) {
        super(username, password, "ADMIN", email, phone);
        this.otpAttempts = 0;
        this.loginCount = 0;
    }
    
    // Implement abstract methods - polymorphism
    @Override
    public boolean canAccessAdminPanel() { return true; }
    @Override
    public boolean canManageUsers() { return true; }
    @Override
    public boolean canViewAllResidents() { return true; }
    
    // Method to check if OTP is required (every 5 logins or after max attempts)
    public boolean requiresOTP() {
        return loginCount % 5 == 0 || otpAttempts >= MAX_OTP_ATTEMPTS;
    }
    
    // Method to generate new OTP
    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        currentOTP = String.valueOf(otp);
        otpExpiry = LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES);
        otpAttempts = 0;
        return currentOTP;
    }
    
    // Method to verify OTP
    public boolean verifyOTP(String otp) {
        if (currentOTP == null || otpExpiry == null) return false;
        if (LocalDateTime.now().isAfter(otpExpiry)) {
            currentOTP = null;
            return false;
        }
        if (currentOTP.equals(otp)) {
            loginCount++;
            currentOTP = null;
            otpAttempts = 0;
            return true;
        } else {
            otpAttempts++;
            if (otpAttempts >= MAX_OTP_ATTEMPTS) currentOTP = null;
            return false;
        }
    }
    
    // Method to increment login count
    public void incrementLoginCount() {
        loginCount++;
    }
}

// Resident user class (regular resident account)
// Inheritance: extends User
class ResidentUser extends User {
    private static final long serialVersionUID = 1L; // For serialization version control
    
    // Encapsulation: private field
    private int residentID; // Associated resident ID
    
    // Constructor
    public ResidentUser(String username, String password, String email, String phone, int residentID) {
        super(username, password, "RESIDENT", email, phone);
        this.residentID = residentID;
    }
    
    // Implement abstract methods - polymorphism
    @Override
    public boolean canAccessAdminPanel() { return false; }
    @Override
    public boolean canManageUsers() { return false; }
    @Override
    public boolean canViewAllResidents() { return false; }
    
    // Getter method
    public int getResidentID() { return residentID; }
}

// Staff user class
// Inheritance: extends User
class Staff extends User {
    private static final long serialVersionUID = 1L; // For serialization version control
    
    // Encapsulation: private field
    private int residentID; // Associated resident ID
    
    // Constructor
    public Staff(String username, String password, String email, String phone, int residentID) {
        super(username, password, "STAFF", email, phone);
        this.residentID = residentID;
    }
    
    // Implement abstract methods - polymorphism
    @Override
    public boolean canAccessAdminPanel() { return true; }
    @Override
    public boolean canManageUsers() { return false; }
    @Override
    public boolean canViewAllResidents() { return true; }
    
    // Getter method
    public int getResidentID() { return residentID; }
}

// ==================== DOCUMENT FILTERS ====================
// Document filter for phone number input validation
// Inheritance: extends PlainDocument
class PhoneDocument extends javax.swing.text.PlainDocument {
    // Override insertString to filter input - polymorphism
    @Override
    public void insertString(int offs, String str, javax.swing.text.AttributeSet a) 
            throws javax.swing.text.BadLocationException {
        if (str == null) return;
        
        // Filter only digits and plus sign
        StringBuilder filtered = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c) || c == '+') {
                filtered.append(c);
            }
        }
        
        // Validate phone number format (09... or +63...)
        String currentText = getText(0, getLength());
        String newText = currentText.substring(0, offs) + filtered.toString() + currentText.substring(offs);
        String validationText = newText.replaceAll("[^\\d+]", "");
        
        // Allow if empty or matches Philippine phone formats
        if (validationText.isEmpty()) {
            super.insertString(offs, filtered.toString(), a);
        } else if (validationText.startsWith("09") && validationText.length() <= 11) {
            super.insertString(offs, filtered.toString(), a);
        } else if (validationText.startsWith("+63") && validationText.length() <= 13) {
            super.insertString(offs, filtered.toString(), a);
        } else if (validationText.length() <= 1) {
            super.insertString(offs, filtered.toString(), a);
        }
    }
}

// ==================== FILE HANDLER (UPDATED) ====================
// Secure file handling with encryption
// File types: residents_secure.dat (master file for residents), users_secure.dat (master file for users),
// system_logs.dat (transaction file for logs), archive_records.dat (master file for archives),
// id_counter.dat (transaction file for ID counter)
class SecureFileHandler {
    // File names - constants for file paths
    private static final String RESIDENTS_FILE = "residents_secure.dat";  // Master file for residents
    private static final String USERS_FILE = "users_secure.dat";          // Master file for users
    private static final String LOGS_FILE = "system_logs.dat";            // Transaction file for logs
    private static final String ARCHIVE_FILE = "archive_records.dat";     // Master file for archives
    private static final String COUNTER_FILE = "id_counter.dat";          // Transaction file for ID counter
    private static final String ENCRYPTION_KEY = "BARANGAY_SECURE_2024";  // Encryption key
    
    private static int nextResidentID = 1; // Counter for next resident ID
    
    // Static initializer - loads counter when class is loaded
    static {
        loadCounter();
    }
    
    // XOR encryption method - simple encryption for data security
    private static String encryptData(String data) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            encrypted.append((char) (data.charAt(i) ^ ENCRYPTION_KEY.charAt(i % ENCRYPTION_KEY.length())));
        }
        return encrypted.toString();
    }
    
    // Decryption method (XOR is symmetric)
    private static String decryptData(String data) {
        return encryptData(data); // XOR encryption is its own inverse
    }
    
    // Getter for next resident ID formatted (6 digits)
    public static String getNextResidentIdFormatted() {
        return String.format("%06d", nextResidentID);
    }
    
    // Getter for next resident ID as integer
    public static int getNextResidentId() {
        return nextResidentID;
    }
    
    // Setter to increment resident ID
    public static void incrementResidentId() {
        nextResidentID++;
        saveCounter(); // Save to file
    }
    
    // Private method to save counter to file
    private static void saveCounter() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COUNTER_FILE))) {
            oos.writeInt(nextResidentID);
        } catch (IOException e) {
            System.err.println("Error saving counter: " + e.getMessage());
        }
    }
    
    // Private method to load counter from file
    private static void loadCounter() {
        File file = new File(COUNTER_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COUNTER_FILE))) {
                nextResidentID = ois.readInt();
            } catch (IOException e) {
                System.err.println("Error loading counter: " + e.getMessage());
            }
        }
    }
    
    // Method to save residents to master file
    public static void saveResidents(List<Resident> residents) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESIDENTS_FILE))) {
            List<String> encryptedList = new ArrayList<>();
            for (Resident resident : residents) {
                if (resident.getStatus() == Resident.ResidentStatus.ACTIVE) { // Save only active residents
                    String data = serializeResident(resident);
                    encryptedList.add(encryptData(data));
                }
            }
            oos.writeObject(encryptedList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving residents: " + e.getMessage());
        }
    }
    
    // Method to load residents from master file
    @SuppressWarnings("unchecked")
    public static List<Resident> loadResidents() {
        List<Resident> residents = new ArrayList<>();
        File file = new File(RESIDENTS_FILE);
        if (!file.exists()) return residents;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RESIDENTS_FILE))) {
            List<String> encryptedList = (List<String>) ois.readObject();
            for (String encrypted : encryptedList) {
                String decrypted = decryptData(encrypted);
                Resident resident = deserializeResident(decrypted);
                if (resident != null && resident.getStatus() == Resident.ResidentStatus.ACTIVE) {
                    residents.add(resident);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading residents: " + e.getMessage());
        }
        return residents;
    }
    
    // Method to save archive records to master file
    public static void saveArchive(List<ArchiveRecord> archive) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVE_FILE))) {
            List<String> encryptedList = new ArrayList<>();
            for (ArchiveRecord record : archive) {
                String data = serializeArchiveRecord(record);
                encryptedList.add(encryptData(data));
            }
            oos.writeObject(encryptedList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving archive: " + e.getMessage());
        }
    }
    
    // Method to load archive records from master file
    @SuppressWarnings("unchecked")
    public static List<ArchiveRecord> loadArchive() {
        List<ArchiveRecord> archive = new ArrayList<>();
        File file = new File(ARCHIVE_FILE);
        if (!file.exists()) return archive;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVE_FILE))) {
            List<String> encryptedList = (List<String>) ois.readObject();
            for (String encrypted : encryptedList) {
                String decrypted = decryptData(encrypted);
                ArchiveRecord record = deserializeArchiveRecord(decrypted);
                if (record != null) {
                    archive.add(record);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading archive: " + e.getMessage());
        }
        return archive;
    }
    
    // Method to save users to master file
    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            List<String> encryptedList = new ArrayList<>();
            for (User user : users) {
                String data = serializeUser(user);
                encryptedList.add(encryptData(data));
            }
            oos.writeObject(encryptedList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving users: " + e.getMessage());
        }
    }
    
    // Method to load users from master file
    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            // Create default admin if no users file exists
            Admin defaultAdmin = new Admin("admin", "admin123", "admin@barangay.ph", "09123456789");
            users.add(defaultAdmin);
            saveUsers(users);
            return users;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            List<String> encryptedList = (List<String>) ois.readObject();
            for (String encrypted : encryptedList) {
                String decrypted = decryptData(encrypted);
                User user = deserializeUser(decrypted);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage());
        }
        return users;
    }
    
    // Method to log activity to transaction file
    public static void logActivity(String username, String action) {
        try (FileWriter fw = new FileWriter(LOGS_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logEntry = timestamp + "|" + username + "|" + action;
            String encrypted = encryptData(logEntry);
            bw.write(encrypted);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error logging activity: " + e.getMessage());
        }
    }
    
    // Method to get login history for a user from transaction file
    public static List<String> getLoginHistory(String username) {
        List<String> history = new ArrayList<>();
        File file = new File(LOGS_FILE);
        if (!file.exists()) return history;
        
        try (BufferedReader br = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String decrypted = decryptData(line);
                if (decrypted.contains(username)) {
                    history.add(decrypted);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading logs: " + e.getMessage());
        }
        return history;
    }
    
    // Method to get all login history from transaction file (for admin view)
    public static List<String> getAllLoginHistory() {
        List<String> allLogs = new ArrayList<>();
        File file = new File(LOGS_FILE);
        if (!file.exists()) return allLogs;
        
        try (BufferedReader br = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String decrypted = decryptData(line);
                allLogs.add(decrypted);
            }
        } catch (IOException e) {
            System.err.println("Error reading logs: " + e.getMessage());
        }
        return allLogs;
    }
    
    // Private method to serialize Resident object to string
    private static String serializeResident(Resident resident) {
        StringBuilder membersData = new StringBuilder();
        for (HouseholdMember member : resident.getHouseholdMembers()) {
            membersData.append(member.getLastName()).append(",");
            membersData.append(member.getFirstName()).append(",");
            membersData.append(member.getQualifier() != null ? member.getQualifier() : "").append(",");
            membersData.append(member.getAge()).append(",");
            membersData.append(member.getBirthday()).append(",");
            membersData.append(member.getCivilStatus()).append(",");
            membersData.append(member.getSex()).append(",");
            membersData.append(member.getRelationship()).append(";");
        }
        
        // Convert birthday to storage format
        String birthday = resident.getBirthday();
        if (birthday != null && !birthday.isEmpty()) {
            try {
                LocalDate date = DateUtils.parseDisplay(birthday);
                birthday = DateUtils.formatStorage(date);
            } catch (DateTimeParseException e) {
                // Keep as is if parsing fails
            }
        }
        
        // Pipe-delimited format for all fields
        return String.join("|",
            String.valueOf(resident.getResidentID()),
            resident.getFirstName(),
            resident.getMInitial() != null ? resident.getMInitial() : "",
            resident.getLastName(),
            resident.getQualifier() != null ? resident.getQualifier() : "",
            String.valueOf(resident.getAge()),
            birthday != null ? birthday : "",
            resident.getSex() != null ? resident.getSex() : "",
            resident.getMedicalCondition() != null ? resident.getMedicalCondition() : "",
            String.valueOf(resident.getIncomeBracket()),
            resident.getMotherTongue() != null ? resident.getMotherTongue() : "",
            resident.getReligion() != null ? resident.getReligion() : "",
            resident.getEmployment() != null ? resident.getEmployment() : "",
            resident.getMaritalStatus() != null ? resident.getMaritalStatus() : "",
            resident.getAddress() != null ? resident.getAddress() : "",
            resident.getPosition() != null ? resident.getPosition() : "",
            resident.getContactNumber() != null ? resident.getContactNumber() : "",
            resident.getOccupation() != null ? resident.getOccupation() : "",
            String.valueOf(resident.getHouseholdHeadID()),
            String.valueOf(resident.isHouseholdHead()),
            resident.getStatus().toString(),
            resident.getCreatedAt().toString(),
            resident.getUpdatedAt().toString(),
            resident.getDeceasedAt() != null ? resident.getDeceasedAt().toString() : "",
            membersData.toString()
        );
    }
    
    // Private method to deserialize Resident from string
    private static Resident deserializeResident(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length < 24) return null;
            
            Resident resident;
            int residentID = Integer.parseInt(parts[0]);
            String firstName = parts[1];
            String mInitial = parts[2].isEmpty() ? "" : parts[2];
            String lastName = parts[3];
            String qualifier = parts[4].isEmpty() ? "" : parts[4];
            int age = Integer.parseInt(parts[5]);
            String birthday = parts[6];
            String sex = parts[7];
            String medicalCondition = parts[8];
            int incomeBracket = Integer.parseInt(parts[9]);
            String motherTongue = parts[10];
            String religion = parts[11];
            String employment = parts[12];
            String maritalStatus = parts[13];
            String address = parts[14];
            String position = parts[15];
            String contactNumber = parts[16];
            String occupation = parts[17];
            int householdHeadID = Integer.parseInt(parts[18]);
            boolean isHouseholdHead = Boolean.parseBoolean(parts[19]);
            
            // Convert birthday to display format
            if (birthday != null && !birthday.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(birthday);
                    birthday = DateUtils.formatDisplay(date);
                } catch (DateTimeParseException e) {
                    // Keep as is if parsing fails
                }
            }
            
            // Create appropriate resident object based on household head status
            if (isHouseholdHead) {
                resident = new Resident(residentID, firstName, mInitial, lastName, qualifier,
                    age, birthday, sex, medicalCondition, incomeBracket, motherTongue, religion,
                    employment, maritalStatus, address, position, contactNumber, occupation);
            } else {
                resident = new Resident(residentID, firstName, mInitial, lastName, qualifier,
                    age, birthday, sex, medicalCondition, incomeBracket, motherTongue, religion,
                    employment, maritalStatus, address, position, contactNumber, occupation, householdHeadID);
            }
            
            // Set status
            resident.setStatus(Resident.ResidentStatus.valueOf(parts[20]));
            
            // Parse household members if present
            if (parts.length > 24) {
                String membersData = parts[24];
                if (!membersData.isEmpty()) {
                    String[] members = membersData.split(";");
                    for (String memberData : members) {
                        if (!memberData.isEmpty()) {
                            String[] memberParts = memberData.split(",");
                            if (memberParts.length >= 8) {
                                HouseholdMember member = new HouseholdMember(
                                    memberParts[0], memberParts[1], memberParts[2],
                                    Integer.parseInt(memberParts[3]), memberParts[4], 
                                    memberParts[5], memberParts[6], memberParts[7]
                                );
                                resident.addHouseholdMember(member);
                            }
                        }
                    }
                }
            }
            
            return resident;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Private method to serialize ArchiveRecord to string
    private static String serializeArchiveRecord(ArchiveRecord record) {
        return serializeResident(record.getResident()) + "|" + 
               record.getArchivedDate().toString() + "|" + 
               record.getFinalStatus();
    }
    
    // Private method to deserialize ArchiveRecord from string
    private static ArchiveRecord deserializeArchiveRecord(String data) {
        try {
            int lastPipe = data.lastIndexOf("|");
            int secondLastPipe = data.lastIndexOf("|", lastPipe - 1);
            
            String residentData = data.substring(0, secondLastPipe);
            String archivedDateStr = data.substring(secondLastPipe + 1, lastPipe);
            String finalStatus = data.substring(lastPipe + 1);
            
            Resident resident = deserializeResident(residentData);
            LocalDateTime archivedDate = LocalDateTime.parse(archivedDateStr);
            
            ArchiveRecord record = new ArchiveRecord(resident, finalStatus);
            // Use reflection to set archivedDate since it's private
            java.lang.reflect.Field field = ArchiveRecord.class.getDeclaredField("archivedDate");
            field.setAccessible(true);
            field.set(record, archivedDate);
            
            return record;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Private method to serialize User to string
    private static String serializeUser(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUsername()).append("|");
        sb.append(user.encryptedPassword).append("|");
        sb.append(user.getRole()).append("|");
        sb.append(user.getEmail()).append("|");
        sb.append(user.getPhone()).append("|");
        sb.append(user.isActive()).append("|");
        
        // Add subclass-specific data
        if (user instanceof Admin) {
            sb.append("ADMIN");
        } else if (user instanceof ResidentUser) {
            sb.append("RESIDENT|").append(((ResidentUser) user).getResidentID());
        } else if (user instanceof Staff) {
            sb.append("STAFF|").append(((Staff) user).getResidentID());
        }
        
        return sb.toString();
    }
    
    // Private method to deserialize User from string
    private static User deserializeUser(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length < 6) return null;
            
            String username = parts[0];
            String encryptedPassword = parts[1];
            String role = parts[2];
            String email = parts[3];
            String phone = parts[4];
            boolean active = Boolean.parseBoolean(parts[5]);
            
            User user;
            // Create appropriate user object based on role
            if ("ADMIN".equals(role)) {
                user = new Admin(username, "temp", email, phone);
                user.encryptedPassword = encryptedPassword;
            } else if ("STAFF".equals(role)) {
                int residentID = parts.length > 7 ? Integer.parseInt(parts[7]) : 0;
                user = new Staff(username, "temp", email, phone, residentID);
                user.encryptedPassword = encryptedPassword;
            } else if ("RESIDENT".equals(role)) {
                int residentID = parts.length > 7 ? Integer.parseInt(parts[7]) : 0;
                user = new ResidentUser(username, "temp", email, phone, residentID);
                user.encryptedPassword = encryptedPassword;
            } else {
                return null;
            }
            
            user.setActive(active);
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}

// ==================== MAIN APPLICATION ====================
// Main application class
// File types used: residents_secure.dat (master), users_secure.dat (master), 
// system_logs.dat (transaction), archive_records.dat (master), id_counter.dat (transaction)
public class BarangayResidentsSystem {
    // Encapsulation: private fields
    private JFrame frame;                           // Main application window
    private JPanel mainPanel;                        // Main container panel
    private CardLayout cardLayout;                   // Layout manager for switching panels
    private User currentUser;                         // Currently logged in user
    private List<Resident> residents;                 // List of active residents (from master file)
    private List<User> users;                          // List of system users (from master file)
    private List<ArchiveRecord> archive;               // List of archived records (from master file)
    
    private LoginPanel loginPanel;                     // Login screen panel
    private OTPScreen otpScreen;                       // OTP verification screen
    private MainDashboard dashboard;                   // Main dashboard after login
    
    // Constructor
    public BarangayResidentsSystem() {
        initializeData();   // Load data from files
        initializeUI();     // Setup user interface
    }
    
    // Method to initialize data from files
    private void initializeData() {
        residents = SecureFileHandler.loadResidents();   // Load from residents_secure.dat (master file)
        users = SecureFileHandler.loadUsers();           // Load from users_secure.dat (master file)
        archive = SecureFileHandler.loadArchive();       // Load from archive_records.dat (master file)
    }
    
    // Method to initialize user interface
    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame = new JFrame("Barangay Residents Record System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BarangayColors.LIGHT_BACKGROUND);
        
        loginPanel = new LoginPanel();
        otpScreen = new OTPScreen();
        
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(otpScreen, "OTP");
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    // Method to show OTP popup dialog
    private void showOTPPopup(String otp, Admin admin) {
        JDialog otpPopup = new JDialog(frame, "OTP Verification", true);
        otpPopup.setSize(450, 400);
        otpPopup.setLocationRelativeTo(frame);
        otpPopup.setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Two-Factor Authentication", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
        gbc.insets = new Insets(0, 10, 15, 10);
        mainPanel.add(titleLabel, gbc);
        
        // Instruction
        JLabel messageLabel = new JLabel("<html><center>Please enter the following OTP<br>to complete your login</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(Color.DARK_GRAY);
        gbc.insets = new Insets(0, 10, 20, 10);
        mainPanel.add(messageLabel, gbc);
        
        // OTP display panel
        JPanel otpPanel = new JPanel(new BorderLayout());
        otpPanel.setBackground(new Color(245, 247, 250));
        otpPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BarangayColors.PRIMARY_BLUE, 2, true),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        
        JLabel otpLabel = new JLabel(otp, SwingConstants.CENTER);
        otpLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        otpLabel.setForeground(BarangayColors.PRIMARY_BLUE);
        otpPanel.add(otpLabel, BorderLayout.CENTER);
        
        gbc.insets = new Insets(0, 10, 15, 10);
        mainPanel.add(otpPanel, gbc);
        
        // Timer display
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        timerPanel.setBackground(Color.WHITE);
        
        JLabel timerLabel = new JLabel("Valid for 5:00 minutes", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timerLabel.setForeground(new Color(220, 53, 69));
        
        timerPanel.add(timerLabel);
        
        gbc.insets = new Insets(0, 10, 20, 10);
        mainPanel.add(timerPanel, gbc);
        
        // OK button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setForeground(Color.BLACK);
        okButton.setBackground(BarangayColors.PRIMARY_BLUE);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 73, 118), 1, true),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Mouse hover effect
        okButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                okButton.setBackground(new Color(57, 113, 158));
            }
            public void mouseExited(MouseEvent evt) {
                okButton.setBackground(BarangayColors.PRIMARY_BLUE);
            }
        });
        
        okButton.addActionListener(e -> otpPopup.dispose());
        
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, gbc);
        
        // Timer for OTP expiry
        Timer timer = new Timer(1000, new ActionListener() {
            int timeLeft = 300; // 5 minutes in seconds
            
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                
                timerLabel.setText(String.format("Valid for %d:%02d minutes", minutes, seconds));
                
                if (timeLeft <= 60) {
                    timerLabel.setForeground(new Color(220, 53, 69));
                }
                
                if (timeLeft <= 0) {
                    ((Timer)e.getSource()).stop();
                    timerLabel.setText("OTP Expired");
                    timerLabel.setForeground(Color.GRAY);
                    otpLabel.setForeground(Color.GRAY);
                    otpLabel.setText("EXPIRED");
                    okButton.setEnabled(false);
                    okButton.setBackground(Color.LIGHT_GRAY);
                }
            }
        });
        timer.start();
        
        otpPopup.add(mainPanel);
        otpPopup.setVisible(true);
    }
    
    // Method to show main dashboard
    private void showDashboard() {
        dashboard = new MainDashboard(currentUser, residents, users, archive);
        mainPanel.add(dashboard, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
        frame.revalidate();
    }
    
    // ==================== LOGIN PANEL ====================
    // Inner class for login screen
    class LoginPanel extends JPanel {
        private StyledTextField usernameField;      // Username input field
        private StyledPasswordField passwordField;  // Password input field
        private StyledComboBox<String> roleComboBox; // Role selection combobox
        
        // Constructor
        public LoginPanel() {
            setLayout(new GridBagLayout());
            setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Main login card panel
            JPanel cardPanel = new JPanel(new GridBagLayout());
            cardPanel.setBackground(Color.WHITE);
            cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
            ));
            
            // Title panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(Color.WHITE);
            JLabel titleLabel = new JLabel("Barangay Records System");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel subtitleLabel = new JLabel("Local Government Information System");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitleLabel.setForeground(BarangayColors.TEXT_COLOR);
            subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            titlePanel.add(titleLabel, BorderLayout.NORTH);
            titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
            
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            gbc.insets = new Insets(0, 0, 30, 0);
            cardPanel.add(titlePanel, gbc);
            
            gbc.gridwidth = 1;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Username field
            gbc.gridy = 1; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
            JLabel userLabel = new JLabel("Username:");
            userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cardPanel.add(userLabel, gbc);
            
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            usernameField = new StyledTextField(20);
            usernameField.putClientProperty("JTextField.placeholderText", "Enter username");
            cardPanel.add(usernameField, gbc);
            
            // Password field
            gbc.gridy = 2; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
            JLabel passLabel = new JLabel("Password:");
            passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cardPanel.add(passLabel, gbc);
            
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            passwordField = new StyledPasswordField(20);
            passwordField.putClientProperty("JTextField.placeholderText", "Enter password");
            cardPanel.add(passwordField, gbc);
            
            // Role combobox
            gbc.gridy = 3; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
            JLabel roleLabel = new JLabel("Role:");
            roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cardPanel.add(roleLabel, gbc);
            
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            roleComboBox = new StyledComboBox<>(new String[]{"ADMIN", "STAFF", "RESIDENT"});
            cardPanel.add(roleComboBox, gbc);
            
            // Login button
            gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(20, 0, 10, 0);
            StyledButton loginButton = new StyledButton("Login", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            loginButton.setPreferredSize(new Dimension(200, 40));
            loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            loginButton.addActionListener(e -> attemptLogin()); // Login action
            cardPanel.add(loginButton, gbc);
            
            // Create account button
            gbc.gridy = 5;
            gbc.insets = new Insets(10, 0, 0, 0);
            StyledButton createAccountButton = new StyledButton("Create New Account", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            createAccountButton.setPreferredSize(new Dimension(200, 40));
            createAccountButton.addActionListener(e -> showCreateAccountDialog()); // Create account action
            cardPanel.add(createAccountButton, gbc);
            
            add(cardPanel);
        }
        
        // Method to attempt login
        private void attemptLogin() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            
            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", 
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check credentials against users list (from users_secure.dat)
            for (User user : users) {
                if (user.getUsername().equals(username) && 
                    user.getRole().equals(role) && 
                    user.isActive() &&
                    user.verifyPassword(password)) {
                    
                    currentUser = user;
                    SecureFileHandler.logActivity(username, "LOGIN_ATTEMPT_SUCCESS"); // Log to system_logs.dat
                    
                    // Check if OTP required for admin
                    if (user instanceof Admin && ((Admin) user).requiresOTP()) {
                        String otp = ((Admin) user).generateOTP();
                        showOTPPopup(otp, (Admin) user);
                        otpScreen.setAdmin((Admin) user);
                        cardLayout.show(mainPanel, "OTP");
                    } else {
                        if (user instanceof Admin) {
                            ((Admin) user).incrementLoginCount();
                        }
                        showDashboard();
                    }
                    return;
                }
            }
            
            // Login failed
            JOptionPane.showMessageDialog(this, 
                "Invalid credentials or inactive account!", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            SecureFileHandler.logActivity(username, "LOGIN_ATTEMPT_FAILED"); // Log to system_logs.dat
        }
        
        // Method to show create account dialog
        private void showCreateAccountDialog() {
            JDialog dialog = new JDialog(frame, "Create New Account", true);
            dialog.setSize(500, 600);
            dialog.setLocationRelativeTo(frame);
            dialog.getContentPane().setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel titleLabel = new JLabel("Create New Account");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            int row = 0;
            
            // Username field
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Username*:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            StyledTextField newUserField = new StyledTextField(15);
            newUserField.putClientProperty("JTextField.placeholderText", "Enter username");
            formPanel.add(newUserField, gbc);
            row++;
            
            // Password field
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Password*:"), gbc);
            gbc.gridx = 1;
            StyledPasswordField newPassField = new StyledPasswordField(15);
            newPassField.putClientProperty("JTextField.placeholderText", "6+ characters");
            formPanel.add(newPassField, gbc);
            row++;
            
            // Confirm password field
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Confirm Password*:"), gbc);
            gbc.gridx = 1;
            StyledPasswordField confirmPassField = new StyledPasswordField(15);
            confirmPassField.putClientProperty("JTextField.placeholderText", "Confirm password");
            formPanel.add(confirmPassField, gbc);
            row++;
            
            // Email field
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Email*:"), gbc);
            gbc.gridx = 1;
            StyledTextField emailField = new StyledTextField(15);
            emailField.putClientProperty("JTextField.placeholderText", "email@example.com");
            formPanel.add(emailField, gbc);
            row++;
            
            // Phone field
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Phone*:"), gbc);
            gbc.gridx = 1;
            StyledTextField phoneField = new StyledTextField(15);
            phoneField.putClientProperty("JTextField.placeholderText", "09xxxxxxxxx");
            phoneField.setDocument(new PhoneDocument()); // Apply phone number filter
            formPanel.add(phoneField, gbc);
            row++;
            
            // Role combobox
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Role*:"), gbc);
            gbc.gridx = 1;
            StyledComboBox<String> roleBox = new StyledComboBox<>(new String[]{"RESIDENT", "STAFF"});
            formPanel.add(roleBox, gbc);
            row++;
            
            // Resident ID field (must match existing resident)
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Resident ID*:"), gbc);
            gbc.gridx = 1;
            StyledTextField residentIdField = new StyledTextField(15);
            residentIdField.putClientProperty("JTextField.placeholderText", "Enter your Resident ID");
            formPanel.add(residentIdField, gbc);
            row++;
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(Color.WHITE);
            
            StyledButton createButton = new StyledButton("Create Account", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            createButton.setPreferredSize(new Dimension(140, 35));
            createButton.addActionListener(e -> {
                // Validate inputs
                String password = new String(newPassField.getPassword());
                String confirm = new String(confirmPassField.getPassword());
                String phone = phoneField.getText().trim();
                String residentIdText = residentIdField.getText().trim();
                
                // Check password match
                if (!password.equals(confirm)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords don't match!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check password length
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters long!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String username = newUserField.getText().trim();
                String role = (String) roleBox.getSelectedItem();
                String email = emailField.getText().trim();
                
                // Check required fields
                if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || residentIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields (marked with *)!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate phone number
                if (!phone.isEmpty() && !isValidPhoneNumber(phone)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Phone number must start with 09 or +63 and be 11-13 digits total!\n" +
                        "Examples: 09123456789 or +639123456789", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if username already exists
                for (User user : users) {
                    if (user.getUsername().equals(username)) {
                        JOptionPane.showMessageDialog(dialog, "Username already exists!", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                try {
                    int residentID = Integer.parseInt(residentIdText);
                    Resident matchingResident = null;
                    
                    // Check if resident ID exists in residents list (from residents_secure.dat)
                    for (Resident r : residents) {
                        if (r.getResidentID() == residentID) {
                            matchingResident = r;
                            break;
                        }
                    }
                    
                    if (matchingResident == null) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Resident ID not found in records! All users must be registered residents first.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Create appropriate user type
                    User newUser;
                    if ("STAFF".equals(role)) {
                        newUser = new Staff(username, password, email, phone, residentID);
                    } else {
                        newUser = new ResidentUser(username, password, email, phone, residentID);
                    }
                    
                    // Add user and save to users_secure.dat
                    users.add(newUser);
                    SecureFileHandler.saveUsers(users); // Save to master file
                    SecureFileHandler.logActivity(username, "ACCOUNT_CREATED"); // Log to transaction file
                    JOptionPane.showMessageDialog(dialog, "Account created successfully!\nYou can now login.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Resident ID must be a valid number!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            StyledButton cancelButton = new StyledButton("Cancel", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            cancelButton.setPreferredSize(new Dimension(100, 35));
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(createButton);
            buttonPanel.add(cancelButton);
            
            JScrollPane scrollPane = new JScrollPane(formPanel);
            scrollPane.setBorder(null);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
        }
        
        // Helper method to validate phone number format
        private boolean isValidPhoneNumber(String phone) {
            if (phone == null || phone.trim().isEmpty()) return false;
            phone = phone.trim().replaceAll("[\\s-]", "");
            if (phone.matches("^09\\d{9}$")) return true; // Philippine mobile format
            if (phone.matches("^\\+63\\d{10}$")) return true; // International format
            return false;
        }
    }
    
    // ==================== OTP SCREEN ====================
    // Inner class for OTP verification screen
    class OTPScreen extends JPanel {
        private StyledTextField otpField;          // OTP input field
        private JLabel instructionLabel;            // Instruction label
        private Admin currentAdmin;                  // Current admin for OTP verification
        private JLabel timerDisplayLabel;            // Timer display label
        private Timer otpTimer;                       // Timer for OTP expiry
        private int timeLeft = 300;                   // 5 minutes in seconds
        
        // Constructor
        public OTPScreen() {
            setLayout(new GridBagLayout());
            setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            JPanel otpPanel = new JPanel();
            otpPanel.setLayout(new BoxLayout(otpPanel, BoxLayout.Y_AXIS));
            otpPanel.setBackground(Color.WHITE);
            otpPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
            ));
            
            // Title
            JLabel titleLabel = new JLabel("Two-Factor Authentication");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Instruction
            instructionLabel = new JLabel("<html><div style='text-align: center;'>A pop-up window has appeared<br>with your OTP code.<br>Please enter it below.</div></html>");
            instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            instructionLabel.setForeground(BarangayColors.TEXT_COLOR);
            instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Timer display
            timerDisplayLabel = new JLabel("Time remaining: 5:00");
            timerDisplayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            timerDisplayLabel.setForeground(Color.RED);
            timerDisplayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // OTP input panel
            JPanel otpInputPanel = new JPanel();
            otpInputPanel.setLayout(new BoxLayout(otpInputPanel, BoxLayout.Y_AXIS));
            otpInputPanel.setBackground(Color.WHITE);
            otpInputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel otpInputLabel = new JLabel("Enter 6-digit OTP:");
            otpInputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            otpInputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            otpField = new StyledTextField(6);
            otpField.setFont(new Font("Segoe UI", Font.BOLD, 20));
            otpField.setHorizontalAlignment(JTextField.CENTER);
            otpField.setMaximumSize(new Dimension(200, 40));
            otpField.setAlignmentX(Component.CENTER_ALIGNMENT);
            otpField.setDocument(new javax.swing.text.PlainDocument() {
                @Override
                public void insertString(int offs, String str, javax.swing.text.AttributeSet a) 
                        throws javax.swing.text.BadLocationException {
                    if (str == null) return;
                    String filtered = str.replaceAll("[^\\d]", ""); // Allow only digits
                    if (getLength() + filtered.length() <= 6) {
                        super.insertString(offs, filtered, a);
                    }
                }
            });
            
            // Button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(Color.WHITE);
            
            StyledButton verifyButton = new StyledButton("Verify OTP", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            verifyButton.setPreferredSize(new Dimension(120, 35));
            verifyButton.addActionListener(e -> verifyOTP());
            
            StyledButton resendButton = new StyledButton("Resend OTP", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            resendButton.setPreferredSize(new Dimension(120, 35));
            resendButton.addActionListener(e -> resendOTP());
            
            StyledButton backButton = new StyledButton("Back to Login", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            backButton.setPreferredSize(new Dimension(150, 35));
            backButton.addActionListener(e -> {
                if (otpTimer != null) otpTimer.stop();
                cardLayout.show(mainPanel, "LOGIN");
            });
            
            buttonPanel.add(verifyButton);
            buttonPanel.add(resendButton);
            buttonPanel.add(backButton);
            
            // Add all components
            otpPanel.add(titleLabel);
            otpPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            otpPanel.add(instructionLabel);
            otpPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            otpPanel.add(timerDisplayLabel);
            otpPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            otpPanel.add(otpInputLabel);
            otpPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            otpPanel.add(otpField);
            otpPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            otpPanel.add(buttonPanel);
            
            add(otpPanel);
        }
        
        // Setter for admin
        public void setAdmin(Admin admin) {
            this.currentAdmin = admin;
            otpField.setText("");
            otpField.requestFocus();
            timeLeft = 300;
            timerDisplayLabel.setText("Time remaining: 5:00");
            
            if (otpTimer != null) otpTimer.stop();
            
            // Start timer
            otpTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    timeLeft--;
                    int minutes = timeLeft / 60;
                    int seconds = timeLeft % 60;
                    timerDisplayLabel.setText(String.format("Time remaining: %d:%02d", minutes, seconds));
                    
                    if (timeLeft <= 0) {
                        ((Timer)e.getSource()).stop();
                        timerDisplayLabel.setText("OTP Expired!");
                    }
                }
            });
            otpTimer.start();
        }
        
        // Method to resend OTP
        private void resendOTP() {
            if (currentAdmin != null) {
                String newOTP = currentAdmin.generateOTP();
                showOTPPopup(newOTP, currentAdmin);
                timeLeft = 300;
                timerDisplayLabel.setText("Time remaining: 5:00");
                otpField.setText("");
                JOptionPane.showMessageDialog(this, 
                    "A new OTP has been sent!\nCheck the pop-up window.", 
                    "OTP Resent", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        // Method to verify OTP
        private void verifyOTP() {
            String otp = otpField.getText().trim();
            if (otp.length() != 6) {
                JOptionPane.showMessageDialog(this, "OTP must be 6 digits!", 
                    "Invalid OTP", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (currentAdmin.verifyOTP(otp)) {
                if (otpTimer != null) otpTimer.stop();
                SecureFileHandler.logActivity(currentAdmin.getUsername(), "OTP_VERIFIED"); // Log to system_logs.dat
                currentAdmin.incrementLoginCount();
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid or expired OTP!", 
                    "Verification Failed", JOptionPane.ERROR_MESSAGE);
                otpField.setText("");
            }
        }
    }
    
    // ==================== MAIN DASHBOARD ====================
    // Inner class for main dashboard after login
    class MainDashboard extends JPanel {
        private User dashboardUser;                         // Current user
        private List<Resident> dashboardResidents;          // Residents list (from master file)
        private List<User> dashboardUsers;                  // Users list (from master file)
        private List<ArchiveRecord> dashboardArchive;       // Archive list (from master file)
        private CardLayout contentLayout;                   // Layout for content panels
        private JPanel contentPanel;                         // Container for content panels
        private Map<String, JButton> sidebarButtons;        // Sidebar buttons for navigation
        
        // Constructor
        public MainDashboard(User user, List<Resident> residents, List<User> users, List<ArchiveRecord> archive) {
            this.dashboardUser = user;
            this.dashboardResidents = residents;
            this.dashboardUsers = users;
            this.dashboardArchive = archive;
            this.sidebarButtons = new HashMap<>();
            
            setLayout(new BorderLayout());
            setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            // Create header and sidebar
            JPanel headerPanel = createHeader();
            JPanel sidebar = createSidebar();
            
            contentLayout = new CardLayout();
            contentPanel = new JPanel(contentLayout);
            contentPanel.setBackground(Color.WHITE);
            
            // Initialize content panels
            initializeContentPanels();
            
            add(headerPanel, BorderLayout.NORTH);
            add(sidebar, BorderLayout.WEST);
            add(contentPanel, BorderLayout.CENTER);
        }
        
        // Method to create header panel
        private JPanel createHeader() {
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(BarangayColors.HEADER_BLUE);
            headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            
            // Left side with logo and title
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);
            
            JLabel logoLabel = new JLabel(" ");
            logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            
            JPanel titlePanel = new JPanel(new GridLayout(2, 1));
            titlePanel.setOpaque(false);
            
            JLabel titleLabel = new JLabel("Barangay Records System");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);
            
            JLabel barangayLabel = new JLabel("Local Government Unit");
            barangayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            barangayLabel.setForeground(new Color(220, 220, 220));
            
            titlePanel.add(titleLabel);
            titlePanel.add(barangayLabel);
            
            leftPanel.add(logoLabel);
            leftPanel.add(Box.createHorizontalStrut(10));
            leftPanel.add(titlePanel);
            
            // Right side with user info
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setOpaque(false);
            
            JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
            userInfoPanel.setOpaque(false);
            
            JLabel userLabel = new JLabel(dashboardUser.getUsername());
            userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            userLabel.setForeground(Color.WHITE);
            
            JLabel roleLabel = new JLabel(dashboardUser.getRole());
            roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            roleLabel.setForeground(new Color(220, 220, 220));
            
            userInfoPanel.add(userLabel);
            userInfoPanel.add(roleLabel);
            
            rightPanel.add(userInfoPanel);
            
            headerPanel.add(leftPanel, BorderLayout.WEST);
            headerPanel.add(rightPanel, BorderLayout.EAST);
            
            return headerPanel;
        }
        
        // Method to create sidebar
        private JPanel createSidebar() {
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setPreferredSize(new Dimension(220, getHeight()));
            sidebar.setBackground(BarangayColors.SIDEBAR_GRAY);
            sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BarangayColors.BORDER_COLOR));
            
            sidebar.add(Box.createVerticalStrut(15));
            
            // Menu buttons
            createMenuButton(sidebar, "Manage Residents", "manage_residents", BarangayColors.PRIMARY_BLUE);
            createMenuButton(sidebar, "Archive Residents", "archive", new Color(108, 117, 125));
            createMenuButton(sidebar, "Reports", "reports", BarangayColors.PRIMARY_BLUE);
            
            // User management only for users with permission
            if (dashboardUser.canManageUsers()) {
                createMenuButton(sidebar, "User Management", "user_management", BarangayColors.PRIMARY_BLUE);
            }
            
            createMenuButton(sidebar, "Account Settings", "account_settings", BarangayColors.PRIMARY_BLUE);
            
            sidebar.add(Box.createVerticalStrut(20));
            
            // Separator
            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(180, 1));
            separator.setForeground(BarangayColors.BORDER_COLOR);
            separator.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(separator);
            
            sidebar.add(Box.createVerticalStrut(15));
            
            // Logout button
            JButton logoutButton = new JButton("Logout") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (getModel().isPressed()) {
                        g2.setColor(BarangayColors.LOGOUT_HOVER);
                    } else if (getModel().isRollover()) {
                        g2.setColor(BarangayColors.LOGOUT_HOVER);
                    } else {
                        g2.setColor(BarangayColors.LOGOUT_RED);
                    }
                    
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            
            logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoutButton.setMaximumSize(new Dimension(200, 40));
            logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
            logoutButton.setForeground(Color.WHITE);
            logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
            logoutButton.setContentAreaFilled(false);
            logoutButton.setOpaque(false);
            
            logoutButton.addActionListener(e -> logout());
            
            sidebar.add(logoutButton);
            sidebar.add(Box.createVerticalStrut(15));
            
            return sidebar;
        }
        
        // Method to create menu button
        private void createMenuButton(JPanel sidebar, String text, String panelName, Color normalColor) {
            JButton menuButton = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (getModel().isSelected() || getModel().isPressed()) {
                        g2.setColor(normalColor);
                        setForeground(Color.WHITE);
                    } else if (getModel().isRollover()) {
                        g2.setColor(normalColor.brighter());
                        setForeground(Color.WHITE);
                    } else {
                        g2.setColor(Color.WHITE);
                        setForeground(BarangayColors.TEXT_COLOR);
                    }
                    
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            
            menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            menuButton.setMaximumSize(new Dimension(200, 40));
            menuButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            menuButton.setForeground(BarangayColors.TEXT_COLOR);
            menuButton.setBackground(Color.WHITE);
            menuButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setContentAreaFilled(false);
            menuButton.setOpaque(false);
            
            // Action to switch panels
            menuButton.addActionListener(e -> {
                // Reset all buttons
                for (JButton btn : sidebarButtons.values()) {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(BarangayColors.TEXT_COLOR);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BarangayColors.BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                    ));
                }
                
                // Highlight selected button
                menuButton.setBackground(normalColor);
                menuButton.setForeground(Color.WHITE);
                menuButton.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(normalColor, 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
                
                // Show corresponding panel
                contentLayout.show(contentPanel, panelName);
            });
            
            sidebarButtons.put(panelName, menuButton);
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(menuButton);
        }
        
        // Method to initialize content panels
        private void initializeContentPanels() {
            contentPanel.add(new ManageResidentsModule(dashboardUser, dashboardResidents, dashboardUsers, dashboardArchive, this), 
                           "manage_residents");
            contentPanel.add(new ArchivePanel(dashboardUser, dashboardArchive, dashboardResidents, this), "archive");
            contentPanel.add(new ReportsPanel(dashboardUser, dashboardResidents, dashboardArchive), "reports");
            
            if (dashboardUser.canManageUsers()) {
                contentPanel.add(new UserManagementPanel(dashboardUser, dashboardUsers), "user_management");
            }
            
            contentPanel.add(new AccountSettingsPanel(dashboardUser, dashboardUsers, dashboardArchive), "account_settings");
            
            // Select first button by default
            JButton firstButton = sidebarButtons.get("manage_residents");
            if (firstButton != null) {
                firstButton.doClick();
            }
        }
        
        // Method to logout
        private void logout() {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                SecureFileHandler.logActivity(dashboardUser.getUsername(), "LOGOUT"); // Log to system_logs.dat
                cardLayout.show(mainPanel, "LOGIN");
                mainPanel.remove(dashboard);
            }
        }
        
        // Method to refresh all data from files
        public void refreshAllData() {
            dashboardResidents.clear();
            dashboardResidents.addAll(SecureFileHandler.loadResidents()); // Reload from residents_secure.dat
            dashboardArchive.clear();
            dashboardArchive.addAll(SecureFileHandler.loadArchive()); // Reload from archive_records.dat
            
            // Refresh current panel if it's the manage residents module
            Component currentPanel = contentPanel.getComponent(0);
            if (currentPanel instanceof ManageResidentsModule) {
                ((ManageResidentsModule) currentPanel).refreshData();
            }
        }
    }
    
    // ==================== ARCHIVE PANEL ====================
    // Panel for viewing archived residents (from archive_records.dat)
    class ArchivePanel extends JPanel {
        private User panelUser;                           // Current user
        private List<ArchiveRecord> panelArchive;          // Archive list (from master file)
        private List<Resident> panelResidents;             // Residents list (from master file)
        private MainDashboard dashboard;                    // Reference to main dashboard
        private StyledTable archiveTable;                   // Table for displaying archives
        private DefaultTableModel tableModel;               // Table model
        private TableRowSorter<DefaultTableModel> sorter;   // Sorter for searching
        private StyledTextField searchField;                 // Search field
        
        // Constructor
        public ArchivePanel(User user, List<ArchiveRecord> archive, List<Resident> residents, MainDashboard dashboard) {
            this.panelUser = user;
            this.panelArchive = archive;
            this.panelResidents = residents;
            this.dashboard = dashboard;
            
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            
            JLabel titleLabel = new JLabel("Archived Residents");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            
            JLabel subtitleLabel = new JLabel("Deceased and transferred residents");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            subtitleLabel.setForeground(Color.GRAY);
            
            JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
            titlePanel.setBackground(Color.WHITE);
            titlePanel.add(titleLabel);
            titlePanel.add(subtitleLabel);
            
            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            searchPanel.setBackground(Color.WHITE);
            
            searchField = new StyledTextField(20);
            searchField.putClientProperty("JTextField.placeholderText", "Search archived residents...");
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { searchArchive(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { searchArchive(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { searchArchive(); }
            });
            
            searchPanel.add(new JLabel("Search:"));
            searchPanel.add(searchField);
            
            headerPanel.add(titlePanel, BorderLayout.WEST);
            headerPanel.add(searchPanel, BorderLayout.EAST);
            
            // Table setup
            String[] columns = {"ID", "Full Name", "Age", "Sex", "Address", "Final Status", "Date Archived"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            archiveTable = new StyledTable(tableModel);
            sorter = new TableRowSorter<>(tableModel);
            archiveTable.setRowSorter(sorter);
            archiveTable.setRowHeight(28);
            
            // Set column widths
            archiveTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            archiveTable.getColumnModel().getColumn(1).setPreferredWidth(180);
            archiveTable.getColumnModel().getColumn(2).setPreferredWidth(50);
            archiveTable.getColumnModel().getColumn(3).setPreferredWidth(70);
            archiveTable.getColumnModel().getColumn(4).setPreferredWidth(200);
            archiveTable.getColumnModel().getColumn(5).setPreferredWidth(100);
            archiveTable.getColumnModel().getColumn(6).setPreferredWidth(120);
            
            // Double-click to view details
            archiveTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        viewArchiveDetails();
                    }
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(archiveTable);
            scrollPane.setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
            buttonPanel.setBackground(Color.WHITE);
            
            StyledButton viewButton = new StyledButton("View Details", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            viewButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            viewButton.addActionListener(e -> viewArchiveDetails());
            
            StyledButton restoreButton = new StyledButton("Restore Resident", 
                new Color(40, 167, 69), Color.WHITE);
            restoreButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            restoreButton.addActionListener(e -> restoreSelected());
            
            StyledButton deleteButton = new StyledButton("Permanently Delete", 
                new Color(220, 53, 69), Color.WHITE);
            deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            deleteButton.addActionListener(e -> permanentlyDelete());
            
            buttonPanel.add(viewButton);
            buttonPanel.add(restoreButton);
            if (panelUser instanceof Admin) { // Only admin can permanently delete
                buttonPanel.add(deleteButton);
            }
            
            add(headerPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            
            loadArchiveData(); // Load data from archive_records.dat
        }
        
        // Method to load archive data from list
        private void loadArchiveData() {
            tableModel.setRowCount(0);
            
            for (ArchiveRecord record : panelArchive) {
                Resident r = record.getResident();
                String formattedId = String.format("%06d", r.getResidentID());
                String archivedDate = DateUtils.formatDisplay(record.getArchivedDate().toLocalDate());
                
                tableModel.addRow(new Object[]{
                    formattedId,
                    r.getFullName(),
                    r.getAge(),
                    r.getSex(),
                    r.getAddress(),
                    record.getFinalStatus(),
                    archivedDate
                });
            }
        }
        
        // Method to search archive
        private void searchArchive() {
            String query = searchField.getText().toLowerCase().trim();
            if (query.isEmpty()) {
                sorter.setRowFilter(null);
                return;
            }
            
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            filters.add(RowFilter.regexFilter("(?i)" + query, 0, 1, 4, 5)); // Search in ID, Name, Address, Status
            
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
        
        // Method to view archive details
        private void viewArchiveDetails() {
            int selectedRow = archiveTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an archived record to view!");
                return;
            }
            
            int modelRow = archiveTable.convertRowIndexToModel(selectedRow);
            String idStr = (String) tableModel.getValueAt(modelRow, 0);
            int residentID = Integer.parseInt(idStr);
            
            for (ArchiveRecord record : panelArchive) {
                if (record.getResident().getResidentID() == residentID) {
                    ResidentDetailsDialog dialog = new ResidentDetailsDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this), record.getResident());
                    dialog.setVisible(true);
                    break;
                }
            }
        }
        
        // Method to restore selected resident
        private void restoreSelected() {
            if (!panelUser.canAccessAdminPanel()) {
                JOptionPane.showMessageDialog(this, "You don't have permission to restore residents!");
                return;
            }
            
            int selectedRow = archiveTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an archived record to restore!");
                return;
            }
            
            int modelRow = archiveTable.convertRowIndexToModel(selectedRow);
            String idStr = (String) tableModel.getValueAt(modelRow, 0);
            int residentID = Integer.parseInt(idStr);
            
            ArchiveRecord selectedRecord = null;
            for (ArchiveRecord record : panelArchive) {
                if (record.getResident().getResidentID() == residentID) {
                    selectedRecord = record;
                    break;
                }
            }
            
            if (selectedRecord != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "<html>Are you sure you want to restore:<br><b>" + 
                    selectedRecord.getResident().getFullName() + "</b>?</html>",
                    "Confirm Restore",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    Resident resident = selectedRecord.getResident();
                    resident.setStatus(Resident.ResidentStatus.ACTIVE);
                    panelResidents.add(resident);
                    panelArchive.remove(selectedRecord);
                    
                    // Save to files
                    SecureFileHandler.saveResidents(panelResidents); // Save to residents_secure.dat
                    SecureFileHandler.saveArchive(panelArchive);     // Save to archive_records.dat
                    SecureFileHandler.logActivity(panelUser.getUsername(), 
                        "RESIDENT_RESTORED: " + String.format("%06d", residentID)); // Log to system_logs.dat
                    
                    loadArchiveData(); // Refresh table
                    dashboard.refreshAllData(); // Refresh main dashboard
                    
                    JOptionPane.showMessageDialog(this, "Resident restored successfully!");
                }
            }
        }
        
        // Method to permanently delete selected record
        private void permanentlyDelete() {
            if (!(panelUser instanceof Admin)) {
                JOptionPane.showMessageDialog(this, "Only administrators can permanently delete records!");
                return;
            }
            
            int selectedRow = archiveTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an archived record to delete!");
                return;
            }
            
            int modelRow = archiveTable.convertRowIndexToModel(selectedRow);
            String idStr = (String) tableModel.getValueAt(modelRow, 0);
            int residentID = Integer.parseInt(idStr);
            
            ArchiveRecord selectedRecord = null;
            for (ArchiveRecord record : panelArchive) {
                if (record.getResident().getResidentID() == residentID) {
                    selectedRecord = record;
                    break;
                }
            }
            
            if (selectedRecord != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "<html><font color='red'><b>WARNING: This action cannot be undone!</b></font><br><br>" +
                    "Are you sure you want to permanently delete:<br><b>" + 
                    selectedRecord.getResident().getFullName() + "</b>?</html>",
                    "Confirm Permanent Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    panelArchive.remove(selectedRecord);
                    SecureFileHandler.saveArchive(panelArchive); // Save to archive_records.dat
                    SecureFileHandler.logActivity(panelUser.getUsername(), 
                        "RESIDENT_PERMANENTLY_DELETED: " + String.format("%06d", residentID)); // Log to system_logs.dat
                    
                    loadArchiveData(); // Refresh table
                    
                    JOptionPane.showMessageDialog(this, "Record permanently deleted!");
                }
            }
        }
    }
    
    // ==================== MANAGE RESIDENTS MODULE (UPDATED) ====================
    // Main panel for managing residents (using residents_secure.dat master file)
    class ManageResidentsModule extends JPanel {
        private User panelUser;                           // Current user
        private List<Resident> panelResidents;            // Residents list (from master file)
        private List<User> panelUsers;                     // Users list (from master file)
        private List<ArchiveRecord> panelArchive;          // Archive list (from master file)
        private MainDashboard dashboard;                    // Reference to main dashboard
        
        // Filter components
        private JPanel filterPanel;
        private StyledComboBox<String> statusFilter;
        private StyledComboBox<String> sexFilter;
        private StyledComboBox<String> ageGroupFilter;
        private JCheckBox householdHeadCheckBox;
        
        // Table components
        private StyledTable residentTable;
        private DefaultTableModel tableModel;
        private TableRowSorter<DefaultTableModel> sorter;
        private StyledTextField searchField;
        private JLabel recordSummaryLabel;
        private JPanel summaryCardsPanel;
        private JLabel[] summaryCardValues;
        
        // Preview panel
        private JPanel previewPanel;
        private JLabel previewNameLabel;
        private JPanel previewDetailsPanel;
        
        private List<Resident> filteredResidents;
        private Resident selectedResident;
        
        // Constructor
        public ManageResidentsModule(User user, List<Resident> residents, List<User> users, 
                                    List<ArchiveRecord> archive, MainDashboard dashboard) {
            this.panelUser = user;
            this.panelResidents = residents;
            this.panelUsers = users;
            this.panelArchive = archive;
            this.dashboard = dashboard;
            this.filteredResidents = new ArrayList<>(residents);
            this.summaryCardValues = new JLabel[4]; // Changed from 5 to 4 (removed deceased counter)
            
            setLayout(new BorderLayout());
            setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            // Create main panels
            JPanel leftSidebar = createLeftSidebar();
            JPanel centerPanel = createCenterPanel();
            JPanel rightPreview = createRightPreviewPanel();
            
            add(leftSidebar, BorderLayout.WEST);
            add(centerPanel, BorderLayout.CENTER);
            add(rightPreview, BorderLayout.EAST);
            
            loadResidentsData(); // Load data from residents_secure.dat
            updateSummaryCards();
        }
        
        // Method to refresh data from files
        public void refreshData() {
            filteredResidents = new ArrayList<>(panelResidents);
            loadResidentsData();
            updateSummaryCards();
            selectedResident = null;
            updatePreviewPanel();
            clearFilters();
            revalidate();
            repaint();
        }
        
        // Method to create left sidebar with filters and actions
        private JPanel createLeftSidebar() {
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setPreferredSize(new Dimension(220, getHeight()));
            sidebar.setBackground(BarangayColors.SIDEBAR_GRAY);
            sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BarangayColors.BORDER_COLOR));
            
            // Filters section
            JLabel titleLabel = new JLabel("FILTERS");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
            
            sidebar.add(titleLabel);
            
            filterPanel = new JPanel();
            filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
            filterPanel.setBackground(BarangayColors.SIDEBAR_GRAY);
            filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
            
            // Status filter
            JLabel statusLabel = new JLabel("Status:");
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            statusLabel.setForeground(BarangayColors.TEXT_COLOR);
            statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            statusFilter = new StyledComboBox<>(new String[]{"All", "Active", "Senior Citizen", "Child", "Adult"});
            statusFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            statusFilter.addActionListener(e -> applyFilters());
            
            // Sex filter
            JLabel sexLabel = new JLabel("Sex:");
            sexLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            sexLabel.setForeground(BarangayColors.TEXT_COLOR);
            sexLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            sexFilter = new StyledComboBox<>(new String[]{"All", "Male", "Female"});
            sexFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            sexFilter.addActionListener(e -> applyFilters());
            
            // Age group filter
            JLabel ageLabel = new JLabel("Age Group:");
            ageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            ageLabel.setForeground(BarangayColors.TEXT_COLOR);
            ageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            ageGroupFilter = new StyledComboBox<>(new String[]{"All", "0-12", "13-19", "20-35", "36-59", "60+"});
            ageGroupFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            ageGroupFilter.addActionListener(e -> applyFilters());
            
            // Household head checkbox
            householdHeadCheckBox = new JCheckBox("Household Heads Only");
            householdHeadCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            householdHeadCheckBox.setBackground(BarangayColors.SIDEBAR_GRAY);
            householdHeadCheckBox.setForeground(BarangayColors.TEXT_COLOR);
            householdHeadCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            householdHeadCheckBox.addActionListener(e -> applyFilters());
            
            // Clear filters button
            StyledButton clearFiltersBtn = new StyledButton("Clear Filters", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            clearFiltersBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            clearFiltersBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            clearFiltersBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            clearFiltersBtn.addActionListener(e -> clearFilters());
            
            // Add filter components
            filterPanel.add(statusLabel);
            filterPanel.add(Box.createVerticalStrut(3));
            filterPanel.add(statusFilter);
            filterPanel.add(Box.createVerticalStrut(8));
            filterPanel.add(sexLabel);
            filterPanel.add(Box.createVerticalStrut(3));
            filterPanel.add(sexFilter);
            filterPanel.add(Box.createVerticalStrut(8));
            filterPanel.add(ageLabel);
            filterPanel.add(Box.createVerticalStrut(3));
            filterPanel.add(ageGroupFilter);
            filterPanel.add(Box.createVerticalStrut(8));
            filterPanel.add(householdHeadCheckBox);
            filterPanel.add(Box.createVerticalStrut(12));
            filterPanel.add(clearFiltersBtn);
            
            sidebar.add(filterPanel);
            
            // Actions section
            JLabel actionsLabel = new JLabel("ACTIONS");
            actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            actionsLabel.setForeground(BarangayColors.ACCENT_ORANGE);
            actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionsLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
            
            sidebar.add(actionsLabel);
            
            JPanel actionPanel = new JPanel();
            actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
            actionPanel.setBackground(BarangayColors.SIDEBAR_GRAY);
            actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
            
            // Action buttons
            StyledButton addButton = createActionButton("Add Resident", BarangayColors.PRIMARY_BLUE, e -> addResident());
            StyledButton editButton = createActionButton("Edit Resident", BarangayColors.PRIMARY_BLUE, e -> editResident());
            StyledButton deleteButton = createActionButton("Delete", new Color(220, 53, 69), e -> deleteResident());
            
            // Archive actions
            JLabel archiveLabel = new JLabel("ARCHIVE");
            archiveLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            archiveLabel.setForeground(new Color(108, 117, 125));
            archiveLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            archiveLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));
            
            StyledButton deceasedButton = createActionButton("Mark Deceased", new Color(108, 117, 125), e -> markAsDeceased());
            StyledButton transferredButton = createActionButton("Mark Transferred", new Color(108, 117, 125), e -> markAsTransferred());
            
            actionPanel.add(addButton);
            actionPanel.add(Box.createVerticalStrut(5));
            actionPanel.add(editButton);
            actionPanel.add(Box.createVerticalStrut(5));
            actionPanel.add(deleteButton);
            actionPanel.add(Box.createVerticalStrut(8));
            actionPanel.add(archiveLabel);
            actionPanel.add(Box.createVerticalStrut(5));
            actionPanel.add(deceasedButton);
            actionPanel.add(Box.createVerticalStrut(5));
            actionPanel.add(transferredButton);
            
            sidebar.add(actionPanel);
            sidebar.add(Box.createVerticalGlue());
            
            return sidebar;
        }
        
        // Helper method to create action button with permission check
        private StyledButton createActionButton(String text, Color bgColor, ActionListener listener) {
            StyledButton button = new StyledButton(text, bgColor, Color.WHITE);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.addActionListener(listener);
            
            // Disable if user doesn't have admin access
            if (!panelUser.canAccessAdminPanel()) {
                button.setEnabled(false);
                button.setToolTipText("Only administrators and staff can perform this action");
            }
            
            return button;
        }
        
        // Method to create center panel with table and summary
        private JPanel createCenterPanel() {
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setBackground(Color.WHITE);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Summary cards
            summaryCardsPanel = new JPanel(new GridLayout(1, 4, 5, 0)); // Changed from 5 to 4 columns
            summaryCardsPanel.setBackground(Color.WHITE);
            summaryCardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            
            summaryCardsPanel.add(createSummaryCard("Total Residents", "0", new Color(52, 152, 219), 0));
            summaryCardsPanel.add(createSummaryCard("Total Population", "0", new Color(46, 204, 113), 1));
            summaryCardsPanel.add(createSummaryCard("Senior Citizens", "0", new Color(155, 89, 182), 2));
            summaryCardsPanel.add(createSummaryCard("Archived", "0", new Color(149, 165, 166), 3)); // Archived count only, no separate deceased
            
            // Search panel
            JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
            searchPanel.setBackground(Color.WHITE);
            searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            
            JLabel searchLabel = new JLabel("Search:");
            searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            searchLabel.setForeground(BarangayColors.TEXT_COLOR);
            
            searchField = new StyledTextField(25);
            searchField.putClientProperty("JTextField.placeholderText", "ID, Name, Address, Contact...");
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { searchResidents(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { searchResidents(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { searchResidents(); }
            });
            
            StyledButton searchButton = new StyledButton("Go", 
                BarangayColors.ACCENT_ORANGE, Color.WHITE);
            searchButton.setPreferredSize(new Dimension(50, 30));
            searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            searchButton.addActionListener(e -> searchResidents());
            
            JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            searchInputPanel.setBackground(Color.WHITE);
            searchInputPanel.add(searchField);
            searchInputPanel.add(searchButton);
            
            searchPanel.add(searchLabel, BorderLayout.WEST);
            searchPanel.add(searchInputPanel, BorderLayout.CENTER);
            
            // Table setup
            String[] columns = {"ID", "Full Name", "Age", "Sex", "Civil Status", "Address", 
                               "Contact", "Household", "Status", "Registered"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            residentTable = new StyledTable(tableModel);
            sorter = new TableRowSorter<>(tableModel);
            residentTable.setRowSorter(sorter);
            residentTable.setRowHeight(26);
            
            // Set column widths
            residentTable.getColumnModel().getColumn(0).setPreferredWidth(60);
            residentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            residentTable.getColumnModel().getColumn(2).setPreferredWidth(40);
            residentTable.getColumnModel().getColumn(3).setPreferredWidth(60);
            residentTable.getColumnModel().getColumn(4).setPreferredWidth(80);
            residentTable.getColumnModel().getColumn(5).setPreferredWidth(180);
            residentTable.getColumnModel().getColumn(6).setPreferredWidth(90);
            residentTable.getColumnModel().getColumn(7).setPreferredWidth(70);
            residentTable.getColumnModel().getColumn(8).setPreferredWidth(70);
            residentTable.getColumnModel().getColumn(9).setPreferredWidth(80);
            
            // Selection listener
            residentTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = residentTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = residentTable.convertRowIndexToModel(selectedRow);
                        String idStr = (String) tableModel.getValueAt(modelRow, 0);
                        int residentID = Integer.parseInt(idStr);
                        selectedResident = findResidentById(residentID);
                        updatePreviewPanel();
                    }
                }
            });
            
            // Context menu
            JPopupMenu contextMenu = new JPopupMenu();
            JMenuItem editItem = new JMenuItem("Edit");
            JMenuItem deleteItem = new JMenuItem("Delete");
            JMenuItem viewItem = new JMenuItem("View Details");
            JMenuItem deceasedItem = new JMenuItem("Mark as Deceased");
            JMenuItem transferredItem = new JMenuItem("Mark as Transferred");
            
            editItem.addActionListener(e -> editResident());
            deleteItem.addActionListener(e -> deleteResident());
            viewItem.addActionListener(e -> viewResidentDetails());
            deceasedItem.addActionListener(e -> markAsDeceased());
            transferredItem.addActionListener(e -> markAsTransferred());
            
            contextMenu.add(editItem);
            contextMenu.add(deleteItem);
            contextMenu.addSeparator();
            contextMenu.add(viewItem);
            contextMenu.addSeparator();
            contextMenu.add(deceasedItem);
            contextMenu.add(transferredItem);
            
            residentTable.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        int row = residentTable.rowAtPoint(e.getPoint());
                        residentTable.setRowSelectionInterval(row, row);
                        contextMenu.show(residentTable, e.getX(), e.getY());
                    }
                }
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        int row = residentTable.rowAtPoint(e.getPoint());
                        residentTable.setRowSelectionInterval(row, row);
                        contextMenu.show(residentTable, e.getX(), e.getY());
                    }
                }
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        viewResidentDetails();
                    }
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(residentTable);
            scrollPane.setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
            scrollPane.getViewport().setBackground(Color.WHITE);
            
            // Bottom panel with record summary
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBackground(Color.WHITE);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            
            recordSummaryLabel = new JLabel("Showing 0 residents");
            recordSummaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            recordSummaryLabel.setForeground(BarangayColors.TEXT_COLOR);
            
            bottomPanel.add(recordSummaryLabel, BorderLayout.WEST);
            
            // Top container
            JPanel topContainer = new JPanel(new BorderLayout());
            topContainer.setBackground(Color.WHITE);
            topContainer.add(summaryCardsPanel, BorderLayout.NORTH);
            topContainer.add(searchPanel, BorderLayout.SOUTH);
            
            centerPanel.add(topContainer, BorderLayout.NORTH);
            centerPanel.add(scrollPane, BorderLayout.CENTER);
            centerPanel.add(bottomPanel, BorderLayout.SOUTH);
            
            return centerPanel;
        }
        
        // Method to create summary card
        private JPanel createSummaryCard(String title, String value, Color color, int index) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
            ));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            titleLabel.setForeground(Color.GRAY);
            
            summaryCardValues[index] = new JLabel(value);
            summaryCardValues[index].setFont(new Font("Segoe UI", Font.BOLD, 16));
            summaryCardValues[index].setForeground(color);
            
            card.add(titleLabel, BorderLayout.NORTH);
            card.add(summaryCardValues[index], BorderLayout.CENTER);
            
            return card;
        }
        
        // Method to create right preview panel
        private JPanel createRightPreviewPanel() {
            previewPanel = new JPanel(new BorderLayout());
            previewPanel.setPreferredSize(new Dimension(280, getHeight()));
            previewPanel.setBackground(Color.WHITE);
            previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, BarangayColors.BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            
            JLabel titleLabel = new JLabel("DETAILS");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BarangayColors.BORDER_COLOR));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            
            previewNameLabel = new JLabel("Select a resident");
            previewNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            previewNameLabel.setForeground(BarangayColors.TEXT_COLOR);
            previewNameLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            
            previewDetailsPanel = new JPanel();
            previewDetailsPanel.setLayout(new BoxLayout(previewDetailsPanel, BoxLayout.Y_AXIS));
            previewDetailsPanel.setBackground(Color.WHITE);
            
            JScrollPane scrollPane = new JScrollPane(previewDetailsPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            
            previewPanel.add(titleLabel, BorderLayout.NORTH);
            previewPanel.add(previewNameLabel, BorderLayout.CENTER);
            previewPanel.add(scrollPane, BorderLayout.SOUTH);
            
            return previewPanel;
        }
        
        // Method to update preview panel with selected resident
        private void updatePreviewPanel() {
            previewDetailsPanel.removeAll();
            
            if (selectedResident == null) {
                previewNameLabel.setText("Select a resident");
                JLabel emptyLabel = new JLabel("No resident selected");
                emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                emptyLabel.setForeground(Color.GRAY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                previewDetailsPanel.add(emptyLabel);
            } else {
                previewNameLabel.setText(selectedResident.getFullName());
                
                // Personal section
                addPreviewSection("PERSONAL");
                addPreviewDetail("ID:", String.format("%06d", selectedResident.getResidentID()));
                addPreviewDetail("Age:", String.valueOf(selectedResident.getAge()));
                addPreviewDetail("Birthday:", selectedResident.getBirthday());
                addPreviewDetail("Sex:", selectedResident.getSex());
                addPreviewDetail("Status:", selectedResident.getMaritalStatus());
                
                // Contact section
                addPreviewSection("CONTACT");
                addPreviewDetail("Address:", selectedResident.getAddress());
                addPreviewDetail("Contact:", selectedResident.getContactNumber());
                addPreviewDetail("Occupation:", selectedResident.getOccupation());
                
                // Household section
                addPreviewSection("HOUSEHOLD");
                addPreviewDetail("Position:", selectedResident.getPosition());
                addPreviewDetail("Head:", selectedResident.isHouseholdHead() ? "Yes" : 
                    "ID: " + String.format("%06d", selectedResident.getHouseholdHeadID()));
                addPreviewDetail("Size:", String.valueOf(selectedResident.getHouseholdSize()));
                
                // Other occupants section
                addPreviewSection("OTHER OCCUPANTS");
                if (selectedResident.getHouseholdMembers().isEmpty()) {
                    addPreviewDetail("Members:", "None");
                } else {
                    for (int i = 0; i < selectedResident.getHouseholdMembers().size(); i++) {
                        HouseholdMember member = selectedResident.getHouseholdMembers().get(i);
                        addPreviewDetail("Member " + (i + 1) + ":", 
                            member.getFullName() + " (Age: " + member.getAge() + ", " + 
                            member.getSex() + ", " + member.getRelationship() + ")");
                    }
                }
                
                // System section
                addPreviewSection("SYSTEM");
                addPreviewDetail("Status:", selectedResident.getStatus().toString());
                addPreviewDetail("Registered:", DateUtils.formatDisplay(selectedResident.getCreatedAt().toLocalDate()));
            }
            
            previewDetailsPanel.revalidate();
            previewDetailsPanel.repaint();
        }
        
        // Helper method to add section header to preview
        private void addPreviewSection(String title) {
            JLabel sectionLabel = new JLabel(title);
            sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            sectionLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sectionLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 3, 0));
            previewDetailsPanel.add(sectionLabel);
        }
        
        // Helper method to add detail row to preview
        private void addPreviewDetail(String label, String value) {
            JPanel detailPanel = new JPanel(new BorderLayout(5, 0));
            detailPanel.setBackground(Color.WHITE);
            detailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
            
            JLabel labelComp = new JLabel(label);
            labelComp.setFont(new Font("Segoe UI", Font.BOLD, 10));
            labelComp.setForeground(Color.GRAY);
            labelComp.setPreferredSize(new Dimension(70, 18));
            
            JLabel valueComp = new JLabel(value != null ? value : "");
            valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            valueComp.setForeground(BarangayColors.TEXT_COLOR);
            
            detailPanel.add(labelComp, BorderLayout.WEST);
            detailPanel.add(valueComp, BorderLayout.CENTER);
            
            previewDetailsPanel.add(detailPanel);
        }
        
        // Method to load residents data into table
        private void loadResidentsData() {
            tableModel.setRowCount(0);
            
            // If user is RESIDENT, only show their own record
            if (panelUser instanceof ResidentUser) {
                int residentID = ((ResidentUser) panelUser).getResidentID();
                for (Resident resident : filteredResidents) {
                    if (resident.getResidentID() == residentID) {
                        String formattedId = String.format("%06d", resident.getResidentID());
                        String status = resident.getStatus().toString();
                        String dateRegistered = DateUtils.formatDisplay(resident.getCreatedAt().toLocalDate());
                        String householdNo = resident.isHouseholdHead() ? "HEAD" : 
                            String.format("%06d", resident.getHouseholdHeadID());
                        
                        tableModel.addRow(new Object[]{
                            formattedId,
                            resident.getFullName(),
                            resident.getAge(),
                            resident.getSex(),
                            resident.getMaritalStatus(),
                            resident.getAddress(),
                            resident.getContactNumber(),
                            householdNo,
                            status,
                            dateRegistered
                        });
                        break;
                    }
                }
            } else {
                // Admin and Staff see all residents
                for (Resident resident : filteredResidents) {
                    String formattedId = String.format("%06d", resident.getResidentID());
                    String status = resident.getStatus().toString();
                    String dateRegistered = DateUtils.formatDisplay(resident.getCreatedAt().toLocalDate());
                    String householdNo = resident.isHouseholdHead() ? "HEAD" : 
                        String.format("%06d", resident.getHouseholdHeadID());
                    
                    tableModel.addRow(new Object[]{
                        formattedId,
                        resident.getFullName(),
                        resident.getAge(),
                        resident.getSex(),
                        resident.getMaritalStatus(),
                        resident.getAddress(),
                        resident.getContactNumber(),
                        householdNo,
                        status,
                        dateRegistered
                    });
                }
            }
            
            updateRecordSummary();
        }
        
        // Method to update record summary
        private void updateRecordSummary() {
            int total = tableModel.getRowCount();
            recordSummaryLabel.setText(String.format("Showing %d resident(s)", total));
        }
        
        // Method to update summary cards
        private void updateSummaryCards() {
            int totalResidents = 0;
            int totalPopulation = 0;
            int seniors = 0;
            int archived = 0; // For archived count (deceased + transferred)
            
            // Count active residents
            for (Resident r : panelResidents) {
                if (r.getStatus() == Resident.ResidentStatus.ACTIVE) {
                    totalResidents++;
                    totalPopulation += r.getTotalPopulation();
                    if (r.getAge() >= 60) seniors++;
                    
                    for (HouseholdMember member : r.getHouseholdMembers()) {
                        if (member.getAge() >= 60) seniors++;
                    }
                }
            }
            
            // Count archived records
            archived = panelArchive.size();
            
            summaryCardValues[0].setText(String.valueOf(totalResidents));
            summaryCardValues[1].setText(String.valueOf(totalPopulation));
            summaryCardValues[2].setText(String.valueOf(seniors));
            summaryCardValues[3].setText(String.valueOf(archived)); // Archived count only
        }
        
        // Method to apply filters
        private void applyFilters() {
            filteredResidents.clear();
            String status = (String) statusFilter.getSelectedItem();
            String sex = (String) sexFilter.getSelectedItem();
            String ageGroup = (String) ageGroupFilter.getSelectedItem();
            boolean onlyHeads = householdHeadCheckBox.isSelected();
            
            for (Resident r : panelResidents) {
                boolean matches = true;
                
                // Status filter
                if (!status.equals("All")) {
                    if (status.equals("Active") && r.getStatus() != Resident.ResidentStatus.ACTIVE) matches = false;
                    else if (status.equals("Senior Citizen") && (r.getAge() < 60 || r.getStatus() != Resident.ResidentStatus.ACTIVE)) matches = false;
                    else if (status.equals("Child") && (r.getAge() > 12 || r.getStatus() != Resident.ResidentStatus.ACTIVE)) matches = false;
                    else if (status.equals("Adult") && (r.getAge() < 13 || r.getAge() > 59 || r.getStatus() != Resident.ResidentStatus.ACTIVE)) matches = false;
                }
                
                // Sex filter
                if (matches && !sex.equals("All") && !r.getSex().equals(sex)) matches = false;
                
                // Age group filter
                if (matches && !ageGroup.equals("All")) {
                    int age = r.getAge();
                    if (ageGroup.equals("0-12") && (age < 0 || age > 12)) matches = false;
                    else if (ageGroup.equals("13-19") && (age < 13 || age > 19)) matches = false;
                    else if (ageGroup.equals("20-35") && (age < 20 || age > 35)) matches = false;
                    else if (ageGroup.equals("36-59") && (age < 36 || age > 59)) matches = false;
                    else if (ageGroup.equals("60+") && age < 60) matches = false;
                }
                
                // Household head filter
                if (matches && onlyHeads && !r.isHouseholdHead()) matches = false;
                
                if (matches) filteredResidents.add(r);
            }
            
            loadResidentsData();
        }
        
        // Method to clear filters
        private void clearFilters() {
            statusFilter.setSelectedItem("All");
            sexFilter.setSelectedItem("All");
            ageGroupFilter.setSelectedItem("All");
            householdHeadCheckBox.setSelected(false);
            filteredResidents = new ArrayList<>(panelResidents);
            loadResidentsData();
        }
        
        // Method to search residents
        private void searchResidents() {
            String query = searchField.getText().toLowerCase().trim();
            if (query.isEmpty()) {
                sorter.setRowFilter(null);
                updateRecordSummary();
                return;
            }
            
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            filters.add(RowFilter.regexFilter("(?i)" + query, 0, 1, 5, 6)); // Search in ID, Name, Address, Contact
            
            sorter.setRowFilter(RowFilter.andFilter(filters));
            updateRecordSummary();
        }
        
        // Helper method to find resident by ID
        private Resident findResidentById(int id) {
            for (Resident r : panelResidents) {
                if (r.getResidentID() == id) return r;
            }
            return null;
        }
        
        // Method to add new resident
        private void addResident() {
            if (!panelUser.canAccessAdminPanel()) {
                showAccessDenied();
                return;
            }
            
            AddEditResidentDialog dialog = new AddEditResidentDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), null, panelResidents);
            if (dialog.showDialog()) {
                Resident newResident = dialog.getResident();
                panelResidents.add(newResident);
                SecureFileHandler.incrementResidentId(); // Increment ID counter and save to id_counter.dat
                SecureFileHandler.saveResidents(panelResidents); // Save to residents_secure.dat
                SecureFileHandler.logActivity(panelUser.getUsername(), 
                    "RESIDENT_ADDED: " + String.format("%06d", newResident.getResidentID())); // Log to system_logs.dat
                
                refreshData();
                dashboard.refreshAllData();
                
                showSuccess("Resident added successfully!");
            }
        }
        
        // Method to edit resident
        private void editResident() {
            if (!panelUser.canAccessAdminPanel()) {
                showAccessDenied();
                return;
            }
            
            if (selectedResident == null) {
                showNoSelection();
                return;
            }
            
            AddEditResidentDialog dialog = new AddEditResidentDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), selectedResident, panelResidents);
            if (dialog.showDialog()) {
                SecureFileHandler.saveResidents(panelResidents); // Save to residents_secure.dat
                SecureFileHandler.logActivity(panelUser.getUsername(), 
                    "RESIDENT_UPDATED: " + String.format("%06d", selectedResident.getResidentID())); // Log to system_logs.dat
                
                refreshData();
                dashboard.refreshAllData();
                
                showSuccess("Resident updated successfully!");
            }
        }
        
        // Method to delete resident
        private void deleteResident() {
            if (!panelUser.canAccessAdminPanel()) {
                showAccessDenied();
                return;
            }
            
            if (selectedResident == null) {
                showNoSelection();
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Are you sure you want to delete:<br><b>" + selectedResident.getFullName() + 
                "</b> (ID: " + String.format("%06d", selectedResident.getResidentID()) + ")?<br><br>" +
                "This action cannot be undone!</html>",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                panelResidents.remove(selectedResident);
                SecureFileHandler.saveResidents(panelResidents); // Save to residents_secure.dat
                SecureFileHandler.logActivity(panelUser.getUsername(), 
                    "RESIDENT_DELETED: " + String.format("%06d", selectedResident.getResidentID())); // Log to system_logs.dat
                
                refreshData();
                dashboard.refreshAllData();
                
                showSuccess("Resident deleted successfully!");
            }
        }
        
        // Method to mark resident as deceased (moves to archive)
        private void markAsDeceased() {
            if (!panelUser.canAccessAdminPanel()) {
                showAccessDenied();
                return;
            }
            
            if (selectedResident == null) {
                showNoSelection();
                return;
            }
            
            if (selectedResident.getStatus() == Resident.ResidentStatus.DECEASED) {
                JOptionPane.showMessageDialog(this, 
                    "This resident is already marked as deceased!", 
                    "Already Deceased", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Are you sure you want to mark:<br><b>" + selectedResident.getFullName() + 
                "</b> (ID: " + String.format("%06d", selectedResident.getResidentID()) + ")<br>as DECEASED?<br><br>" +
                "This will archive their record.</html>",
                "Mark as Deceased",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                selectedResident.setStatus(Resident.ResidentStatus.DECEASED);
                ArchiveRecord record = new ArchiveRecord(selectedResident, "DECEASED");
                panelArchive.add(record);
                panelResidents.remove(selectedResident);
                
                // Save to files
                SecureFileHandler.saveResidents(panelResidents); // Save to residents_secure.dat
                SecureFileHandler.saveArchive(panelArchive);     // Save to archive_records.dat
                SecureFileHandler.logActivity(panelUser.getUsername(), 
                    "RESIDENT_DECEASED: " + String.format("%06d", selectedResident.getResidentID())); // Log to system_logs.dat
                
                refreshData();
                dashboard.refreshAllData();
                
                showSuccess("Resident marked as deceased and archived.");
            }
        }
        
        // Method to mark resident as transferred (moves to archive)
        private void markAsTransferred() {
            if (!panelUser.canAccessAdminPanel()) {
                showAccessDenied();
                return;
            }
            
            if (selectedResident == null) {
                showNoSelection();
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Are you sure you want to mark:<br><b>" + selectedResident.getFullName() + 
                "</b> (ID: " + String.format("%06d", selectedResident.getResidentID()) + ")<br>as TRANSFERRED?<br><br>" +
                "This will archive their record.</html>",
                "Mark as Transferred",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                selectedResident.setStatus(Resident.ResidentStatus.TRANSFERRED);
                ArchiveRecord record = new ArchiveRecord(selectedResident, "TRANSFERRED");
                panelArchive.add(record);
                panelResidents.remove(selectedResident);
                
                // Save to files
                SecureFileHandler.saveResidents(panelResidents); // Save to residents_secure.dat
                SecureFileHandler.saveArchive(panelArchive);     // Save to archive_records.dat
                SecureFileHandler.logActivity(panelUser.getUsername(), 
                    "RESIDENT_TRANSFERRED: " + String.format("%06d", selectedResident.getResidentID())); // Log to system_logs.dat
                
                refreshData();
                dashboard.refreshAllData();
                
                showSuccess("Resident marked as transferred and archived.");
            }
        }
        
        // Method to view resident details
        private void viewResidentDetails() {
            if (selectedResident == null) {
                showNoSelection();
                return;
            }
            
            ResidentDetailsDialog dialog = new ResidentDetailsDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), selectedResident);
            dialog.setVisible(true);
        }
        
        // Helper methods for messages
        private void showAccessDenied() {
            JOptionPane.showMessageDialog(this, 
                "You don't have permission to perform this action.", 
                "Access Denied", JOptionPane.WARNING_MESSAGE);
        }
        
        private void showNoSelection() {
            JOptionPane.showMessageDialog(this, 
                "Please select a resident first.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
        }
        
        private void showSuccess(String message) {
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // ==================== ADD/EDIT RESIDENT DIALOG (UPDATED) ====================
    // Dialog for adding or editing residents
    class AddEditResidentDialog {
        private JDialog dialog;
        private Resident resident;
        private boolean saved;
        private List<Resident> dialogResidents;
        
        // Form fields
        private StyledTextField idField, firstNameField, mInitialField, lastNameField, qualifierField,
                               ageField, birthdayField, medicalConditionField,
                               motherTongueField, addressField, positionField, 
                               contactField, occupationField;
        private StyledComboBox<String> sexComboBox, incomeComboBox, employmentComboBox, 
                                      civilStatusComboBox, religionComboBox;
        private StyledTextField religionOtherField;
        
        // Household members table
        private DefaultTableModel memberTableModel;
        private JTable memberTable;
        private List<HouseholdMember> householdMembers;
        
        // Constructor
        public AddEditResidentDialog(JFrame parent, Resident existingResident, List<Resident> residents) {
            this.resident = existingResident;
            this.saved = false;
            this.dialogResidents = residents;
            this.householdMembers = existingResident != null ? 
                new ArrayList<>(existingResident.getHouseholdMembers()) : new ArrayList<>();
            
            dialog = new JDialog(parent, existingResident == null ? "Add New Resident" : "Edit Resident", true);
            dialog.setSize(700, 850);
            dialog.setLocationRelativeTo(parent);
            dialog.getContentPane().setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JLabel titleLabel = new JLabel(existingResident == null ? 
                "Add New Resident" : "Edit Resident");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(3, 5, 3, 5);
            
            int row = 0;
            
            // Resident ID field (auto-generated, read-only)
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Resident ID:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            idField = new StyledTextField(15);
            idField.putClientProperty("JTextField.placeholderText", "Auto-generated");
            if (existingResident != null) {
                idField.setText(String.format("%06d", existingResident.getResidentID()));
                idField.setEditable(false);
                idField.setBackground(BarangayColors.LIGHT_BACKGROUND);
            } else {
                String nextId = SecureFileHandler.getNextResidentIdFormatted(); // Get from id_counter.dat
                idField.setText(nextId);
                idField.setEditable(false);
                idField.setBackground(BarangayColors.LIGHT_BACKGROUND);
            }
            formPanel.add(idField, gbc);
            row++;
            
            // First name
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("First Name*:"), gbc);
            gbc.gridx = 1;
            firstNameField = new StyledTextField(15);
            firstNameField.putClientProperty("JTextField.placeholderText", "Enter first name");
            if (existingResident != null) firstNameField.setText(existingResident.getFirstName());
            formPanel.add(firstNameField, gbc);
            row++;
            
            // Middle initial
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Middle Initial:"), gbc);
            gbc.gridx = 1;
            mInitialField = new StyledTextField(5);
            mInitialField.putClientProperty("JTextField.placeholderText", "M.I.");
            if (existingResident != null) mInitialField.setText(existingResident.getMInitial());
            formPanel.add(mInitialField, gbc);
            row++;
            
            // Last name
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Last Name*:"), gbc);
            gbc.gridx = 1;
            lastNameField = new StyledTextField(15);
            lastNameField.putClientProperty("JTextField.placeholderText", "Enter last name");
            if (existingResident != null) lastNameField.setText(existingResident.getLastName());
            formPanel.add(lastNameField, gbc);
            row++;
            
            // Qualifier (Jr., Sr., etc.)
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Qualifier:"), gbc);
            gbc.gridx = 1;
            qualifierField = new StyledTextField(10);
            qualifierField.putClientProperty("JTextField.placeholderText", "Jr., Sr., III");
            if (existingResident != null) qualifierField.setText(existingResident.getQualifier());
            formPanel.add(qualifierField, gbc);
            row++;
            
            // Birthday
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Birthday*:"), gbc);
            gbc.gridx = 1;
            birthdayField = new StyledTextField(15);
            birthdayField.putClientProperty("JTextField.placeholderText", "MM-DD-YYYY");
            if (existingResident != null) birthdayField.setText(existingResident.getBirthday());
            formPanel.add(birthdayField, gbc);
            row++;
            
            // Age (auto-calculated)
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Age:"), gbc);
            gbc.gridx = 1;
            ageField = new StyledTextField(5);
            ageField.putClientProperty("JTextField.placeholderText", "Auto-calculated");
            ageField.setEditable(false);
            ageField.setBackground(BarangayColors.LIGHT_BACKGROUND);
            if (existingResident != null) ageField.setText(String.valueOf(existingResident.getAge()));
            formPanel.add(ageField, gbc);
            row++;
            
            // Sex
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Sex*:"), gbc);
            gbc.gridx = 1;
            sexComboBox = new StyledComboBox<>(new String[]{"Male", "Female"});
            if (existingResident != null) sexComboBox.setSelectedItem(existingResident.getSex());
            formPanel.add(sexComboBox, gbc);
            row++;
            
            // Position in household
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Position*:"), gbc);
            gbc.gridx = 1;
            positionField = new StyledTextField(15);
            positionField.putClientProperty("JTextField.placeholderText", "Household Head, Spouse, Child");
            if (existingResident != null) {
                positionField.setText(existingResident.getPosition());
            } else {
                positionField.setText("Household Head"); // Default for new residents
            }
            formPanel.add(positionField, gbc);
            row++;
            
            // Civil status
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Civil Status*:"), gbc);
            gbc.gridx = 1;
            String[] civilStatusOptions = {"Single", "Married", "Widowed", "Separated", "Annulled"};
            civilStatusComboBox = new StyledComboBox<>(civilStatusOptions);
            if (existingResident != null) {
                civilStatusComboBox.setSelectedItem(existingResident.getMaritalStatus());
            }
            formPanel.add(civilStatusComboBox, gbc);
            row++;
            
            // Religion with "Other" option
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Religion:"), gbc);
            gbc.gridx = 1;
            
            JPanel religionPanel = new JPanel(new BorderLayout(5, 0));
            religionPanel.setBackground(Color.WHITE);
            
            String[] religionOptions = {"Roman Catholic", "Iglesia ni Cristo", "Islam", 
                                       "Born Again Christian", "Protestant", "Jehovah's Witness", "Other"};
            religionComboBox = new StyledComboBox<>(religionOptions);
            religionComboBox.setPreferredSize(new Dimension(150, 30));
            
            religionOtherField = new StyledTextField(10);
            religionOtherField.putClientProperty("JTextField.placeholderText", "if Other please Specify");
            religionOtherField.setEnabled(false);
            
            // Enable/disable other field based on selection
            religionComboBox.addActionListener(e -> {
                religionOtherField.setEnabled("Other".equals(religionComboBox.getSelectedItem()));
                if (!"Other".equals(religionComboBox.getSelectedItem())) {
                    religionOtherField.setText("");
                }
            });
            
            religionPanel.add(religionComboBox, BorderLayout.WEST);
            religionPanel.add(religionOtherField, BorderLayout.CENTER);
            
            // Set existing religion if editing
            if (existingResident != null) {
                String religion = existingResident.getReligion();
                boolean isOther = true;
                for (String opt : religionOptions) {
                    if (opt.equals(religion)) {
                        religionComboBox.setSelectedItem(religion);
                        isOther = false;
                        break;
                    }
                }
                if (isOther && religion != null && !religion.isEmpty()) {
                    religionComboBox.setSelectedItem("Other");
                    religionOtherField.setText(religion);
                    religionOtherField.setEnabled(true);
                }
            }
            
            formPanel.add(religionPanel, gbc);
            row++;
            
            // Medical condition
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Medical Condition:"), gbc);
            gbc.gridx = 1;
            medicalConditionField = new StyledTextField(20);
            medicalConditionField.putClientProperty("JTextField.placeholderText", "e.g., eczema, lung cancer");
            if (existingResident != null) medicalConditionField.setText(existingResident.getMedicalCondition());
            formPanel.add(medicalConditionField, gbc);
            row++;
            
            // Income bracket
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Income Bracket:"), gbc);
            gbc.gridx = 1;
            String[] incomeOptions = {"Select income level", "Less than ₱20,000", "₱20,000 - ₱40,000", 
                                     "₱40,001 - ₱60,000", "₱60,001 - ₱80,000", "₱80,001 - ₱100,000", 
                                     "More than ₱100,000"};
            incomeComboBox = new StyledComboBox<>(incomeOptions);
            if (existingResident != null) {
                int income = existingResident.getIncomeBracket();
                if (income < 20000) incomeComboBox.setSelectedItem("Less than ₱20,000");
                else if (income <= 40000) incomeComboBox.setSelectedItem("₱20,000 - ₱40,000");
                else if (income <= 60000) incomeComboBox.setSelectedItem("₱40,001 - ₱60,000");
                else if (income <= 80000) incomeComboBox.setSelectedItem("₱60,001 - ₱80,000");
                else if (income <= 100000) incomeComboBox.setSelectedItem("₱80,001 - ₱100,000");
                else if (income > 100000) incomeComboBox.setSelectedItem("More than ₱100,000");
                else incomeComboBox.setSelectedIndex(0);
            }
            formPanel.add(incomeComboBox, gbc);
            row++;
            
            // Mother tongue
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Mother Tongue:"), gbc);
            gbc.gridx = 1;
            motherTongueField = new StyledTextField(15);
            motherTongueField.putClientProperty("JTextField.placeholderText", "e.g., Tagalog, Cebuano");
            if (existingResident != null) motherTongueField.setText(existingResident.getMotherTongue());
            formPanel.add(motherTongueField, gbc);
            row++;
            
            // Employment status
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Employment*:"), gbc);
            gbc.gridx = 1;
            String[] employmentOptions = {"Select employment status", "Employed", "Self-employed", 
                                         "Unemployed", "Student", "Retired", "OFW", "Contractual", 
                                         "Part-time", "Homemaker", "Disabled"};
            employmentComboBox = new StyledComboBox<>(employmentOptions);
            if (existingResident != null) {
                employmentComboBox.setSelectedItem(existingResident.getEmployment());
            }
            formPanel.add(employmentComboBox, gbc);
            row++;
            
            // Occupation
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Occupation:"), gbc);
            gbc.gridx = 1;
            occupationField = new StyledTextField(15);
            occupationField.putClientProperty("JTextField.placeholderText", "e.g., Teacher, Farmer");
            if (existingResident != null) occupationField.setText(existingResident.getOccupation());
            formPanel.add(occupationField, gbc);
            row++;
            
            // Address
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Address*:"), gbc);
            gbc.gridx = 1;
            addressField = new StyledTextField(20);
            addressField.putClientProperty("JTextField.placeholderText", "Street, Barangay, City");
            if (existingResident != null) addressField.setText(existingResident.getAddress());
            formPanel.add(addressField, gbc);
            row++;
            
            // Contact number
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Contact Number*:"), gbc);
            gbc.gridx = 1;
            contactField = new StyledTextField(15);
            contactField.putClientProperty("JTextField.placeholderText", "09xxxxxxxxx");
            contactField.setDocument(new PhoneDocument()); // Phone number filter
            if (existingResident != null) contactField.setText(existingResident.getContactNumber());
            formPanel.add(contactField, gbc);
            row++;
            
            // Household members section (only for household heads)
            if (existingResident == null || existingResident.isHouseholdHead()) {
                gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                JLabel membersLabel = new JLabel("HOUSEHOLD MEMBERS / OTHER OCCUPANTS");
                membersLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                membersLabel.setForeground(BarangayColors.PRIMARY_BLUE);
                formPanel.add(membersLabel, gbc);
                row++;
                
                // Members table
                gbc.gridy = row; gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0; gbc.weighty = 0.3;
                
                String[] memberColumns = {"Last Name", "First Name", "Qualifier", "Age", "Birthday", 
                                          "Civil Status", "Sex", "Relationship"};
                memberTableModel = new DefaultTableModel(memberColumns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                
                memberTable = new JTable(memberTableModel);
                memberTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                memberTable.setRowHeight(22);
                JScrollPane memberScrollPane = new JScrollPane(memberTable);
                memberScrollPane.setPreferredSize(new Dimension(600, 150));
                formPanel.add(memberScrollPane, gbc);
                row++;
                
                // Load existing members
                for (HouseholdMember member : householdMembers) {
                    memberTableModel.addRow(new Object[]{
                        member.getLastName(),
                        member.getFirstName(),
                        member.getQualifier() != null ? member.getQualifier() : "",
                        member.getAge(),
                        member.getBirthday(),
                        member.getCivilStatus(),
                        member.getSex(),
                        member.getRelationship()
                    });
                }
                
                // Member buttons panel
                gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
                gbc.weightx = 0; gbc.weighty = 0;
                gbc.gridwidth = 2;
                
                JPanel memberButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
                memberButtonPanel.setBackground(Color.WHITE);
                
                StyledButton addMemberButton = new StyledButton("Add Occupant", 
                    BarangayColors.PRIMARY_BLUE, Color.WHITE);
                addMemberButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                addMemberButton.setPreferredSize(new Dimension(100, 28));
                addMemberButton.addActionListener(e -> showAddMemberDialog());
                
                StyledButton removeMemberButton = new StyledButton("Remove", 
                    new Color(220, 53, 69), Color.WHITE);
                removeMemberButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                removeMemberButton.setPreferredSize(new Dimension(100, 28));
                removeMemberButton.addActionListener(e -> removeSelectedMember());
                
                memberButtonPanel.add(addMemberButton);
                memberButtonPanel.add(removeMemberButton);
                formPanel.add(memberButtonPanel, gbc);
                row++;
            }
            
            // Save/Cancel buttons
            gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(15, 5, 5, 5);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(Color.WHITE);
            
            StyledButton saveButton = new StyledButton("Save Resident", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            saveButton.setPreferredSize(new Dimension(120, 32));
            saveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            saveButton.addActionListener(e -> saveResident());
            
            StyledButton cancelButton = new StyledButton("Cancel", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            cancelButton.setPreferredSize(new Dimension(100, 32));
            cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            formPanel.add(buttonPanel, gbc);
            
            JScrollPane mainScrollPane = new JScrollPane(formPanel);
            mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
            mainScrollPane.setBorder(null);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(mainScrollPane, BorderLayout.CENTER);
            
            dialog.add(mainPanel);
            
            // Keyboard shortcut: Enter to save
            dialog.getRootPane().registerKeyboardAction(
                e -> saveResident(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
            );
            
            // Birthday field listeners for age calculation
            birthdayField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    calculateAgeFromBirthday();
                }
            });
            
            birthdayField.addActionListener(e -> calculateAgeFromBirthday());
        }
        
        // Method to calculate age from birthday
        private void calculateAgeFromBirthday() {
            String birthday = birthdayField.getText().trim();
            if (!birthday.isEmpty()) {
                try {
                    LocalDate birthDate = DateUtils.parseDisplay(birthday);
                    LocalDate currentDate = LocalDate.now();
                    int age = Period.between(birthDate, currentDate).getYears();
                    ageField.setText(String.valueOf(age));
                    
                    if (age < 0 || age > 150) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Please enter a valid birth date (age should be between 0 and 150)", 
                            "Invalid Age", JOptionPane.WARNING_MESSAGE);
                        ageField.setText("");
                    }
                } catch (DateTimeParseException ex) {
                    // Invalid date format, ignore
                }
            }
        }
        
        // Method to show add member dialog
        private void showAddMemberDialog() {
            JDialog memberDialog = new JDialog(dialog, "Add Household Member / Occupant", true);
            memberDialog.setSize(500, 500);
            memberDialog.setLocationRelativeTo(dialog);
            memberDialog.getContentPane().setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(3, 5, 3, 5);
            
            int row = 0;
            
            // Last name
            gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
            panel.add(new JLabel("Last Name*:"), gbc);
            gbc.gridx = 1;
            StyledTextField lastNameField = new StyledTextField(12);
            lastNameField.putClientProperty("JTextField.placeholderText", "Enter last name");
            panel.add(lastNameField, gbc);
            row++;
            
            // First name
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("First Name*:"), gbc);
            gbc.gridx = 1;
            StyledTextField firstNameField = new StyledTextField(12);
            firstNameField.putClientProperty("JTextField.placeholderText", "Enter first name");
            panel.add(firstNameField, gbc);
            row++;
            
            // Qualifier
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Qualifier:"), gbc);
            gbc.gridx = 1;
            StyledTextField qualifierField = new StyledTextField(8);
            qualifierField.putClientProperty("JTextField.placeholderText", "Jr., Sr., III");
            panel.add(qualifierField, gbc);
            row++;
            
            // Sex
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Sex*:"), gbc);
            gbc.gridx = 1;
            StyledComboBox<String> sexComboBox = new StyledComboBox<>(new String[]{"Male", "Female"});
            panel.add(sexComboBox, gbc);
            row++;
            
            // Relationship to head
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Relationship*:"), gbc);
            gbc.gridx = 1;
            String[] relationshipOptions = {
                "Son", "Daughter", "Father", "Mother", "Grandfather", "Grandmother",
                "Spouse", "Live-in Partner", "Brother", "Sister", "Grandson", "Granddaughter",
                "Nephew", "Niece", "Uncle", "Aunt", "Cousin", "Maid", "Boarder", "Other"
            };
            StyledComboBox<String> relationshipComboBox = new StyledComboBox<>(relationshipOptions);
            panel.add(relationshipComboBox, gbc);
            row++;
            
            // Birthday
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Birthday*:"), gbc);
            gbc.gridx = 1;
            StyledTextField birthdayField = new StyledTextField(12);
            birthdayField.putClientProperty("JTextField.placeholderText", "MM-DD-YYYY");
            panel.add(birthdayField, gbc);
            row++;
            
            // Age (auto-calculated)
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Age:"), gbc);
            gbc.gridx = 1;
            StyledTextField ageField = new StyledTextField(5);
            ageField.putClientProperty("JTextField.placeholderText", "Auto");
            ageField.setEditable(false);
            ageField.setBackground(BarangayColors.LIGHT_BACKGROUND);
            panel.add(ageField, gbc);
            row++;
            
            // Civil status
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Civil Status*:"), gbc);
            gbc.gridx = 1;
            StyledComboBox<String> civilStatusBox = new StyledComboBox<>(
                new String[]{"Single", "Married", "Widowed", "Separated", "Annulled"});
            panel.add(civilStatusBox, gbc);
            row++;
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(Color.WHITE);
            
            StyledButton addButton = new StyledButton("Add", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            addButton.setPreferredSize(new Dimension(100, 30));
            addButton.addActionListener(e -> {
                // Validate inputs
                String lastName = lastNameField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String qualifier = qualifierField.getText().trim();
                String sex = (String) sexComboBox.getSelectedItem();
                String relationship = (String) relationshipComboBox.getSelectedItem();
                String birthday = birthdayField.getText().trim();
                String civilStatus = (String) civilStatusBox.getSelectedItem();
                
                if (lastName.isEmpty() || firstName.isEmpty() || birthday.isEmpty()) {
                    JOptionPane.showMessageDialog(memberDialog, 
                        "Please fill in all required fields!", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    // Calculate age
                    LocalDate birthDate = DateUtils.parseDisplay(birthday);
                    LocalDate currentDate = LocalDate.now();
                    int age = Period.between(birthDate, currentDate).getYears();
                    
                    if (age < 0 || age > 150) {
                        JOptionPane.showMessageDialog(memberDialog, 
                            "Please enter a valid birth date", 
                            "Invalid Age", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Create member and add to list
                    HouseholdMember member = new HouseholdMember(
                        lastName, firstName, qualifier, age, birthday, 
                        civilStatus, sex, relationship
                    );
                    householdMembers.add(member);
                    memberTableModel.addRow(new Object[]{
                        lastName, firstName, qualifier, age, birthday, civilStatus, sex, relationship
                    });
                    memberDialog.dispose();
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(memberDialog, 
                        "Please enter a valid date in MM-DD-YYYY format!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            StyledButton cancelButton = new StyledButton("Cancel", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            cancelButton.setPreferredSize(new Dimension(100, 30));
            cancelButton.addActionListener(e -> memberDialog.dispose());
            
            buttonPanel.add(addButton);
            buttonPanel.add(cancelButton);
            
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);
            
            // Age calculation on birthday field
            birthdayField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String bday = birthdayField.getText().trim();
                    if (!bday.isEmpty()) {
                        try {
                            LocalDate birthDate = DateUtils.parseDisplay(bday);
                            LocalDate currentDate = LocalDate.now();
                            int age = Period.between(birthDate, currentDate).getYears();
                            ageField.setText(String.valueOf(age));
                        } catch (DateTimeParseException ex) {
                            // Ignore
                        }
                    }
                }
            });
            
            memberDialog.add(panel);
            memberDialog.setVisible(true);
        }
        
        // Method to remove selected member
        private void removeSelectedMember() {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please select a household member to remove!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            householdMembers.remove(selectedRow);
            memberTableModel.removeRow(selectedRow);
        }
        
        // Method to show dialog and return whether saved
        public boolean showDialog() {
            dialog.setVisible(true);
            return saved;
        }
        
        // Getter for created/edited resident
        public Resident getResident() {
            return resident;
        }
        
        // Method to save resident
        private void saveResident() {
            try {
                // Validate required fields
                if (firstNameField.getText().trim().isEmpty() ||
                    lastNameField.getText().trim().isEmpty() ||
                    birthdayField.getText().trim().isEmpty() ||
                    addressField.getText().trim().isEmpty() ||
                    contactField.getText().trim().isEmpty() ||
                    positionField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(dialog, 
                        "Please fill in all required fields (marked with *)!", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Parse form data
                int id = Integer.parseInt(idField.getText().trim());
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String qualifier = qualifierField.getText().trim();
                String birthday = birthdayField.getText().trim();
                String sex = (String) sexComboBox.getSelectedItem();
                String address = addressField.getText().trim();
                String contact = contactField.getText().trim();
                String position = positionField.getText().trim();
                String maritalStatus = (String) civilStatusComboBox.getSelectedItem();
                
                // Validate phone number
                if (!isValidPhoneNumber(contact)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Contact number must start with 09 or +63 and be 11-13 digits total!", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Validate employment
                String employment = (String) employmentComboBox.getSelectedItem();
                if (employment == null || employment.equals("Select employment status")) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please select an employment status!", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Handle religion
                String religion = (String) religionComboBox.getSelectedItem();
                if ("Other".equals(religion)) {
                    religion = religionOtherField.getText().trim();
                    if (religion.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Please specify religion in the text field!", 
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                // Handle income bracket
                String incomeSelection = (String) incomeComboBox.getSelectedItem();
                int incomeBracket = 0;
                if (incomeSelection != null && !incomeSelection.equals("Select income level")) {
                    if (incomeSelection.equals("Less than ₱20,000")) incomeBracket = 15000;
                    else if (incomeSelection.equals("₱20,000 - ₱40,000")) incomeBracket = 30000;
                    else if (incomeSelection.equals("₱40,001 - ₱60,000")) incomeBracket = 50000;
                    else if (incomeSelection.equals("₱60,001 - ₱80,000")) incomeBracket = 70000;
                    else if (incomeSelection.equals("₱80,001 - ₱100,000")) incomeBracket = 90000;
                    else if (incomeSelection.equals("More than ₱100,000")) incomeBracket = 120000;
                }
                
                // Calculate age from birthday
                LocalDate birthDate = DateUtils.parseDisplay(birthday);
                LocalDate currentDate = LocalDate.now();
                int age = Period.between(birthDate, currentDate).getYears();
                
                if (age < 0 || age > 150) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Please enter a valid birth date", 
                        "Invalid Age", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Create or update resident
                if (resident == null) {
                    // Create new resident
                    resident = new Resident(
                        id, firstName, mInitialField.getText().trim(), lastName, qualifier,
                        age, birthday, sex, medicalConditionField.getText().trim(),
                        incomeBracket,
                        motherTongueField.getText().trim(),
                        religion,
                        employment,
                        maritalStatus,
                        address,
                        position,
                        contact,
                        occupationField.getText().trim()
                    );
                    
                    // Add household members
                    for (HouseholdMember member : householdMembers) {
                        resident.addHouseholdMember(member);
                    }
                    
                } else {
                    // Update existing resident
                    resident.setFirstName(firstName);
                    resident.setMInitial(mInitialField.getText().trim());
                    resident.setLastName(lastName);
                    resident.setQualifier(qualifier);
                    resident.setAge(age);
                    resident.setBirthday(birthday);
                    resident.setSex(sex);
                    resident.setMedicalCondition(medicalConditionField.getText().trim());
                    resident.setIncomeBracket(incomeBracket);
                    resident.setMotherTongue(motherTongueField.getText().trim());
                    resident.setReligion(religion);
                    resident.setEmployment(employment);
                    resident.setMaritalStatus(maritalStatus);
                    resident.setAddress(address);
                    resident.setPosition(position);
                    resident.setContactNumber(contact);
                    resident.setOccupation(occupationField.getText().trim());
                    resident.setHouseholdMembers(new ArrayList<>(householdMembers));
                }
                
                saved = true;
                dialog.dispose();
                
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid date in MM-DD-YYYY format!", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter valid numbers for numeric fields!", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // Helper method to validate phone number
        private boolean isValidPhoneNumber(String phone) {
            if (phone == null || phone.trim().isEmpty()) return false;
            phone = phone.trim().replaceAll("[\\s-]", "");
            if (phone.matches("^09\\d{9}$")) return true;
            if (phone.matches("^\\+63\\d{10}$")) return true;
            return false;
        }
    }
    
    // ==================== RESIDENT DETAILS DIALOG (UPDATED) ====================
    // Dialog for viewing resident details (read-only)
    class ResidentDetailsDialog extends JDialog {
        private Resident resident;
        
        // Constructor
        public ResidentDetailsDialog(JFrame parent, Resident resident) {
            super(parent, "Resident Details", true);
            this.resident = resident;
            
            setSize(500, 650);
            setLocationRelativeTo(parent);
            setResizable(false);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Header
            JLabel titleLabel = new JLabel("Resident Information");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel nameLabel = new JLabel(resident.getFullName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setForeground(BarangayColors.TEXT_COLOR);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel idLabel = new JLabel("ID: " + String.format("%06d", resident.getResidentID()));
            idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            idLabel.setForeground(Color.GRAY);
            idLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JPanel headerPanel = new JPanel(new GridLayout(3, 1, 0, 2));
            headerPanel.setBackground(Color.WHITE);
            headerPanel.add(titleLabel);
            headerPanel.add(nameLabel);
            headerPanel.add(idLabel);
            
            // Details panel
            JPanel detailsPanel = new JPanel(new GridBagLayout());
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(2, 5, 2, 5);
            
            int row = 0;
            
            // Personal details
            addDetailRow(detailsPanel, gbc, row++, "First Name:", resident.getFirstName());
            addDetailRow(detailsPanel, gbc, row++, "Middle Initial:", resident.getMInitial());
            addDetailRow(detailsPanel, gbc, row++, "Last Name:", resident.getLastName());
            addDetailRow(detailsPanel, gbc, row++, "Qualifier:", resident.getQualifier());
            addDetailRow(detailsPanel, gbc, row++, "Age:", String.valueOf(resident.getAge()));
            addDetailRow(detailsPanel, gbc, row++, "Birthday:", resident.getBirthday());
            addDetailRow(detailsPanel, gbc, row++, "Sex:", resident.getSex());
            addDetailRow(detailsPanel, gbc, row++, "Civil Status:", resident.getMaritalStatus());
            addDetailRow(detailsPanel, gbc, row++, "Religion:", resident.getReligion());
            addDetailRow(detailsPanel, gbc, row++, "Address:", resident.getAddress());
            addDetailRow(detailsPanel, gbc, row++, "Contact:", resident.getContactNumber());
            addDetailRow(detailsPanel, gbc, row++, "Occupation:", resident.getOccupation());
            addDetailRow(detailsPanel, gbc, row++, "Position:", resident.getPosition());
            addDetailRow(detailsPanel, gbc, row++, "Household Head:", 
                resident.isHouseholdHead() ? "Yes" : "ID: " + String.format("%06d", resident.getHouseholdHeadID()));
            
            // Household members
            if (!resident.getHouseholdMembers().isEmpty()) {
                addDetailRow(detailsPanel, gbc, row++, "Other Occupants:", "");
                for (int i = 0; i < resident.getHouseholdMembers().size(); i++) {
                    HouseholdMember member = resident.getHouseholdMembers().get(i);
                    addDetailRow(detailsPanel, gbc, row++, "  " + (i + 1) + ".", 
                        member.getFullName() + " (Age: " + member.getAge() + ", " + 
                        member.getSex() + ", " + member.getRelationship() + ")");
                }
            }
            
            // System information
            addDetailRow(detailsPanel, gbc, row++, "Status:", resident.getStatus().toString());
            addDetailRow(detailsPanel, gbc, row++, "Registered:", 
                DateUtils.formatDisplay(resident.getCreatedAt()));
            
            JScrollPane scrollPane = new JScrollPane(detailsPanel);
            scrollPane.setBorder(null);
            
            // Close button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            
            StyledButton closeButton = new StyledButton("Close", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            closeButton.setPreferredSize(new Dimension(100, 32));
            closeButton.addActionListener(e -> dispose());
            
            buttonPanel.add(closeButton);
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
        
        // Helper method to add detail row
        private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(BarangayColors.TEXT_COLOR);
            panel.add(lbl, gbc);
            
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            
            JTextField field = new JTextField(value != null ? value : "");
            field.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            field.setEditable(false);
            field.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
            field.setBackground(BarangayColors.LIGHT_BACKGROUND);
            field.setPreferredSize(new Dimension(250, 22));
            panel.add(field, gbc);
        }
    }
    
    // ==================== REPORTS PANEL (UPDATED) ====================
    // Panel for generating reports from residents_secure.dat master file
    class ReportsPanel extends JPanel {
        private User reportUser;                     // Current user
        private List<Resident> reportResidents;      // Residents list (from master file)
        
        // Constructor
        public ReportsPanel(User user, List<Resident> residents, List<ArchiveRecord> archive) {
            this.reportUser = user;
            this.reportResidents = residents;
            
            setLayout(new BorderLayout());
            setBackground(BarangayColors.LIGHT_BACKGROUND);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel titleLabel = new JLabel("Barangay Reports & Analytics");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.add(titleLabel, BorderLayout.WEST);
            
            JLabel dateLabel = new JLabel("As of: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dateLabel.setForeground(Color.GRAY);
            headerPanel.add(dateLabel, BorderLayout.EAST);
            
            // Report cards grid
            JPanel reportsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
            reportsGrid.setBackground(Color.WHITE);
            reportsGrid.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
            
            reportsGrid.add(createReportCard("Demographic Summary", 
                "Overview of barangay population statistics including average age and sex percentages", new Color(76, 175, 80), 
                e -> showDemographicSummary()));
            reportsGrid.add(createReportCard("Sex Distribution", 
                "Breakdown by male and female (including all household members)", new Color(33, 150, 243),
                e -> showSexSummary()));
            reportsGrid.add(createReportCard("Age Group Analysis", 
                "Distribution across age categories (including all household members)", new Color(255, 152, 0),
                e -> showAgeGroupSummary()));
            reportsGrid.add(createReportCard("Household Summary", 
                "Number of households and population", new Color(156, 39, 176),
                e -> showHouseholdSummary()));
            
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(reportsGrid, BorderLayout.CENTER);
            
            add(mainPanel, BorderLayout.CENTER);
        }
        
        // Method to create report card with hover effect
        private JPanel createReportCard(String title, String description, Color accentColor, ActionListener listener) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Mouse hover effects
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(BarangayColors.LIGHT_BACKGROUND);
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(accentColor, 2),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                    ));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(Color.WHITE);
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BarangayColors.BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                    ));
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                }
            });
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(BarangayColors.TEXT_COLOR);
            
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descLabel.setForeground(Color.GRAY);
            
            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setBackground(card.getBackground());
            textPanel.add(titleLabel);
            textPanel.add(descLabel);
            
            JLabel iconLabel = new JLabel(">");
            iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            iconLabel.setForeground(accentColor);
            
            card.add(textPanel, BorderLayout.CENTER);
            card.add(iconLabel, BorderLayout.EAST);
            
            return card;
        }
        
        // Method to show demographic summary
        private void showDemographicSummary() {
            int totalResidents = 0;
            int totalPopulation = 0;
            int households = 0;
            int totalAgeSum = 0;
            int maleCount = 0;
            int femaleCount = 0;
            
            // Calculate from residents_secure.dat data
            for (Resident r : reportResidents) {
                if (r.getStatus() == Resident.ResidentStatus.ACTIVE) {
                    totalResidents++;
                    totalPopulation += r.getTotalPopulation();
                    if (r.isHouseholdHead()) households++;
                    
                    // For average age calculation
                    totalAgeSum += r.getAge();
                    
                    // For sex percentage
                    if (r.getSex() != null) {
                        if (r.getSex().equalsIgnoreCase("male")) maleCount++;
                        else if (r.getSex().equalsIgnoreCase("female")) femaleCount++;
                    }
                    
                    // Add household members to counts
                    for (HouseholdMember member : r.getHouseholdMembers()) {
                        totalAgeSum += member.getAge();
                        if (member.getSex() != null) {
                            if (member.getSex().equalsIgnoreCase("male")) maleCount++;
                            else if (member.getSex().equalsIgnoreCase("female")) femaleCount++;
                        }
                    }
                }
            }
            
            double avgAge = totalPopulation > 0 ? (double) totalAgeSum / totalPopulation : 0;
            double malePct = totalPopulation > 0 ? (maleCount * 100.0 / totalPopulation) : 0;
            double femalePct = totalPopulation > 0 ? (femaleCount * 100.0 / totalPopulation) : 0;
            
            String message = String.format(
                "<html><div style='width: 450px;'>" +
                "<h2 style='color: #2F5D8A;'>Barangay Demographic Summary</h2>" +
                "<hr>" +
                "<table style='width: 100%%; border-collapse: collapse;'>" +
                "<tr><td style='padding: 8px;'><b>Total Registered Residents (Heads):</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 8px;'><b>Total Population (including household members):</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr><td style='padding: 8px;'><b>Total Households:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 8px;'><b>Average Age:</b></td><td style='padding: 8px;'>%.1f years</td></tr>" +
                "<tr><td style='padding: 8px;'><b>Male Percentage:</b></td><td style='padding: 8px;'>%.1f%% (%d individuals)</td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 8px;'><b>Female Percentage:</b></td><td style='padding: 8px;'>%.1f%% (%d individuals)</td></tr>" +
                "<tr><td style='padding: 8px;'><b>Report Date:</b></td><td style='padding: 8px;'>%s</td></tr>" +
                "</table>" +
                "</div></html>",
                totalResidents, totalPopulation, households, avgAge, malePct, maleCount, femalePct, femaleCount,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            
            JOptionPane.showMessageDialog(this, message, 
                "Demographic Summary", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Method to show sex distribution
        private void showSexSummary() {
            int male = 0, female = 0;
            
            // Count including household members
            for (Resident r : reportResidents) {
                if (r.getStatus() == Resident.ResidentStatus.ACTIVE) {
                    if (r.getSex() != null) {
                        if (r.getSex().equalsIgnoreCase("male")) male++;
                        else if (r.getSex().equalsIgnoreCase("female")) female++;
                    }
                    
                    for (HouseholdMember member : r.getHouseholdMembers()) {
                        if (member.getSex() != null) {
                            if (member.getSex().equalsIgnoreCase("male")) male++;
                            else if (member.getSex().equalsIgnoreCase("female")) female++;
                        }
                    }
                }
            }
            
            int total = male + female;
            double malePct = total > 0 ? (male * 100.0 / total) : 0;
            double femalePct = total > 0 ? (female * 100.0 / total) : 0;
            
            String message = String.format(
                "<html><div style='width: 450px;'>" +
                "<h2 style='color: #2F5D8A;'>Barangay Sex Distribution</h2>" +
                "<p><i>Note: Includes all household members with sex data.</i></p>" +
                "<hr>" +
                "<table style='width: 100%%; border-collapse: collapse;'>" +
                "<tr style='background-color: #e3f2fd;'><td style='padding: 10px;'><b>Male:</b></td>" +
                "<td style='padding: 10px;'>%d individuals</td>" +
                "<td style='padding: 10px;'><b>%.1f%%</b></td></tr>" +
                "<tr style='background-color: #fce4ec;'><td style='padding: 10px;'><b>Female:</b></td>" +
                "<td style='padding: 10px;'>%d individuals</td>" +
                "<td style='padding: 10px;'><b>%.1f%%</b></td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 10px;'><b>Total Population:</b></td>" +
                "<td style='padding: 10px;' colspan='2'><b>%d individuals</b></td></tr>" +
                "</table>" +
                "</div></html>",
                male, malePct, female, femalePct, total
            );
            
            JOptionPane.showMessageDialog(this, message, 
                "Sex Distribution Report", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Method to show age group analysis
        private void showAgeGroupSummary() {
            Map<String, Integer> ageGroups = new LinkedHashMap<>();
            ageGroups.put("Children (0-12)", 0);
            ageGroups.put("Teens (13-19)", 0);
            ageGroups.put("Young Adults (20-35)", 0);
            ageGroups.put("Adults (36-59)", 0);
            ageGroups.put("Seniors (60+)", 0);
            
            // Count including household members
            for (Resident r : reportResidents) {
                if (r.getStatus() == Resident.ResidentStatus.ACTIVE) {
                    int age = r.getAge();
                    categorizeAge(ageGroups, age);
                    
                    for (HouseholdMember member : r.getHouseholdMembers()) {
                        categorizeAge(ageGroups, member.getAge());
                    }
                }
            }
            
            int total = 0;
            for (int count : ageGroups.values()) {
                total += count;
            }
            
            StringBuilder rows = new StringBuilder();
            String[] colors = {"#e8f5e9", "#f1f8e9", "#fff3e0", "#ffecb3", "#ffccbc"};
            int i = 0;
            
            for (Map.Entry<String, Integer> entry : ageGroups.entrySet()) {
                double percentage = total > 0 ? (entry.getValue() * 100.0 / total) : 0;
                rows.append(String.format(
                    "<tr style='background-color: %s;'><td style='padding: 10px;'><b>%s</b></td>" +
                    "<td style='padding: 10px;'>%d individuals</td>" +
                    "<td style='padding: 10px;'><b>%.1f%%</b></td></tr>",
                    colors[i % colors.length], entry.getKey(), entry.getValue(), percentage
                ));
                i++;
            }
            
            String message = String.format(
                "<html><div style='width: 500px;'>" +
                "<h2 style='color: #2F5D8A;'>Barangay Age Group Distribution</h2>" +
                "<p><i>Note: Includes all household members.</i></p>" +
                "<hr>" +
                "<table style='width: 100%%; border-collapse: collapse;'>%s" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 10px;'><b>Total Population:</b></td>" +
                "<td style='padding: 10px;' colspan='2'><b>%d individuals</b></td></tr>" +
                "</table>" +
                "</div></html>",
                rows.toString(), total
            );
            
            JOptionPane.showMessageDialog(this, message, 
                "Age Group Analysis", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Helper method to categorize age
        private void categorizeAge(Map<String, Integer> ageGroups, int age) {
            if (age <= 12) ageGroups.put("Children (0-12)", ageGroups.get("Children (0-12)") + 1);
            else if (age <= 19) ageGroups.put("Teens (13-19)", ageGroups.get("Teens (13-19)") + 1);
            else if (age <= 35) ageGroups.put("Young Adults (20-35)", ageGroups.get("Young Adults (20-35)") + 1);
            else if (age <= 59) ageGroups.put("Adults (36-59)", ageGroups.get("Adults (36-59)") + 1);
            else ageGroups.put("Seniors (60+)", ageGroups.get("Seniors (60+)") + 1);
        }
        
        // Method to show household summary
        private void showHouseholdSummary() {
            int households = 0;
            int totalPopulation = 0;
            
            for (Resident r : reportResidents) {
                if (r.isHouseholdHead() && r.getStatus() == Resident.ResidentStatus.ACTIVE) {
                    households++;
                    totalPopulation += r.getHouseholdSize();
                }
            }
            
            String message = String.format(
                "<html><div style='width: 450px;'>" +
                "<h2 style='color: #2F5D8A;'>Barangay Household Summary</h2>" +
                "<hr>" +
                "<table style='width: 100%%; border-collapse: collapse;'>" +
                "<tr><td style='padding: 8px;'><b>Total Households:</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr style='background-color: #f5f5f5;'><td style='padding: 8px;'><b>Total Population (including household members):</b></td><td style='padding: 8px;'>%d</td></tr>" +
                "<tr><td style='padding: 8px;'><b>Report Date:</b></td><td style='padding: 8px;'>%s</td></tr>" +
                "</table>" +
                "</div></html>",
                households, totalPopulation,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            
            JOptionPane.showMessageDialog(this, message, 
                "Household Summary Report", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // ==================== USER MANAGEMENT PANEL ====================
    // Panel for managing users (using users_secure.dat master file)
    class UserManagementPanel extends JPanel {
        private User panelUser;                       // Current user
        private List<User> panelUsers;                 // Users list (from master file)
        private StyledTable usersTable;                 // Table for displaying users
        private DefaultTableModel tableModel;           // Table model
        
        // Constructor
        public UserManagementPanel(User user, List<User> users) {
            this.panelUser = user;
            this.panelUsers = users;
            
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Header
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            
            JLabel titleLabel = new JLabel("User Management");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            
            JLabel subtitleLabel = new JLabel("Manage system users and permissions");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            subtitleLabel.setForeground(Color.GRAY);
            
            JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
            titlePanel.setBackground(Color.WHITE);
            titlePanel.add(titleLabel);
            titlePanel.add(subtitleLabel);
            
            // Toolbar
            JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            toolbar.setBackground(Color.WHITE);
            
            StyledButton removeButton = new StyledButton("Remove User", 
                new Color(220, 53, 69), Color.WHITE);
            removeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            removeButton.addActionListener(e -> removeSelectedUser());
            
            StyledButton toggleButton = new StyledButton("Toggle Status", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            toggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            toggleButton.addActionListener(e -> toggleUserStatus());
            
            toolbar.add(toggleButton);
            toolbar.add(removeButton);
            
            headerPanel.add(titlePanel, BorderLayout.WEST);
            headerPanel.add(toolbar, BorderLayout.EAST);
            
            // Table setup
            String[] columns = {"Username", "Role", "Email", "Phone", "Resident ID", "Status"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            usersTable = new StyledTable(tableModel);
            usersTable.setRowHeight(28);
            usersTable.getColumnModel().getColumn(0).setPreferredWidth(120);
            usersTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            usersTable.getColumnModel().getColumn(2).setPreferredWidth(180);
            usersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            usersTable.getColumnModel().getColumn(4).setPreferredWidth(80);
            usersTable.getColumnModel().getColumn(5).setPreferredWidth(80);
            
            JScrollPane scrollPane = new JScrollPane(usersTable);
            scrollPane.setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
            
            add(headerPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            
            loadUsersData(); // Load data from users_secure.dat
        }
        
        // Method to load users data into table
        private void loadUsersData() {
            tableModel.setRowCount(0);
            for (User user : panelUsers) {
                String status = user.isActive() ? "Active" : "Inactive";
                String residentId = "";
                
                if (user instanceof Staff) {
                    residentId = String.valueOf(((Staff) user).getResidentID());
                } else if (user instanceof ResidentUser) {
                    residentId = String.valueOf(((ResidentUser) user).getResidentID());
                } else {
                    residentId = "N/A";
                }
                
                tableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getRole(),
                    user.getEmail(),
                    user.getPhone(),
                    residentId,
                    status
                });
            }
        }
        
        // Method to remove selected user
        private void removeSelectedUser() {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a user to remove!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = usersTable.convertRowIndexToModel(selectedRow);
            String username = (String) tableModel.getValueAt(modelRow, 0);
            
            // Prevent removing own account
            if (username.equals(panelUser.getUsername())) {
                JOptionPane.showMessageDialog(this, 
                    "You cannot remove your own account!", 
                    "Operation Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Prevent removing default admin
            if (username.equals("admin")) {
                JOptionPane.showMessageDialog(this, 
                    "The default admin account cannot be removed!", 
                    "Operation Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Are you sure you want to remove user:<br><b>" + username + "</b>?</html>",
                "Confirm Removal", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                panelUsers.removeIf(u -> u.getUsername().equals(username));
                SecureFileHandler.saveUsers(panelUsers); // Save to users_secure.dat
                SecureFileHandler.logActivity(panelUser.getUsername(), "USER_REMOVED: " + username); // Log to system_logs.dat
                loadUsersData();
                JOptionPane.showMessageDialog(this, 
                    "User removed successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        // Method to toggle user status (active/inactive)
        private void toggleUserStatus() {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a user!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = usersTable.convertRowIndexToModel(selectedRow);
            String username = (String) tableModel.getValueAt(modelRow, 0);
            
            // Prevent changing own status
            if (username.equals(panelUser.getUsername())) {
                JOptionPane.showMessageDialog(this, 
                    "You cannot change your own account status!", 
                    "Operation Denied", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            for (User user : panelUsers) {
                if (user.getUsername().equals(username)) {
                    String newStatus = user.isActive() ? "deactivate" : "activate";
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to " + newStatus + " user: " + username + "?",
                        "Confirm Status Change", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        user.setActive(!user.isActive());
                        SecureFileHandler.saveUsers(panelUsers); // Save to users_secure.dat
                        SecureFileHandler.logActivity(panelUser.getUsername(), 
                            "USER_STATUS_CHANGED: " + username); // Log to system_logs.dat
                        loadUsersData();
                        
                        String message = user.isActive() ? 
                            "User activated successfully!" : "User deactivated successfully!";
                        JOptionPane.showMessageDialog(this, message, 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                }
            }
        }
    }
    
    // ==================== ACCOUNT SETTINGS PANEL ====================
    // Panel for viewing and changing account settings
    class AccountSettingsPanel extends JPanel {
        private User settingsUser;                   // Current user
        private List<User> allUsers;                  // All users list (from master file)
        private List<ArchiveRecord> allArchive;        // Archive list for reference
        
        // Constructor
        public AccountSettingsPanel(User user, List<User> users, List<ArchiveRecord> archive) {
            this.settingsUser = user;
            this.allUsers = users;
            this.allArchive = archive;
            
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel("Account Settings");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            
            // User information card
            JPanel userCard = new JPanel(new BorderLayout());
            userCard.setBackground(Color.WHITE);
            userCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BarangayColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            
            JPanel userInfoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
            userInfoPanel.setBackground(Color.WHITE);
            
            addUserInfoRow(userInfoPanel, "Username:", settingsUser.getUsername());
            addUserInfoRow(userInfoPanel, "Role:", settingsUser.getRole());
            addUserInfoRow(userInfoPanel, "Email:", settingsUser.getEmail());
            addUserInfoRow(userInfoPanel, "Phone:", settingsUser.getPhone());
            
            if (settingsUser instanceof Staff) {
                addUserInfoRow(userInfoPanel, "Resident ID:", String.valueOf(((Staff) settingsUser).getResidentID()));
            } else if (settingsUser instanceof ResidentUser) {
                addUserInfoRow(userInfoPanel, "Resident ID:", String.valueOf(((ResidentUser) settingsUser).getResidentID()));
            }
            
            addUserInfoRow(userInfoPanel, "Status:", settingsUser.isActive() ? "Active" : "Inactive");
            
            userCard.add(userInfoPanel, BorderLayout.CENTER);
            
            // Action buttons
            JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
            actionsPanel.setBackground(Color.WHITE);
            actionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            
            StyledButton changePassBtn = new StyledButton("Change Password", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            changePassBtn.setPreferredSize(new Dimension(200, 35));
            changePassBtn.addActionListener(e -> changePassword());
            
            StyledButton viewLogsBtn = new StyledButton("View Login History", 
                BarangayColors.BUTTON_BLACK, Color.WHITE);
            viewLogsBtn.setPreferredSize(new Dimension(200, 35));
            viewLogsBtn.addActionListener(e -> viewLoginHistory());
            
            actionsPanel.add(changePassBtn);
            actionsPanel.add(viewLogsBtn);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(userCard, BorderLayout.CENTER);
            mainPanel.add(actionsPanel, BorderLayout.SOUTH);
            
            add(mainPanel, BorderLayout.CENTER);
        }
        
        // Helper method to add user info row
        private void addUserInfoRow(JPanel panel, String label, String value) {
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(BarangayColors.TEXT_COLOR);
            panel.add(lbl);
            
            JTextField field = new JTextField(value != null ? value : "");
            field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            field.setEditable(false);
            field.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            field.setBackground(BarangayColors.LIGHT_BACKGROUND);
            panel.add(field);
        }
        
        // Method to change password
        private void changePassword() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(3, 5, 3, 5);
            
            // Current password
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Current Password:"), gbc);
            gbc.gridx = 1;
            JPasswordField currentPass = new StyledPasswordField(15);
            currentPass.putClientProperty("JTextField.placeholderText", "Enter current password");
            panel.add(currentPass, gbc);
            
            // New password
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("New Password:"), gbc);
            gbc.gridx = 1;
            JPasswordField newPass = new StyledPasswordField(15);
            newPass.putClientProperty("JTextField.placeholderText", "6+ characters");
            panel.add(newPass, gbc);
            
            // Confirm password
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Confirm Password:"), gbc);
            gbc.gridx = 1;
            JPasswordField confirmPass = new StyledPasswordField(15);
            confirmPass.putClientProperty("JTextField.placeholderText", "Confirm new password");
            panel.add(confirmPass, gbc);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                String current = new String(currentPass.getPassword());
                String newP = new String(newPass.getPassword());
                String confirm = new String(confirmPass.getPassword());
                
                // Verify current password
                if (!settingsUser.verifyPassword(current)) {
                    JOptionPane.showMessageDialog(this, 
                        "Current password is incorrect!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate new password
                if (newP.length() < 6) {
                    JOptionPane.showMessageDialog(this, 
                        "New password must be at least 6 characters long!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check password confirmation
                if (!newP.equals(confirm)) {
                    JOptionPane.showMessageDialog(this, 
                        "New passwords don't match!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update password
                settingsUser.encryptedPassword = settingsUser.encryptPassword(newP);
                
                // Update in users list
                for (int i = 0; i < allUsers.size(); i++) {
                    if (allUsers.get(i).getUsername().equals(settingsUser.getUsername())) {
                        allUsers.set(i, settingsUser);
                        break;
                    }
                }
                
                // Save to users_secure.dat
                SecureFileHandler.saveUsers(allUsers);
                SecureFileHandler.logActivity(settingsUser.getUsername(), "PASSWORD_CHANGED"); // Log to system_logs.dat
                
                JOptionPane.showMessageDialog(this, 
                    "Password changed successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        // Method to view login history from system_logs.dat
        private void viewLoginHistory() {
            List<String> logs;
            
            // Admin can view all logs, others only their own
            if (settingsUser instanceof Admin) {
                logs = SecureFileHandler.getAllLoginHistory();
            } else {
                logs = SecureFileHandler.getLoginHistory(settingsUser.getUsername());
            }
            
            if (logs.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No login history found.", 
                    "Login History", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JDialog historyDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Login History", true);
            historyDialog.setSize(600, 400);
            historyDialog.setLocationRelativeTo(this);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            String title = settingsUser instanceof Admin ? 
                "All Users Login History" : "Login History for " + settingsUser.getUsername();
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            titleLabel.setForeground(BarangayColors.PRIMARY_BLUE);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            
            // Table for logs
            String[] columns = {"Timestamp", "Username", "Action"};
            DefaultTableModel logModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Add logs in reverse chronological order
            for (int i = logs.size() - 1; i >= 0; i--) {
                String log = logs.get(i);
                String[] parts = log.split("\\|");
                if (parts.length >= 3) {
                    logModel.addRow(new Object[]{parts[0], parts[1], parts[2]});
                }
            }
            
            JTable logTable = new StyledTable(logModel);
            logTable.setRowHeight(25);
            logTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            logTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            logTable.getColumnModel().getColumn(2).setPreferredWidth(300);
            JScrollPane scrollPane = new JScrollPane(logTable);
            scrollPane.setBorder(new LineBorder(BarangayColors.BORDER_COLOR, 1));
            
            // Close button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            StyledButton closeButton = new StyledButton("Close", 
                BarangayColors.PRIMARY_BLUE, Color.WHITE);
            closeButton.setPreferredSize(new Dimension(100, 30));
            closeButton.addActionListener(e -> historyDialog.dispose());
            buttonPanel.add(closeButton);
            
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            historyDialog.add(mainPanel);
            historyDialog.setVisible(true);
        }
    }
    
    // ==================== MAIN METHOD ====================
    // Entry point of the application
    public static void main(String[] args) {
        // Run on Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BarangayResidentsSystem();
        });
    }
}
