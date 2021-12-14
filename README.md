![CI](https://github.com/vgearen/pikpak-webdav/actions/workflows/CI.yml/badge.svg)

# Pikpak Webdav

本项目实现了Pikpak网盘的Webdav协议。



## 使用方法

**Pakpik必须设置代理使用！**
**Pakpik必须设置代理使用！**
**Pakpik必须设置代理使用！(当然如果你的服务器在墙外当我没说)**

- [Jar包运行](#jar包运行)
- [Docker](#Docker)
- [Docker Compose](#Docker-Compose)



### Jar包运行
[点击下载JAR](https://github.com/VGEAREN/pikpak-webdav/releases/latest)

```bash
[root@localhost ~]# java -jar pikpak-webdav.jar --pikpak.username="username" --pikpak.password="password" --pikpak.proxy.host="" --pikpak.proxy.port="" --pikpak.proxy.proxyType="HTTP/SOCKS/DIRECT"
```

### Docker

```bash
[root@localhost ~]# docker run -d --name=pikpak-webdav --restart=unless-stopped --network=host -v /etc/localtime:/etc/localtime -e TZ="Asia/Shanghai" -e JAVA_OPTS="-Xmx512m" -e SERVER_PORT="8080" -e PIKPAK_USERNAME="PIKPAK_USERNAME" -e PIKPAK_PASSWORD="PIKPAK_PASSWORD" -e PIKPAK_PROXY_HOST="" -e PIKPAK_PROXY_PORT="" -e PIKPAK_PROXY_PROXY-TYPE="HTTP/SOCKS/DIRECT"  vgearen/pikpak-webdav
```

默认认证账号密码admin/admin，需要修改添加参数` -e PIKPAK_AUTH_USER_NAME="USERNAME" -e PIKPAK_AUTH_PASSWORD="PASSWORD"` 

PIKPAK_PROXY_PROXY-TYPE参数为HTTP或SOCKS或DIRECT

**请确认容器内的服务能正常访问代理，否则会启动失败！**

### Docker Compose

```yaml
version: '3'
services:
  pikpak-webdav:
    image: vgearen/pikpak-webdav
    container_name: pikpak-webdav
    restart: unless-stopped
    volumes:
      - /etc/localtime:/etc/localtime
    network_mode: "host" # 使用宿主机的代理
    tty: true
    environment:
      - TZ=Asia/Shanghai
      - PIKPAK_USERNAME=<change me>
      - PIKPAK_PASSWORD=<change me>
      - PIKPAK_PROXY_HOST=<change me>
      - PIKPAK_PROXY_PORT=<change me>
      - PIKPAK_PROXY_PROXY-TYPE=<change me>
      - SERVER_PORT=8080
      # - PIKPAK_AUTH_USER_NAME=<change me>
      # - PIKPAK_AUTH_PASSWORD=<change me>

```



## 功能

1. 文件列表
2. 复制
3. 移动
4. 重命名
5. 删除



## License
This work is released under the MIT license. A copy of the license is provided in the [LICENSE](./LICENSE) file.
