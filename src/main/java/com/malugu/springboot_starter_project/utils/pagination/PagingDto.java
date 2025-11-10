package com.malugu.springboot_starter_project.utils.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PagingDto {
    private int pageIndex;
    private int pageSize;
    private int totalPage;
    private boolean lastPage;

}
