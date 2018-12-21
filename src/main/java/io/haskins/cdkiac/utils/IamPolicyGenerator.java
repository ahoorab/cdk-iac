package io.haskins.cdkiac.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for generating IAM resources, such as Instance Profiles, Trust Polices and Policy Documents.
 */
public class IamPolicyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IamPolicyGenerator.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private IamPolicyGenerator() { }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// public methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Generates a Service Trust Policy for the passed princip
     * @param service e.g. ec2.amazonaws.com
     * @return  <pre>
     *     {
     *   "Version": "2012-10-17",
     *   "Statement": [
     *     {
     *       "Effect": "Allow",
     *       "Principal": {
     *         "Service": "ec2.amazonaws.com"
     *       },
     *       "Action": "sts:AssumeRole"
     *     }
     *   ]
     * }
     * </pre>
     */
    public static ObjectNode getServiceTrustPolicy(String service) {
        return (ObjectNode)createServiceTrustPolicy(Collections.singletonList(service));
    }

    public static JsonNode getPolicyStatement(String effect, List<String> actions, List<String> resources) {
        return createStatementJsonNode(effect, actions, resources);
    }

    public static ObjectNode getPolicyDocument(List<JsonNode> statements) {

        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("Version", "2012-10-17");

        ArrayNode array = mapper.valueToTree(statements);
        objectNode.putArray("Statement").addAll(array);

        return objectNode;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// private helper methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static JsonNode createAwsTrustPolicy(List<String> aws) {

        String json = "\"AWS\": [" + escapeStrings(aws) + "]";
        return createTrustedJsonNode(json);
    }

    private static JsonNode createServiceTrustPolicy(List<String> services) {

        String json = "\"Service\":[" + escapeStrings(services) + "]";
        return createTrustedJsonNode(json);
    }

    private static String escapeStrings(List<String> strings) {

        List<String> tmp = new ArrayList<>();
        for (String string: strings) {
            tmp.add("\"" + string + "\"");
        }

        return String.join(",", tmp);
    }

    private static JsonNode createTrustedJsonNode(String principal ) {

        StringBuilder json = new StringBuilder()
                .append("{")
                .append("\"Version\":\"2012-10-17\",")
                .append("\"Statement\": [ {")
                .append("\"Effect\": \"Allow\",")
                .append("\"Principal\":{").append(principal).append("},")
                .append("\"Action\":\"sts:AssumeRole\"")
                .append("}]}");

        return createJsonNode(json.toString());
    }

    private static JsonNode createStatementJsonNode(String effect, List<String> actions, List<String> resources) {

        StringBuilder json = new StringBuilder()
                .append("{")
                .append("\"Effect\": ").append("\"").append(effect).append("\",")
                .append("\"Action\": [").append(escapeStrings(actions)).append("],")
                .append("\"Resources\":[").append(escapeStrings(resources)).append("]")
                .append("}");

        return createJsonNode(json.toString());
    }

    private static JsonNode createJsonNode(String json) {

        JsonNode jsonNode = null;
        try {
            jsonNode =  mapper.readTree(json);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return jsonNode;
    }
}