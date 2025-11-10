package com.malugu.springboot_starter_project.config.graphql;

import graphql.language.OperationDefinition;
import graphql.parser.Parser;

import java.util.Optional;

public class GraphQLOperationUtils {
	public static OperationDefinition.Operation getOperationType(String query) {
		try {
			// Parse the query document
			var document = new Parser().parseDocument(query);

			// Find the first operation definition
			Optional<OperationDefinition> operationDef = document.getDefinitions().stream()
					.filter(OperationDefinition.class::isInstance).map(OperationDefinition.class::cast).findFirst();

			// Return the operation type or default to QUERY
			return operationDef.map(OperationDefinition::getOperation).orElse(OperationDefinition.Operation.QUERY);

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return OperationDefinition.Operation.QUERY; // Fallback to query
		}
	}
}
