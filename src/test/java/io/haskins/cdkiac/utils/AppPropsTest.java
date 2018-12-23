package io.haskins.cdkiac.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AppPropsTest {

    private AppProps appProps;

    @Before
    public void setUp() {
        appProps = new AppProps();
    }

    @Test
    public void addProp() {

        appProps.addProp("Key", "Value");
        Assert.assertEquals(1, appProps.size());
    }

    @Test
    public void getPropAsString() {

        String expected = "Value";

        appProps.addProp("Key", expected);
        String property = appProps.getPropAsString("Key");

        Assert.assertEquals(expected, property);
    }

    @Test
    public void getPropAsInteger() {

        int expected = 10;

        appProps.addProp("Key", "10");
        int property = appProps.getPropAsInteger("Key");

        Assert.assertEquals(expected, property);
    }

    @Test
    public void getPropAsBoolean() {

        boolean expected = false;

        appProps.addProp("Key", "false");
        boolean property = appProps.getPropAsBoolean("Key");

        Assert.assertEquals(expected, property);
    }

    @Test
    public void getPropAsStringList() {

        List<String> expected = Arrays.asList("tom","dick","harry");

        appProps.addProp("Key", "tom,dick,harry");
        List<String> property = appProps.getPropAsStringList("Key");

        Assert.assertEquals(expected, property);
        Assert.assertEquals(3, property.size());
    }

    @Test
    public void getPropAsObjectList() {

        List<Object> expected = Arrays.asList("tom","dick","harry");

        appProps.addProp("Key", "tom,dick,harry");
        List<Object> property = appProps.getPropAsObjectList("Key");

        Assert.assertEquals(expected, property);
        Assert.assertEquals(3, property.size());
    }

    @Test
    public void getUniqueIdWithVpc() {

        appProps.addProp("dtap", "dev");
        appProps.addProp("vpc", "a");
        appProps.addProp("app_id", "test");

        String expected = "dev-a-test";

        String uniqueId = appProps.getUniqueId();

        Assert.assertEquals(expected, uniqueId);
    }

    @Test
    public void getUniqueIdWithOutVpc() {

        appProps.addProp("dtap", "dev");
        appProps.addProp("app_id", "test");

        String expected = "dev-test";

        String uniqueId = appProps.getUniqueId();

        Assert.assertEquals(expected, uniqueId);
    }

    @Test
    public void clear() {

        appProps.addProp("dtap", "dev");
        appProps.addProp("vpc", "a");
        appProps.addProp("app_id", "test");

        int expected = 0;

        appProps.clear();

        Assert.assertEquals(expected, appProps.size());
    }
}