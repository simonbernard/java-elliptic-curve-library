package de.rub.nds.ec.math;

/**
 * This class represents an elliptic curve over a finite field. It stores
 * the values {@link #a} and {@link #b}, by which the curve is represented.
 * <p />
 * <tt>EllipticCurve</tt> contains a subclass {@link Point} which represents
 * a point on this curve.
 */
public class EllipticCurve {

    /**
     * The field this curve is defined on.
     */
    public final FiniteField field;

    /**
     * Coefficient a of elliptic curve Y^2 = X^3 + a X Z^4 + b Z^6.
     */
    public final FiniteField.FieldElement a;

    /**
     * Coefficient b of elliptic curve Y^2 = X^3 + a X Z^4 + b Z^6.
     */
    public final FiniteField.FieldElement b;

    /**
     * This value has to be calculated once while the curve is initialized.
     * It is needed to accelerate point operations.
     *
     * @see Point#add
     */
    private final FiniteField.FieldElement inverseOfTwo;




    /**
     * Construct a new elliptic curve, given the field and the values a and b.
     *
     * @throws  IllegalArgumentException Parameters do not represent an elliptic curve.
     */
    public EllipticCurve(FiniteField field, FiniteField.FieldElement a, FiniteField.FieldElement b) throws IllegalArgumentException {
        this.field = field;
        this.a = a;
        this.b = b;
        inverseOfTwo = field.new FieldElement("2").invert();

        if(! this.valid())
            throw new IllegalArgumentException( "FiniteField.EllipticCurve.<init>: Not a valid curve." );
    }




    /**
     * Test if this is a valid elliptic curve.
     *
     * @return  <tt>true</tt> if and only if
     *          <code>4 * a^3 + 27 * b^2 != 0 (mod p)</code>, <tt>false</tt>
     *          otherwise.
     */
    public boolean valid() {
        FiniteField.FieldElement aRes = field.new FieldElement(a.toString());
        FiniteField.FieldElement bRes = field.new FieldElement(b.toString());

        aRes = aRes.multiply( a ).multiply( a );
        aRes = aRes.multiplyByWord( 4 );

        bRes = bRes.multiply( b );
        bRes = bRes.multiplyByWord( 27 );

        aRes = aRes.add( bRes );

        return ( ! aRes.equals( field.ZERO ) );
    }

    /**
     * Get a string representation of this elliptic curve.
     *
     * @return a String representing <tt>EllipticCurve this</tt>.
     */
    public String toString( ) {

        return "Y^2 = X^3 + " + a.toString() + " * X + " + b.toString();

    }




    /**
     * This class represents a point on the elliptic curve
     * over a finite field. The point is stored in Jacobian-projective
     * coordinates. For details on this please refer to <b>GECC</b>, p. 93.
     * <p />
     * Storing the points in Jacobian-projective coordinates allows us to
     * implement point operations like {@link #add} or {@link #multiply}
     * without any divisions, which makes it very efficient.
     * The only case where we need a modular division is if we want to
     * convert the point back to affine coordinates.
     */
    public class Point {

        /**
         * Coordinate x in Jacobian-projective coordinates of the point.
         */
        private FiniteField.FieldElement x;

        /**
         * Coordinate y in Jacobian-projective coordinates of the point.
         */
        private FiniteField.FieldElement y;

        /**
         * Coordinate z in Jacobian-projective coordinates of the point.
         */
        private FiniteField.FieldElement z;

        /**
         * Is this point the point at infinity?
         */
        private boolean infinity = false;




        /**
         * Creates a new point without initializing the coordinates,
         * i.e. create a point at infinity.
         */
        public Point( ) {

            this.infinity = true;
            this.x = null;
            this.y = null;
            this.z = null;

        }

        /**
         * Create a new point on the elliptic curve, given the
         * coordinates in affine description in a string representation.
         *
         * @throws  IllegalArgumentException Parameters do not represent a
         *          point on the given elliptic curve.
         */
        public Point( FiniteField.FieldElement x, FiniteField.FieldElement y ) throws IllegalArgumentException {
            this.x = x;
            this.y = y;
            this.z = field.new FieldElement("1");

            if( ! this.onCurve() )
                throw new IllegalArgumentException("FiniteField.EllipticCurve.Point.<init>: Point not on curve." );
        }

