package org.xmlsh.types;

public class JavaType extends TypeBase implements IType
{

    private JavaType(Class<?> cls) {
        super(cls);
    }

    @Override
    public TypeFamily family() {
      return TypeFamily.JAVA ;
    }

    @Override
    public XTypeKind kind() {
        return null;
    }
    
    public static IType getType(Object obj) {
        return new JavaType( obj == null ? null : obj.getClass() );
    }

  

}
