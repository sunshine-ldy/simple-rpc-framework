/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.liyue2008.rpc.server;

import com.github.liyue2008.rpc.NameService;
import com.github.liyue2008.rpc.RpcAccessPoint;
import com.github.liyue2008.rpc.hello.HelloService;
import com.github.liyue2008.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.URI;

/**
 * 服务器端
 *
 * @author LiYue
 * Date: 2019/9/20
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        // 获取HelloService规范名称
        String serviceName = HelloService.class.getCanonicalName();
        // URI create
        URI nameServiceUri = URI.create("jdbc:hsqldb:hsql://localhost/nameservice");
        // 账号密码
        System.setProperty("nameservice.jdbc.username", "SA");
        System.setProperty("nameservice.jdbc.password", "");
        // 创建HelloService实现类
        HelloService helloService = new HelloServiceImpl();
        logger.info("创建并启动RpcAccessPoint...");
        /**
         * SPI方式获取PRC框架对外提供服务的实例
         */
        try (RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
             Closeable ignored = rpcAccessPoint.startServer()) {
            // 获取nameService
            NameService nameService = rpcAccessPoint.getNameService(nameServiceUri);
            assert nameService != null;
            logger.info("向RpcAccessPoint注册{}服务...", serviceName);
            // 服务端注册服务的实现实例
            URI uri = rpcAccessPoint.addServiceProvider(helloService, HelloService.class);
            logger.info("服务名: {}, 向NameService注册...", serviceName);
            // 向NameService注册服务
            nameService.registerService(serviceName, uri);
            logger.info("开始提供服务，按任何键退出.");
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
            logger.info("Bye!");
        }
    }

}
