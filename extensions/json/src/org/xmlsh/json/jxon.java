/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.saxon.s9api.BuildingStreamWriter;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.xml.sax.ContentHandler;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.Options;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XEnvironment;
import org.xmlsh.core.XValue;
import org.xmlsh.core.XVariable;
import org.xmlsh.core.io.OutputPort;
import org.xmlsh.core.io.VariableOutputPort;
import org.xmlsh.schema.Schema;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.StAXUtils;
import org.xmlsh.util.Util;




public class jxon  extends XCommand{

		
		private static final String CONTEXT_TYPE 		= "type_decl";
		private static final String CONTEXT_ATTRIBUTE = "attribute";
		private static final String CONTEXT_ELEMENT 	= "element";
		private static final String CONTEXT_ELEMENT_REF = "element_ref";	
		private static final String CONTEXT_DOCUMENT 	= "document";
		private static final String JXON_NS 			= "http://www.xmlsh.org/jxon";
		
		private  XdmNode 	parse( XSAnnotation xanno , SerializeOpts opts ) throws XPathException, SaxonApiException, CoreException, IOException
		{

			XVariable var = XVariable.anonymousInstance( new XValue());
			OutputPort out = new VariableOutputPort( var );
			
			ContentHandler handler  = out.asContentHandler(opts );
			xanno.writeAnnotation(handler, XSAnnotation.SAX_CONTENTHANDLER);
			out.flush();
			out.close();
			
			
			XdmValue xv = var.getValue().xpath( getShell() , "/xs:annotation/xs:appinfo/jxon:*").toXdmValue();
			
			/*
			 * Reserialize with a namespacer reduicer
			 */
			
			var = XVariable.anonymousInstance(XValue.nullValue());

			out = new VariableOutputPort( var );
			Util.writeXdmValue(xv ,out.asDestination(opts ));
			out.flush();
			out.close();
			return var.getValue().asXdmNode();
			
		}
		
		
		
		private class Annotation extends XValue
		{
			
			
			
			public Annotation( XSAnnotation xanno ) throws XPathException, SaxonApiException, CoreException, IOException{
				
				
			
				super( parse( xanno , mSerializeOpts ) );
				
				
				
			}
			
		}
		
		@SuppressWarnings("serial")
		private static class AnnotationList extends ArrayList<Annotation>
		{


			public AnnotationList()
			{
				super();
			}
			public AnnotationList(int length) {
				super(length);
			}

			/* (non-Javadoc)
			 * @see java.util.ArrayList#add(java.lang.Object)
			 */
			@Override
			public boolean add(Annotation e) {
				if( e == null || e.isNull() )
					throw new RuntimeException("Invalid Null Annotation");
				
				
				return super.add(e);
			}
			
			
			
			
		}
		
		
		
		private class	AnnotationEntry 
		{
			QName 				mName;			// required
			String 				mContext;		// required
			XSTypeDefinition	mType; 			// may be null 
			AnnotationList		mAnnotation;	// required
			AnnotationEntryList	mChildren;		// optional
			List<QName>			mChildRefs;		// optional
			public AnnotationEntry(String context , QName name , AnnotationEntry parent , XSTypeDefinition type, AnnotationList annotation) {
				super();
				mName = name ;
				
				mContext = context ;
				mType = type;
				mAnnotation = annotation;
				
				if( parent != null )
					parent.add( this );
			}
			
			
			private void add(AnnotationEntry annotationEntry) {
				if( mChildren == null )
					mChildren = new AnnotationEntryList();
				mChildren.add(annotationEntry);
				
			}

