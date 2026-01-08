# EcoRevive (JavaFX Edition)

**EcoRevive** is a modern JavaFX-based desktop application designed to streamline the management and tracking of electronic waste (E-Waste) for recycling. It features a professional dark-themed GUI styled with CSS (JavaFX styling) to help users log, monitor, and visualize their recycling efforts suitable for the modern desktop environment.

## Features

*   **E-Waste Inventory:** Add, edit, delete, and search various types of electronic waste items.
*   **Queue Management:** A queue-based system for submitting items before final processing.
*   **Visual Dashboard:** Real-time statistics with charts (Pie & Bar charts) visualizing category distribution and weight contributions.
*   **Data Persistence:** Automatically saves inventory to `ewaste_data.txt` so records are preserved between sessions.
*   **CSV Export:** Export your inventory data to `.csv` files for external use.
*   **Modern Dark UI:** Fully styled using an external `styles.css` file for a premium look and feel.

## Tech Stack

*   **Language:** Java 21
*   **GUI Framework:** JavaFX 21
*   **Build Tool:** Maven
*   **Styling:** CSS3 (JavaFX variants)

## Project Structure

*   **`EcoReviveFX.java`**: The primary JavaFX Application class.
*   **`Launcher.java`**: A helper class to run the application easily within IDEs (VS Code, IntelliJ, etc.) without needing complex module configuration.
*   **`FXDashboardPanel.java`**: Handles the visualization logic (charts and graphs).
*   **`RecyclingManager.java`**: Core logic for managing inventory and queues.
*   **`FileService.java`**: Persistence layer for saving/loading data and exporting CSVs.
*   **`styles.css`**: Contains all visual styling definitions.

## How to Run

### Option 1: Using an IDE (VS Code, IntelliJ) - **Recommended**

If you encounter "JavaFX runtime components are missing" errors when running `EcoReviveFX.java` directly, use the **Launcher** instead:

1.  Navigate to `src/main/java/EcoRevive/Launcher.java`.
2.  Click the **Run** button (or right-click -> Run).

*This bypasses the module-path strictness by treating the app as a standard non-modular application at startup.*

### Option 2: Using Terminal (Maven)

You can run the application directly from the command line using Maven:

```bash
mvn clean javafx:run
```

### Prerequisites

*   Java JDK 21+
*   Maven 3.x+

## Contributing

Contributions are welcome! Feel free to fork the repository and submit pull requests.
