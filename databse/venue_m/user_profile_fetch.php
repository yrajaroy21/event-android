<?php
require "conn.php";

if(isset($_POST['username'])) {
    $username = $_POST['username'];

    $stmt = $conn->prepare("SELECT username, email, phone FROM tbl_login WHERE username = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->store_result();

    if($stmt->num_rows > 0) {
        $stmt->bind_result($username, $email, $phone);
        $stmt->fetch();

        $response = array("status" => "success", "username" => $username, "email" => $email, "phone" => $phone);
    } else {
        $response = array("status" => "error", "message" => "User not found.");
    }

    $stmt->close();
} else {
    $response = array("status" => "error", "message" => "Incomplete parameters.");
}

header('Content-Type: application/json');
echo json_encode($response);
?>