			public void  serialize( XMLStreamWriter sw ) throws SaxonApiException, XMLStreamException, XPathException, CoreException
			{

				sw.writeStartElement( "jxon", mContext , JXON_NS );
				// sw.writeDefaultNamespace(JXON_NS);


				XSSimpleTypeDefinition itemType = null ;
				XSObjectList memberTypes = null ;
				if( mType != null ){
					sw.writeAttribute("typeCategory", getTypeCategory(mType));
					if( mType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE ){
						XSComplexTypeDefinition ctd = (XSComplexTypeDefinition) mType ;
						sw.writeAttribute( "contentType" , getTypeContentType( ctd ) );
						if( mName.getLocalName().equals("value"))
							sw.writeAttribute("abstract", ctd.getAbstract() ? "true" : "false" );
					}
					else
					if( mType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE ){
						XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) mType ;
						sw.writeAttribute( "variety" , getVariety( std ) );
						itemType = std.getItemType();
						memberTypes = std.getMemberTypes();
					}
				}
				
				if( ! mContext.equals(CONTEXT_DOCUMENT)){
					writeQName( sw , "name" , mName );

					if( mType != null ){
						if( mContext.equals(CONTEXT_TYPE )){
							QName baseType = getName(mType.getBaseType());
	
							// Prevent infinate recursion
							if( baseType != null && ! baseType.equals(mName))
								writeQName( sw , "basetype" , baseType );
							if( itemType != null )
								writeQName( sw , "itemtype" , getName( itemType ));
							if( memberTypes != null ){
								for( int i = 0 ; i < memberTypes.getLength() ; i++ ){
									XSSimpleTypeDefinition member = (XSSimpleTypeDefinition ) memberTypes.item(i);
									writeQName( sw , "membertype" , getName( member ));
								}
							}
						}
						else
							writeQName( sw , "type" , getName(mType) );
					}
				}
				

				if( mAnnotation != null ){
						
					for( Annotation a : mAnnotation ){

						StAXUtils.copy( a.asNodeInfo() , sw );

						
					}
				}
				
				if( mChildren != null ){
					mChildren.serialize(sw);
					
				}
				if( mChildRefs != null ){
					for( QName c : mChildRefs )
						writeQName( sw , "child" , c );
					
					
					
					
				}
				
				
				sw.writeEndElement();
				
			}




			private String getVariety(XSSimpleTypeDefinition std) {
				switch( std.getVariety()) {
				case	XSSimpleTypeDefinition.VARIETY_ABSENT :
					return "absent" ; 
				case	XSSimpleTypeDefinition.VARIETY_ATOMIC:
					return "atomic" ; 
				case	XSSimpleTypeDefinition.VARIETY_LIST :
					return "list" ; 
				case	XSSimpleTypeDefinition.VARIETY_UNION:
					return "union" ; 
				default :
					return "absent" ;
					
				}
			}




			private String getTypeContentType(XSComplexTypeDefinition type) {
				switch( type.getContentType() ){
				case	XSComplexTypeDefinition.CONTENTTYPE_ELEMENT :
					return "element" ;
				case	XSComplexTypeDefinition.CONTENTTYPE_EMPTY :
					return "empty" ;
				case	XSComplexTypeDefinition.CONTENTTYPE_MIXED :
					return "mixed" ;
				case	XSComplexTypeDefinition.CONTENTTYPE_SIMPLE:
					return "simple" ;
				default: 
					return "";
				
				}
			}




			private String getTypeCategory(XSTypeDefinition type) {
				switch( type.getTypeCategory()){
				case	XSTypeDefinition.COMPLEX_TYPE :
					return "complex";
				case	XSTypeDefinition.SIMPLE_TYPE :
					return "simple" ;
				default:
					return "";
				
				
				}
			}


