package com.hss.bean;

/**
 * 优惠券类目
 */
public class CouponClass {
    /** 编号 */
    private Integer id;
    /** 名称 */
    private String name;
    /** 数量 */
    private Integer number;
    /** 秘钥 */
    private String secretKey;

    public CouponClass() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }


    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String toString() {
        return "CouponClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }
}
