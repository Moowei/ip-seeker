IPIP.net的datx格式IP数据库解析程序，包括对地市IP库和县区IP库的解析
==
# 一、UDF调用：
## 1、地市IP库解析的UDF
#### 使用类 com.moowei.ipip17mon.udf.Ip2CityString
```hql
add file hdfs://moowei/user/learning_test/hive_db/udf_blw/mydata4vipday2.datx;
add jar hdfs://moowei/user/learning_test/hive_db/udf_blw/original-ip-seeker-1.0-SNAPSHOT.jar;
create temporary function ip2CityString as 'com.moowei.ipip17mon.udf.Ip2CityString';
select ip,arr[0] as country,arr[1] as province,arr[2] as city 
from ( 
  select ip,split(ip2CityString(ip), '\t') as arr from learning_test.ip_test 
  limit 10
) tmp
```
## 2、区县IP库解析的UDF
### 使用类 com.moowei.ipip17mon.udf.Ip2CountyString
```hql
add file hdfs://moowei/user/learning_test/hive_db/udf_blw/quxian.datx;
add jar hdfs://moowei/user/learning_test/hive_db/udf_blw/original-ip-seeker-1.0-SNAPSHOT.jar;
create temporary function ip2CountyString as 'com.moowei.ipip17mon.udf.Ip2CountyString';
select ip,arr[0] as country,arr[1] as province,arr[2] as city ,arr[3] as county
from (
  select ip,split(ip2CountyString(ip), '\t') as arr from learning_test.ip_test
  limit 10
  ) tmp
```
<br>
-----------------------

# 二、外部RPC调用，使用如下：
* 1、启动服务（服务进程每隔10s检测数据文件/opt/blw_test/php/mydata4vipday2.datx是否变动，只有当文件变动时才会自动加载进内存并生成新的实例）
```shell
java -cp ip-seeker-1.0-SNAPSHOT.jar com.moowei.ipip17mon.thirdparty.RPCServer
```
* 2、客户端调用：
```shell
java -cp ip-seeker-1.0-SNAPSHOT.jar com.moowei.ipip17mon.thirdparty.RPCClient 118.24.8.8
```
<br>
----------------

# 三、程序集成
## 1、地市IP库使用：

* 1、首先设置IP库的路径：datxFilePathCity
* 2、创建LocatorImplForCity实例，之后调用实例中的public Object findLocationInfoByStrIp(String ipStr)方法

* eg：
### 方式一：使用findLocationInfoByStrIp 方法返回封装好的 LocationInfoForCity 实例对象 <br>
```java
String datxFilePathCity ="D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180129\\mydata4vipday2.datx";
LocatorImplForCity locatorImplForCity = LocatorImplForCity.getInstance(datxFilePathCity);
Object obj = locatorImplForCity.findLocationInfoByStrIp(aimIp);
if (obj instanceof LocationInfoForCity){
    System.out.println(((LocationInfoForCity)obj).toString());
}
```
### 方式二：使用 getLocationInfoByStrIp 方法直接返回\t分隔的字符串。 <br>
```java
@Test
public void isIPStr() {
    String aimIp = "118.24";//
    LocatorImplForCity locatorImplForCity = LocatorImplForCity.getInstance(datxFilePathCity);
    System.out.println(locatorImplForCity.getLocationInfoByStrIp(aimIp));
}
```


## 2、县区IP库使用

* 1、首先设置IP库的路径：datxFilePathCounty
* 2、创建LocatorImplForCounty实例，之后调用实例中的public Object findLocationInfoByStrIp(String ipStr)方法

* eg:
### 方式一：使用 findLocationInfoByStrIp 方法返回封装好的 LocationInfoForCounty 实例对象 <br>
```java
String datxFilePathCounty ="D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180201\\quxian.datx";
LocatorImplForCounty locatorImplForCounty = LocatorImplForCounty.getInstance(datxFilePathCounty);
Object obj = locatorImplForCounty.findLocationInfoByStrIp(aimIp);
if (obj instanceof LocationInfoForCounty){
    System.out.println(((LocationInfoForCounty)obj).toString());
}
```
### 方式二：使用 getLocationInfoByStrIp 方法直接返回\t分隔的字符串。<br>
```java
@Test
public void isIPStr() {
    String aimIp = "118.24";//
    Locator locator = LocatorImplForCounty.getInstance(datxFilePathCounty2);
    System.out.println(locator.getLocationInfoByStrIp(aimIp));
}
```
<br>
----------------

# 四、地市IP库的datx数据结构

