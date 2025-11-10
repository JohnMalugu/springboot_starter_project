package com.malugu.springboot_starter_project.utils.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDataDto {
	private SearchCombinationType searchCombinationType = SearchCombinationType.OR;
	private PageableParam pageableParam;
	List<SearchFieldsDto> searchFields= new ArrayList<>();

	public SearchDataDto(List<SearchFieldsDto> searchFields, SearchCombinationType searchCombinationType) {
		this.searchCombinationType = searchCombinationType;
		this.searchFields = searchFields;
	}

	public SearchDataDto(List<SearchFieldsDto> searchFields) {
		this.searchFields = searchFields;
	}
}
