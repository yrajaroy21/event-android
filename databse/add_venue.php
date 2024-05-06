<?php
require "conn.php";
header('Content-Type: application/json');

// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Retrieve the data from the raw POST request (JSON)
    $jsonData = file_get_contents('php://input');
    $postData = json_decode($jsonData, true);

    // Check if the required fields are present in the JSON data
    $requiredFields = ['venueName', 'venueLocation', 'venueCapacity', 'venueFloor', 'venueArea', 'image'];
    $missingFields = array_diff($requiredFields, array_keys($postData));
    
    if (!empty($missingFields)) {
        // Send an error response if any required field is missing
        $response = array('status' => 'error', 'message' => 'Required fields are missing: ' . implode(', ', $missingFields));
        echo json_encode($response);
        exit(); // Stop further execution
    }

    // Extract data from JSON
    $venueName = $postData['venueName'];
    $venueLocation = $postData['venueLocation'];
    $venueCapacity = $postData['venueCapacity'];
    $venueFloor = $postData['venueFloor'];
    $venueArea = $postData['venueArea'];
    $imageData = $postData['image'];
    $status = "Available"; // Add a semicolon here

    // Additional validation if needed
    if (empty($venueName) || empty($venueLocation) || empty($venueCapacity) || empty($venueFloor) || empty($venueArea) || empty($imageData)) {
        // Send an error response if any required field is empty
        $response = array('status' => 'error', 'message' => 'Required fields cannot be empty');
        echo json_encode($response);
        exit(); // Stop further execution
    }

    // Decode image data and save to file
    $decodedImage = base64_decode($imageData);
    $imageFileName = 'uploads/' . time() . '.jpg';
    file_put_contents($imageFileName, $decodedImage);

    // Prepare and execute database insertion
    $query = "INSERT INTO tbl_venue (name, location, floor, capacity, area, image, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
    // Invalid request method
    $response = array('status' => 'error', 'message' => 'Invalid request method');
    echo json_encode($response);
}

?>
