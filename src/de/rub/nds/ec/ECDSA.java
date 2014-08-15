/*
 * ECDSA.java
 */
 




package de.rub.nds.ec;




import de.rub.nds.ec.math.FiniteField;




/**
 * This class implements the ECDSA for signature generation
 * and verification using elliptic curves.
 * 
 * Use like this:<p /><tt>
 * FiniteField F = new FiniteField( String value );
 * <br />
 * FiniteField.EllipticCurve E = F.new EllipticCurve( String value );
 * <br />
 * FiniteField.EllipticCurve.Point P = E.new Point( String value );
 * <br />
 * FiniteField.FieldElement privateKey = F.new FieldElement( String value );
 * <br />
 * FiniteField.EllipticCurve.Point Q = P.multiply( privateKey );
 * <p />
 * FiniteField.FieldElement message = F.new FieldElement( );
 * <p />
 * ECDSA ecdsa = new ECDSA( F, E, P );
 * <p />
 * FiniteField.FieldElement[] signature = ecdsa.generateSignature( message, privateKey );
 * <p />
 * boolean valid = ecdsa.verifySignature( Q, message, signature );
 * </tt>
 * @author Simon Bernard, simon@bernard.cc, Ruhr-University Bochum
 */
public class ECDSA {
    
    /**
     * Holds the field on which we operate.
     */
    private FiniteField F;
    
    /**
     * Holds the curve on which we operate.
     */    
    private FiniteField.EllipticCurve E;
    
    /**
     * Holds the domain parameter P.
     */    
    private FiniteField.EllipticCurve.Point P;
    
    /**
     * Precomputed points which are used for point
     * multiplication.
     */
    private FiniteField.EllipticCurve.Point[] nafPoints;
    
    /**
     * Initialize the ECDSA Signer with the field, the elliptic curve
     * and the point P.
     * @param F The field
     * @param E The elliptic curve
     * @param P The domain parameter P
     */
    public ECDSA( FiniteField F, FiniteField.EllipticCurve E,
            FiniteField.EllipticCurve.Point P ) {
        
        this.F = F;
        this.E = E;
        this.P = P;
        this.nafPoints = P.precomputeNAFPoints( (byte)6 );
        
    }
    
    /**
     * Generate a signature for a given message, using the given private
     * key.
     *
     * @param   message a SHA1 hash of the message to sign.
     * @param   privateKey a <tt>FieldElement</tt> holding the private key.
     * @return  the signature for the given message.
     */
    public FiniteField.FieldElement[] generateSignature( 
            FiniteField.FieldElement message,
            FiniteField.FieldElement privateKey ) {
        
        FiniteField.FieldElement[] result = new FiniteField.FieldElement[2];
        result[0] = null;
        result[1] = null;
        
        FiniteField.EllipticCurve.Point R = null;
        
        do {
            FiniteField.FieldElement k = F.new FieldElement( );
            
            do {
                R = P.multiply( k, nafPoints, (byte)6 );
                result[0] = R.getX( );
            } while( result[0].compareTo( F.new FieldElement( "0" ) ) == FiniteField.EQ );
            
            result[1] = k.invert( ).multiply( message.add( privateKey.multiply( result[0] ) ) );
            
        } while( result[1].compareTo( F.new FieldElement( "0" ) ) == FiniteField.EQ );
        
        return result;
        
    }
    
    /**
     * verify a signature, given a message and the public key Q.
     *
     * @param Q         the public key.
     * @param message   the message to verify the signature for.
     * @param signature the signature to verify
     *
     * @return  <tt>true</tt> if and only if <tt>signature</tt> is a valid
     *          signature for <tt>message</tt> in the given domain.
     */
    public boolean verifySignature( FiniteField.EllipticCurve.Point Q,
            FiniteField.FieldElement message,
            FiniteField.FieldElement[] signature ) {
        
        FiniteField.FieldElement w = signature[1].invert( );
        FiniteField.FieldElement u1 = message.multiply( w );
        FiniteField.FieldElement u2 = signature[0].multiply( w );
        
        FiniteField.EllipticCurve.Point X = P.multiply( u1, nafPoints, (byte)6 ).add( Q.multiply( u2 ) );
        
        if( X.isInfinity() )
            return false;
        
        if( X.getX().compareTo( signature[0] ) == FiniteField.EQ )
            return true;
        
        return false;
        
    }    
}
