package com.malugu.springboot_starter_project.utils.pagination;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class PageableParam {
    private String sortBy;
    private String sortDirection;
    private Integer size;
    private Integer first;

    public PageableParam(String sortBy, String sortDirection, Integer size, Integer first) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.size = size;
        this.first = first;
    }

    public PageableParam(Integer size, Integer first) {
        this.size = size;
        this.first = first;
    }

}

