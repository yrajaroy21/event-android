<?php
require "conn.php";

if(isset($_POST['username'], $_POST['email'], $_POST['phone'], $_POST['password'])) {
    $username = $_POST['username'];
    $email = $_POST['email'];
    $phone = $_POST['phone'];
    $password = $_POST['password'];

    $stmt_check_username = $conn->prepare("SELECT * FROM tbl_login WHERE username = ?");
    $stmt_check_username->bind_param("s", $username);
    $stmt_check_username->execute();
    $result_username = $stmt_check_username->get_result();

    if($result_username->num_rows > 0) {
        $response = array("status" => "error", "message" => "Username already exists.");
    } else {
        $stmt_check_email = $conn->prepare("SELECT * FROM tbl_login WHERE email = ?");
        $stmt_check_email->bind_param("s", $email);
        $stmt_check_email->execute();
        $result_email = $stmt_check_email->get_result();
        if($result_email->num_rows > 0) {
            $response = array("status" => "error", "message" => "Email already exists.");
        } else {
    
            $stmt = $conn->prepare("INSERT INTO tbl_login (username, email, phone, password, type) VALUES (?, ?, ?, ?, 'Student')");
            $stmt->bind_param("ssss", $username, $email, $phone, $password);

            if ($stmt->execute()) {
                $insertedId = $stmt->insert_id;
                $response = array("status" => "success", "message" => "User registered successfully.", "id" => $insertedId);
            } else {
                $response = array("status" => "error", "message" => "Error registering user.");
            }

            $stmt->close();
        }

        $stmt_check_email->close();
    }

    $stmt_check_username->close();
} else {
    $response = array("status" => "error", "message" => "Incomplete parameters.");
}

header('Content-Type: application/json');
echo json_encode($response);
?>
