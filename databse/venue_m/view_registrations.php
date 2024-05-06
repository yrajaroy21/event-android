<?php
require "conn.php";

if (isset($_POST['action'])) {
    $action = $_POST['action'];

    if ($action == 'fetch') {
        // Fetching event titles
        if (isset($_POST['organized_by'])) {
            $organized_by = $_POST['organized_by'];
            $eventSql = "SELECT id, title FROM tbl_events WHERE organizes_by = '$organized_by'";
            $eventResult = $conn->query($eventSql);

            $eventData = array();
            if ($eventResult->num_rows > 0) {
                $titles = array();
                while ($row = $eventResult->fetch_assoc()) {
                    $eventData[] = $row;
                    $titles[] = $row['title'];
                }
                $responseData = array(
                    "status" => "success",
                    "titles" => $titles
                );
                echo json_encode($responseData);
            } else {
                echo json_encode(array("status" => "error", "message" => "No events found for the specified organizer"));
            }
        } else {
            echo json_encode(array("status" => "error", "message" => "Organizer ID not provided"));
        }
    } elseif ($action == 'register') {
        // Fetching student IDs and corresponding login data
        if (isset($_POST['title'])) {
            $title = $_POST['title'];
            $eventIdSql = "SELECT id FROM tbl_events WHERE title = '$title'";
            $eventIdResult = $conn->query($eventIdSql);

            if ($eventIdResult->num_rows > 0) {
                $eventIdRow = $eventIdResult->fetch_assoc();
                $eventId = $eventIdRow['id'];

                $registerSql = "SELECT student FROM tbl_register WHERE event = '$eventId'";
                $registerResult = $conn->query($registerSql);

                $registerData = array();
                if ($registerResult->num_rows > 0) {
                    while ($row = $registerResult->fetch_assoc()) {
                        $studentId = $row['student'];
                        $userData = array();
                        $userSql = "SELECT username, email, phone FROM tbl_login WHERE id = '$studentId'";
                        $userResult = $conn->query($userSql);
                        if ($userResult->num_rows > 0) {
                            $userData = $userResult->fetch_assoc();
                        }
                        $registerData[] = array_merge(array('student' => $studentId), $userData);
                    }
                    $responseData = array(
                        "status" => "success",
                        "register_data" => $registerData
                    );
                    echo json_encode($responseData);
                } else {
                    echo json_encode(array("status" => "error", "message" => "No registrations found for the specified event"));
                }
            } else {
                echo json_encode(array("status" => "error", "message" => "Event not found with the specified title"));
            }
        } else {
            echo json_encode(array("status" => "error", "message" => "Title not provided"));
        }
    } else {
        echo json_encode(array("status" => "error", "message" => "Invalid action"));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "Action not provided"));
}

$conn->close();
?>
