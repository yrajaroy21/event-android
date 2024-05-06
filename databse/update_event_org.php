<?php
require "conn.php";
// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    // Check if title and live values are provided
    if (isset($_POST['title']) && isset($_POST['live'])) {
        
        // Database connection parameters
     
        // Prepare and bind parameters
        $stmt = $conn->prepare("UPDATE tbl_events SET live = ? WHERE title = ?");
        $stmt->bind_param("ss", $_POST['live'], $_POST['title']);

        // Execute the update statement
        if ($stmt->execute()) {
            // Success message
            $response = array("status" => "success", "message" => "Event updated successfully.");
        } else {
            // Error message
            $response = array("status" => "error", "message" => "Error updating event: " . $conn->error);
        }

        // Close statement and database connection
        $stmt->close();
        $conn->close();
        
    } else {
        // If title and live values are not provided
        $response = array("status" => "error", "message" => "Title and live values are required.");
    }
} else {
    // If the request method is not POST
    $response = array("status" => "error", "message" => "Only POST method is allowed.");
}

// Return response in JSON format
header('Content-Type: application/json');
echo json_encode($response);
?>
