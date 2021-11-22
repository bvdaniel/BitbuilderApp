<?php
require "init_send.php";

$user_name = $_POST["user"];
$id_miner = $_POST["id_miner"];
$t_i = $_POST["t_i"];
$t_f = $_POST["t_f"];
$ener = $_POST["ener"];

$sql_query = "insert into bitbuilder values('$user_name','$id_miner','$t_i','$t_f','$ener');";

if(mysqli_query($con, $sql_query))
{
//echo "<h3> Data insertion Success...</h3>";
}
else
{
//echo "Data insertion error...".mysqli_error($con);
}

?>
