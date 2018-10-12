/*
 * ECDSA.java
 */
 




package de.rub.nds.ec;




import de.rub.nds.ec.math.EllipticCurve;
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
    private EllipticCurve E;
    
    /**
     * Holds the domain parameter P.
     */    
    private EllipticCurve.Point P;
    
    /**
     * Precomputed points which are used for point
     * multiplication.
     */
    private EllipticCurve.Point[] nafPoints;
    
    /**
     * Initialize the ECDSA Signer with the field, the elliptic curve
     * and the point P.
     * @param F The field
     * @param E The elliptic curve
     * @param P The domain parameter P
     */
    public ECDSA( FiniteField F, EllipticCurve E, EllipticCurve.Point P ) {
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
    public Signature generateSignature(FiniteField.FieldElement message, FiniteField.FieldElement privateKey) {

        FiniteField.FieldElement r;
        FiniteField.FieldElement s;
        FiniteField.FieldElement k;

        EllipticCurve.Point R;
        
        do {
            k = F.new FieldElement( );
            
            do {
                R = P.multiply( k, nafPoints, (byte)6 );
                r = R.getX( );
            } while( r.compareTo( F.ZERO ) == FiniteField.EQ );

            s = k.invert( ).multiply( message.add( privateKey.multiply( r ) ) );
            
        } while( s.compareTo( F.new FieldElement( "0" ) ) == FiniteField.EQ );

        return new Signature(r, s, k);
        
    }
    
    /**
     * verify a signature, given a message and the public key Q.
     *
     * @param Q         the public key.
     * @param message   the message to verify the signature for.
     * @param signature the signature to verify
     *
     * @return  <tt>true</tt> if <tt>signature</tt> is a valid
     *          signature for <tt>message</tt> in the given domain.
     */
    public boolean verifySignature(EllipticCurve.Point Q, FiniteField.FieldElement message, Signature signature) {
        
        FiniteField.FieldElement w = signature.getS().invert( );
        FiniteField.FieldElement u1 = message.multiply( w );
        FiniteField.FieldElement u2 = signature.getR().multiply( w );
        
        EllipticCurve.Point X = P.multiply( u1, nafPoints, (byte)6 ).add( Q.multiply( u2 ) );

        if( X.isInfinity() )
            return false;
        
        if( X.getX().compareTo( signature.getR() ) == FiniteField.EQ )
            return true;
        
        return false;
        
    }    
}
