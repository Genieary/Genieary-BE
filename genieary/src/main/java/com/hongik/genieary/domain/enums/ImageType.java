package com.hongik.genieary.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    PROFILE("profile"),
    DIARY("diary");

    private final String directory;
}