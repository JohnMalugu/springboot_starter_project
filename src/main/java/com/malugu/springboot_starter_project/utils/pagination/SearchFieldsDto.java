package com.malugu.springboot_starter_project.utils.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchFieldsDto {
    private String fieldName;
    private String fieldValue;
    private SearchOperationType searchType;
    private List<String> fieldValues;

    public SearchFieldsDto(String fieldName, String fieldValue, SearchOperationType searchType) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.searchType = searchType;
    }
}
