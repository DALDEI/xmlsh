/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.AugmentedSource;
import net.sf.saxon.event.Builder;
import net.sf.saxon.om.MutableNodeInfo;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.StandardNames;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SaxonApiUncheckedException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.s9api.XdmSequenceIterator;
import net.sf.saxon.tree.DocumentImpl;
import org.xmlsh.core.Options;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.core.Options.OptionValue;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.Util;


public class xed extends XCommand {

	private DocumentBuilder mBuilder;
	private XPathCompiler mCompiler;
	private Processor mProcessor;

	private void setupBuilders()
	{
/*		
		mProcessor = new Processor(false);
		mProcessor.setConfigurationProperty(FeatureKeys.TREE_MODEL, net.sf.saxon.event.Builder.LINKED_TREE);
*/
		mProcessor = Shell.getProcessor();
		

		mCompiler = mProcessor.newXPathCompiler();
		mBuilder = mProcessor.newDocumentBuilder();
		
	}
	
	
	
	@Override
	public int run( List<XValue> args )
	throws Exception 
	{
		boolean 	opt_delete	= false ;
		XValue		opt_add 	= null;
		XValue		opt_replace = null;
		boolean		opt_matches = false ;
		
		
		Options opts = new Options( "f:,i:,n,v,r:,a:,d,matches" , args );
		opts.parse();
		
		setupBuilders();

		XdmNode	context = null;

		
		// boolean bReadStdin = false ;
		if( ! opts.hasOpt("n" ) ){ // Has XML data input
			OptionValue ov = opts.getOpt("i");

			
			// If -i argument is an XML expression take the first node as the context
			if( ov != null  && ov.getValue().isXExpr() ){
				XdmItem item = ov.getValue().asXdmValue().itemAt(0);
				if( item instanceof XdmNode )
				//   context = (XdmNode) item ; // builder.build(((XdmNode)item).asSource());
				 // context = (XdmNode) ov.getValue().toXdmValue();
				context = importNode( (XdmNode)item);

			}
			if( context == null )
			{
	
				if( ov != null && ! ov.getValue().toString().equals("-"))
					context = build( getFile(ov.getValue()));
				else {
					context = build(getStdin().asSource());
				}	
			}
		}
		

		List<XValue> xvargs = opts.getRemainingArgs();
		

		
		OptionValue ov = opts.getOpt("f");
		String xpath = null;
		if( ov != null )
			xpath = Util.readString( getFile(ov.getValue().toString()) ) ;
		else 
			xpath = xvargs.remove(0).toString();
		

		
		if( opts.hasOpt("v")){
			// Read pairs from args to set
			for( int i = 0 ; i < xvargs.size()/2 ; i++ ){
				String name = xvargs.get(i*2).toString();
				mCompiler.declareVariable(new QName(name));			
			}
		}
		

		
		opt_add		= opts.getOptValue("a");
		opt_replace = opts.getOptValue("r");
		opt_delete  = opts.hasOpt("d");
		opt_matches = opts.hasOpt("matches");
		
		
		XPathExecutable expr;
		if( !opt_matches )
		  expr = mCompiler.compile( xpath );
		else 
		  expr = mCompiler.compilePattern( xpath );
		
		
		XPathSelector eval = expr.load();
		
		if( opts.hasOpt("v")){
			// Read pairs from args to set
			for( int i = 0 ; i < xvargs.size()/2 ; i++ ){
				String name = xvargs.get(i*2).toString();
				XValue value = xvargs.get(i*2+1);
				eval.setVariable( new QName(name),  value.asXdmValue() );	
			}
		}
		
		Iterable<XdmItem>  results = getResults( eval ,  context , opt_matches );
		
		
	
		for( XdmItem item : results ){
			Object obj = item.getUnderlyingValue();
			if( obj instanceof MutableNodeInfo ){
				MutableNodeInfo node = (MutableNodeInfo) obj;
				if( opt_replace != null )
					replace(node, opt_replace);
				if( opt_add != null )
					add( node , opt_add );
				if( opt_delete )
					delete( node );
				
				// else
				// if( opt_add != null )
				//	add( builder , )
			}
			
			
		}
		
		// Shell.getProcessor().writeXdmValue( mValue, ser);
		// Util.writeXdmValue( context , getStdout().asDestination() );
		Serializer ser = new Serializer();
		ser.setOutputStream( getStdout().asOutputStream() );
		ser.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
		ser.setOutputProperty(Serializer.Property.METHOD, "xml");

		Util.writeXdmValue( context , ser );
	
		
		return 0;

	}

