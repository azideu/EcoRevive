package EcoRevive;

import java.util.Comparator; // Keep Comparator
// Removed java.util.Collections/Queue/Stack logic in favor of custom ADTs

public class RecyclingManager {
    private MyLinkedList<EWasteItem> recycledItems;
    private MyQueue<EWasteItem> pendingQueue;
    private MyStack<EWasteItem> deletedStack;
    private int nextId = 1;

    public RecyclingManager() {
        this.recycledItems = new MyLinkedList<>();
        this.pendingQueue = new MyQueue<>();
        this.deletedStack = new MyStack<>();
    }

    public String generateNextId() {
        return String.format("%03d", nextId++);
    }

    // Add item to the pending queue
    public void addItemToQueue(EWasteItem item) {
        pendingQueue.enqueue(item);
    }

    // Process all items in the queue (move to recycled list)
    // Returns the list of items that were processed
    public MyLinkedList<EWasteItem> processQueue() {
        MyLinkedList<EWasteItem> processed = new MyLinkedList<>();
        if (pendingQueue.isEmpty()) {
            return processed;
        }

        while (!pendingQueue.isEmpty()) {
            EWasteItem item = pendingQueue.dequeue();
            recycledItems.add(item);
            processed.add(item);
        }
        return processed;
    }

    public MyQueue<EWasteItem> getPendingItems() {
        return pendingQueue;
    }

    // Display all recycled items
    public void displayInventory() {
        if (recycledItems.isEmpty()) {
            System.out.println("No recycled items in inventory.");
            return;
        }

        System.out.println("\n--- Recycled Inventory ---");
        for (EWasteItem item : recycledItems) {
            System.out.println(item);
        }
        System.out.println("--------------------------");
    }

    public MyLinkedList<EWasteItem> getRecycledItems() {
        return recycledItems;
    }

    public void setRecycledItems(MyLinkedList<EWasteItem> items) {
        this.recycledItems = items;
        // Update nextId based on existing items
        int maxId = 0;
        for (EWasteItem item : items) {
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
        // Must iterate manually using index or iterator
        for (EWasteItem item : recycledItems) {
            if (item.getId().equals(id)) {
                // O(N) scan but sufficient for requirements
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
            EWasteItem item = recycledItems.get(i);
            if (item.getId().equals(id)) {
                // Create new item with updated details but same ID
                EWasteItem updatedItem = new EWasteItem(id, newName, newCategory, newWeight, newCondition);
                recycledItems.set(i, updatedItem);
                return true;
            }
        }
        return false;
    }

    // Undo last delete
    public boolean undoDelete() {
        if (!deletedStack.isEmpty()) {
            EWasteItem item = deletedStack.pop();
            recycledItems.add(item);
            return true;
        }
        return false;
    }

    // Sort items
    public void sortItems(Comparator<EWasteItem> comparator) {
        recycledItems.sort(comparator);
    }

    // Search items by query (ID, Name, or Category)
    public MyLinkedList<EWasteItem> searchItems(String query) {
        MyLinkedList<EWasteItem> result = new MyLinkedList<>();
        String lowerQuery = query.toLowerCase();
        for (EWasteItem item : recycledItems) {
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

        for (EWasteItem item : recycledItems) {
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
        
        for (EWasteItem item : recycledItems) {
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