        /**
         * Create a new point on the elliptic curve, given the
         * coordinates in Jacobian-projective description in a string
         * representation.
         */
        private Point( FiniteField.FieldElement x, FiniteField.FieldElement y, FiniteField.FieldElement z ) {
            this.x = x;
            this.y = y;
            this.z = z;
        }


        /**
         * Create a new point, given a point. This is used to create return
         * values for methods like {@link #add}.
         */
        private Point( Point P ) {
            this.x = field.new FieldElement(P.x.toString());
            this.y = field.new FieldElement(P.y.toString());
            this.z = field.new FieldElement(P.z.toString());
            this.infinity = P.infinity;
        }




        /**
         * Returns the x-coordinate of point <tt>this</tt>
         * in affine representation
         *
         * @return x-coordinate of this
         */
        public FiniteField.FieldElement getX( ) {

            return x.divide( z.multiply( z ) );

        }

        /**
         * Returns the y-coordinate of point <tt>this</tt>
         * in affine representation
         *
         * @return y-coordinate of this
         */
        public FiniteField.FieldElement getY( ) {

            return y.divide( z.multiply( z ).multiply( z ) );

        }

        /**
         * Determines wether <tt>this</tt> is the point at infinity.
         *
         * @return  <tt>true</tt> if and only if this is the point at
         *          infinity, <tt>false</tt> otherwise.
         */
        public boolean isInfinity( ) {

            return this.infinity;

        }

        /**
         * Checks wether this point lies on the given curve.
         *
         * @return  true if and only if this is a point on the curve,
         *          false otherwise.
         */
        private boolean onCurve( ) {

            if( infinity )
                return true;

            FiniteField.FieldElement zPow2 = z.multiply( z );
            FiniteField.FieldElement zPow4 = zPow2.multiply( zPow2 );
            FiniteField.FieldElement zPow6 = zPow4.multiply( zPow2 );

            FiniteField.FieldElement left  = y.multiply( y );
            FiniteField.FieldElement right = x.multiply( x.multiply( x ) );
            right = right.add( a.multiply( x.multiply( zPow4 ) ) );
            right = right.add( b.multiply( zPow6 ) );

            return left.equals( right );

        }

        /**
         * Add two points. The algorithm for doing that can be found in
         * <b>CAi</b>, p. 39.
         * <p />
         * This method first checks whether the points are the same or one
         * is the negated of the other. If this is the case it calls
         * {@link #twice} or returns the point at infinity. Otherwise the
         * two points are added.
         *
         * @param   operand the point to add to <tt>this</tt>.
         * @return  <code>this + operand</code>
         *
         * @see     #twice
         */
        public Point add( Point operand ) {

            if( this.infinity ) {
                if( operand.infinity )
                    return new Point( );
                else
                    return new Point( operand );
            }
            if( operand.infinity )
                return new Point( this );

            if( this.equals( operand ) )
                return this.twice();

            FiniteField.FieldElement z1Pow2 = this.z.multiply( this.z );
            FiniteField.FieldElement z2Pow2 = operand.z.multiply( operand.z );

            FiniteField.FieldElement lambda1 = this.x.multiply( z2Pow2 );
            FiniteField.FieldElement lambda2 = operand.x.multiply( z1Pow2 );
            FiniteField.FieldElement lambda3 = lambda1.subtract( lambda2 );
            FiniteField.FieldElement lambda7 = lambda1.add( lambda2 );

            FiniteField.FieldElement lambda4 = this.y.multiply(
                    z2Pow2.multiply( operand.z ) );
            FiniteField.FieldElement lambda5 = operand.y.multiply(
                    z1Pow2.multiply( this.z ) );
            FiniteField.FieldElement lambda6 = lambda4.subtract( lambda5 );
            FiniteField.FieldElement lambda8 = lambda4.add( lambda5 );

            FiniteField.FieldElement lambda3Pow2 = lambda3.multiply( lambda3 );

            FiniteField.FieldElement z3 = this.z.multiply( operand.z ).
                    multiply( lambda3 );

            // this was -operand, therfor the result is infinity
            if( z3.equals( field.ZERO ) )
                return new Point( );

            FiniteField.FieldElement x3 = lambda6.multiply( lambda6 );
            x3 = x3.subtract( lambda7.multiply( lambda3Pow2 ) );

            FiniteField.FieldElement lambda9 = lambda7.multiply( lambda3Pow2 );
            lambda9 = lambda9.subtract( x3.add( x3 ) );

            FiniteField.FieldElement y3 = lambda9.multiply( lambda6 ).
                    subtract( lambda8.
                            multiply( lambda3Pow2.multiply(lambda3) ) );
            y3 = y3.multiply( inverseOfTwo );

            return new Point( x3, y3, z3 );

        }

