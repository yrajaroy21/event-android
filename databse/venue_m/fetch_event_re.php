<?php
require "conn.php";

if (isset($_POST['action'])) {
    $action = $_POST['action'];
    
    if ($action == 'fetch') {
        // Fetch student ID
        $student = $_POST['student'];

        $today = date("Y-m-d");
        $eventsSql = "SELECT id, image, category, title, event_venue, start_date FROM tbl_events WHERE status = 'approved' AND live = 'open' AND start_date >= '$today'";
        $eventsResult = $conn->query($eventsSql);

        if ($eventsResult->num_rows > 0) {
            $eventsData = array();

            while($row = $eventsResult->fetch_assoc()) {
                $eventsData[] = $row;
            }

            $venueData = array();
            foreach ($eventsData as $event) {
                $eventVenueName = $event['event_venue'];
                $venueSql = "SELECT capacity FROM tbl_venue WHERE name = '$eventVenueName'";
                $venueResult = $conn->query($venueSql);

                if ($venueResult->num_rows > 0) {
                    while($row = $venueResult->fetch_assoc()) {
                        $venueData[] = $row;
                    }
                }
            }

            $countData = array();
            foreach ($eventsData as $event) {
                $eventId = $event['id'];
                $countSql = "SELECT COUNT(*) AS count FROM tbl_register WHERE event = '$eventId'";
                $countResult = $conn->query($countSql);

                if ($countResult->num_rows > 0) {
                    while($row = $countResult->fetch_assoc()) {
                        $countData[] = $row;
                    }
                }
            }

            $studentCountData = array();
            foreach ($eventsData as $event) {
                $eventId = $event['id'];
                $studentCountSql = "SELECT COUNT(*) AS student_count FROM tbl_register WHERE event = '$eventId' AND student = '$student'";
                $studentCountResult = $conn->query($studentCountSql);

                if ($studentCountResult->num_rows > 0) {
                    while($row = $studentCountResult->fetch_assoc()) {
                        $studentCountData[] = $row;
                    }
                }
            }

            $responseData = array(
                "status" => "success",
                "events" => $eventsData,
                "venue" => $venueData,
                "count" => $countData,
                "student_count" => $studentCountData
            );

            echo json_encode($responseData);
        } else {
            echo json_encode(array("status" => "error", "message" => "No events found with the specified criteria"));
        }
    } elseif ($action == 'register') {
        // Handle registration action
        $eventId = $_POST['event_id'];
        $timestamp = date("Y-m-d H:i:s"); // Current timestamp
        $student = $_POST['student_id'];

        // Insert data into tbl_register table
        $insertSql = "INSERT INTO tbl_register (event, created_on, student) VALUES ('$eventId', '$timestamp', '$student')";
        if ($conn->query($insertSql) === TRUE) {
            echo json_encode(array("status" => "success", "message" => "Registration successful"));
        } else {
            echo json_encode(array("status" => "error", "message" => "Error: " . $conn->error));
        }
    } else {
        echo json_encode(array("status" => "error", "message" => "Invalid action"));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "Invalid action or action parameter not provided"));
}

$conn->close();
?>
