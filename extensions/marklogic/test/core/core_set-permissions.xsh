# test get permissions
. ../init
ml:put -uri /test1.xml <[ <foo/> ]>
ml:set-permissions -x test -u test -r test -i test /test1.xml
ml:get-permissions /test1.xml
ml:del /test1.xml 
