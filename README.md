# coupon_lock

## 1 demo1(抢券系统)
> 分布式锁学习和应用
### 1.1 核心逻辑
1. 查询类目表中数量
2. 如果查出数量大于0，
3. 添加抢券记录
4. 并将数量减一

### 1.2 使用java锁
* synchronized
* lock  
java的锁可以保证单节点,但对于集群环境无能为力;

### 1.3 数据库锁应用
1. 创建抢券接口，合理使用数据库锁。防止出现多抢或错强
2. 使用jmeter模拟高并发，观测抢票结果
3. URL: **localhost:8082/grabCouponRecordDbLock?userName=hss&secretKey=HEQIZHENG**

### 1.4 使用redisson分布式锁
1. 保证数据的一致性、可用性、分区容错性。
2. 提供丰富类型的锁，应对各种场景.
3. 核心原理：看门狗机制（保证锁的有效期限）、lua脚本（保证加锁、解锁的原子性）

## 2 demo2(售货系统)
> 手动实现分布式锁
### 2.1 系统环境
* java8
* redis4
* jmeter5.4.1

### 2.2 核心逻辑
1. 查询库存
2. 执行售货逻辑
3. 库存减一

### 2.3 实现类 
在com.hss.controller.handWrittenDistributedLock.DistributedLockDemoController中，一步步
改进分布式锁。  
核心点：
1. 保证加锁、解锁操作的原子性；
2. 加锁必定要解锁；
3. 给锁设置期限（既要保证，核心逻辑没有执行完，锁不能释放；又要保证，核心逻辑执行完，锁要及时释放；）

## 3 demo3(redis集群)
> 自行搭建redis集群（主从、哨兵、集群），通过项目去整合连接并使用集群

在application.properties中，通过 spring.redis.mode 属性，选择性连接redis服务。
实现了springBoot项目的redis客户端和redisson，整合各种并操作redis集群模式。 