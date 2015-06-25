package org.xmlsh.aws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import net.sf.saxon.s9api.SaxonApiException;

import org.xmlsh.aws.util.AWSDDBCommand;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.Options;
import org.xmlsh.core.UnexpectedException;
import org.xmlsh.core.XValue;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.Select;

public class ddbScan extends AWSDDBCommand {
    private int kLIMIT = 100;

    /**
     * @param args
     * @throws IOException 
     */
    @Override
    public int run(List<XValue> args) throws Exception {
        
        Options opts = getOptions(sTABLE_OPTIONS, sKEY_OPTIONS , sATTR_EXPR_OPTIONS , sCONSISTANT_OPTS ,
              "limit:,filter=filter-expression:,select:,index-name:,key-condition-expression:,projection-expression:");
        parseOptions(opts, args);

        args = opts.getRemainingArgs();
        setSerializeOpts(this.getSerializeOpts(opts));

        try {
            getDDBClient(opts);
        } catch (UnexpectedException e) {
            usage(e.getLocalizedMessage());
            return 1;
        }

        int ret = -1;
        ret = scan(opts);

        return ret;
    }

    private int scan(Options opts) throws IOException, XMLStreamException, SaxonApiException, CoreException 
    {

        String filterExpression = opts.getOptString("filter", null);

        ScanRequest scanRequest = new ScanRequest().
                withTableName(opts.getOptStringRequired("table")). 
                withExpressionAttributeNames(parseAttrNameExprs(opts)).
                withExpressionAttributeValues(parseAttrValueExprs(opts));

        int userLimit = opts.getOptInt("limit", 0);
        if (userLimit > 0)
            scanRequest.setLimit(Math.min(userLimit, kLIMIT));

        if( opts.hasOpt("index-name"))
            scanRequest.setIndexName( opts.getOptStringRequired("index-name"));

        if( opts.hasOpt("projection-expression"))
            scanRequest.setProjectionExpression( 
                    opts.getOptStringRequired("projection-expression"));             
        if (filterExpression != null)
            scanRequest.setFilterExpression(filterExpression);
        if( opts.hasOpt("select"))
            scanRequest.setSelect(parseSelect(opts.getOptStringRequired("select")));

        ArrayList<RequestMetrics> metrics = new ArrayList<RequestMetrics>();
        Map<String, AttributeValue> exclusiveStartKey =null;
        boolean bStarted = false ;

        do {

            traceCall("scan");
            if (exclusiveStartKey != null)
                scanRequest.setExclusiveStartKey(exclusiveStartKey);
            ScanResult result = null;
            try {
                result =  getAWSClient().scan(scanRequest);
            } catch (AmazonClientException e) {
                    return handleException(e);
            }
               finally {
                   if( bStarted )
                      endResult();
            }
                if( ! bStarted )
                    bStarted = startResult();
            metrics.add( new RequestMetrics(result.getCount(), result.getScannedCount(), result.getConsumedCapacity()));

            for( Map<String, AttributeValue> item :  result.getItems() ){
                writeItem(item);
            }
            exclusiveStartKey = result.getLastEvaluatedKey();

        } while( exclusiveStartKey != null );

        writeMetrics( metrics );
        endResult();
        return 0;
    }

    private Select parseSelect(String select) {
        return Select.fromValue(select.toUpperCase());
    }

}

