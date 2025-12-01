# EcoRevive

**EcoRevive** is a Java-based desktop application designed to streamline the management and tracking of electronic waste (E-Waste) for recycling. The application features a custom "Modern" graphical user interface (GUI) to help users easily log, monitor, and manage recyclable electronic items, promoting sustainable disposal practices.

## Features

  * **E-Waste Management:** Add and track various types of electronic waste items.
  * **Dashboard View:** A dedicated dashboard for viewing recycling statistics and active items.
  * **Data Persistence:** Automatically saves and retrieves recycling data using a local file system (`ewaste_data.txt`), ensuring your records are never lost.
  * **Modern UI:** Built with custom Java Swing components (`ModernButton`, `ModernPanel`, `ModernTextField`) for a clean, aesthetic look.
  * **Object-Oriented Design:** structured using clear separation of concerns with dedicated managers and service classes.

## Tech Stack

  * **Language:** Java (JDK 8+)
  * **GUI Framework:** Java Swing / AWT
  * **Data Storage:** File-based persistence (Text file)

## Project Structure

Here is an overview of the key files in the repository:

  * **`EcoReviveMain.java`**: The entry point of the application. Run this file to start the program.
  * **`RecyclingManager.java`**: Contains the core business logic for handling recycling operations.
  * **`DashboardPanel.java`**: The main GUI panel that displays the user interface.
  * **`EWasteItem.java`**: The data model representing a single piece of electronic waste.
  * **`FileService.java`**: Handles reading from and writing to the `ewaste_data.txt` file.
  * **`Modern*.java`**: Custom UI components (Buttons, Panels, TextFields) that override standard Swing elements for improved styling.
  * **`ewaste_data.txt`**: The database file where e-waste records are stored.

## Getting Started

### Prerequisites

Ensure you have the **Java Development Kit (JDK)** installed on your system.

  * To check, run: `java -version`
  * If not installed, download it from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/).

### Installation & Run

1.  **Clone the Repository**

    ```bash
    git clone https://github.com/azideu/EcoRevive.git
    cd EcoRevive
    ```

2.  **Compile the Code**
    Compile all Java files in the directory.

    ```bash
    javac *.java
    ```

3.  **Run the Application**
    Execute the main class to launch the GUI.

    ```bash
    java EcoReviveMain
    ```

## Contributing

Contributions are welcome\! If you have ideas for new features (like data visualization charts or export to CSV), feel free to fork the repository and submit a pull request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## License

This project is open-source. Please check the repository for specific license details.
