<?php
date_default_timezone_set("PRC");
//下载数据库
function download_file($download_url){
	$data = file_get_contents($download_url);//注意要替换成自己的下载地址，不同文件格式地址不同。
	$verified = FALSE;
	$filename = '';
	foreach ($http_response_header AS $line){
		if (strpos($line, 'Content-Disposition:') !== FALSE){
			if (preg_match('/Content-Disposition: attachment; filename="([\w|\.]+)"/', $line, $match) > 0){
				$filename = $match[1]; //获取默认下载文件名称
			}
		}
		elseif (strpos($line, 'ETag:') !== FALSE){
			$value = explode('sha1-', $line)[1]; // 
			if ($value == sha1($data)){
				$verified = TRUE;
			}
		}
	}

	if ($verified){
		if ($filename == ''){
			$filename = 'ipip.dat';
		}
		file_put_contents(__DIR__ . '/' . $filename, $data, LOCK_EX);
	}
}

//IP库每日高级版（每天更新，更新时间 6:00 - 9:00）
$version_ip_url = "https://user.ipip.net/download.php?a=version&token=*****";//示例：2018-03-20
$datx_ip_url = "https://user.ipip.net/download.php?type=datx&token=*****";
$txtx_ip_url = "https://user.ipip.net/download.php?type=txtx&token=*****";
//手机号码归属地库(每月更新，更新日期不定)
$version_phonenum_url = "https://user.ipip.net/download.php?a=version&token=*****";//示例：20180308
$txt_phonenum_url = "https://user.ipip.net/download.php?token=*****";
//IP库区县版(每半月更新，更新日期不定)
$version_qxip_url = "https://user.ipip.net/download.php?a=version&token=*****";//示例：2018-03-18
$datx_qxip_url = "https://user.ipip.net/download.php?type=dat&token=*****";
$txtx_qxip_url = "https://user.ipip.net/download.php?token=*****";

//默认检测下载IP库
$version_url = file_get_contents($version_ip_url);
$download_url = $datx_ip_url;

switch ($argv[1]){
	case "1":
	  echo "\n每日区县IP库";
	  $version_url = file_get_contents($version_qxip_url);
      $download_url = $datx_qxip_url;
	  break;  
	case "2":
	  echo "\n手机号归属库";
	  $version_url = file_get_contents($version_phonenum_url);
      $download_url = $txt_phonenum_url;
	  break;
	default:
	  echo "\n每日IP库";
	  $version_url = file_get_contents($version_ip_url);
	  $download_url = $datx_ip_url;
}


$file_version = intval(str_replace('-','',$version_url));//获取检查页面中IP库的版本号，去除-
$current_date = intval(date("Ymd",time()));
if($current_date == $file_version){
	echo "\n检测接口version_url：";
	echo $version_url;
	echo "\n下载地址download_url：";
	echo $download_url;
	echo "\n开始下载......."; 
	//download_file($download_url);
	echo "\n下载完成\n";
} else {
	echo "\n数据版本：";
	echo $file_version;
	echo "\n当前日期：";
	echo $current_date;
	echo "\n";
}

