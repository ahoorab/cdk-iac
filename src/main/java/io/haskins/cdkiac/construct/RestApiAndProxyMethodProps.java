package io.haskins.cdkiac.construct;

public class RestApiAndProxyMethodProps {

    private String unqiueId;

    /**
     * @return A builder for {@link RestApiAndProxyMethodProps}.
     */
    public static RestApiAndProxyMethodPropsBuilder builder() {
        return new RestApiAndProxyMethodPropsBuilder();
    }

    public String getUniqueId() {
        return unqiueId;
    }

    /**
     * Builder for {@link RestApiAndProxyMethod}.
     */
    public static final class RestApiAndProxyMethodPropsBuilder {

        private String unqiueId;

        private RestApiAndProxyMethodPropsBuilder() {
        }

        public RestApiAndProxyMethodPropsBuilder withUniqueId(String unqiueId) {
            this.unqiueId = unqiueId;
            return this;
        }

        public RestApiAndProxyMethodProps build() {
            RestApiAndProxyMethodProps sinkQueueProps = new RestApiAndProxyMethodProps();
            sinkQueueProps.unqiueId = this.unqiueId;
            return sinkQueueProps;
        }
    }
}