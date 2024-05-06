    <?php
    require "conn.php";
// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Database configuration
 
    // Fetch all venue details
    $query = "SELECT name,location,floor,capacity,area,image,status FROM tbl_venue";
    $result = $conn->query($query);

    // Check if there are results
    if ($result->num_rows > 0) {
        $venues = array();

        // Fetch rows and add to the $venues array
        while ($row = $result->fetch_assoc()) {
            $venues[] = $row;
        }

        // Output venues as JSON
        echo json_encode(array("status" => "Success", "data" => $venues));
    } else {
        // No venues found
        echo json_encode(array("status" => "NoData", "message" => "No venues found"));
    }

    // Close the connection
    $conn->close();
} else {
    // Invalid request method
    echo json_encode(array("status" => "Error", "message" => "Invalid request method"));
}
?>
