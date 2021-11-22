<?php
require "init.php";
$name = $_POST["user"];
$user_name = $_POST["user_name"];
$user_pass = md5($_POST["user_pass"]);

$sql_query = "insert into bitbuilderusers values('$name','$user_name','$user_pass');";

if(mysqli_query($con, $sql_query))
{
//echo "<h3> Data insertion Success...</h3>";
}
else
{
//echo "Data insertion error...".mysqli_error($con);
}

?>
