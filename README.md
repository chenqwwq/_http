# _http

2020.03.20
找工作期间范围不怎么想看书，就想撸个码

简单的Http服务器，主要就是对Request的解析和对Response的包装。
希望是有Nio，Bio，Netty三种实现方法，加上配置文件切换

晚上试验pdf展示，直接将pdf文件塞到响应体，并且修改`Content-Type`，浏览器显示异常。

趁今天心态爆炸，估计也睡不着整理下Http相关的内容：
1. Http（HyperText Transfer Protocol）的中文叫做超文本传输协议，超文本就是不单单是文本，还可以传送图片、音频、视频等信息。
2. Http是基于TCP的。
3. Http有几个特性，简单，灵活，无状态，即Http协议中不保存客户端的任何以往数据。



Http请求体格式
| METHOD | URI | HTTP_VERSION |
| HTTP_HEADERS

| HTTP_BODY

以上就是Http的请求体的格式，
METHOD就是Http的请求方法，最常用的例如GET，POST
URI就是请求的资源地址，
URI称为统一资源标志符，URL称为统一资源定位符号

// 以下拿斗鱼的URL说明
|协议名|主机名|端口|URI|锚点
https://www.douyu.com/9999#mark
协议名除了https，http之外还有ftp等
主机名可以简单理解为域名，经过DNS域名系统转化之后，变成IP
端口，协议都有默认端口，http为80，https为443
URI就是希望请求的资源，可以是任何形式的东西
锚点就是页面中的一个标记，转入之后可以定位到具体为止

GET请求后面可能还会跟着`?key=value&key2=value2`这样的查询字段(query string)

Http响应体格式
|HTTP_VERSION|RETURN_CODE|RETURN_STRING|
|HTTP_HEADERS

|HTTP_BODY

RETURN_CODE和RETURN_STRING就是返回码的组合，常见的有以下几个
1. 200 OK 请求成功
2. 500 Internal Server Error 服务器内部错误
3. 404 NOT FOUND 资源未找到
4. 400 BAD REQUEST 请求体解析错误
5. 403 Forbidden 权限不足


