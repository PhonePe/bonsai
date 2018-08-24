package com.phonepe.platform.bonsai.core.vital.provided;

import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.Mapper;
import com.phonepe.platform.bonsai.core.ObjectExtractor;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.query.filter.general.EqualsFilter;
import com.phonepe.platform.bonsai.core.vital.BonsaiIdGen;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.core.vital.Edge;
import com.phonepe.platform.bonsai.core.vital.Knot;
import com.phonepe.platform.bonsai.core.vital.provided.impl.MapBasedHashEdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.MapBasedKnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.MapBasedMappingStore;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.NodeVisitors;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 12:57 PM
 */
public class IBonsaiTest {

    @Test
    public void testIBonsaiEvaluation() throws IOException {

        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);
        Bonsai bonsai = new IBonsai(new MapBasedMappingStore(), new MapBasedKnotStore(), new MapBasedHashEdgeStore());

        Knot homePageKnot = bonsai.add("home_page_1", MultiKnotData.builder()
                                                                   .key("widget_1")
                                                                   .key("widget_2")
                                                                   .key("widget_3")
                                                                   .build());
        Knot femaleConditionKnot = bonsai.create(MultiKnotData.builder()
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
                                                .id(BonsaiIdGen.newId())
                                                .knot(femaleConditionKnot)
                                                .build());

        /* checking multiple additions */
        bonsai.connect(widgetKnot1.getId(), Edge.builder()
                                                .condition(new EqualsFilter("$.gender", "female"))
                                                .pivot("gender")
                                                .id(BonsaiIdGen.newId())
                                                .knot(femaleConditionKnot)
                                                .build());

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext1))
                                                                                .build());
        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assert.assertEquals(user1HomePageEvaluation.getKey(), "home_page_1");
        Assert.assertEquals(user1HomePageEvaluation.getNode().getId(), homePageKnot.getId());
        Assert.assertTrue(NodeVisitors.isList(user1HomePageEvaluation.getNode()));

        Assert.assertEquals(((ListNode) user1HomePageEvaluation.getNode()).getNodes().size(), 3);
        Assert.assertTrue(NodeVisitors.isList(((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                                                                                            .get(0)
                                                                                            .getNode()));

        Assert.assertEquals(((ListNode) (((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                                                                                       .get(0)
                                                                                       .getNode())).getNodes()
                                                                                                   .size(), 4);

        /* evaluate with context 2 */
        KeyNode user2HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext2))
                                                                                .build());


        Assert.assertEquals(user2HomePageEvaluation.getKey(), "home_page_1");
        Assert.assertEquals(user2HomePageEvaluation.getNode().getId(), homePageKnot.getId());
        Assert.assertTrue(NodeVisitors.isList(user2HomePageEvaluation.getNode()));

        Assert.assertEquals(((ListNode) user2HomePageEvaluation.getNode()).getNodes().size(), 3);
        Assert.assertTrue(NodeVisitors.isList(((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                                            .get(0)
                                                                                            .getNode()));

        Assert.assertEquals(((ListNode) (((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                                       .get(0)
                                                                                       .getNode())).getNodes()
                                                                                                   .size(), 3);

        Assert.assertEquals((((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                           .get(0)
                                                                           .getNode()).getId(), femaleConditionKnot.getId());

        System.out.println(Mapper.MAPPER.writeValueAsString(user2HomePageEvaluation));
    }
}