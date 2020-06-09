# AWS FTP

## 总体结构

![ftp](/aws-ftp.jpg)



### 绿线部分

1. FTP Client 连接 FTP Server，并发送 `USER user-1\r\n PASS pass-1\r\n` 指令，表示使用 user-1/pass-1 用户名/密码登录
2. FTP Server 接收到登录指令后，向 api-gateway 发起用户认证 REST 请求，`uri=/servers/${server-id}/users/user-1 ,header=Password: pass-1` 
3. api-gateway 处理用户认证 REST 请求，调用一个 lambda 函数，访问 SecretsManager 使用 user-1 查找对应的密码文件并读取文件内容，然后根据文件内容（在当前应用中是一个 ftp 用户信息的 json）, 通过比对 pass-1 和 json 中的 Password 字段是否一致，一致则将表示身份验证通过，返回整个用户信息 json
4. FTP Server 根据返回的用户信息 json 来判断是否登录成功，当前用户 role, home 目录等信息

### 蓝线部分

1. 用户登录成功后，希望下载 home 目录下某个文件，FTP Client 会发送 `PWD\r\nPASV\r\nRETR xxx.txt\r\n` 指令给 FTP Server
2. FTP Server 收到指令后会从对应的 S3 bucket 中下载对应的文件返回给 FTP Client



## FTP 



## S3



## SecretsManager