        /**
         * Subtract two points. We just have to negate <tt>operand</tt>
         * and call method {@link #add}.
         *
         * @param   operand the point to subtract from <tt>this</tt>.
         * @return  <code>this - operand</code>
         *
         * @see     #add
         * @see     #negate
         */
        public Point subtract( Point operand ) {

            if( operand.infinity )
                return new Point( this );

            return this.add( operand.negate() );

        }

        /**
         * Double a point. The algorithm for doing that can be found in
         * <b>CAi</b>, p. 39.
         *
         * @return  <code>2 * this</code>
         */
        public Point twice( ) {

            if( infinity )
                return new Point( );
            if( y.equals( field.ZERO ) )
                return new Point( );

            FiniteField.FieldElement yPow2 = y.multiply( y );
            FiniteField.FieldElement zPow2 = z.multiply( z );

            FiniteField.FieldElement lambda1 = x.multiply(x).multiplyByWord( 3 ).
                    add( a.multiply( zPow2.multiply(zPow2) ) );
            FiniteField.FieldElement lambda2 = x.multiply( yPow2 ).multiplyByWord( 4 );
            FiniteField.FieldElement lambda3 = yPow2.multiply( yPow2 ).
                    multiplyByWord( 8 );

            FiniteField.FieldElement z3 = y.multiply( z );
            z3 = z3.add( z3 );
            FiniteField.FieldElement x3 = lambda1.multiply( lambda1 ).
                    subtract( lambda2.add( lambda2 ) );
            FiniteField.FieldElement y3 = lambda1.multiply( lambda2.subtract(x3) ).
                    subtract( lambda3 );

            return new Point( x3, y3, z3 );

        }

        /**
         * Multiply a point by a scalar. This method calls a window
         * NAF algorithm for point multiplication which can be found in
         * <b>GECC</b>, p. 100. The optimal window width for the current
         * bitsize is determined before calling the actual multiplication
         * algorithm.
         *
         * @param   factor the <tt>FieldElement</tt> by which to multiply
         *          <tt>this</tt>.
         * @return  <code>this * factor</code>
         *
         * @see     #twice
         * @see     #add
         */
        public Point multiply( FiniteField.FieldElement factor ) {

            // bitsize of the factor
            int bitSize = factor.numBits();

            // calculate the window width we want to use for the
            // multiplication. this depends on the bitsize of the
            // factor (see GECC, p. 100)
            byte width = 2;
            int additions = ( ( 1 << (width - 2) ) - 1 )
                    + ( bitSize / (width + 1) );

            while( width < 6 ) {

                width ++;
                int additions2 = ( 1 << (width - 2) ) - 1;
                additions2 += bitSize / (width + 1);

                if( additions2 > additions ) {
                    width --;
                    break;
                }

                additions = ( 1 << (width - 2) ) - 1;
                additions += bitSize / (width + 1);
            }

            return multiply( factor, width );

        }

        /**
         * Multiply a point by a scalar. This method calls a window
         * NAF algorithm for point multiplication which can be found in
         * <b>GECC</b>, p. 100.
         *
         * @param   factor the <tt>FieldElement</tt> by which to multiply
         *          <tt>this</tt>.
         * @param   width window width to use.
         * @return  <code>this * factor</code>
         *
         * @see     FiniteField.FieldElement#toNAF
         * @see     #twice
         * @see     #add
         */
        public Point multiply(FiniteField.FieldElement factor, byte width ) {

            Point[] precomputed = precomputeNAFPoints( width );

            return this.multiply( factor, precomputed, width );

        }

