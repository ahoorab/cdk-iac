package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.CfnTable;
import software.amazon.awscdk.services.dynamodb.CfnTableProps;

public class DynamoDb extends CdkIacStack {

    public DynamoDb(final App parent,
              final String name,
              final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private DynamoDb(final App parent,
               final String name,
               final StackProps props,
               final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        CfnTable table = new CfnTable(this, "DynamoDb", CfnTableProps.builder()
                .withTableName(uniqueId)
                .withProvisionedThroughput(CfnTable.ProvisionedThroughputProperty.builder()
                        .withReadCapacityUnits(1)
                        .withWriteCapacityUnits(1)
                        .build())
                .build());
    }
}