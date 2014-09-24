import commands posix
import module j=json

OUT=$(mktemp -d)

cd ../../samples


j:jxon -o $OUT -v -xsd books2.xsd > $OUT/all.xml 
xslt -f $OUT/tojson.xsl < books.xml > $OUT/books.jxml
xsdvalidate "http://www.xmlsh.org/jxml ../schemas/jxml.xsd" $OUT/books.jxml && xml2json -p < $OUT/books.jxml


rm -r -f $OUT