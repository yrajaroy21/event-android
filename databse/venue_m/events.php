<?php
// Database connection parameters
require("conn.php");

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Handle POST requests

    // Check if the 'action' parameter is set in the POST request
    if (isset($_POST['action'])) {
        $action = $_POST['action'];

        if ($action === 'getEventDetails') {
            // Check if the 'title' parameter is set in the POST request
            if (isset($_POST['title'])) {
                $title = $_POST['title'];

                // Fetch event details based on the provided title
                $queryEventDetails = "SELECT * FROM tbl_events WHERE title = '$title'";
                $resultEventDetails = $conn->query($queryEventDetails);

                // Check if there are any results
                if ($resultEventDetails->num_rows > 0) {
                    // Fetch data and store it in an array
                    $row = $resultEventDetails->fetch_assoc();

                    // Combine the event details into a response array
                    $response = array(
                        array(
                            'category' => $row['category'],
                            'name' => $row['title'],
                            'guest' => $row['resource_person_name'],
                            'designation' => $row['resource_person_des'],
                            'venue' => $row['event_venue'],
                            'start_date' => $row['start_date'],
                            'end_date' => $row['end_date'],
                            'start_time' => $row['start_time'],
                            'end_time' => $row['end_time'],
                            'organizer' => $row['organizes_by'],
                            'status' => $row['status'] // Assuming 'status' is a column in your table
                        )
                    );

                    // Return JSON response
                    header('Content-Type: application/json');
                    echo json_encode($response);
                } else {
                    // No event details found for the provided title
                    echo "No event details found for the provided title.";
                }
            } else {
                // 'title' parameter not set
                echo "No title specified.";
            }
        } elseif ($action === 'updateStatus') {
            // Check if the required parameters are set in the POST request
            if (isset($_POST['title']) && isset($_POST['newStatus'])) {
                $title = $_POST['title'];
                $newStatus = $_POST['newStatus'];

                // Update the status in the tbl_events table
                $updateQuery = "UPDATE tbl_events SET status = '$newStatus' WHERE title = '$title'";
                if ($conn->query($updateQuery) === TRUE) {
                    echo "Status updated successfully.";
                } else {
                    echo "Error updating status: " . $conn->error;
                }
            } else {
                // Required parameters not set
                echo "Missing parameters for status update.";
            }
        } else {
            // Invalid action
            echo "Invalid action.";
        }
    } else {
        // 'action' parameter not set
        echo "No action specified.";
    }
} else {
    // Request method is not POST
    echo "Invalid request method.";
}

// Close the database connection
$conn->close();
?>