        /**
         * Multiply a point by a scalar. This method implements a window
         * NAF algorithm for point multiplication which can be found in
         * <b>GECC</b>, p. 100.
         *
         * @param   factor the <tt>FieldElement</tt> by which to multiply
         *          <tt>this</tt>.
         * @param   precomputed an array containing precomputed points,
         *          these can be computed by {@link #precomputeNAFPoints}.
         * @param   width the window width to use.
         * @return  <code>this * factor</code>
         *
         * @see     #precomputeNAFPoints
         * @see     FiniteField.FieldElement#toNAF
         * @see     #twice
         * @see     #add
         */
        public Point multiply( FiniteField.FieldElement factor,
                               Point[] precomputed,
                               byte width ) {

            byte[] naf = factor.toNAF( width );
            Point result = new Point( );

            for( int i = naf.length - 1; i >= 0; i-- ) {

                result = result.twice( );

                if( naf[i] != 0 ) {

                    if( naf[i] > 0 )
                        result = result.
                                add( precomputed[ (naf[i] - 1) >> 1 ] );
                    else
                        result = result.
                                subtract( precomputed[ ((-naf[i]) - 1) >> 1 ] );

                }
            }

            return result;

        }

        /**
         * Precompute NAF points. These are needed for {@link #multiply(
         * FiniteField.FieldElement,EllipticCurve.Point[],byte)
         * NAF multiplication algorithm}. This is quite fast if you have to
         * multiply the same point by different factors, since you only
         * have to precompute these points once.
         * <p />
         * Once you precomputed the points you just have to compute the NAF
         * representation of your factor and call {@link #multiply(
         * FiniteField.FieldElement,
         * EllipticCurve.Point[],byte)}.
         * This is significantly faster than using {@link #multiply}.
         *
         * @param   width window width to use.
         * @return  array of precomputed points.
         *
         * @see     FiniteField.FieldElement#toNAF
         * @see     #multiply(FiniteField.FieldElement, EllipticCurve.Point[],byte)
         */
        public Point[] precomputeNAFPoints( byte width ) {

            int length = 1 << (width - 1);
            Point[] result = new Point[length];

            result[0] = new Point( this );

            Point tmp = this.twice();

            for( int i = 1; i < length; i++ )
                result[i] = result[i - 1].add( tmp );

            return result;

        }

        /**
         * Negate a point.
         *
         * @return  <code>P = (x, -y, z)</code>
         *
         * @see     #subtract
         * @see     FiniteField.FieldElement#negate
         */
        public Point negate( ) {

            return new Point( this.x, this.y.negate(), this.z );

        }

        /**
         * Compare two points.
         *
         * @param   operand the point to compare to <tt>this</tt>.
         * @return  <tt>true</tt> if and only if <code>this == operand
         *          </code>, <tt>false</tt> otherwise.
         */
        public boolean equals( Point operand ) {
            if( this.infinity && operand.infinity )
                return true;
            if( this.infinity || operand.infinity )
                return false;

            if(this.x.compareTo( operand.x ) == FiniteField.EQ && this.y.compareTo( operand.y ) == FiniteField.EQ && this.z.compareTo( operand.z ) == FiniteField.EQ)
                return true;

            return false;
        }

        /**
         * Returns a string representation of this point in affine
         * coordinates.
         *
         * @return a String representing point <tt>this</tt>.
         */
        public String toString( ) {

            if( infinity )
                return "INF";

            FiniteField.FieldElement zPow2 = z.multiply( z );
            FiniteField.FieldElement zPow3 = zPow2.multiply( z );

            FiniteField.FieldElement xAffine = x.divide( zPow2 );
            FiniteField.FieldElement yAffine = y.divide( zPow3 );

            return "(" + xAffine.toString() + ", " +
                    yAffine.toString() + ")";

        }

    }

}