	private Iterable<XdmItem> getResults(XPathSelector eval, XdmNode root , boolean opt_matches) throws SaxonApiException {
		
		if( ! opt_matches ){
			if( root != null )
				eval.setContextItem(root);
			return eval ;
		}
		ArrayList<XdmItem>	results = new ArrayList<XdmItem>();
		if( root == null )
			return results;
		
		XdmSequenceIterator iter = root.axisIterator(Axis.DESCENDANT_OR_SELF);
		while( iter.hasNext() ){
			XdmItem item = iter.next();
			eval.setContextItem(item);
			if( eval.effectiveBooleanValue())
				results.add(item);
		}
		return results ;
	}



	private void delete(MutableNodeInfo node) {
		node.delete();
	}



	private void add(MutableNodeInfo node, XValue add) throws IndexOutOfBoundsException, SaxonApiUncheckedException, SaxonApiException {
		if( ! add.isAtomic() ){
			XdmNode xnode = (XdmNode) add.asXdmValue();
			if( xnode.getNodeKind() == 	XdmNodeKind.ATTRIBUTE ) {
				NodeInfo anode = xnode.getUnderlyingNode();
				NamePool pool = node.getNamePool();
				int nameCode  = pool.allocate( anode.getPrefix(), anode.getURI() , anode.getLocalPart() );
				node.putAttribute(nameCode,  StandardNames.XS_UNTYPED_ATOMIC, anode.getStringValueCS(), 0);
				if( !Util.isEmpty(anode.getPrefix()) ){
					int nsCode = pool.allocateNamespaceCode(nameCode);
					node.addNamespace(nsCode, false);
				}
			} else {
				node.insertChildren( new NodeInfo[]  { getNodeInfo(xnode) } , true ,true );
			}
		} else
			node.replaceStringValue(node.getStringValue() + add.toString() );
		
	}



	private void replace(MutableNodeInfo node, XValue replace)
			throws IndexOutOfBoundsException, SaxonApiUncheckedException, SaxonApiException {
		if(  ! replace.isAtomic() ){
			XdmNode xnode = (XdmNode) replace.asXdmValue();
			if( xnode.getNodeKind() == 	XdmNodeKind.ATTRIBUTE ) {
				NodeInfo anode = xnode.getUnderlyingNode();
				
			
				NamePool pool = node.getNamePool();
				int nameCode  = pool.allocate( anode.getURI() , anode.getPrefix(),anode.getLocalPart() );
				
				node.putAttribute(nameCode,  StandardNames.XS_UNTYPED_ATOMIC, anode.getStringValueCS(), 0);
				
				
			} else 
				node.replace( new NodeInfo[]  { getNodeInfo(xnode) } , true );
				
			
		}
		else
			node.replaceStringValue( replace.toString() );
	}

	/*
	 * Import the node using the builder into this object model
	 */
	private XdmNode importNode( XdmNode node ) throws SaxonApiException
	{
		Source src = node.asSource();
		return build(src);
	}

	
	private NodeInfo getNodeInfo( XdmNode node) throws IndexOutOfBoundsException,
		SaxonApiUncheckedException, SaxonApiException {
		
		XdmNode xnode = importNode(node);
		
		return ((DocumentImpl) xnode.getUnderlyingNode().getDocumentRoot()).getDocumentElement();
	}
	
	/*
	 * Creates/Builds a Tree (LINKED_TREE) type node from any source
	 */
	
	private XdmNode build( Source src ) throws SaxonApiException
	{
		AugmentedSource asrc = AugmentedSource.makeAugmentedSource(src); 
		asrc.setTreeModel(Builder.LINKED_TREE); 
		return mBuilder.build(asrc);
		
	}
	
	private XdmNode build( File file ) throws SaxonApiException
	{
		Source src = new StreamSource(file);
		return build(src);
	}
	

}

//
//
//Copyright (C) 2008, David A. Lee.
//
//The contents of this file are subject to the "Simplified BSD License" (the "License");
//you may not use this file except in compliance with the License. You may obtain a copy of the
//License at http://www.opensource.org/licenses/bsd-license.php 
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied.
//See the License for the specific language governing rights and limitations under the License.
//
//The Original Code is: all this file.
//
//The Initial Developer of the Original Code is David A. Lee
//
//Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
//Contributor(s): none.
//
