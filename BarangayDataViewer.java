import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/*Simple GUI Decryptor for Barangay Records System with Buttons*/
public class BarangayDataViewer extends JFrame {
    
    private static final String ENCRYPTION_KEY = "BARANGAY_SECURE_2024";
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private JLabel statusLabel;
    
    public BarangayDataViewer() {
        setTitle("Barangay Data Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ========== TOP PANEL WITH BUTTONS ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("File Operations"));
        
        // Create buttons
        JButton btnResidents = createButton("View Residents", "residents_secure.dat");
        JButton btnArchive = createButton("View Archive", "archive_records.dat");
        JButton btnUsers = createButton("View Users", "users_secure.dat");
        JButton btnLogs = createButton("View Logs", "system_logs.dat");
        JButton btnCounter = createButton("View Counter", "id_counter.dat");
        JButton btnBrowse = new JButton("Browse Other File");
        JButton btnClear = new JButton("Clear");
        JButton btnExport = new JButton("Export All");
        
        // Style buttons
        btnBrowse.setBackground(new Color(70, 130, 180));
        btnBrowse.setForeground(Color.BLACK);
        btnBrowse.setFocusPainted(false);
        
        btnClear.setBackground(new Color(100, 100, 100));
        btnClear.setForeground(Color.BLACK);
        btnClear.setFocusPainted(false);
        
        btnExport.setBackground(new Color(46, 125, 50));
        btnExport.setForeground(Color.BLACK);
        btnExport.setFocusPainted(false);
        
        // Add action listeners
        btnBrowse.addActionListener(e -> browseFile());
        btnClear.addActionListener(e -> textArea.setText(""));
        btnExport.addActionListener(e -> exportAllData());
        
        // Add buttons to panel
        buttonPanel.add(btnResidents);
        buttonPanel.add(btnArchive);
        buttonPanel.add(btnUsers);
        buttonPanel.add(btnLogs);
        buttonPanel.add(btnCounter);
        buttonPanel.add(btnBrowse);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExport);
        
        // ========== TEXT AREA ==========
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Decrypted Data"));
        
        // ========== STATUS BAR ==========
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel = new JLabel(" Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        // ========== ADD TO MAIN PANEL ==========
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Initialize file chooser
        fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter("DAT files", "dat"));
    }
    
    private JButton createButton(String text, String filename) {
        JButton button = new JButton(text);
        button.setBackground(new Color(60, 63, 65));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.addActionListener(e -> openSpecificFile(filename));
        return button;
    }
    
    private void openSpecificFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            textArea.setText("");
            textArea.append("[ERROR] File not found: " + filename + "\n");
            textArea.append("\nMake sure the file is in the current directory:\n");
            textArea.append("Current directory: " + new File(".").getAbsolutePath());
            statusLabel.setText(" File not found: " + filename);
            return;
        }
        
        textArea.setText("");
        textArea.append("[FILE] " + file.getName() + "\n");
        textArea.append("[SIZE] " + file.length() + " bytes\n");
        textArea.append("=".repeat(80) + "\n\n");
        
        try {
            if (filename.equals("id_counter.dat")) {
                readCounterFile(file);
            } else {
                readEncryptedFile(file);
            }
            statusLabel.setText(" Loaded: " + filename);
        } catch (Exception ex) {
            textArea.append("\n[ERROR] " + ex.getMessage() + "\n");
            statusLabel.setText(" Error loading: " + filename);
        }
    }
    
    private void browseFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            openSpecificFile(file.getName());
        }
    }
    
    private void readEncryptedFile(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                textArea.append("[RECORDS] Total: " + list.size() + "\n\n");
                
                for (int i = 0; i < list.size(); i++) {
                    textArea.append("--- Record #" + (i + 1) + " ---\n");
                    if (list.get(i) instanceof String) {
                        String decrypted = decryptData((String) list.get(i));
                        
                        // Format the output for better readability
                        String[] lines = decrypted.split("\\|");
                        for (int j = 0; j < lines.length; j++) {
                            textArea.append(String.format("  [%02d] %s\n", j, lines[j]));
                        }
                        textArea.append("\n");
                    }
                }
            } else {
                textArea.append("[WARNING] Unknown data format\n");
            }
            
        } catch (Exception e) {
            textArea.append("\n[ERROR] Reading file: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
    
    private void readCounterFile(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            int counter = ois.readInt();
            textArea.append("[COUNTER DATA]\n");
            textArea.append("   Next Resident ID: " + counter + "\n");
            textArea.append("   Formatted: " + String.format("%06d", counter) + "\n");
            textArea.append("\n[NOTE] This counter represents the NEXT resident ID to be assigned.\n");
        } catch (IOException e) {
            textArea.append("\n[ERROR] Reading counter file: " + e.getMessage() + "\n");
        }
    }
    
    private void exportAllData() {
        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String exportFile = "barangay_data_export_" + timestamp + ".txt";
        
        textArea.setText("");
        textArea.append("[EXPORTING ALL DATA TO: " + exportFile + "]\n");
        textArea.append("=".repeat(80) + "\n\n");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(exportFile))) {
            writer.println("=".repeat(80));
            writer.println("BARANGAY RECORDS SYSTEM - DATA EXPORT");
            writer.println("Generated: " + java.time.LocalDateTime.now());
            writer.println("=".repeat(80));
            writer.println();
            
            // Export each file
            exportFileToWriter(writer, "residents_secure.dat", "RESIDENTS DATA");
            exportFileToWriter(writer, "archive_records.dat", "ARCHIVE DATA");
            exportFileToWriter(writer, "users_secure.dat", "USERS DATA");
            exportFileToWriter(writer, "system_logs.dat", "SYSTEM LOGS");
            
            textArea.append("\n[SUCCESS] Export completed!\n");
            textArea.append("[FILE] " + exportFile + "\n");
            statusLabel.setText(" Exported to: " + exportFile);
            
        } catch (IOException e) {
            textArea.append("\n[ERROR] Export failed: " + e.getMessage() + "\n");
        }
    }
    
    private void exportFileToWriter(PrintWriter writer, String filename, String title) {
        writer.println("=".repeat(80));
        writer.println(title);
        writer.println("=".repeat(80));
        writer.println();
        
        File file = new File(filename);
        if (!file.exists()) {
            writer.println("[FILE NOT FOUND: " + filename + "]");
            writer.println();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                writer.println("Total Records: " + list.size());
                writer.println();
                
                for (int i = 0; i < list.size(); i++) {
                    writer.println("--- Record #" + (i + 1) + " ---");
                    if (list.get(i) instanceof String) {
                        String decrypted = decryptData((String) list.get(i));
                        writer.println(decrypted);
                    }
                    writer.println();
                }
            }
        } catch (Exception e) {
            writer.println("[ERROR READING FILE: " + e.getMessage() + "]");
        }
        
        writer.println();
    }
    
    private String decryptData(String encryptedData) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < encryptedData.length(); i++) {
            decrypted.append((char) (encryptedData.charAt(i) ^ ENCRYPTION_KEY.charAt(i % ENCRYPTION_KEY.length())));
        }
        return decrypted.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BarangayDataViewer().setVisible(true);
        });
    }
}