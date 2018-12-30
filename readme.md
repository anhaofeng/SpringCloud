#####1.注册消费者
    a.在user、eureka上
    <dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-eureka</artifactId>
    		</dependency>
    b.在main @EnableEurekaServer
    c.eureka 
    eureka:
      client:
        register-with-eureka: false
        fetch-registry: false
        service-url:
          defaultZone: http://user:password123@localhost:8761/eureka
     使用ip访问的话
      instance:
         prefer-ip-address: true
         instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}
    d.eruka添加验证
    security:
      basic:
        enabled: true
      user:
        name: user
        password: password123
      导入依赖
      <dependency>
      			<groupId>org.springframework.boot</groupId>
      			<artifactId>spring-boot-starter-security</artifactId>
      		</dependency>
     e.健康检查
     <dependency>
     			<groupId>org.springframework.boot</groupId>
     			<artifactId>spring-boot-starter-actuator</artifactId>
     		</dependency>
     eureka:
       client:
         healthcheck:
           enabled: true
2.ribbon实现调用注册的负载均衡

    a.在消费者movie的restTemplate加@RibbonClient 并声明configuration
    springCloud默认轮循
    b.configuration 中声明规则为randomRule 不能和main放在同一目录 scan会重复扫描
    或者使用注解忽视规则 
    c.ribbon脱离eureka的使用 使用指定节点
    microservice-provider-user:
      ribbon:
        listOfServers: localhost:7900
        
3.feign配置

    a.导入依赖
    b.配置feign 加configuration 调用接口
    c.加日志
    logging:
      level:
        com.cloud.feign.UserFeignClient: DEBUG
4.eureka高可用
    
    配置host名
      spring:
        application:
          name: EUREKA-HA
      ---
      server:
        port: 8761
      spring:
        profiles: peer1
      eureka:
        instance:
          hostname: peer1
        client:
          serviceUrl:
            defaultZone: http://peer2:8762/eureka/,http://peer3:8763/eureka/
      ---
      server:
        port: 8762
      spring:
        profiles: peer2
      eureka:
        instance:
          hostname: peer2
        client:
          serviceUrl:
            defaultZone: http://peer1:8761/eureka/,http://peer3:8763/eureka/
      ---
      server:
        port: 8763
      spring:
        profiles: peer3
      eureka:
        instance:
          hostname: peer3
        client:
          serviceUrl:
            defaultZone: http://peer1:8761/eureka/,http://peer2:8762/eureka/
    配置:
        spring.cloud.consul.discovery.hostname
        访问服务器时使用的主机名
        eureka.instance.appname
        
        unknown
        获取要在eureka注册的应用程序的名称
        
        eureka.dashboard.enabled
        标志以启用Eureka仪表板。默认值为true。
        
        eureka.dashboard.path
        到Eureka仪表板（相对于servlet路径）的路径。默认为“/”
        
        eureka.client.registry-fetch-interval-seconds
        30
        指示从eureka服务器获取注册表信息的频率（以秒为单位）
        
        eureka.client.register-with-eureka
        true
        指示此实例是否应将其信息注册到eureka服务器以供其他人发现。
        在某些情况下，您不希望发现实例，而您只想发现其他实例。
        
        eureka.client.fetch-registry
        true
        指示该客户端是否应从eureka服务器获取eureka注册表信息。
        
        eureka.client.fetch-remote-regions-registry
        逗号分隔将获取eureka注册表信息的区域列表。必须为availabilityZones返回的每个区域定义可用性区域。否则，将导致发现客户端启动失败。
5.eureka常见问题
    
    a.更改控制台信息
    eureka.datacenter=cloud
    eureka.environment=product
    b.Eureka开启自我保护的提示
    server端:
    eureka.server.enable-self-preservation			（设为false，关闭自我保护主要）
    eureka.server.eviction-interval-timer-in-ms     清理间隔（单位毫秒，默认是60*1000）
    client端：
    eureka.client.healthcheck.enabled = true				开启健康检查（需要spring-boot-starter-actuator依赖）
    eureka.instance.lease-renewal-interval-in-seconds =10		租期更新时间间隔（默认30秒）
    eureka.instance.lease-expiration-duration-in-seconds =30  租期到期时间（默认90秒）
6.hystrix断路器
    
    a.加pom
    b.main加 @EnableCircuitBreaker
    c.controller 加回调注解@HystrixCommand(fallbackMethod = "findByIdFallback")
      并写 public User findByIdFallback(Long id)方法 回调与参数类型一致
    d. @HystrixCommand(fallbackMethod = "findByIdFallback", commandProperties = @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE"))
     回调函数和请求函数在一个线程中
    f.fallback,fallbackfactory只能用一个
    g.超时问题
    设置熔断器检测时间（默认1秒）
    hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 5000
    关闭熔断器超时检测时间功能，也就是不超时
    hystrix.command.default.execution.timeout.enabled: false
    直接禁用feign的hystrix
    feign.hystrix.enabled: false (不好用)
7.dashboard配置
      
    a.	<dependency>
      			<groupId>org.springframework.cloud</groupId>
      			<artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
      		</dependency>
    b.@EnableHystrixDashboard
8.turbine配置

    a.<dependency>
      			<groupId>org.springframework.cloud</groupId>
      			<artifactId>spring-cloud-starter-turbine</artifactId>
      		</dependency>
    b.默认集群
    turbine:
      aggregator:
        clusterConfig: default
      appConfig: microservice-consumer-movie-ribbon-with-hystrix,microservice-consumer-movie-feign-with-hystrix
      clusterNameExpression: "'default'"
    c.@EnableTurbine
    d.http://192.168.0.207:8031/turbine.stream
    e.http://localhost:8030/hystrix/ dashboard显示
    f.如果消费者配置server.context-path:/ribbon
       需要配置 instance.home-page-url-path:/ribbon   默认为'/'
       也可以在相应的turbine中配置
       turbine.instanceUrlSuffix.MICROSERVICE-CONSUMER-MOVIE-RIBBON-WITH-HYSTRIX2: /ribbon/hystrix.stream
     turbine 和dashboard 做集群监控的
    g.也可以配置一个服务端口和一个管理端口 就不用上述配置了
    消费者management: # spring-boot-starter-acturator
         port: 8081
         instance:
             prefer-ip-address: true
             metadata-map:
               management.port: 8081
9.zuul的配置见代码
     