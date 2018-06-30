#!/bin/sh
#下载IP数据库的脚本，支持指定下再路径和日志输出，（set -x开启追踪;set +x关闭追踪;set -o查看追踪）
#author:moowei
#date:  2018-05-02
#shellName:脚本名称 download_ip_file.sh
#脚本使用说明：
#   ./download_ip_file.sh 1 #下载IP数据库(datx)
#   ./download_ip_file.sh 2 #下载县区数据库(datx)
#   ./download_ip_file.sh 3 #下载手机号归属地(txt)




#引入日志函数
#. ./log.sh

#全局变量初始化
#数据库状态，是否更新：0未更新；1已更新
flag=0
#日志保存的路径，中间产生的日志会写到该路径下，名称与当前脚本文件名一致
#log_path="/tmp/moowei_test"
log_path="/tmp/ipip.net"
#数据库保存路径,脚本调用时，默认将文件下载到该路径
#svae_path="/tmp/moowei_test"
svae_path="/tmp/ipip.net"
#HDFS 的目录
hdfs_ip_path="/user/datahome/hive_udf"


#说明：
#   日志函数
#使用示例：
#   log debug "there are $# parameters:$@"
#   log info "funcname:${FUNCNAME[@]},lineno:$LINENO"
#   log warn "funcname:${FUNCNAME[0]},lineno:$LINENO"
#   log error "the first para:$1;the second para:$2"   
function log {
    local msg;local logtype
    logtype=$1
    msg=$2
    datetime=`date +'%F %H:%M:%S'`
    #使用内置变量$LINENO不行，不能显示调用那一行行号
    #logformat="[${logtype}]\t${datetime}\tfuncname:${FUNCNAME[@]} [line:$LINENO]\t${msg}"
    logformat="[${logtype}]\t${datetime}\tfuncname: ${FUNCNAME[@]/log/}\t[line:`caller 0 | awk '{print$1}'`]\t${msg}"
    #funname格式为log error main,如何取中间的error字段，去掉log好办，再去掉main,用echo awk? ${FUNCNAME[0]}不能满足多层函数嵌套
    {  
    case $logtype in 
        debug)
            [[ $loglevel -le 0 ]] && echo -e "\033[30m${logformat}\033[0m" ;;
        info)
            [[ $loglevel -le 1 ]] && echo -e "\033[32m${logformat}\033[0m" ;;
        warn)
            [[ $loglevel -le 2 ]] && echo -e "\033[33m${logformat}\033[0m" ;;
        error)
            [[ $loglevel -le 3 ]] && echo -e "\033[31m${logformat}\033[0m" ;;
    esac
    }
    #| tee -a $logfile
}

#说明：
#   参数初始化函数，根据输入的数据库编码，返回数据库对应的版本校验地址（URL）、数据下载地址（URL）、数据库下载到本地所对应的文件名
#使用实例：
#   init_arg file_type version_url datx_url file_name
#输入参数：
#   file_type:要下载文件的编号（1:datx地市IP库；2:datx格式的区县IP库；3:txtx格式的手机号归属地）
#输出参数：（但调用函数时以下参数必须传入）
#   version_url:用于接收函数返回的数据版本号检测地址（URL）
#   datx_url:   用于接收函数返回的数据下载地址（URL）       
#   file_name:  用于接收函数返回的文件名称
function init_arg(){
    #缓冲区大小
    chunk_size=3072
    # IP库每日高级版（每天更新，更新时间 6:00 - 9:00）
    ip_version_url="https://user.ipip.net/download.php?a=version&token=****"
    ip_datx_url="https://user.ipip.net/download.php?type=datx&token=****"
    ip_txtx_url="https://user.ipip.net/download.php?type=txtx&token=****"

    # /手机号码归属地库(每月更新，更新日期不定)
    phonenum_version_url="https://user.ipip.net/download.php?a=version&token=****";#示例：20180308
    phonenum_txt_url="https://user.ipip.net/download.php?token=****";

    #IP库区县版(每半月更新，更新日期不定)
    quxian_version_url="https://user.ipip.net/download.php?a=version&token=****";#示例：2018-03-18
    quxian_datx_url="https://user.ipip.net/download.php?type=dat&token=****";
    quxian_txtx_url="https://user.ipip.net/download.php?token=****";
    case $1 in
    "1")
        log debug "目标库为：每日IP库" >> $log_path/$(basename -s .sh $0).log
        eval $2="'$ip_version_url'";
        #log debug "版本号URL：$ip_version_url" >> $log_path/$0.log
        eval $3="'$ip_datx_url'";
        #log debug "版本号URL：$ip_datx_url" >> $log_path/$0.log
        eval $4="'mydata4vipday2.datx'";
        log debug "下载文件：$svae_path/mydata4vipday2.datx" >> $log_path/$(basename -s .sh $0).log
        ;;
    "2")
        log debug "目标库为：区县IP库" >> $log_path/$(basename -s .sh $0).log
        eval $2="'$quxian_version_url'"
        eval $3="'$quxian_datx_url'"
        eval $4="'quxian.datx'"
        log debug "下载文件：$svae_path/quxian.datx" >> $log_path/$(basename -s .sh $0).log
        ;;
    "3")
        log debug "目标库为：手机号归属库" >> $log_path/$(basename -s .sh $0).log
        eval $2="'$phonenum_version_url'"
        eval $3="'$phonenum_txt_url'"
        eval $4="'phone_number.txt'"
        log debug "下载文件：$svae_path/phone_number.txt" >> $log_path/$(basename -s .sh $0).log
        ;;
    *)
        log error "init_arg参数错误" >> $log_path/$(basename -s .sh $0).log
        exit 2
        ;;
    esac
}

