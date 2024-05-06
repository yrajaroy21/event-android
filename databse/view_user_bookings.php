<?php
require "conn.php";
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $student = $_POST['student'];
    $eventsSql = "SELECT event FROM tbl_register WHERE student = '$student' ";
    $eventsResult = $conn->query($eventsSql);
    
    if ($eventsResult->num_rows > 0) {
        $eventsData = array();

        while($row = $eventsResult->fetch_assoc()) {
            $eventsData[] = $row;
        }

        $venueData = array();
        foreach ($eventsData as $event) {
            $eventVenueName = $event['event'];
            $venueSql = "SELECT title,category,image,start_date FROM tbl_events WHERE id = '$eventVenueName'";
            $venueResult = $conn->query($venueSql);

            if ($venueResult->num_rows > 0) {
                while($row = $venueResult->fetch_assoc()) {
                    $venueData[] = $row;
                }
            }
        }

        $responseData = array(
            "status" => "success",
            "user_events" => $venueData
        );
        $json_response = json_encode($responseData);
        echo $json_response;
    } else {
        echo json_encode(array("status" => "error", "message" => "No events found with the specified criteria"));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "Invalid action or action parameter not provided"));
}

$conn->close();
?>
