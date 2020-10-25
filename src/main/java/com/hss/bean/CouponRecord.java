package com.hss.bean;

/**
 * 优惠券记录
 */
public class CouponRecord {
    /** 编号 */
    private Integer id;
    /** 类目 */
    private CouponClass couponClass;
    /** 用户名 */
    private String userName;
    /** 排名 */
    private Integer seqNo;

    public CouponRecord() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CouponClass getCouponClass() {
        return couponClass;
    }

    public void setCouponClass(CouponClass couponClass) {
        this.couponClass = couponClass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    @Override
    public String toString() {
        return "CouponRecord{" +
                "id=" + id +
                ", couponClass=" + couponClass +
                ", userName='" + userName + '\'' +
                ", seqNo=" + seqNo +
                '}';
    }
}
