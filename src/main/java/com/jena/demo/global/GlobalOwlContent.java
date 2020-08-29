package com.jena.demo.global;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalOwlContent {

    private String baseUrl;

    private List<String> classNameList;

    private List<String> namedIndividualList;

    private List<String> objectPropertyList;
}
