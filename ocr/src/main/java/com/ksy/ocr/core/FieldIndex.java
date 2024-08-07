package com.ksy.ocr.core;

import lombok.Getter;

@Getter
public enum FieldIndex {

    COMP(2),
    TOTAL(1),
    DATE(2);

    private final int index;

    FieldIndex(int index) {
        this.index = index;
    }


}
