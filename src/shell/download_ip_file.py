# -*- coding: utf-8 -*-

'IP file download'
__author__ = 'moowei'

"""
@version: 1.1
@author: moowei
@contact: 990624607@qq.com
@software: PyCharm
@file: download_ip_file.py
@time: 2018/04/27 14:09
"""

import requests
import time
import sys

'''
version_url     检查IP库版本的URL地址
download_url    IP库下载地址
chunk_size      下载IP库时缓冲区大小
file_name       IP库下载到本地时使用的文件名
download_path   IP库下载的本地哪个目录下
'''
#数据下载
def start_download(version_url,download_url,chunk_size,file_name,download_path):
    #当前日期
    current_date = time.strftime('%Y%m%d',time.localtime(time.time()))
    #获取Http request
    version_http_responce = requests.get(version_url)
    #数据版本号
    current_version = version_http_responce.content
    #下载文件路径
    download_file_path = str(download_path)+'/'+str(file_name)
    if(current_version.replace('-','') == current_date):
        file_http_responce = requests.get(download_url, stream=True)
        with open(download_file_path, "wb") as ipfile:
            for chunk in file_http_responce.iter_content(chunk_size):
                if chunk:
                    ipfile.write(chunk)
    else:
        print('数据未更新\n\t数据版本号：'+current_version.replace('-','')+'\t当前日期：'+current_date)

#参数初始化
def set_property(arg):
    #缓冲区大小
    chunk_size=3072
    
    # IP库每日高级版（每天更新，更新时间 6:00 - 9:00）
    ip_version_url = "https://user.ipip.net/download.php?a=version&token=*****";#示例：2018-03-20
    ip_datx_url = "https://user.ipip.net/download.php?type=datx&token=*****";
    ip_txtx_url = "https://user.ipip.net/download.php?type=txtx&token=*****";

    # /手机号码归属地库(每月更新，更新日期不定)
    phonenum_version_url = "https://user.ipip.net/download.php?a=version&token=*****";#示例：20180308
    phonenum_txt_url = "https://user.ipip.net/download.php?token=*****";

    #IP库区县版(每半月更新，更新日期不定)
    quxian_version_url = "https://user.ipip.net/download.php?a=version&token=*****";#示例：2018-03-18
    quxian_datx_url = "https://user.ipip.net/download.php?type=dat&token=*****";
    quxian_txtx_url = "https://user.ipip.net/download.php?token=*****";
    print('参数arg: '+arg)
    if(arg == '1'):
        print('\n每日IP库')
        return (ip_version_url,ip_datx_url,chunk_size,"mydata4vipday2.datx")
    elif(arg == '2'):
        print('\n区县IP库')
        return (quxian_version_url,quxian_datx_url,chunk_size,"quxian.datx")
    elif(arg == '3'):
        print('\n手机号归属库')
        return (phonenum_version_url,phonenum_txt_url,chunk_size,"phone_number.txt")
    else:
        return ('11','11','11')

if __name__=='__main__':
    arg = sys.argv[1]
    download_path = sys.argv[2]
    #获取初始化参数
    (version_url,download_url,chunk_size,file_name) = set_property(arg)
    if(version_url == '11'):
        print('参数输入错误')
    else:
        #下载IP库
        start_download(version_url,download_url,chunk_size,file_name,download_path)
        