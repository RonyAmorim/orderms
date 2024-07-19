package com.rony.orderms.controller.dto;

import java.util.List;
import java.util.Map;

public record ApiResponse<T>(
        Map<String, Object> sumary,
        List<T> data,
        PaginationResponse pagination
) {
}
