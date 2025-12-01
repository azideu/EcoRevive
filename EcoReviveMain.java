package EcoRevive;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

public class EcoReviveMain extends JFrame {
    private RecyclingManager manager;
    private FileService fileService;
    
    // Components
    private static final Dimension STANDARD_BUTTON_SIZE = new Dimension(160, 40);
    private ModernTextField nameField, weightField;
    private JComboBox<String> categoryBox, conditionBox;
    private JTextArea logArea;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JLabel pendingCountLabel;
    private DashboardPanel dashboardPanel;
    private ModernTextField searchField;

    public EcoReviveMain() {
        applyDarkTheme();
        manager = new RecyclingManager();
        fileService = new FileService();
        
        // Load data
        manager.setRecycledItems(fileService.loadInventory());

        setTitle("EcoRevive: Smart E-Waste Recycling");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set main frame background
        getContentPane().setBackground(new Color(18, 18, 18));

        // Save on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAndExit();
            }
        });

        initComponents();
        updateInventoryTable();
        updatePendingCount();
    }

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. Navigation Bar (Centered)
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        navPanel.setOpaque(false); // Use parent background

        ButtonGroup tabGroup = new ButtonGroup();
        JToggleButton submitTab = createTabButton("Submit Item");
        JToggleButton processTab = createTabButton("Process Queue");
        JToggleButton inventoryTab = createTabButton("Inventory");
        JToggleButton statsTab = createTabButton("Statistics");

        tabGroup.add(submitTab);
        tabGroup.add(processTab);
        tabGroup.add(inventoryTab);
        tabGroup.add(statsTab);

        navPanel.add(submitTab);
        navPanel.add(processTab);
        navPanel.add(inventoryTab);
        navPanel.add(statsTab);

        add(navPanel, BorderLayout.NORTH);

        // 2. Content Panel (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        contentPanel.add(createSubmitPanel(), "Submit Item");
        contentPanel.add(createProcessPanel(), "Process Queue");
        contentPanel.add(createInventoryPanel(), "Inventory");
        contentPanel.add(createStatsPanel(), "Statistics");

        add(contentPanel, BorderLayout.CENTER);

        // 3. Action Listeners
        submitTab.addActionListener(e -> cardLayout.show(contentPanel, "Submit Item"));
        processTab.addActionListener(e -> cardLayout.show(contentPanel, "Process Queue"));
        inventoryTab.addActionListener(e -> cardLayout.show(contentPanel, "Inventory"));
        statsTab.addActionListener(e -> {
            cardLayout.show(contentPanel, "Statistics");
            updateStats();
        });

        // Select first tab by default
        submitTab.setSelected(true);
    }

    private JToggleButton createTabButton(String text) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        // Styling will be handled by LookAndFeel, but we can add padding
        btn.setMargin(new Insets(6, 12, 6, 12));
        btn.setPreferredSize(STANDARD_BUTTON_SIZE);
        return btn;
    }

    private JPanel createSubmitPanel() {
        // Use ModernPanel for the card look
        ModernPanel card = new ModernPanel(new GridBagLayout());
        
        // Wrapper panel to center the card
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        nameField = new ModernTextField(20);
        
        String[] categories = {"Mobile", "Laptop", "Tablet", "TV", "Appliance", "Other"};
        categoryBox = new JComboBox<>(categories);

        weightField = new ModernTextField(20);
        String[] conditions = {"New", "Used", "Broken", "Refurbished"};
        conditionBox = new JComboBox<>(conditions);

        // Add components to card
        addLabelAndComponent(card, "Name:", nameField, gbc, 0);
        addLabelAndComponent(card, "Category:", categoryBox, gbc, 1);
        addLabelAndComponent(card, "Weight (kg):", weightField, gbc, 2);
        addLabelAndComponent(card, "Condition:", conditionBox, gbc, 3);

        ModernButton submitBtn = new ModernButton("Submit Item");
        submitBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        submitBtn.addActionListener(e -> submitItem());
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.insets = new Insets(25, 10, 10, 10); // Add top spacing
        card.add(submitBtn, gbc);

        return wrapper;
    }

    private void addLabelAndComponent(JPanel panel, String labelText, JComponent comp, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(comp, gbc);
    }

    private JPanel createProcessPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        pendingCountLabel = new JLabel("Pending Items: 0");
        pendingCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        ModernButton processBtn = new ModernButton("Process Pending");
        processBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        processBtn.addActionListener(e -> processQueue());
        
        topPanel.add(pendingCountLabel);
        topPanel.add(processBtn);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchField = new ModernTextField(20);
        
        ModernButton searchBtn = new ModernButton("Search");
        searchBtn.addActionListener(e -> searchItems());
        
        ModernButton clearSearchBtn = new ModernButton("Clear");
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            updateInventoryTable();
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearSearchBtn);
        
        // Sort Panel
        String[] sortOptions = {"Sort By...", "Weight (Low-High)", "Weight (High-Low)", "Category", "Name", "Condition"};
        JComboBox<String> sortBox = new JComboBox<>(sortOptions);
        sortBox.addActionListener(e -> sortItems((String) sortBox.getSelectedItem()));
        searchPanel.add(sortBox);

        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Category", "Weight", "Condition"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        
        panel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        
        ModernButton deleteBtn = new ModernButton("Delete Selected");
        deleteBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        deleteBtn.addActionListener(e -> deleteSelectedItem());
        
        ModernButton editBtn = new ModernButton("Edit Selected");
        editBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        editBtn.addActionListener(e -> editSelectedItem());

        ModernButton undoBtn = new ModernButton("Undo Delete");
        undoBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        undoBtn.addActionListener(e -> undoDelete());
        ModernButton exportBtn = new ModernButton("Export CSV");
        exportBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        exportBtn.addActionListener(e -> exportCSV());
        ModernButton refreshBtn = new ModernButton("Refresh");
        refreshBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        refreshBtn.addActionListener(e -> updateInventoryTable());
        
        bottomPanel.add(deleteBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(undoBtn);
        bottomPanel.add(exportBtn);
        bottomPanel.add(refreshBtn);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dashboardPanel = new DashboardPanel(manager);
        
        panel.add(dashboardPanel, BorderLayout.CENTER);
        
        
        ModernButton refreshBtn = new ModernButton("Refresh Statistics");
        refreshBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        refreshBtn.addActionListener(e -> updateStats());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void submitItem() {
        try {
            String name = nameField.getText();
            String category = (String) categoryBox.getSelectedItem();
            double weight = Double.parseDouble(weightField.getText());
            String condition = (String) conditionBox.getSelectedItem();

            if (name.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            String id = manager.generateNextId();
            EWasteItem item = new EWasteItem(id, name, category, weight, condition);
            manager.addItemToQueue(item);
            
            // Clear fields
            nameField.setText("");
            categoryBox.setSelectedIndex(0);
            weightField.setText("");
            
            updatePendingCount();
            JOptionPane.showMessageDialog(this, "Item added to queue!");
            log("Submitted: " + item.getName());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid weight format.");
        }
    }

    private void processQueue() {
        LinkedList<EWasteItem> processed = manager.processQueue();
        if (processed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items to process.");
        } else {
            for (EWasteItem item : processed) {
                log("Processed: " + item);
            }
            updateInventoryTable();
            updatePendingCount();
            JOptionPane.showMessageDialog(this, "Processed " + processed.size() + " items.");
        }
    }

    private void updateInventoryTable() {
        updateInventoryTable(manager.getRecycledItems());
    }

    private void updateInventoryTable(LinkedList<EWasteItem> items) {
        tableModel.setRowCount(0);
        for (EWasteItem item : items) {
            Object[] row = {
                item.getId(),
                item.getName(),
                item.getCategory(),
                item.getWeight(),
                item.getCondition()
            };
            tableModel.addRow(row);
        }
    }

    private void searchItems() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            updateInventoryTable();
        } else {
            updateInventoryTable(manager.searchItems(query));
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete item ID: " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.removeItem(id)) {
                updateInventoryTable();
                JOptionPane.showMessageDialog(this, "Item deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting item.");
            }
        }
    }

    private void editSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentCategory = (String) tableModel.getValueAt(selectedRow, 2);
        double currentWeight = (Double) tableModel.getValueAt(selectedRow, 3);
        String currentCondition = (String) tableModel.getValueAt(selectedRow, 4);

        // Create a custom dialog for editing
        JDialog editDialog = new JDialog(this, "Edit Item: " + id, true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 350);
        editDialog.setLocationRelativeTo(this);

        ModernPanel formPanel = new ModernPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ModernTextField editNameField = new ModernTextField(20);
        editNameField.setText(currentName);
        
        String[] categories = {"Mobile", "Laptop", "Tablet", "TV", "Appliance", "Other"};
        JComboBox<String> editCategoryBox = new JComboBox<>(categories);
        editCategoryBox.setSelectedItem(currentCategory);

        ModernTextField editWeightField = new ModernTextField(20);
        editWeightField.setText(String.valueOf(currentWeight));
        
        String[] conditions = {"New", "Used", "Broken", "Refurbished"};
        JComboBox<String> editConditionBox = new JComboBox<>(conditions);
        editConditionBox.setSelectedItem(currentCondition);

        addLabelAndComponent(formPanel, "Name:", editNameField, gbc, 0);
        addLabelAndComponent(formPanel, "Category:", editCategoryBox, gbc, 1);
        addLabelAndComponent(formPanel, "Weight (kg):", editWeightField, gbc, 2);
        addLabelAndComponent(formPanel, "Condition:", editConditionBox, gbc, 3);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        ModernButton saveBtn = new ModernButton("Save Changes");
        saveBtn.setPreferredSize(STANDARD_BUTTON_SIZE);
        saveBtn.addActionListener(e -> {
            try {
                String newName = editNameField.getText();
                String newCategory = (String) editCategoryBox.getSelectedItem();
                double newWeight = Double.parseDouble(editWeightField.getText());
                String newCondition = (String) editConditionBox.getSelectedItem();

                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Name cannot be empty.");
                    return;
                }

                if (manager.updateItem(id, newName, newCategory, newWeight, newCondition)) {
                    updateInventoryTable();
                    updateStats(); // Update stats as weight/category might have changed
                    editDialog.dispose();
                    JOptionPane.showMessageDialog(this, "Item updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Error updating item.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Invalid weight format.");
            }
        });

        buttonPanel.add(saveBtn);

        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }

    private void updateStats() {
        if (dashboardPanel != null) {
            dashboardPanel.repaint();
        }
    }

    private void updatePendingCount() {
        pendingCountLabel.setText("Pending Items: " + manager.getPendingItems().size());
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }

    private void undoDelete() {
        if (manager.undoDelete()) {
            updateInventoryTable();
            JOptionPane.showMessageDialog(this, "Undo successful.");
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
        }
    }

    private void sortItems(String criteria) {
        if (criteria.equals("Weight (Low-High)")) {
            manager.sortItems(java.util.Comparator.comparingDouble(EWasteItem::getWeight));
        } else if (criteria.equals("Weight (High-Low)")) {
            manager.sortItems((i1, i2) -> Double.compare(i2.getWeight(), i1.getWeight()));
        } else if (criteria.equals("Category")) {
            manager.sortItems(java.util.Comparator.comparing(EWasteItem::getCategory));
        } else if (criteria.equals("Name")) {
            manager.sortItems(java.util.Comparator.comparing(EWasteItem::getName));
        } else if (criteria.equals("Condition")) {
            manager.sortItems(java.util.Comparator.comparing(EWasteItem::getCondition));
        }
        updateInventoryTable();
    }

    private void exportCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }
            fileService.exportToCSV(manager.getRecycledItems(), filePath);
            JOptionPane.showMessageDialog(this, "Exported to " + filePath);
        }
    }

    private void saveAndExit() {
        fileService.saveInventory(manager.getRecycledItems());
        System.exit(0);
    }

    private void applyDarkTheme() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Color darkBg = new Color(18, 18, 18);
        Color darkerBg = new Color(30, 30, 30);
        Color lightText = new Color(240, 240, 240); // Brighter text
        Color accentColor = new Color(75, 110, 175);

        // Nimbus specific overrides
        UIManager.put("control", darkBg);
        UIManager.put("text", lightText);
        UIManager.put("nimbusBase", new Color(50, 50, 50));
        UIManager.put("nimbusFocus", accentColor);
        UIManager.put("nimbusLightBackground", darkerBg);
        UIManager.put("nimbusSelectionBackground", accentColor);
        UIManager.put("nimbusSelectedText", Color.WHITE);

        UIManager.put("Panel.background", darkBg);
        UIManager.put("Label.foreground", lightText);
        
        UIManager.put("TextField.background", darkerBg);
        UIManager.put("TextField.foreground", lightText);
        UIManager.put("TextField.caretForeground", lightText);
        
        UIManager.put("TextArea.background", darkerBg);
        UIManager.put("TextArea.foreground", lightText);
        
        UIManager.put("ComboBox.background", darkerBg);
        UIManager.put("ComboBox.foreground", lightText);
        
        UIManager.put("Button.background", darkerBg);
        UIManager.put("Button.foreground", lightText);
        
        UIManager.put("Table.background", darkerBg);
        UIManager.put("Table.foreground", lightText);
        UIManager.put("Table.gridColor", new Color(80, 80, 80));
        UIManager.put("TableHeader.background", darkBg);
        UIManager.put("TableHeader.foreground", lightText);
        
        UIManager.put("ScrollPane.background", darkBg);
        UIManager.put("Viewport.background", darkBg);
        
        UIManager.put("OptionPane.background", darkBg);
        UIManager.put("OptionPane.messageForeground", lightText);
        
        // TabbedPane styling
        UIManager.put("TabbedPane.background", darkBg);
        UIManager.put("TabbedPane.foreground", lightText);
        UIManager.put("TabbedPane.contentAreaColor", darkBg);
        UIManager.put("TabbedPane.selected", darkerBg);
        UIManager.put("TabbedPane.borderHightlightColor", darkBg);
        UIManager.put("TabbedPane.darkShadow", darkBg);
        UIManager.put("TabbedPane.shadow", darkBg);
        UIManager.put("TabbedPane.light", darkBg);
        UIManager.put("TabbedPane.highlight", darkBg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EcoReviveMain().setVisible(true);
        });
    }
}
