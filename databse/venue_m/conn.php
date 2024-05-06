<?php
$conn = mysqli_connect("localhost", "root", "", "android");

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}


// Perform other database operations if needed

?>
