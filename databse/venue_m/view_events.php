<?php
require "conn.php";

if ($_SERVER["REQUEST_METHOD"] == "POST") {
// SQL query to retrieve event details
$sql = "SELECT title, event_venue, start_date, end_date  FROM tbl_events";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $response = array();

    // Fetch data and store in the response array
    while ($row = $result->fetch_assoc()) {
        $event = array(
            "title" => $row["title"],
            "venue" => $row["event_venue"],
            "start" => $row["start_date"],
            "end" => $row["end_date"]
        );

        array_push($response, $event);
    }

    // Convert the response array to a JSON string and echo it
    echo json_encode($response);
} else {
    echo "0 results";
}
}
$conn->close();

?>
