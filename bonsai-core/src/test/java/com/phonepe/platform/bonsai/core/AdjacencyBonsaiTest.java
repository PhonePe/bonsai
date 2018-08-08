package com.phonepe.platform.bonsai.core;

import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.core.*;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.query.filter.general.EqualsFilter;
import com.phonepe.platform.bonsai.models.KeyNode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 2:36 PM
 */
public class AdjacencyBonsaiTest {

    @Before
    public void setUp() {
        JsonPathSetup.setup();
    }

    @Test
    public void testBonsai() throws IOException, BonsaiError {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);
        Bonsai bonsai = new AdjacencyBonsai();

        Knot add = bonsai.add("home_page_1", MultiKnotData.builder()
                                                          .key("widget_1")
                                                          .key("widget_2")
                                                          .key("widget_3")
                                                          .build());
        Knot femaleKnot = bonsai.create(MultiKnotData.builder()
                                                     .key("icon_3")
                                                     .key("icon_1")
                                                     .key("icon_4")
                                                     .build());

        Knot widgetKnot1 = bonsai.add("widget_1", MultiKnotData.builder()
                                                               .key("icon_1")
                                                               .key("icon_4")
                                                               .key("icon_2")
                                                               .key("icon_3")
                                                               .build());
        bonsai.connect(widgetKnot1.getId(), Edge.builder()
                                                .condition(new EqualsFilter("$.gender", "female"))
                                                .pivot("gender")
                                                .knot(femaleKnot)
                                                .build());

        bonsai.connect(widgetKnot1.getId(), Edge.builder()
                                                .condition(new EqualsFilter("$.gender", "female"))
                                                .pivot("aaa")
                                                .knot(femaleKnot)
                                                .build());

        KeyNode homePageEvaluation;
        homePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                   .documentContext(JsonPath.parse(userContext1))
                                                                   .build());

        System.out.println(Mapper.MAPPER.writeValueAsString(homePageEvaluation));

        homePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                   .documentContext(JsonPath.parse(userContext2))
                                                                   .build());

        System.out.println(Mapper.MAPPER.writeValueAsString(homePageEvaluation));
    }
}