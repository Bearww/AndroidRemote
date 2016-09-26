package com.test.wu.remotetest;

public class MemberStatus {

    public static enum STATUS {
        NONE,

        VISITOR,          // 訪客
        PARTICIPANE,    // 與會者
        CHAIRMAN,      // 主席
        ADMIN;          // 主控
    }

    private STATUS mStatus = STATUS.NONE;

    public MemberStatus() {
    }

    public void changeStatus(STATUS status) {
        mStatus = status;
    }
}
