<?php
require "conn.php";

// Get bioid and updated status from the POST request
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $bioid = $_POST['bioid'];
    $status = $_POST['status'];

    // Update the status in the database
    $updateSql = "UPDATE tbl_organizers SET status = '$status' WHERE bioid = '$bioid'";
    $updateResult = $conn->query($updateSql);

    if ($updateResult) {
        echo json_encode(["message" => "Status updated successfully"]);
    } else {
        echo json_encode(["error" => "Error updating status: " . $conn->error]);
    }

    // Close the database connection
    $conn->close();
} else {
    echo json_encode(["error" => "Invalid request method"]);
}
?>
