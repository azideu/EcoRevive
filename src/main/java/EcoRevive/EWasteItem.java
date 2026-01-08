package EcoRevive;

public class EWasteItem {
    private String id;
    private String name;
    private String category;
    private double weight;
    private String condition;

    public EWasteItem(String id, String name, String category, double weight, String condition) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.weight = weight;
        this.condition = condition;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getWeight() { return weight; }
    public String getCondition() { return condition; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Category: " + category + 
               ", Weight: " + weight + "kg, Condition: " + condition;
    }

    // Format for file storage
    public String toFileString() {
        return id + "," + name + "," + category + "," + weight + "," + condition;
    }

    // Create from file string
    public static EWasteItem fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 5) {
            return new EWasteItem(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), parts[4]);
        }
        return null;
    }
}
