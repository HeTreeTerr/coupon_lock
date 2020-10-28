package com.hss.enums;

public enum LockEnum {

    SHARE_LOCK("share_lock"),FOT_UPDATE("for_update");

    // 成员变量
    private String name;

    // 构造方法
    private LockEnum(String name) {
        this.name = name;
    }
    //覆盖方法
    @Override
    public String toString() {
        return this.name;
    }
}
