1.config-server配置
    
    a.导入依赖,更改配置,加入注解
    b.Spring Cloud Config服务器从git存储库（必须提供）为远程客户端提供配置：
    spring:
        cloud:
          config:
            server:
              git:
                uri: https://github.com/spring-cloud-samples/config-repo
    c.访问路径
    /{application}/{profile}[/{label}]
    /{application}-{profile}.yml
    /{label}/{application}-{profile}.yml
    /{application}-{profile}.properties
    /{label}/{application}-{profile}.properties
    默认master分支 8080/profile/dev
2.config-client配置
    
    a.配置远程端口要在bootstrap.yml配置 有些配置一样的话 优先加载bootstrap 所以只加载一次
    uri配置的有默认的8888
    spring:
      cloud:
        config:
          uri: http://localhost:8080
          profile: dev
          label: master   # 当configserver的后端存储是Git时，默认就是master 
      application:
        name: foobar
3.config client,server配置
  
    spring:
      cloud:
        config:
          discovery:
            enabled: true
            service-id: microservice-config-server-eureka
      application:
        name: foobar
        
    eureka:
      client:
        serviceUrl:
          defaultZone: http://user:password123@localhost:8761/eureka
      instance:
        prefer-ip-address: true
4.spring config配置刷新
    
    在需要刷新的类上加上@RefreshScope,尽量不要和@Configuration一块使用,加上actuator依赖
    使用/refresh接口进行刷新
    使用rabbitMq 加依赖,curl -X POST http://localhost:8081/bus/refresh
    局部刷新bus/refresh?destination=applicationName:port
5.配置IDEArun dashboard
        
        https://blog.csdn.net/chinoukin/article/details/80577890
        