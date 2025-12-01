package EcoRevive;

import java.io.*;
import java.util.LinkedList;

public class FileService {
    private static final String FILE_NAME = "ewaste_data.txt";

    // Save the list of recycled items to file
    public void saveInventory(LinkedList<EWasteItem> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (EWasteItem item : items) {
                writer.write(item.toFileString());
                writer.newLine();
            }
            System.out.println("Inventory saved to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    // Load items from file
    public LinkedList<EWasteItem> loadInventory() {
        LinkedList<EWasteItem> items = new LinkedList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No existing data file found. Starting fresh.");
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                EWasteItem item = EWasteItem.fromFileString(line);
                if (item != null) {
                    items.add(item);
                }
            }
            System.out.println("Inventory loaded from " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
        return items;
    }

    // Export to CSV
    public void exportToCSV(LinkedList<EWasteItem> items, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Header
            writer.write("ID,Name,Category,Weight,Condition");
            writer.newLine();
            for (EWasteItem item : items) {
                writer.write(item.toFileString());
                writer.newLine();
            }
            System.out.println("Inventory exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
        }
    }
}
