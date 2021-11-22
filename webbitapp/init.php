<?php
$db_name="bitbuilderusers";
$mysql_user ="bitbuilder1";
$mysql_pass = "*******";
$server_name = "10.10.0.239";

$con = mysqli_connect($server_name, $mysql_user, $mysql_pass, $db_name);
if(!$con)
{
echo"Connection Error...".myqli_connect_error();
}
else
{
echo "Database connection Succes...!;
}

?>
