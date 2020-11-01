# coupon_lock
核心逻辑：
* 1.查询类目表中数量
* 2.如果查出数量大于0，
* 3.添加抢券记录
* 4.并将数量减一
>数据库锁应用
> * 创建抢券接口，合理使用数据库锁。防止出现多抢或错强
> * 使用postMan模拟高并发，观测抢票结果
> * URL: **localhost:8082/grabCouponRecordDbLock?userName=hss&secretKey=HEQIZHENG**
> * 步骤一中添加排他锁
