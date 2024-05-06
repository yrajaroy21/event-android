<?php
// Assuming you have a database connection established already
require "conn.php";
// Check if the request method is POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Retrieve values from the POST request
    $Bioid = $_POST['Bioid'];
    $Name = $_POST['Name'];
    $email = $_POST['email'];
    $status = $_POST['status'];

    // Insert query for first table
    $insert_query1 = "INSERT INTO tbl_login (Bioid, username, email, type, status) VALUES ('$Bioid', '$Name', '$email', 'Staff', '$status')";

    // Insert query for second table
    $insert_query2 = "INSERT INTO tbl_organizers (Bioid, username, email, status) VALUES ('$Bioid', '$Name', '$email', '$status')";

    // Execute the first query
    $response = array();
    if (mysqli_query($conn, $insert_query1)) {
        // Execute the second query if the first one is successful
        if (mysqli_query($conn, $insert_query2)) {
            $response['success'] = true;
            $response['message'] = "Records inserted successfully into both tables";
        } else {
            $response['success'] = false;
            $response['message'] = "Error: " . $insert_query2 . "<br>" . mysqli_error($conn);
        }
    } else {
        $response['success'] = false;
        $response['message'] = "Error: " . $insert_query1 . "<br>" . mysqli_error($conn);
    }

    // Close the database connection
    mysqli_close($conn);

    // Respond with JSON
    header('Content-Type: application/json');
    echo json_encode($response);
} else {
    // Respond with error if request method is not POST
    $response['success'] = false;
    $response['message'] = "Invalid request method";
    header('Content-Type: application/json');
    echo json_encode($response);
}
?>
