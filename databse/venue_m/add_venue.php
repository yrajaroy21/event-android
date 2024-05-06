<?php
require "conn.php";
header('Content-Type: application/json');

// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Retrieve the data from the raw POST request (JSON)
    $jsonData = file_get_contents('php://input');
    $postData = json_decode($jsonData, true);

    // Check if the required fields are present in the JSON data
    if (isset($postData['venueName'], $postData['venueLocation'], $postData['venueCapacity'], $postData['venueFloor'], $postData['venueArea'],  $postData['image'])) {
        // Extract data from JSON
        $venueName = $postData['venueName'];
        $venueLocation = $postData['venueLocation'];
        $venueCapacity = $postData['venueCapacity'];
        $venueFloor = $postData['venueFloor'];
        $venueArea = $postData['venueArea'];
        $status ="Available";
        $imageData = $postData['image'];
        $decodedImage = base64_decode($imageData);
        $imageFileName = 'uploads/' .time() . '.jpg';
        file_put_contents($imageFileName, $decodedImage);

        // Perform any necessary database operations using the extracted data
        // For example, you can use mysqli or PDO to connect to your database
        $query = "INSERT INTO tbl_venue (name, location,  floor,capacity, area, image,status ) VALUES (?, ?, ?, ?, ?, ?, ?)";
        $stmt = $conn->prepare($query);
        $stmt->bind_param("sssssss", $venueName, $venueLocation, $venueFloor, $venueCapacity, $venueArea, $imageFileName, $status);

        if ($stmt->execute()) {
            // Send a success response
            $response = array('status' => 'success', 'message' => 'Data received and processed successfully');
            echo json_encode($response);
        } else {
            // Send an error response
            $response = array('status' => 'error', 'message' => 'Failed to process data');
            echo json_encode($response);
        }
    } else {
        // Send an error response if required fields are missing
        $response = array('status' => 'error', 'message' => 'Missing required fields');
        echo json_encode($response);
    }
} else {
    // Invalid request method
    $response = array('status' => 'error', 'message' => 'Invalid request method');
    echo json_encode($response);
}
?>
