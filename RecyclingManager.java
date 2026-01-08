package EcoRevive;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Collections;
import java.util.Comparator;

public class RecyclingManager {
    private LinkedList<EcoRevive.EWasteItem> recycledItems;
    private Queue<EcoRevive.EWasteItem> pendingQueue;
    private Stack<EcoRevive.EWasteItem> deletedStack;
    private int nextId = 1;

    public RecyclingManager() {
        this.recycledItems = new LinkedList<>();
        this.pendingQueue = new LinkedList<>();
        this.deletedStack = new Stack<>();
    }

    public String generateNextId() {
        return String.format("%03d", nextId++);
    }

    // Add item to the pending queue
    public void addItemToQueue(EcoRevive.EWasteItem item) {
        pendingQueue.add(item);
    }

    // Process all items in the queue (move to recycled list)
    // Returns the list of items that were processed
    public LinkedList<EcoRevive.EWasteItem> processQueue() {
        LinkedList<EcoRevive.EWasteItem> processed = new LinkedList<>();
        if (pendingQueue.isEmpty()) {
            return processed;
        }

        while (!pendingQueue.isEmpty()) {
            EcoRevive.EWasteItem item = pendingQueue.poll();
            recycledItems.add(item);
            processed.add(item);
        }
        return processed;
    }

    public Queue<EcoRevive.EWasteItem> getPendingItems() {
        return pendingQueue;
    }

    // Display all recycled items
    public void displayInventory() {
        if (recycledItems.isEmpty()) {
            System.out.println("No recycled items in inventory.");
            return;
        }

        System.out.println("\n--- Recycled Inventory ---");
        for (EcoRevive.EWasteItem item : recycledItems) {
            System.out.println(item);
        }
        System.out.println("--------------------------");
    }

    public LinkedList<EcoRevive.EWasteItem> getRecycledItems() {
        return recycledItems;
    }

    public void setRecycledItems(LinkedList<EcoRevive.EWasteItem> items) {
        this.recycledItems = items;
        // Update nextId based on existing items
        int maxId = 0;
        for (EcoRevive.EWasteItem item : items) {
            try {
                int id = Integer.parseInt(item.getId());
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignore non-numeric IDs
            }
        }
        nextId = maxId + 1;
    }

    // Remove item by ID and push to stack
    public boolean removeItem(String id) {
        for (EcoRevive.EWasteItem item : recycledItems) {
            if (item.getId().equals(id)) {
                recycledItems.remove(item);
                deletedStack.push(item);
                return true;
            }
        }
        return false;
    }

    // Update item details
    public boolean updateItem(String id, String newName, String newCategory, double newWeight, String newCondition) {
        for (int i = 0; i < recycledItems.size(); i++) {
            EcoRevive.EWasteItem item = recycledItems.get(i);
            if (item.getId().equals(id)) {
                // Create new item with updated details but same ID
                EcoRevive.EWasteItem updatedItem = new EcoRevive.EWasteItem(id, newName, newCategory, newWeight, newCondition);
                recycledItems.set(i, updatedItem);
                return true;
            }
        }
        return false;
    }

    // Undo last delete
    public boolean undoDelete() {
        if (!deletedStack.isEmpty()) {
            EcoRevive.EWasteItem item = deletedStack.pop();
            recycledItems.add(item);
            return true;
        }
        return false;
    }

    // Sort items
    public void sortItems(Comparator<EcoRevive.EWasteItem> comparator) {
        Collections.sort(recycledItems, comparator);
    }

    // Search items by query (ID, Name, or Category)
    public LinkedList<EcoRevive.EWasteItem> searchItems(String query) {
        LinkedList<EcoRevive.EWasteItem> result = new LinkedList<>();
        String lowerQuery = query.toLowerCase();
        for (EcoRevive.EWasteItem item : recycledItems) {
            if (item.getId().toLowerCase().contains(lowerQuery) ||
                    item.getName().toLowerCase().contains(lowerQuery) ||
                    item.getCategory().toLowerCase().contains(lowerQuery)) {
                result.add(item);
            }
        }
        return result;
    }

    // Calculate statistics
    public String getStatistics() {
        int totalItems = recycledItems.size();
        double totalWeight = 0;
        java.util.Map<String, Integer> categoryCount = new java.util.HashMap<>();

        for (EcoRevive.EWasteItem item : recycledItems) {
            totalWeight += item.getWeight();
            categoryCount.put(item.getCategory(), categoryCount.getOrDefault(item.getCategory(), 0) + 1);
        }

        String mostCommonCategory = "N/A";
        int maxCount = 0;
        for (java.util.Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommonCategory = entry.getKey();
            }
        }

        return String.format("Total Items Recycled: %d\nTotal Weight Recycled: %.2f kg\nMost Common Category: %s\nTotal Eco-Points: %d",
                totalItems, totalWeight, mostCommonCategory, calculateEcoPoints());
    }

    // Calculate total eco points
    private int calculateEcoPoints() {
        int totalPoints = 0;

        for (EcoRevive.EWasteItem item : recycledItems) {
            double points = 0;

            // 1. Base points based on Category (Precious metals/complexity value)
            switch (item.getCategory()) {
                case "Laptop":
                case "Mobile":
                case "Tablet":
                    points += 50; // High value
                    break;
                case "TV":
                case "Appliance":
                    points += 20; // Medium value
                    break;
                default:
                    points += 10; // Standard value
            }

            // 2. Points for Weight (Encourage recycling heavy items)
            points += (item.getWeight() * 5);

            // 3. Multiplier based on Condition (Encourage reuse)
            double multiplier = 1.0;
            switch (item.getCondition()) {
                case "New":
                    multiplier = 2.0; // Best for reuse
                    break;
                case "Refurbished":
                    multiplier = 1.5;
                    break;
                case "Used":
                    multiplier = 1.0;
                    break;
                case "Broken":
                    multiplier = 0.5; // Recycling only
                    break;
            }

            totalPoints += (int) (points * multiplier);
        }
        return totalPoints;
    }
}
