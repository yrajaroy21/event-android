<?php
require "conn.php";

// Get bioid from the POST request
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $bioid = $_POST['bioid'];

    $sql = "SELECT bioid,username,email,status FROM tbl_organizers WHERE bioid = '$bioid'";
    $result = $conn->query($sql);

    // Check if the query was successful
    if ($result) {
        // Check if any rows were returned
        if ($result->num_rows > 0) {
            // Fetch organizer details
            $row = $result->fetch_assoc();


            // Convert details to JSON and echo the response
            echo json_encode($row);
        } else {
            echo json_encode(["error" => "No organizer found with the provided bioid"]);
        }
    } else {
        echo json_encode(["error" => "Error: " . $sql . "<br>" . $conn->error]);
    }

    // Close the database connection
    $conn->close();
} else {
    echo json_encode(["error" => "Invalid request method"]);
}

// Function to get the default spinner item

?>