## 1、结构图如下（原图片地址为 https://github.com/Moowei/ip-seeker/blob/master/src/conf/ipip_datx.png ）
(注意要想是图片在github中显示需要将原图片地址中blob改为raw，或在原图片地址后面添加?raw=true)：
![image](https://github.com/Moowei/ip-seeker/raw/master/src/conf/ipip_datx.png) 

## 2、Index区说明：
>其中index为四个字节，与AreaData区起始的下标有关，若四个字节表示的int值为 areaDataStartIndex 那么,相关的指标可以用如下公式计算：<br>
>* 1、Ip库中的记录数= (areaDataStartIndex - 256*256*4 - 256*256*4 - 4)/9<br>
>* 2、AreaData区域从datx的第(areaDataStartIndex-256*256*4)个字节开始，之后所有的字节都属于AreaData区域，其中存储地域和运营商信息。<br>

## 3、IPIndex区：
>存放IP库中每条记录的startIP的索引(IPIndex区的下标),根据该索引可以很快锁定要查询的IP在IPIndex区的位置范围。以便为之后使用二分查找从IP库中搜寻指定ip值时缩小查询的范围，而无需遍历所有IP库的数据。<br>
>该区域由256*256*4=262144 个byte组成（即256*256个Int），每四个byte按小端模式组成一个int,共256*256个int，下标对应要查询IP的前两个IP字段组成的Int值，数值为areaDataIndex区的下标（即IpIndex区存储的为指针，指向areaIndex区）。例如，对于IP 为115.236.173.94，前两个IP字段为115和236，那么115.236.173.94详细地域和运营商数据的索引在areaDataIndex区的大致位置，就可以缩小为areaDataIndex[115*256+236]~areaDataIndex[115*256+236+1]之间，而无需扫描全部的areaDataIndex中全部数据，仅仅查询areaDataIndex[115*256+236]~areaDataIndex[115*256+236+1]之间的数据即可。<br>

## 4、areaDataIndex区：
>该区域每9个byte为一组，共Counter组，其中counter为Ip库中记录的条数，即每9byte对应IP库中一条记录的索引，且与IP库中数据一一对应，IP库中第一条记录对应该区第一组(9Byte)字节,IP库中第二条对应第2组(9Byte)字节。<br>
>每组中9byte可以分成3部分：<br>
>* byte[0]~byte[3]:存放当前记录的endIP值,大端模式，解析成Long型。
>* byte[4]~byte[6]:存放该记录对应的地域和运营商信息(存储在AreaData中以\t分隔的字节数组)的索引（AreaData中的下标），小端模式。
>* byte[7]~byte[8]:当前记录中地域和运营商信息(存储在AreaData中以\t分隔的字节数组)所占字节byte长度。<br>
>为方便解释和操作，定义三个数组（其中ipDataCount为IP库中包含的记录数）：<br>
>* IPIndexData1[ipDataCount]：对应于上面的byte[0]~byte[3]
>* IPIndexData2[ipDataCount]：对应于上面的byte[4]~byte[6]
>* IPIndexData3[ipDataCount]：对应于上面的byte[7]~byte[8]

## 5、AreaData区：
>记录所有IP库中所有的地域和运营商信息，字段间以\t分隔;<br>
>例如："中国\t上海\t上海\t浦东\t电信"

<br>
-----------------------

# 五、县区IP库的datx数据结构
## areaDataIndex区：
>* 除areaDataIndex区域外，其他区域结构与地市IP库一致，
>* 县区IP库中areaDataIndex区域为13个byte一组分别为：
>* byte[0]~byte[3]:存放当前记录的startIP值,大端模式;
>* byte[4]~byte[7]:存放当前记录的endIP值,大端模式;
>* byte[8]~byte[11]:存放该记录对应的地域和运营商信息(存储在AreaData中以\t分隔的字节数组)的索引（AreaData中的下标），小端模式。
>* byte[12]:当前记录中地域和运营商信息(存储在AreaData中以\t分隔的字节数组)所占字节byte长度。<br>

<br>
----------------------

# 六、下载数据库的脚本


## 下载IP库的shell脚本地址:https://github.com/Moowei/ip-seeker/blob/master/src/shell/download_ip_file.sh

## 脚本使用说明：
###   ./download_ip_file.sh 1 #下载IP数据库(datx)
###   ./download_ip_file.sh 2 #下载县区数据库(datx)
###   ./download_ip_file.sh 3 #下载手机号归属地(txt)

# 此外：
## 下载IP库的Python版脚本地址:https://github.com/Moowei/ip-seeker/blob/master/src/shell/download_ip_file.py
## 下载IP库的PHP版脚本地址:https://github.com/Moowei/ip-seeker/blob/master/src/shell/download_ip_file.php
