<?php
require "conn.php";

if(isset($_POST['email'], $_POST['password'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];

    $stmt = $conn->prepare("SELECT id, username FROM tbl_login WHERE email = ? AND password = ?");
    $stmt->bind_param("ss", $email, $password);
    $stmt->execute();
    $stmt->store_result();

    if($stmt->num_rows > 0) {
        $stmt->bind_result($id, $username);
        $stmt->fetch();

        $response = array("status" => "success", "message" => "Login successful.", "id" => $id, "username" => $username);
    } else {
        $response = array("status" => "error", "message" => "Invalid email or password.");
    }

    $stmt->close();
} else {
    $response = array("status" => "error", "message" => "Incomplete parameters.");
}

header('Content-Type: application/json');
echo json_encode($response);
?>