			public void addChildReference(QName child) {
				if( mChildRefs == null )
					mChildRefs = new ArrayList<QName>();
				mChildRefs.add( child );
			}
			
			
		};
		
		@SuppressWarnings("serial")
		private class AnnotationEntryList extends  ArrayList<AnnotationEntry>
		{

			
			
			public void serialize(XMLStreamWriter sw ) throws SaxonApiException, XMLStreamException, XPathException, CoreException
			{
				
				for( AnnotationEntry a : this ){
					a.serialize(sw);
					
				}
				
				
			}
			
			
		}
		
		
		
	
		private SerializeOpts 	  mSerializeOpts ;
		private	 XdmNode				  mPatterns;

		private static int level = 0;
		
		private Schema	mSchema = null;
		
		public int run(List<XValue> args) throws Exception {
			Options opts = new Options("xsd:,o=output:,v,n",SerializeOpts.getOptionDefs());
			opts.parse(args);

			args = opts.getRemainingArgs();

			mSerializeOpts = getSerializeOpts(opts);

			String xsd = opts.getOptStringRequired("xsd");
			String output = opts.getOptString("o", ".");
			
			
			Shell shell = getShell();
			shell.getEnv().declareNamespace("jxon",JXON_NS );
			mSchema = new Schema(   shell.getURI( xsd ).toString()  );
			
			
			URL patterns_url =  mShell.getResource("/org/xmlsh/json/patterns/patterns.xml");
			mPatterns = Util.asXdmNode( patterns_url);
			
			
			
			
			
			AnnotationEntry docEntry = getGlobalAnnotations();
			
			
			getTypeAnnotations( docEntry);
			getElementAnnotations( docEntry );
						
			File	outputDir = shell.getExplicitFile(output,false);
			if( ! outputDir.exists() )
				outputDir.mkdirs();

			XEnvironment env = shell.getEnv();
			
			
			
			
		
			if( opts.hasOpt("v")){
				XMLStreamWriter sw = this.getStdout().asXMLStreamWriter(mSerializeOpts);
				
				serialize( sw , docEntry);
				sw.close();
				this.getStdout().flush();
				
				
			}
	
			if( ! opts.hasOpt("n")){
				OutputPort	xml_port   = env.getOutput( shell.getExplicitFile( outputDir, "toxml.xsl" , false ),false);
				OutputPort 	json_port  = env.getOutput( shell.getExplicitFile( outputDir, "tojson.xsl" , false ), false);
				
		
				BuildingStreamWriter b = Shell.getProcessor().newDocumentBuilder().newBuildingStreamWriter();
				
				serialize( b , docEntry );
				

				
				createXSLT( b.getDocumentNode() , "patterns/create_xml.xquery" , xml_port );
				createXSLT( b.getDocumentNode() , "patterns/create_json.xquery" , json_port );
				
				
	
			}
			
			
			return 0;
			
			
			
			
	}

		private void serialize(XMLStreamWriter sw, AnnotationEntry entry ) throws XMLStreamException, SaxonApiException, XPathException, CoreException 
		{

			entry.serialize(sw);
			sw.flush();
			
			
			
		}

		private void createXSLT(XdmItem annos , String script , OutputPort xmlPort) throws SaxonApiException, IOException, CoreException, URISyntaxException {
		
			
			
			XQueryExecutable exe = getXQuery(script);
			XQueryEvaluator eval = exe.load();
			
			// pass nodes as a sequence of items 
			eval.setDestination( xmlPort.asDestination(mSerializeOpts));
			eval.setExternalVariable( new QName("http://www.xmlsh.org/jsonxml/common","annotations"), annos);
			eval.setExternalVariable( new QName("http://www.xmlsh.org/jsonxml/common","patterns"), mPatterns );
			
			
			eval.run();
			
			
			
		}

		private AnnotationEntry getGlobalAnnotations() throws XPathException,
				XMLStreamException, CoreException, SaxonApiException, IOException {
			XSObjectList annotations = mSchema.getAnnotations();
			
			AnnotationList value = getAnnotations( annotations );
			AnnotationEntry self = new AnnotationEntry( CONTEXT_DOCUMENT ,  null , null , null , value);

			return self ;
		
		}



		private void getAnnotations(XSParticle particle, AnnotationEntry parent ) throws XPathException,	
			XMLStreamException, CoreException, SaxonApiException, IOException {
			
//			if( level > 40)
//				return ;
			
			
			
			if( particle == null )
				return ;
			XSTerm term = particle.getTerm() ;
			if( term == null )
				return ;
			
			level++;
			
			
			switch( term.getType() ){
			case	XSConstants.ELEMENT_DECLARATION :
			{
				
				
				
				XSElementDeclaration child = (XSElementDeclaration) term ;
				
				/*
				 * Only recurse for local elements
				 */
				if( child.getScope() == XSConstants.SCOPE_LOCAL )
					getAnnotations( child , parent  );
				
				/*
				 * Reference to a child, add it as a reference
				 */
				else
					addReference( parent , child );
				
				

				/*
				 * If this is a member of a substitution group then add all other elements of the same group but dont recurse
				 */
				List<XSElementDeclaration> subs  =	getSubElements(child);
				for( XSElementDeclaration e : subs ){
					
					// XSTypeDefinition stype = e.getTypeDefinition();	
					
					QName selemName = getName(e);
					
					new AnnotationEntry( CONTEXT_ELEMENT_REF ,   selemName,  parent ,  null  , null  );
				}
				
				
				break ;
			}
			
			
			case	XSConstants.MODEL_GROUP : ;
			{
				
				XSModelGroup group = (XSModelGroup) term ;
				XSObjectList particles = group.getParticles();
				for( int i = 0 ; i < particles.getLength() ; i++ ){
					XSParticle p = (XSParticle) particles.item(i);
					getAnnotations( p , parent   );
				}
				
				break;
				
				
			}
			
			
			
			case	XSConstants.WILDCARD :
				break ;
			}
			level--;
			
		}
		

		private void addReference(AnnotationEntry parent, XSElementDeclaration child) {
			parent.addChildReference( this.getName(child));
			
			
		}

		private void getElementAnnotations( AnnotationEntry parent ) throws XPathException,
				XMLStreamException, CoreException, SaxonApiException, IOException {
			
			
			XSModel model = mSchema.getModel();
			XSNamedMap types  = model.getComponents(XSConstants.ELEMENT_DECLARATION);
			for( int i = 0 ; i < types.getLength() ; i++ ){
				XSObject obj = types.item(i);

				if( obj instanceof XSElementDeclaration )
					getAnnotations((XSElementDeclaration)obj, parent );
				
			}
		}

		private List<XSElementDeclaration> getSubElements(XSElementDeclaration that ) 
			throws XPathException,
		XMLStreamException, CoreException, SaxonApiException {
	
			ArrayList<XSElementDeclaration>  subs = new ArrayList<XSElementDeclaration>();
			
			XSModel model = mSchema.getModel();
			XSNamedMap types  = model.getComponents(XSConstants.ELEMENT_DECLARATION);
			for( int i = 0 ; i < types.getLength() ; i++ ){
				XSObject obj = types.item(i);
		
				if( obj instanceof XSElementDeclaration ){
					XSElementDeclaration xed = (XSElementDeclaration) obj;
					
					if( xed != that && xed.getSubstitutionGroupAffiliation() == that )
						subs.add(xed);
				}
					
				
			}
			return subs;
		}
		
				
		
		
		private void getAnnotations(XSElementDeclaration obj, AnnotationEntry parent ) throws XPathException,
				XMLStreamException, CoreException, SaxonApiException, IOException {
			{

				
				AnnotationList annotation = getAnnotations( obj.getAnnotations() );
				
				
				XSTypeDefinition type = obj.getTypeDefinition();	
				
				QName elemName = getName(obj);
				boolean bAbstract = obj.getAbstract();
			

				
				AnnotationEntry self = new AnnotationEntry( CONTEXT_ELEMENT ,   elemName,  parent ,  type , annotation  );

				
				
				if( type instanceof XSComplexTypeDefinition ){
					XSComplexTypeDefinition ctype = (XSComplexTypeDefinition) type ;
					XSObjectList attrList = ctype.getAttributeUses();

					/*
					 * Recurse to attributes
					 */
					for( int n = 0 ; n < attrList.getLength() ; n++ ){
						XSObject attrobj = attrList.item(n);
						if( attrobj instanceof XSAttributeUse  ){
							
							XSAttributeUse attrUse = (XSAttributeUse) attrobj ;
							
							XSAttributeDeclaration attrDecl = attrUse.getAttrDeclaration();
							
							
							 AnnotationList useAnno  = getAnnotations( attrUse.getAnnotations() );
							
							
							 type = attrDecl.getTypeDefinition() ;

							
							 AnnotationList declAnno = getAnnotations( attrDecl.getAnnotations() );
							
							 /*
							  * If there is both a use and a declaraition annotation then use the use 
							  *  	
							  */
							 
							 		 
							new AnnotationEntry( CONTEXT_ATTRIBUTE  , getName(attrDecl) , self , type , useAnno != null ? useAnno : declAnno  );
					
							
						}
						
					}
					

					/*
					 * Recuse to local elements
					 */
					
					XSParticle particle = ctype.getParticle();
					getAnnotations( particle , self );
					
				}
			}
		}



		private void getTypeAnnotations( AnnotationEntry parent) throws XPathException,	XMLStreamException, CoreException, SaxonApiException, IOException {
			XSModel model = mSchema.getModel();
			XSNamedMap types  = model.getComponents(XSConstants.TYPE_DEFINITION);
			for( int i = 0 ; i < types.getLength() ; i++ ){
				XSObject obj = types.item(i);

				if( obj instanceof XSTypeDefinition ){
					AnnotationList value = null ;

					XSTypeDefinition type = (XSTypeDefinition) obj ;
					if( type instanceof XSComplexTypeDefinition )
						value = getAnnotations( ((XSComplexTypeDefinition)type).getAnnotations() );
					else
						if( type instanceof XSSimpleTypeDefinition )
							value = getAnnotations( ((XSSimpleTypeDefinition)type).getAnnotations() );

					
					
					
					new AnnotationEntry(  CONTEXT_TYPE,  getName(type) , parent , type , value  );


				}


			}
		}
		
		private QName getName(XSTypeDefinition type) {
			if( type == null )
				return null ;
			
			String namespace = type.getNamespace();
			String name = type.getName();
			QName qname = new QName(Util.notNull(namespace), Util.notNull(name) );
			return qname;
		
		
		}
		private QName getName(XSSimpleTypeDefinition type) {
			if( type == null )
				return null ;
			
			String namespace = type.getNamespace();
			String name = type.getName();
			QName qname = new QName(Util.notNull(namespace), Util.notNull(name) );
			return qname;
		
		
		}


		private QName getName(XSObject obj) {
			QName qname = new QName(obj.getNamespace(), obj.getName() );
			return qname;
		
		
		}

		private XQueryExecutable getXQuery(String string) throws SaxonApiException, IOException, URISyntaxException {

			XQueryCompiler mXQueryCompiler = Shell.getProcessor().newXQueryCompiler();

			
			InputStream isQuery = getClass().getResourceAsStream(string);
			try {
				URL url = mShell.getResource("/org/xmlsh/json/" + string );
				if( url != null ){
					URI uri = url.toURI();
					mXQueryCompiler.setBaseURI(  uri );
				}
				
				return mXQueryCompiler.compile( isQuery );
			} finally {
				isQuery.close();
			}
			
		}

		private AnnotationList  getAnnotations(XSObjectList annotations) throws XPathException, XMLStreamException, CoreException, SaxonApiException, IOException {
			
			if( annotations == null || annotations.getLength() == 0 )
				return null ;
			AnnotationList  annos = new AnnotationList( annotations.getLength() );
			
			for( int i = 0 ; i < annotations.getLength() ; i++ ){
				XSAnnotation annotation = (XSAnnotation) annotations.item(i);
				annos.add(new Annotation(annotation));
			}
			
			return annos;

			
		}


		private void writeQName( XMLStreamWriter sw , String name , QName q ) throws XMLStreamException
		{
			if( q == null )
				return ;
			
			if( Util.isBlank(q.getLocalName()))
				return ;
			
			sw.writeStartElement("jxon" ,  name,JXON_NS );
			// sw.writeAttribute("prefix", q.getPrefix() );
			sw.writeAttribute("uri" , q.getNamespaceURI());
			sw.writeAttribute("localname", q.getLocalName());
			sw.writeEndElement();			
			
		}

		
}



//
//
//Copyright (C) 2008-2014 David A. Lee.
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
