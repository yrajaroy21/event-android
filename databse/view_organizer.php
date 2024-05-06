<?php

require "conn.php";

// Check if the request method is POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    
    // SQL query to retrieve organizer details
    $sql = "SELECT bioid, username, email, status FROM tbl_organizers";
    $result = $conn->query($sql);

 


if ($result->num_rows > 0) {
    $data = array();
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
    echo json_encode($data); // Return JSON response
} else {
    echo "0 results";
}
}
?>
