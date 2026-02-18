package com.posit.posit.domain.store.dto.request;

import java.util.List;

public record ConvinceUpdate(
        List<String> convinces
) {
    public static ConvinceUpdate of (List<String> convinceList) {
        return new ConvinceUpdate(convinceList);
    }
}
