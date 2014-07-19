package org.xmlsh.aws;

import net.sf.saxon.s9api.SaxonApiException;
import org.xmlsh.aws.util.AWSEC2Command;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.UnexpectedException;
import org.xmlsh.core.XValue;
import org.xmlsh.util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Tag;


public class ec2CreateTags extends AWSEC2Command {

	


	/**
	 * @param args
	 * @throws IOException 
	 */
	@Override
	public int run(List<XValue> args) throws Exception {

		
		Options opts = getOptions("t=tag:+");
		opts.parse(args);

		args = opts.getRemainingArgs();
		

		
		
		
		if( args.size() < 1 ){
			usage(null);
			return 1;
		}
		

		setSerializeOpts(this.getSerializeOpts(opts));
		try {
			 getEC2Client(opts);
		} catch (UnexpectedException e) {
			usage( e.getLocalizedMessage() );
			return 1;
			
		}
		
		int ret = createTags( parseTags(opts) , Util.toStringArray(args) );
		
		
		
		
		
		return ret;
		
		
	}







	private int createTags( List<Tag> tags , String[] resources ) throws InvalidArgumentException, IOException, XMLStreamException, SaxonApiException 
	{
	
		CreateTagsRequest request = new CreateTagsRequest().withTags(tags).withResources(Arrays.asList(resources));
		
		
		traceCall("createTags");

		mAmazon.createTags(request);
	
		return 0;
		
	
	
	}


	
	

}
