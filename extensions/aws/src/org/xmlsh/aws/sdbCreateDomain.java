package org.xmlsh.aws;

import net.sf.saxon.s9api.SaxonApiException;
import org.xmlsh.aws.util.AWSSDBCommand;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.UnexpectedException;
import org.xmlsh.core.XValue;
import org.xmlsh.util.Util;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.amazonaws.services.elasticloadbalancing.model.InstanceState;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;


public class sdbCreateDomain	 extends  AWSSDBCommand {



	/**
	 * @param args
	 * @throws IOException 
	 */
	@Override
	public int run(List<XValue> args) throws Exception {

		Options opts = getOptions();
		opts.parse(args);

		args = opts.getRemainingArgs();



		setSerializeOpts(this.getSerializeOpts(opts));






		try {
			getSDBClient(opts);
		} catch (UnexpectedException e) {
			usage( e.getLocalizedMessage() );
			return 1;

		}


		int ret = -1;
		ret = create(Util.toStringList(args));



		return ret;


	}


	private int create(List<String> domains) throws IOException, XMLStreamException, SaxonApiException, CoreException 
	{

		OutputPort stdout = this.getStdout();
		mWriter = stdout.asXMLStreamWriter(getSerializeOpts());





		startDocument();
		startElement(getName());



		for( String domainName : domains ){

			traceCall("createDomain");
			CreateDomainRequest createDomainRequest = new CreateDomainRequest().withDomainName(domainName);
			mAmazon.createDomain(createDomainRequest);

			writeElementAttribute("domain", "name", domainName);


		}
		endElement();
		endDocument();



		closeWriter();
		stdout.writeSequenceTerminator(getSerializeOpts());

		return 0;

	}


	private void writeInstanceState( InstanceState s) throws XMLStreamException {
		attribute(	"description",	s.getDescription() );
		attribute( "reason_code" ,  s.getReasonCode() );
		attribute( "state" , s.getState() );



	}


	@Override
	public void usage() {
		super.usage();
	}





}
