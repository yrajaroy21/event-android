<?php
// Assuming you have a mysqli database connection established
require "conn.php"; // Add a semicolon here

// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Check if 'organizes_by' parameter is set in the POST request
    if (isset($_POST['organizer'])) {
        // Sanitize the input to prevent SQL injection
        $organizer = $_POST['organizer'];

        // Prepare and execute the SQL query
        $sql = "SELECT live, title, category, image, start_date FROM tbl_events WHERE organizes_by = ? AND status = 'approved'";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $organizer);
        $stmt->execute();
        $result = $stmt->get_result();

        // Fetch the results as an associative array
        $events = $result->fetch_all(MYSQLI_ASSOC);

        // Check if there are any results
        if ($events) {
            // Encode the results as JSON and send as response
            header('Content-Type: application/json');
            echo json_encode(array("status" => "success", "data" => $events));
        } else {
            // If no events found for the given organizer, return a message
            header('Content-Type: application/json');
            echo json_encode(array("status" => "error", "message" => "No events found for the given organizer."));
        }

        // Close the database connection
        $stmt->close();
      
    } else {
        // If 'organizer' parameter is not set, return an error message
        header('Content-Type: application/json');
        echo json_encode(array("status" => "error", "message" => "Missing 'organizer' parameter."));
    }
} else {
    // If the request method is not POST, return an error message
    header('Content-Type: application/json');
    echo json_encode(array("status" => "error", "message" => "Invalid request method."));
}
?>
