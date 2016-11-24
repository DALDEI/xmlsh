package org.xmlsh.aws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.xmlsh.aws.util.AWSDDBCommand;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.UnexpectedException;
import org.xmlsh.core.XValue;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import net.sf.saxon.s9api.SaxonApiException;

public class ddbCreateTable extends AWSDDBCommand {

  /**
   * @param args
   * @throws IOException
   */
  @Override
  public int run(List<XValue> args) throws Exception {
    Options opts = getOptions(sTABLE_OPTIONS, sKEY_OPTIONS,
        "table:,read-capacity:,write-capacity:,attribute:+,global-secondary-index:+,local-secondary-index:+,key:+");
    parseOptions(opts, args);
    setSerializeOpts(opts);

    args = opts.getRemainingArgs();

    if(args.size() != 0) {
      usage();
      return 1;
    }

    try {
      getDDBClient(opts);
    } catch (UnexpectedException e) {
      usage(e.getLocalizedMessage());
      return 1;

    }

    int ret = -1;
    ret = create(opts);

    return ret;

  }

  private int create(Options opts) throws IOException,
      XMLStreamException, SaxonApiException, CoreException {

    String tableName = opts.getOptStringRequired("table");

    ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
        .withReadCapacityUnits(opts.getOptLong("read-capacity", 5L))
        .withWriteCapacityUnits(opts.getOptLong("write-capacity", 5L));

    Collection<AttributeDefinition> attributeDefinitions = parseAttributes(
        opts);
    Collection<KeySchemaElement> keySchema = parseKeySchema(opts);

    CreateTableRequest createTableRequest = new CreateTableRequest()
        .withTableName(tableName)
        .withProvisionedThroughput(provisionedThroughput)
        .withAttributeDefinitions(attributeDefinitions)
        .withKeySchema(keySchema);

    parseGlobalSecondaryIndex(opts, createTableRequest);
    parseLocalSecondaryIndexList(opts, createTableRequest);

    traceCall("createTable");
    CreateTableResult result = null;
    try {
      result = getAWSClient().createTable(createTableRequest);
    } catch (AmazonClientException e) {
      return handleException(e);
    }

    startResult();
    writeTableDescription(result.getTableDescription());

    endResult();

    return 0;
  }

  private void parseLocalSecondaryIndexList(Options opts,
      CreateTableRequest createTableRequest)
      throws InvalidArgumentException, UnexpectedException {

    if(!opts.hasOpt("local-secondary-index"))
      return;
    Collection<LocalSecondaryIndex> localSecondaryIndexes = new ArrayList<LocalSecondaryIndex>();
    for(XValue xv : opts.getOptValues("local-secondary-index"))
      localSecondaryIndexes.add(parseLocalSecondaryIndex(xv));

    createTableRequest.setLocalSecondaryIndexes(localSecondaryIndexes);

  }

  private void parseGlobalSecondaryIndex(Options opts,
      CreateTableRequest createTableRequest)
      throws InvalidArgumentException, UnexpectedException {

    if(!opts.hasOpt("global-secondary-index"))
      return;
    Collection<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<GlobalSecondaryIndex>();
    for(XValue xv : opts.getOptValues("global-secondary-index"))
      globalSecondaryIndexes.add(parseGlobalSecondaryIndex(xv));

    createTableRequest.setGlobalSecondaryIndexes(globalSecondaryIndexes);

  }

  private Collection<KeySchemaElement> parseKeySchema(Options opts)
      throws InvalidArgumentException {

    ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();

    for(XValue xv : opts.getOptValues("key")) {

      KeySchemaElement keyElement = parseKeySchemaElement(xv);
      tableKeySchema.add(keyElement);

    }
    return tableKeySchema;
  }

  private Collection<AttributeDefinition> parseAttributes(Options opts)
      throws InvalidArgumentException, UnexpectedException {

    ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
    for(XValue xv : opts.getOptValues("attribute")) {

      AttributeDefinition attr = parseKeyAttribute(xv);

      attributeDefinitions.add(attr);
    }

    return attributeDefinitions;

  }

  @Override
  public void usage() {
    super.usage();
  }

}
