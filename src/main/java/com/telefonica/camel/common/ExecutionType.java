package com.telefonica.camel.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExecutionType {

    MANUAL("manual"), SCHEDULED("scheduled");

    private final String label;

}
