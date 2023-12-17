```yaml
props:
    sql-show: true
#mode:
#  type: Cluster
#  repository:
#    type: ZooKeeper
#    props:
#      namespace: governance
#      server-lists: localhost:2181
#      retryIntervalMilliseconds: 500
#      timeToLiveSeconds: 60
dataSources:
  ds1:
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/sharding_demo?useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true&allowMultiQueries=true
    username: "root"
    password: "040202"
  ds2:
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3307/sharding_demo?useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true&allowMultiQueries=true
    username: "root"
    password: "wdnmd2004"

rules:
- !SHARDING
  tables:
    t_user:  #虚拟的逻辑表
      actualDataNodes: ds1.t_user #实际的表
    t_order: #虚拟的逻辑表
      actualDataNodes: ds2.t_order #实际表

```

使用水平分片，配置分片规则的时候，最好将经常作为查询条件的字段作为分片字段，如这张表所依赖的外键字段，这样可以在分片的时候会有优化，查询的表会少

```java
props:
    sql-show: true
#mode:
#  type: Cluster
#  repository:
#    type: ZooKeeper
#    props:
#      namespace: governance
#      server-lists: localhost:2181
#      retryIntervalMilliseconds: 500
#      timeToLiveSeconds: 60
dataSources:
  ds1:
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/sharding_demo?useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true&allowMultiQueries=true
    username: "root"
    password: "040202"
  ds2:
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3307/sharding_demo?useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true&allowMultiQueries=true
    username: "root"
    password: "wdnmd2004"

rules:
- !SHARDING
  tables:
    t_user:  #虚拟的逻辑表
      actualDataNodes: ds1.t_user #实际的表
#      tableStrategy:
#        standard:
#          shardingColumn:  id #分片的列
#          shardingAlgorithmName:  order_table_inline_id #分片算法,根据该列分配到不同库与表当中

    t_order: #虚拟的逻辑表 order订单表
      actualDataNodes: ds$->{1..2}.t_order_$->{0..1} #配置多个表，支持inline表达式
      tableStrategy:  #配置分表策略
        standard:
          shardingColumn:  id #分片的列
          shardingAlgorithmName:  order_table_inline_id #分片算法,根据该列分配到不同库与表当中
      databaseStrategy:
        standard:  #配置分库策略
          shardingColumn: id
          shardingAlgorithmName: database_inline_id
      keyGenerateStrategy:
        column: id
        keyGeneratorName: alg_snowflake
#  defaultDatabaseStrategy:
#    standard:  #配置分库策略
#      shardingColumn: id
#      shardingAlgorithmName: database_inline_id
    t_order_item: #虚拟的逻辑表 order订单表
      actualDataNodes: ds$->{1..2}.t_order_item_$->{0..1}
      tableStrategy:  #配置分表策略
        standard:
          shardingColumn:  id #分片的列
          shardingAlgorithmName:  order_table_inline_id
      databaseStrategy:
        standard:  #配置分库策略
          shardingColumn: id
          shardingAlgorithmName: database_inline_id
      keyGenerateStrategy:
        column: id
        keyGeneratorName: alg_snowflake

  shardingAlgorithms:
    database_inline_id:  #这个算法用来分库
      type: INLINE  #自定义行表达式分片算法
      props:
        algorithm-expression: ds$->{id % 2 + 1} #通过行表达式动态计算出该分配到哪个数据源,取奇数偶数
    order_table_inline_id:  #这个算法用来分
      type: INLINE  #自定义行表达式分片算法
      props:
        algorithm-expression: t_order_$->{id % 2} #通过行表达式动态计算出该分配到哪个数据源,取奇数偶数
    order_table_inline_mod_id:  #取模
      type: MOD
      props:
        sharding-count: 2
  keyGenerators:
    alg_snowflake:
      type: SNOWFLAKE
##配置广播表 ,意思是每个数据源当中都有一个对应得到表
- !BROADCAST
  tables:
    - t_dict
```

## 广播表：

指所有的分片数据源中都存在的表，表结构及其数据在每个数据库中均完全一致。适用于数据量不大且需要与海量数据的表进行关联查询的场景，例如:字典表。
广播具有以下特性:
(1)插入、更新操作会实时在所有节点上执行，保持各个分片的数据一致性
(2)查询操作，只从一个节点获取
(3)可以跟任何一个表进行JOIN 操作

## maven依赖

```xml
 <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.3.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>shardingsphere-jdbc-core</artifactId>
            <version>${shardingsphere.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>shardingsphere-cluster-mode-repository-zookeeper</artifactId>
            <version>${shardingsphere.version}</version>
        </dependency>
        <dependency>
            <groupId>org.yaml</groupId>
            <artifactId>snakeyaml</artifactId>
            <version>1.33</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

    </dependencies>
```

## springboot 配置

```properties
# 配置 DataSource Driver
spring.datasource.driver-class-name=org.apache.shardingsphere.driver.ShardingSphereDriver
## 指定 YAML 配置文件
spring.datasource.url=jdbc:shardingsphere:classpath:sharding-jdbc-level.yaml
```

## MYBATIS

在MyBatis中，如果你的关联查询的`select`方法需要多个参数，你可以通过`java.util.Map`来传递参数。你需要在`<association>`标签的`column`属性中指定一个`Map`，这个`Map`的键是参数名，值是参数值。

首先，你需要修改你的`select`方法，使其接受一个`Map`参数：

```xml
<select id="selectByIdAndDate" parameterType="map" resultType="com.lxl.business.domain.DailyTrain">
    select *
    from `daily_train`
    where `id` = #{id} and `date` = #{date}
</select>
```

然后，你需要在`<association>`标签中指定一个`Map`，并设置`select`属性为新的`select`方法：

```xml
<association property="dailyTrain" column="{id=daily_train_id, date=start_date}" select="com.lxl.business.mapper.DailyTrainMapper.selectByIdAndDate"/>
```

这样，当你查询`DailyTrainTicket`时，MyBatis就会根据`daily_train_id`和`start_date`去查询对应的`DailyTrain`，并设置到`DailyTrainTicket`的`dailyTrain`属性中。