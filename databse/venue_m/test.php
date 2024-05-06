<?php
// Database connection parameters
require("conn.php");

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Handle POST requests

    // Check if the 'action' parameter is set in the POST request
    if (isset($_POST['action'])) {
        $action = $_POST['action'];

        if ($action === 'fetchData') {
            // Fetch available venues
            $queryVenues = "SELECT name FROM tbl_venue WHERE status='Available'";
            $resultVenues = $conn->query($queryVenues);

            // Check if there are any results
            if ($resultVenues->num_rows > 0) {
                $venues = array();

                // Fetch data and store it in an array
                while ($row = $resultVenues->fetch_assoc()) {
                    $venues[] = $row['name'];
                }

                // Fetch event categories from the event_category table
                $queryEventCategories = "SELECT category_name FROM tbl_event_category";
                $resultEventCategories = $conn->query($queryEventCategories);

                // Check if there are any results
                if ($resultEventCategories->num_rows > 0) {
                    $eventCategories = array();

                    // Fetch data and store it in an array
                    while ($row = $resultEventCategories->fetch_assoc()) {
                        $eventCategories[] = $row['category_name'];
                    }

                    // Combine the venues and event categories into a single response array
                    $response = array(
                        'venues' => $venues,
                        'eventCategories' => $eventCategories
                    );

                    // Return JSON response
                    header('Content-Type: application/json');
                    echo json_encode($response);
                } else {
                    // No event categories found
                    echo "No event categories found.";
                }

            } else {
                // No venues found
                echo "No venues found.";
            }
        } elseif ($action === 'insertDetails') {
            // Check if the file is uploaded successfully
            if (
                isset($_POST['rpImageBase64']) &&
                isset($_POST['invitationBase64'])
            ) {
                // Decode base64 images
                $rpImageBase64 = $_POST['rpImageBase64'];
                $invitationImageBase64 = $_POST['invitationBase64'];

                // Create unique filenames for the images
                $rpImageFileName = uniqid() . '_rpImage.png';
                $invitationImageFileName = uniqid() . '_invitationImage.png';

                // Specify the target directory for uploads
                $uploadDirectory = 'uploads/';

                // Construct the full paths for storing in the database
                $rpImagePathInDatabase = $uploadDirectory . $rpImageFileName;
                $invitationImagePathInDatabase = $uploadDirectory . $invitationImageFileName;

                // Save images to the "uploads" directory
                if (
                    file_put_contents($uploadDirectory . $rpImageFileName, base64_decode($rpImageBase64)) &&
                    file_put_contents($uploadDirectory . $invitationImageFileName, base64_decode($invitationImageBase64))
                ) {
                    // Insert details into the database
             // Insert details into the database
$insertQuery = "INSERT INTO tbl_events (category, title, image, resource_person_name, resource_person_des, resource_person_image, event_venue, start_date, start_time, end_date, end_time, organizes_by, created_on) VALUES ('" . $_POST['selectedCategory'] . "', '" . $_POST['eventName'] . "', '" . $rpImagePathInDatabase . "', '" . $_POST['resourcePersonName'] . "', '" . $_POST['resourcePersonDesignation'] . "', '" . $rpImagePathInDatabase . "', '" . $_POST['selectedVenue'] . "', '" . $_POST['startDate'] . "', '" . $_POST['startTime'] . "', '" . $_POST['endDate'] . "', '" . $_POST['endTime'] . "', 'raja', NOW())";

// Execute the query
if ($conn->query($insertQuery) === TRUE) {
    echo "Details inserted successfully.";
} else {
    echo "Error inserting details: " . $conn->error;
}

                    // Execute the statement
               
                    // Close the statement
             
                } else {
                    echo "Error saving one or more images to the target directory.";
                }
            } else {
                echo "rpImage64 or invitationImage64 not set in the POST request.";
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