#说明：
#   检测数据库版本号，如果数据更新就下载数据库，否则不下载。
#使用示例：
#   if_download file_type download_path
#输入参数：
#   file_type:要下载的数据库编号（1:datx地市IP库；2:datx格式的区县IP库；3:txtx格式的手机号归属地）
#   download_path:文件保存路径（即文件下载到本地磁盘哪个目录下）
function if_download(){
    version_filename=version_$1.zip
    if [ $# -eq 2 ]; then
        file_type=$1 
        download_path=$2
        init_arg $file_type version_url datx_url file_name && log info "目标数据库：$file_name" >> $log_path/$(basename -s .sh $0).log
        #echo $version_url;echo $datx_url;
        echo "目标数据库：$file_name"
        # 版本号检测
        echo "临时保存路径：$download_path/$version_filename"
        wget -q -O $download_path/$version_filename $version_url
        version=$(tail -1 $download_path/$version_filename) 
        time=$(date "+%Y-%m-%d")
        current_version=`echo ${version//-/}` && log info "当前数据库版本：$current_version" >> $log_path/$(basename -s .sh $0).log
        current_date=`echo ${time//-/}` && log info "当前日期：$current_date" >> $log_path/$(basename -s .sh $0).log
        echo "当前库版本version:"$version"---"$current_version;echo "当前日期time:"$time"---"$current_date
        if [ "`echo ${version//-/}`" == "`echo ${time//-/}`" ] ;then
            echo "开始下载...." && log info "数据下载开始..." >> $log_path/$(basename -s .sh $0).log
            #echo "download_path:"$download_path;echo "version_url:"$version_url
            wget -q -O $download_path/$file_name $datx_url && log info "数据下载成功" >> $log_path/$(basename -s .sh $0).log
            echo "下载完成！！"
            return 1
            #exit 11
        else 
            log info "数据无更新" >> $log_path/$(basename -s .sh $0).log
            echo "数据无更新！"
            return 0
        fi
    else
        log error "if_download调用参数错误" >> $log_path/$(basename -s .sh $0).log
        return 0
    fi 
}


if [ $# -eq 1 ];then
    
    mkdir -p $log_path
    mkdir -p $svae_path
    echo `klist`
    klist > $log_path/$(basename -s .sh $0)_klist.log
    if_download $1 $svae_path
    if [ $? = 1 ];then
        case $1 in
        "1")
            hdfs dfs -rm $hdfs_ip_path/mydata4vipday2.datx || exit 1
            echo "开始上传HDFS...." && log info "开始上传HDFS..." >> $log_path/$(basename -s .sh $0).log
            hdfs dfs -put $svae_path/mydata4vipday2.datx $hdfs_ip_path/ && log info "数据上传HDFS成功" >> $log_path/$(basename -s .sh $0).log || exit 1
            echo "上传HDFS完成！！"
            ;;
        "2")
            hdfs dfs -rm $hdfs_ip_path/quxian.datx || exit 1
            echo "开始上传HDFS...." && log info "开始上传HDFS..." >> $log_path/$(basename -s .sh $0).log
            hdfs dfs -put $svae_path/quxian.datx $hdfs_ip_path/ && log info "数据上传HDFS成功" >> $log_path/$(basename -s .sh $0).log || exit 1
            echo "上传HDFS完成！！"
            ;;
        "3")
            hdfs dfs -rm $hdfs_ip_path/phone_number.txt || exit 1
            echo "开始上传HDFS...." && log info "开始上传HDFS..." >> $log_path/$(basename -s .sh $0).log
            hdfs dfs -put $svae_path/phone_number.txt $hdfs_ip_path/ && log info "数据上传HDFS成功" >> $log_path/$(basename -s .sh $0).log || exit 1
            echo "上传HDFS完成！！"
            ;;
        esac
    else 
        echo "数据无更新，上传失败" && log warn "数据上传失败" >> $log_path/$(basename -s .sh $0).log
    fi
fi

