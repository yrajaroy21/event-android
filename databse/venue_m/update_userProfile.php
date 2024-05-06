<?php
require "conn.php";

if(isset($_POST['username'], $_POST['new_contact'])) {
    $username = $_POST['username'];
    $newContact = $_POST['new_contact'];

    $stmt = $conn->prepare("UPDATE tbl_login SET phone = ? WHERE username = ?");
    $stmt->bind_param("ss", $newContact, $username);

    if ($stmt->execute()) {
        // Update successful
        $response = array("status" => "success", "message" => "Contact updated successfully.");
    } else {
        // Update failed
        $response = array("status" => "error", "message" => "Error updating contact.");
    }

    $stmt->close();
} else {
    // Parameters not provided
    $response = array("status" => "error", "message" => "Incomplete parameters.");
}

// Return response in JSON format
header('Content-Type: application/json');
echo json_encode($response);
?>
