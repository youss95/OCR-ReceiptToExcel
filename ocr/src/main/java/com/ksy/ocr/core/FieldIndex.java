package com.ksy.ocr.core;

import lombok.Getter;

@Getter
public enum FieldIndex {

    COMP("사업자번호",2),
    TOTAL("합계",1),
    STORE("가맹점명", 1),
    DATE("거래일자",2);

    private final int index;
    private final String name;

    FieldIndex(String name, int index) {
        this.index = index;
        this.name = name;
    }



}
