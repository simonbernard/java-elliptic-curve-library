/*
 * FiniteField.java
 */




package de.rub.nds.ec.math;




import java.util.Random;




/**
 * This class represents a finite field of prime order. Therefor it stores
 * its order in a <tt>FieldElement</tt> with name p and nests the class
 * <tt>FieldElement</tt> which provides arithmetic over this field of arbitrary
 * precision. Once you generated a new object of type <tt>FiniteField</tt>
 * you can use it to generate new <tt>FieldElements</tt> over that field.
 * The syntax for that is:<br />
 * <code>
 * FiniteField F = new FiniteField( String prime );<br />
 * FiniteField.FieldElement a = F.new FieldElement( String value );<br />
 * </code>
 * <p />
 * Furthermore it nests the class <tt>EllipticCurve</tt> which provides basic
 * arithmetic operations on elliptic curves over finite fields such as point
 * addition and point multiplication. The syntax for generating a new elliptic
 * curve is:<br />
 * <code>
 * FiniteField.EllipticCurve E = F.new EllipticCurve( String a, String b );
 * <br />
 * </code>
 * <p />
 * List of abbreviations used throughout this javadoc:
 * <ul>
 * <li><b>HAC:</b> Handbook of Applied Cryptography. A. Menezes, P. van
 *                 Oorschot, and S. Vanstone. CRC Press, 1996.</li>
 * <li><b>GECC:</b> Guide to Elliptic Curve Cryptography. D. Hankerson,
 *                  A. Menezes, S. Vanstone. Springer, New York, 2004.</li>
 * <li><b>CAi:</b> Cryptography: An introduction. Nigel Smart.
 *                 McGraw-Hill Education, Berkshire, 2003.</li>
 * </ul>
 * @author Simon Bernard, mail@s-m-n.org, Ruhr-University Bochums
 * @see FiniteField.FieldElement
 * @see FiniteField.EllipticCurve
 */
public class FiniteField {
    
    /**
     * Holds the prime order of this finite field.
     *
     * This <tt>FieldElement</tt> is set to the prime you passed to the
     * constructor for this field.
     */
    public final FieldElement p;
    
    /**
     * This <tt>FieldElement</tt> holds the constant needed for the Barrett
     * reduction algorithm. For details on Barrett reduction please refer
     * to <b>HAC</b>, p. 604.
     * 
     * @see FiniteField.FieldElement#mod
     */
    private final FieldElement barrettConstant;
    
    /**
     * Return value of method {@link FiniteField.FieldElement#compareTo} if
     * this was less than operand.
     * 
     * @see FiniteField.FieldElement#compareTo
     */
    public final static byte LT = -1;
    
    /**
     * Return value of method {@link FiniteField.FieldElement#compareTo} if
     * this was equal to operand.
     * 
     * @see FiniteField.FieldElement#compareTo
     */
    public final static byte EQ = 0;
    
    /**
     * Return value of method {@link FiniteField.FieldElement#compareTo} if
     * this was greater than operand.
     * 
     * @see FiniteField.FieldElement#compareTo
     */
    public final static byte GT = 1;
        
    /**
     * Constant with value 0.
     */
    public final FieldElement ZERO = new FieldElement( 0 );
    
    /**
     * Constant with value 1.
     */    
    public final FieldElement ONE  = new FieldElement( 1 );
    
    /**
     * Construct a new finite field given a string representation of
     * the prime which should be the order of this field.
     * The string has to be in radix 16. After initializing the field,
     * the constant {@link #barrettConstant} is computed to allow faster
     * modular reductions.
     *
     * @param value The value which to initialise <tt>this</tt> with in hex.
     */
    public FiniteField ( String value ) {
        
        p = new FieldElement ( value );
        barrettConstant = p.calculateBarrettConstant( );
       
    }
    

    
    
    /**
     * This class represents an element of arbitrary size over a
     * finite field of order (and character) p. This means, that all
     * arithmetic functions provide results within this field. You can not
     * initialize a <tt>FieldElement</tt> with a value that is greater than
     * or equal to the order of the underlying field.
     *
     * @see #FiniteField.FieldElement(java.lang.String)
     */
    public class FieldElement {
        
        /**
         * This bit mask is needed for the conversion of a signed integer
         * to an unsigned long.
         */
        private final static long INT2LONG = 0xFFFFFFFFL;
        
        /**
         * The signum is 0 for 0 and 1 for positive numbers,
         * we dont have negative numbers at all, since we provide finite field
         * arithmetic.
         */
        private byte signum;

        /**
         * The magnitude holds the actual value of our <tt>FieldElement</tt>,
         * the rightmost bit of magnitude[0] is the least significant bit.
         */
        private int[] magnitude;
        
        
        
        /**
         * This private constructor is used to generate return
         * values of arithmetic functions. 
         * The <tt>FieldElement</tt> given to the constructor is just copied.
         */
        private FieldElement( FieldElement value ) {
            
            magnitude = new int[value.magnitude.length];
            for( int i = 0; i < magnitude.length; i++ )
                magnitude[i] = value.magnitude[i];
            signum = value.signum;
            
        }
        
        /**
         * This private constructor is used to construct return
         * values of arithmetic functions.
         * The magnitude given to the constructor is not copied!
         */
        private FieldElement( int[] mag, byte sig ) {
            
            magnitude = mag;
            signum = sig;
            
        }
        
        /**
         * This private constructor is used to generate
         * FieldElements with small values for use in functions
         * like {@link #multiply} or {@link #divide}.
         *
         * @throws NumberFormatException <tt>value</tt> < 0.
         */
        private FieldElement( int value ) throws NumberFormatException {
            
            if ( value < 0 )
                throw new NumberFormatException
                ( "FiniteField.FieldElement.<init>: Integer too small" );
            
            signum = 1;
            
            if ( value == 0 )
                signum = 0;
            
            magnitude = new int[1];
            magnitude[0] = value;
            
        }
        
        /**
         * Construct a new <tt>FieldElement</tt> given a String representation
         * of the value in radix 16.
         *
         * @throws  NumberFormatException <tt>value</tt> has zero length.
         * @throws  NumberFormatException <tt>value</tt> contains illegal digits.
         * @throws  NumberFormatException <tt>value</tt> is greater than order
         *          of this field.
         */
        public FieldElement( String value ) throws NumberFormatException {
            
            if ( value.length() == 0 )
                throw new NumberFormatException
                ( "FiniteField.FieldElement.<init>: Length of value is 0" );
            
            // counter for the string
            int i = 0;
            
            // length of string
            int length = value.length();
            
            // number of digits in string (after skipping leading zeros)
            int numDigits;
            
            // skip leading zeros
            while( i < length && Character.digit( value.charAt(i), 16 ) == 0 )
                i++;
            
            // value is zero, we are done
            if( i == length ) {
                magnitude = new int[1];
                signum = 0;
                return;
            }
            
            // calculate num digits in string
            else {
                signum = 1;
                numDigits = length - i;
            }
            
            // calculate expected size of magnitude and allocate
            // memory for it
            // number of bits needed for each digit: 4
            int numBits = (numDigits * 4) + 1;
            int numWords = (numBits + 31) / 32;
            magnitude = new int[numWords];
            
            // the length of the first group might be smaller
            // than 7
            int lengthFirstDigitGroup = numDigits % 7;
            if( lengthFirstDigitGroup == 0 )
                lengthFirstDigitGroup = 7;
            
            // Calculate value of first digit group
            String group = value.substring( i, i += lengthFirstDigitGroup );
            magnitude[0] = Integer.parseInt( group, 16 );
            
            if( magnitude[0] < 0 )
                throw new NumberFormatException
                ( "FiniteField.FieldElement.<init>: Illegal digit in value" );
            
            // holds value of current digit group as an integer
            int valueOfGroupI = 0;
            
            // holds value of current digit group as a long
            long valueOfGroupL;
            
            // value of current word in radix 10
            long radix = 0x10000000 & INT2LONG;
            
            while( i < length ) {
                
                // get value of current group
                group = value.substring( i, i += 7 );
                valueOfGroupI = Integer.parseInt( group, 16 );
                
                if( valueOfGroupI < 0 )
                    throw new NumberFormatException
                    ( "FiniteField.FieldElement.<init>: Illegal digit in value" );
                
                // convert value of group to a long
                valueOfGroupL = valueOfGroupI & INT2LONG;
                
                // weight each word of magnitude with radix 10 while
                // propagating the carry
                long product = 0;
                for( int k = 0; k < numWords; k++ ) {
                    product = radix * (magnitude[k] & INT2LONG)
                            + (product >>> 32);
                    magnitude[k] = (int)product;
                }
                
                // add the value of the current group and propagate
                // the carry to upper positions
                long sum = (magnitude[0] & INT2LONG) + valueOfGroupL;
                magnitude[0] = (int)sum;
                for( int k = 1; k < numWords; k++ ) {
                    sum = (magnitude[k] & INT2LONG) + (sum >>> 32);
                    magnitude[k] = (int)sum;
                }
            }
            
            magnitude = stripZeros( magnitude );
            
            if( p != null )
                if( this.compareTo( p ) >= EQ )
                    throw new NumberFormatException
                    ( "FiniteField.FieldElement.<init>:" +
                            "Value to great for underlying field" );

        }
        
        /**
         * Generate a new <tt>FieldElement</tt> randomly.
         */
        public FieldElement(  ) {
            
            Random random = new Random( );
            
            int numWords = p.magnitude.length;
            int numWordsMinusOne = numWords - 1;
            
            magnitude = new int[numWords];
            
            for( int i = 0; i < numWordsMinusOne; i++ ) {
                magnitude[i] = random.nextInt( );
            }
            
            // we have to make sure, that the new FieldElement does
            // not get larger than p.
            int highWord = p.magnitude[numWordsMinusOne];
            if( highWord > 0 ) {
                magnitude[numWordsMinusOne] = random.nextInt( highWord );
            }
            else {
                magnitude[numWordsMinusOne] = random.nextInt( -highWord );
                if( random.nextInt(2) > 0 )
                    magnitude[numWordsMinusOne] |= 0x80000000;
            }
            
            magnitude = stripZeros( magnitude );
            
            signum = 0;
            if( !(magnitude.length == 1 && magnitude[0] == 0) )
                signum = 1;
            
        }
        

        
        
        /**
         * Add two <tt>FieldElements</tt>.
         *
         * @param   operand <tt>FieldElement</tt> which is added to
         *          <tt>this</tt>.
         * @return  <code>this + operand (mod p)</code>
         */
        public FieldElement add( FieldElement operand ) {
            
            int[] mag;
            byte compare;
            FieldElement result;
            
            if ( this.signum == 0 )
                return operand;
            if ( operand.signum == 0 )
                return this;
            
            if ( this.magnitude.length > operand.magnitude.length )
                mag = add ( this.magnitude, operand.magnitude );
            else
                mag = add ( operand.magnitude, this.magnitude );
            
            result = new FieldElement( mag, (byte)1 );

            compare = result.compareTo( p );
            
            // The result is equal to p, we return zero
            if ( compare == EQ )
                return new FieldElement( new int[1], (byte)0 );
            
            // The result is greater than p, we have to subtract
            // p, then return
            else if ( compare == GT ) {
                result.magnitude = subtract( result.magnitude, p.magnitude );
            }
            
            return result;

        }
        
        /**
         * This method is used to add the magnitudes of two
         * <tt>FieldElements</tt>. Only to be called if you know which one
         * of the magnitudes is the longer one.
         *
         * @param   bigger The longer summand.
         * @param   smaller The shorter summand.
         * @return  <code>bigger + smaller</code>
         */
        private int[] add( int[] bigger, int[] smaller ) {
            
            long sum = 0;
            int i = 0;
            int[] result = new int[bigger.length + 1];
            boolean carry;

            // add the parts of both numbers which are of the same length
            while ( i < smaller.length ) {
                sum = (bigger[i] & INT2LONG) + (smaller[i] & INT2LONG) + sum;
                result[i] = (int)sum;
                sum >>>= 32;
                ++ i;
            }
            
            // copy the part of the longer number into the result
            // while propagating the carry
            carry = (sum != 0);
            while ( i < bigger.length && carry ) {
                carry = ((result[i] = bigger[i] + 1) == 0);
                ++ i;
            }
            
            if ( carry )
                result[bigger.length] = 1;
            
            // copy the longer number to the result array, where no carry
            // propagation is needed
            while ( i < bigger.length ) {
                result[i] = bigger[i];
                ++ i;
            }
            
            return stripZeros ( result );
        }
        
        /**
         * Subtract two <tt>FieldElements</tt>.
         *
         * @param   operand <tt>FieldElement</tt> which is subtracted from
         *          <tt>this</tt>.
         * @return  <code>this - operand (mod p)</code>
         */
        public FieldElement subtract( FieldElement operand ) {
            
            int[] mag;
            byte compare;
            
            compare = this.compareTo( operand );
            
            // operands are the same which means the result is zero
            if( compare == EQ )
                return new FieldElement( 0 );
            
            // this is greater than operand => subtract the magnitudes,
            // the result is already modulos p
            else if( compare == GT )
                mag = subtract( this.magnitude, operand.magnitude );
            
            // in this case we subtract this from operand and subtract
            // the result from p
            else {
                mag = subtract( operand.magnitude, this.magnitude );
                mag = subtract( p.magnitude, mag );
            }
                       
            return new FieldElement ( mag, (byte)1 );
        }
        
        /**
         * This method is used to subtract the magnitudes of two
         * <tt>FieldElements</tt>. Only to be called if you know which one
         * of the magnitudes is the longer one.
         *
         * @param   bigger The longer subtrahend.
         * @param   smaller The shorter subtrahend.
         * @return  <code>bigger - smaller</code>
         */
        private int[] subtract( int[] bigger, int[] smaller ) {
            
            long diff = 0;
            int i = 0;
            int[] result = new int[bigger.length];
            boolean borrow;

            // subtract the parts of both numbers which are of the same length
            while ( i < smaller.length ) {
                diff = (bigger[i] & INT2LONG) - (smaller[i] & INT2LONG)
                     + (diff >> 32);
                result[i] = (int)diff;
                ++i;
            }
            
            // copy the part of the longer number into the result
            // while propagating the borrow
            borrow = (diff >> 32 != 0);
            while ( borrow && i < bigger.length ) {
                borrow = ((result[i] = bigger[i] - 1) == -1);
                ++i;
            }
            
            // copy the longer number to the result array, where no carry
            // propagation is needed
            while ( i < bigger.length ) {
                result[i] = bigger[i];
                ++i;
            }
            
            return stripZeros ( result );
        }
        
        /**
         * Multiply two <tt>FieldElements</tt>.
         *
         * @param   factor <tt>FieldElement</tt> which to multiply
         *          <tt>this</tt> with.
         * @return  <code>this * factor (mod p)</code>
         */
        public FieldElement multiply( FieldElement factor ) {
            
            if( factor.signum == 0 || this.signum == 0 )
                return new FieldElement( 0 );
            if( this.magnitude.length == 1 && 
                    (this.magnitude[0] & INT2LONG) == 1 )
                return factor;
            if( factor.magnitude.length == 1 && 
                    (factor.magnitude[0] & INT2LONG) == 1 )
                return this;

            int[] result = multiply( this.magnitude, factor.magnitude );
            
            return new FieldElement( result, (byte)1 ).mod( );
            
        }
        
        /**
         * This method is used to multiply the magnitudes of two
         * <tt>FieldElements</tt>. The size of the operands does not
         * matter.
         *
         * @param   operand1 The first factor.
         * @param   operand2 The second factor.
         * @return  <code>operand1 * operand2</code>
         */
        private int[] multiply( int[] operand1, int[] operand2 ) {
            
            int lengthThis = operand1.length, lengthFactor = operand2.length;
            
            int[] result = new int[lengthThis + lengthFactor];
            
            for( int i = 0; i < lengthFactor; i++ ) {
                
                long factor_i = (operand2[i] & INT2LONG);
                long value = 0;

                for( int j = 0; j < lengthThis; j++ ) {
                    int iPlusj = i + j;
                    value += factor_i * (operand1[j] & INT2LONG)
                           + (result[iPlusj] & INT2LONG);
                    result[iPlusj] = (int)value;
                    value >>>= 32;
                }

                result[i + lengthThis] = (int)value;
            }
    
            result = stripZeros( result );
            
            return result;
            
        }
        
        /**
         * Multiply <tt>this</tt> by an integer.
         *
         * @param   factor <tt>int</tt> which to multiply
         *          <tt>this</tt> with.
         * @return  <code>this * factor (mod p)</code>
         */
        public FieldElement multiplyByWord( int factor ) {
            
            int length = this.magnitude.length;
            int[] result = new int[length + 1];
            long carry = 0;
            
            for( int i = 0; i < length; i++ ) {
                carry = ((this.magnitude[i] & INT2LONG) * factor) + carry;
                result[i] = (int)carry;
                carry >>>= 32;
            }
            if( carry != 0 )
                result[length] = (int)carry;
            else
                result = stripZeros( result );
            
            return new FieldElement( result, (byte)1 ).mod( );            
        }
        
        /**
         * Divide two <tt>FieldElements</tt>, this operation is only needed
         * for converting projective to affine coordinates in {@link 
         * FiniteField.EllipticCurve.Point} or for algorithms like ECDSA.
         * <p>
         * Therfor a binary division algorithm is used which can be found
         * in <b>GECC</b>, p. 41.
         *
         * @param   divisor <tt>FieldElement</tt> which to divide this
         *          by.
         * @return  <code>this * divisor^-1 (mod p)</code>
         *
         * @throws  ArithmeticException <tt>divisor</tt> is zero.
         *
         * @see FiniteField.EllipticCurve.Point
         */
        public FieldElement divide( FieldElement divisor )
               throws ArithmeticException {
            
            FieldElement u = new FieldElement( divisor );
            FieldElement v = new FieldElement( p );
            FieldElement x1 = new FieldElement( this );
            FieldElement x2 = new FieldElement( 0 );
            
            if( divisor.signum == 0 )
                throw new ArithmeticException
                ( "FiniteField.FieldElement.divide: Division by zero." );
            else if( divisor.magnitude.length == 1 && 
                    (divisor.magnitude[0] & INT2LONG) == 1 )
                return new FieldElement( this );
            
            if( this.signum == 0 )
                return new FieldElement( 0 );
            
            while( !(u.magnitude.length == 1 && 
                    (u.magnitude[0] & INT2LONG) == 1)
                && !(v.magnitude.length == 1 && 
                    (v.magnitude[0] & INT2LONG) == 1)
                ) {
                
                while( (u.magnitude[0] & 1) == 0 ) {
                    u.magnitude = rightShift( u.magnitude );
                    if( (x1.magnitude[0] & 1) == 0 )
                        x1.magnitude = rightShift( x1.magnitude );
                    else {
                        x1.magnitude = add( p.magnitude, x1.magnitude );
                        x1.magnitude = rightShift( x1.magnitude );
                    }
                }
                
                while( (v.magnitude[0] & 1) == 0 ) {
                    v.magnitude = rightShift( v.magnitude );
                    if( (x2.magnitude[0] & 1) == 0 )
                        x2.magnitude = rightShift( x2.magnitude );
                    else {
                        x2.magnitude = add( p.magnitude, x2.magnitude );
                        x2.magnitude = rightShift( x2.magnitude );
                    }
                }
                
                if( u.compareTo( v ) >= EQ ) {
                    u.magnitude = subtract( u.magnitude, v.magnitude );
                    x1 = x1.subtract( x2 );
                }
                else {
                    v.magnitude = subtract( v.magnitude, u.magnitude );
                    x2 = x2.subtract( x1 );
                }
            }
            
            if( u.magnitude.length == 1 && (u.magnitude[0] & INT2LONG) == 1 )
                return x1;
            
            return x2;
            
        }
        
        /**
         * Invert <tt>FieldElement this</tt>. This is needed for point addition
         * in {@link FiniteField.EllipticCurve.Point}.
         *
         * The algorithm used is the same as for divsion and can be
         * found in <b>GECC</b>, p. 41.
         *
         * @return  <code>this^-1 (mod p)</code>
         * @see     #divide
         * @see     FiniteField.EllipticCurve#inverseOfTwo
         */
        public FieldElement invert( ) {
           
            // The multiplicative inverse is 1/this (mod p)
            return ONE.divide( this );
            
        }
        
        /**
         * Negates <tt>FieldElement this</tt> by subtracting it from p. This
         * is needed if we want to subtract two points on an elliptic curve
         * from each other.
         *
         * @return  <code>p - this</code>
         * @see     FiniteField.EllipticCurve.Point#subtract
         */
        public FieldElement negate( ) {
            
            return p.subtract( this );
            
        }
        
        /**
         * This method implements a modular reduction algorithm proposed
         * by Barrett. The algorithm can be found in <b>HAC</b>, p. 604.
         *
         * @return  <code>this mod p</code>
         * @see     FiniteField#barrettConstant
         * @see     FiniteField.FieldElement#calculateBarrettConstant
         */
        private FieldElement mod( ) {
            
            int k = p.magnitude.length;
            int kMinusOne = k - 1;
            int kPlusOne = k + 1;
            int xLength = this.magnitude.length;
            int lengthToCopy, i = 0;
            
            if( this.compareTo( p ) == LT )
                return new FieldElement( this );
                        
            // calculate q1, this is a right shift by k-1 words
            lengthToCopy = xLength - kMinusOne;
            int[] q1 = new int[lengthToCopy];
            for( i = 0; i < lengthToCopy; i++ )
                q1[i] = this.magnitude[i + kMinusOne];

            // calculate q2
            int[] q2 = multiply( barrettConstant.magnitude, q1 );

            // calculate q3, we use q1 for the result
            lengthToCopy = q2.length - kPlusOne;
            q1 = new int[lengthToCopy];
            for( i = 0; i < lengthToCopy; i++ )
                q1[i] = q2[i + kPlusOne];

            // calculate r1, which means keep the k+1 lowest
            // words of x, we use q2 to store the result
            lengthToCopy = (kPlusOne > xLength ? xLength : kPlusOne);
            q2 = new int[lengthToCopy];
            for( i = 0; i < lengthToCopy; i++ )
                q2[i] = this.magnitude[i];
            
            // calculate r2, the result is stored in q3
            int[] tmp = multiply( p.magnitude, q1 );
            lengthToCopy = (kPlusOne > tmp.length ? tmp.length : kPlusOne);
            int[] q3 = new int[lengthToCopy];
            for( i = 0; i < lengthToCopy; i++ )
                q3[i] = tmp[i];

            // calculate r, this includes step 3 of algorithm
            if( q2.length < q3.length || 
                    (q2.length == q3.length && compareTo( q2, q3 ) == LT) ) {
                int[] b = new int[kPlusOne];
                b[k] = 0x01;
                q2 = add( b, q2 );
            }
            
            q1 = subtract( q2, q3 );
            
            // final subtractions, step 4 in algorithm
            // this has to be done at most twice
            while( (q1.length < p.magnitude.length ? LT : 
                    compareTo( q1, p.magnitude ) ) >= EQ )
                q1 = subtract( q1, p.magnitude );

            return new FieldElement( q1, (byte)1 );
            
        }

        /**
         * Divides <tt>FieldElement this</tt> by 2.
         *
         * This is a simple right shift and no modular operation. This method
         * is therfor private.
         *
         * @return  <code>this >> 1</code>
         */
        private FieldElement rightShift( ) {
            
            byte sign = 1;
            
            if( this.signum == 0 )
                return new FieldElement ( new int[1], (byte)0 );
            if( this.magnitude.length == 1 && 
                    (this.magnitude[0] & INT2LONG) == 1 )
                return new FieldElement ( new int[1], (byte)0 );
        
            return new FieldElement ( rightShift(this.magnitude), (byte)1 );
            
        }
        
        /**
         * Divide a given magnitude by 2.
         *
         * This is a simple right shift and no modular operation.
         *
         * @param   operand magnitude of <tt>FieldElement</tt> to shift.
         * @return  <code>operand >> 1</code>
         */
        private int[] rightShift( int[] operand ) {

            int carry = 0, i = operand.length;
            
            int[] result = new int[i];

            while ( --i >= 0 ) {
                result[i] = (operand[i] >>> 1) | carry;
                carry = operand[i] << 31;
            }
            
            return stripZeros ( result );
            
        }
        
        /**
         * Multiplies <tt>FieldElement this</tt> by 2.
         *
         * This is a simple left shift and no modular operation. This method
         * is therfor private.
         *
         * @return  <code>this << 1</code>
         */
        private FieldElement leftShift( ) {
            
            if ( this.signum == 0 )
                return new FieldElement ( new int[1], (byte)0 );
            
            return new FieldElement ( leftShift(this.magnitude), (byte)1 );
            
        }
        
        /**
         * Multiply a given magnitude by 2.
         *
         * This is a simple left shift and no modular operation.
         *
         * @param   operand magnitude of <tt>FieldElement</tt> which to shift.
         * @return  <code>operand << 1</code>
         */
        private int[] leftShift( int[] operand ) {

            int length = operand.length, i = length - 1;
            int[] mag;

            int highBits = operand[length - 1] >>> 31;
            
            if ( highBits != 0 ) {
                mag = new int[length + 1];
                mag[length] = highBits;
            }
            else
                mag = new int[length];
            
            int j = length - 1;
            while ( j > 0 )
                mag[i--] = (operand[j--] << 1) | (operand[j] >>> 31);

            mag[i] = operand[j] << 1;
            
            return mag;
            
        }
        
        /**
         * Counts the number of relevant bits in <tt>FieldElement this</tt>.
         *
         * @return number of relevant bits in <tt>this</tt>
         */
        public int numBits( ) {
            
            int lengthMinusOne = this.magnitude.length - 1;
            int result = (lengthMinusOne) * 32;
            
            result += numBits( this.magnitude[lengthMinusOne] );
            
            return result;
            
        }
        
        /**
         * Counts the number of relevant bits in <tt>w</tt>.
         *
         * @param   w integer to count the bits of.
         * @return  number of relevant bits in <tt>w</tt>
         */
        private int numBits( int w ) {
            
            return
                (w < 1<<15 ?
                (w < 1<<7 ?
                (w < 1<<3 ?
                (w < 1<<1 ? (w < 1<<0 ? (w<0 ? 32 : 0) : 1):(w < 1<<2 ? 2 : 3)):
                (w < 1<<5 ? (w < 1<<4 ? 4 : 5) : (w < 1<<6 ? 6 : 7))) :
                (w < 1<<11 ?
                (w < 1<<9 ? (w < 1<<8 ? 8 : 9) : (w < 1<<10 ? 10 : 11)) :
                (w < 1<<13 ? (w < 1<<12 ? 12 : 13) : (w < 1<<14 ? 14 : 15)))) :
                (w < 1<<23 ?
                (w < 1<<19 ?
                (w < 1<<17 ? (w < 1<<16 ? 16 : 17) : (w < 1<<18 ? 18 : 19)) :
                (w < 1<<21 ? (w < 1<<20 ? 20 : 21) : (w < 1<<22 ? 22 : 23))) :
                (w < 1<<27 ?
                (w < 1<<25 ? (w < 1<<24 ? 24 : 25) : (w < 1<<26 ? 26 : 27)) :
                (w < 1<<29 ? (w < 1<<28 ? 28 : 29) : (w < 1<<30 ? 30 : 31)))));
            
        }
        
        /**
         * This method is used to calculate the constant
         * <tt>barrettConstant</tt> which is neccessary for barrett reduction.
         *
         * It implements a binary division algorithm and divides
         * <code>b^2 * p.magnitude.length</code> by <tt>this</tt>.
         * 
         * @return  <code>(b^2 * p.magnitude.length) / this</code>
         * @see     #mod
         * @see     FiniteField#barrettConstant
         */
        private FieldElement calculateBarrettConstant( ) {
            
            // initialise the numerator
            int lengthNumerator = this.magnitude.length << 1;
            int[] numArray = new int[lengthNumerator + 1];
            numArray[lengthNumerator] =  0x00000001;
            FieldElement numerator = new FieldElement( numArray, (byte)1 );
            
            // initialise the denominator
            FieldElement denominator = new FieldElement( this );

            // initialise result
            FieldElement result = new FieldElement( 0 );
            
            int count = 0;

            // actual division
            while( numerator.compareTo( denominator ) == GT ) {
                denominator = denominator.leftShift( );
                count++;
            }
            
            denominator = denominator.rightShift( );
            count--;
            
            while( count != 0 ) {
                if( numerator.compareTo( denominator ) >= EQ ) {
                    numerator.magnitude = subtract( numerator.magnitude,
                                                    denominator.magnitude );
                    result = result.setBit( count );
                }
                denominator = denominator.rightShift( );
                count--;
            }

            return result;
            
        }
                
        /**
         * This method strips leading zeros off the given magnitude.
         * It is called by almost all function inside <tt>FieldElement</tt>.
         *
         * @param   value magnitude which the zeros should be stripped off.
         * @return  new magnitude without leading zeros.
         */
        private int[] stripZeros( int[] value ) {
            
            int counter = value.length;
            for ( ; counter >= 2 && value[counter-1] == 0; counter-- )
                ;

            if ( counter != value.length ) {
                int result[] = new int[counter];

                for ( int i = 0; i < counter; i++ ) {
                    result[i] = value[i];
                }

                return result;
            }

            return value;
            
        }
        
        /**
         * Compares two <tt>FieldElements</tt>.
         *
         * @param   compare <tt>FieldElement</tt> which <tt>this</tt> should
         *          be compared to.
         * @return  {@link FiniteField#LT LT} if <tt>this</tt> is less than
         *          <tt>compare</tt>, {@link FiniteField#EQ EQ} if <tt>this</tt>
         *          is equal to <tt>compare</tt> and {@link FiniteField#GT GT}
         *          if <tt>this</tt> is greater than <tt>compare</tt>.
         */
        public byte compareTo( FieldElement compare ) {
            
            if( this.magnitude.length > compare.magnitude.length )
                return GT;
            else if( this.magnitude.length < compare.magnitude.length )
                return LT;
            
            return compareTo( this.magnitude, compare.magnitude );
            
        }
        
        /**
         * Compares the magnitudes of two <tt>FieldElements</tt>. Only
         * to be called if these are the same length.
         *
         * @param   operand1 first magnitude.
         * @param   operand2 second magnitude.
         * @return  {@link FiniteField#LT LT} if <tt>operand1</tt> is less than
         *          <tt>operand2</tt>, {@link FiniteField#EQ EQ} if
         *          <tt>operand1</tt> is equal to <tt>operand2</tt> and
         *          {@link FiniteField#GT GT} if <tt>operand1</tt> is greater
         *          than <tt>operand2</tt>.
         */
        private byte compareTo( int[] operand1, int[] operand2 ) {
            
            for( int i = operand1.length - 1; i >= 0 ; i-- )
                
                if( operand1[i] != operand2[i] ) {
                    
                    if( (operand1[i] & INT2LONG) > (operand2[i] & INT2LONG) )
                        return GT;
                    else
                        return LT;
                    
                }
           
            return EQ;
            
        }
        
        /**
         * Compares two <tt>FieldElements</tt>.
         *
         * @param   compare <tt>FieldElement</tt> which <tt>this</tt> should
         *          be compared to.
         * @return  true if and only if <tt>this</tt> is equal to
         *          <tt>compare</tt>, false otherwise.
         */
        public boolean equals( FieldElement compare ) {
            
            if( this.magnitude.length != compare.magnitude.length )
                return false;
            
            return  compareTo( this.magnitude, compare.magnitude ) == EQ
                    ? true : false;
            
        }        
        
        /**
         * Tests bit n of <tt>FieldElement this</tt>, needed for point
         * multiplication algorithms in {@link FiniteField.EllipticCurve.Point}.
         *
         * @param   n position of bit to test.
         * @return  <tt>true</tt> if and only if bit n is set, <tt>false</tt>
         *          otherwise.
         *
         * @see     FiniteField.EllipticCurve.Point#multiply
         */
        public boolean testBit( int n ) {
            
            int word = n >> 5;
            if( word >= 0 && word < magnitude.length )
                return ( ( magnitude[word] >> (n & 0x1F) ) & 1 ) != 0;
            
            return false;
            
        }
        
        /**
         * Sets bit n of <tt>FieldElement this</tt>.
         *
         * @param   n the position of the bit to set.
         * @return  new <tt>FieldElement</tt> with n'th bit set to one.
         */
        private FieldElement setBit( int n ) {
            
            int word = n >> 5;
            int bit = 1 << (n & 0x1F);
            
            // if the bit we want to set is out of the range of
            // the magnitude, we have to make it longer
            int[] mag = new int[( (word + 1) >= this.magnitude.length ?
                        (word + 1) : this.magnitude.length )];
			
            for( int i = 0; i < this.magnitude.length; i++ )
                mag[i] = this.magnitude[i];

            mag[word] |= bit;
            
            return new FieldElement( mag, (byte)1 );
            
        }
        
        /**
         * Provides a string representation of <tt>FieldElement this</tt>
         * in radix 16.
         *
         * @return String representing <tt>FieldElement this</tt>.
         */
        public String toString( ) {
            
            if ( this.signum == 0 )
                return "0";

            String s = new String();
            String h;

            for ( int i = this.magnitude.length - 1; i >= 0 ; i-- ) {
                h = "0000000" + Long.toString
                                ( this.magnitude[i] & INT2LONG, 16 );
                h = h.substring( h.length() - 8 );
                s = s + h;
            }
            
            // cut off leading zeros
            int k = 0;
            while( s.substring( k, k + 1 ).equals( "0" ) )
                k++;

            return s.substring( k, s.length() );
            
        }
        
        /**
         * Returns a <tt>byte[]</tt> containing the width <tt>w</tt> NAF
         * representation of <tt>FieldElement this</tt>. For details on NAF
         * please refer to <b>GECC</b>, p. 98 f.
         * <p />
         * The NAF representation is used for point multiplication in
         * {@link FiniteField.EllipticCurve.Point}.
         * <p />
         * This implementation only allows the width to be between 2 and 6,
         * otherwise we would have to return a <tt>short[]</tt> which would
         * be too large (e.g. 2560bit for a <tt>FieldElement</tt> of 160bit).
         *
         * @param   w window width to use.
         *
         * @return  <code>NAF_w(k)</code>
         *
         * @throws  IllegalArgumentException <tt>w</tt> is out of range.
         *
         * @see     FiniteField.EllipticCurve.Point#multiply(
         *          FiniteField.FieldElement,FiniteField.EllipticCurve.Point[],
         *          byte)
         */
        public byte[] toNAF( byte w ) throws IllegalArgumentException {
            
            int width = 1 << w;
            int widthTimesTwo = width << 1;
            
            if( width > Byte.MAX_VALUE || w < 2 )
                throw new IllegalArgumentException
                ( "FiniteField.FieldElement.toNAF:" +
                        "Width has to be beetwen 2 and 6" );
            
            int bitLength = this.numBits( );
            byte[] naf = new byte[bitLength + 1];
            
            FieldElement k = new FieldElement( this );
            
            int i = 0;
            
            while( ! (k.magnitude.length == 1 && k.magnitude[0] == 0) ) {
                
                if( (k.magnitude[0] & 1) == 1 ) {
                    
                    naf[i] = (byte)((k.magnitude[0]) & (widthTimesTwo - 1));
                    
                    if( (naf[i] & width) != 0 ) {
                        naf[i] -= widthTimesTwo;
                    }
                    
                    if( naf[i] < 0 ) {
                        int[] tmp = { -naf[i] };
                        k.magnitude = add( k.magnitude, tmp );
                    }
                    else {
                        int[] tmp = { naf[i] };
                        k.magnitude = subtract( k.magnitude, tmp );
                    }
                    
                }
                else
                    naf[i] = 0;
                
                i++;
                k.magnitude = rightShift( k.magnitude );
                
            }
            
            while( i < bitLength )
                naf[i++] = 0;
            
            return naf;
        
        }
        
    }
    
    
    
    
    /**
     * This class represents an elliptic curve over a finite field. It stores
     * the values {@link #a} and {@link #b}, by which the curve is represented.
     * <p />
     * <tt>EllipticCurve</tt> contains a subclass {@link Point} which represents
     * a point on this curve.
     */
    public class EllipticCurve {
        
        /**
         * Coefficient a of elliptic curve Y^2 = X^3 + a X Z^4 + b Z^6.
         * This has to be less than p.
         */
        public final FieldElement a;
        
        /**
         * Coefficient b of elliptic curve Y^2 = X^3 + a X Z^4 + b Z^6.
         * This has to be less than p.
         */
        public final FieldElement b;
        
        /**
         * This value has to be calculated once while the curve is initialized.
         * It is needed to accelerate point operations.
         *
         * @see Point#add
         */
        private final FieldElement inverseOfTwo = new FieldElement(2).invert();
        
        
        
                
        /**
         * Construct a new elliptic curve, given the values a and b in
         * a string representation in radix 16.
         *
         * @throws  IllegalArgumentException Parameters do not represent an
         *          elliptic curve.
         */
        public EllipticCurve( String a, String b )
               throws IllegalArgumentException {
            
            this.a = new FieldElement( a );
            this.b = new FieldElement( b );
            
            if( ! this.valid() )
                throw new IllegalArgumentException
                ( "FiniteField.EllipticCurve.<init>: Not a valid curve." );
            
        }
        
        
        
        
        /**
         * Test if this is a valid elliptic curve.
         *
         * @return  <tt>true</tt> if and only if 
         *          <code>4 * a^3 + 27 * b^2 != 0 (mod p)</code>, <tt>false</tt>
         *          otherwise.
         */
        public boolean valid( ) {
            
            FieldElement aRes = new FieldElement( a );
            FieldElement bRes = new FieldElement( b );
            
            aRes = aRes.multiply( a ).multiply( a );
            aRes = aRes.multiplyByWord( 4 );
                
            bRes = bRes.multiply( b );
            bRes = bRes.multiplyByWord( 27 );
            
            aRes = aRes.add( bRes );
            
            return ( ! aRes.equals( ZERO ) );
            
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
            private FieldElement x;
            
            /**
             * Coordinate y in Jacobian-projective coordinates of the point.
             */
            private FieldElement y;
            
            /**
             * Coordinate z in Jacobian-projective coordinates of the point.
             */
            private FieldElement z;
            
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
            public Point( String x, String y ) throws IllegalArgumentException {
                
                this.x = new FieldElement( x );
                this.y = new FieldElement( y );
                this.z = new FieldElement( 1 );
                
                if( ! this.onCurve() )
                    throw new IllegalArgumentException
                    ( "FiniteField.EllipticCurve.Point.<init>:" +
                            " Point not on curve." );
                
            }
            
            /**
             * Create a new point on the elliptic curve, given the
             * coordinates in Jacobian-projective description in a string
             * representation.
             */
            private Point( String x, String y, String z ) {
                
                this.x = new FieldElement( x );
                this.y = new FieldElement( y );
                this.z = new FieldElement( z );
                
            }
            
            /**
             * Create a new point on the elliptic curve, given the
             * coordinates in Jacobian-projective description in a 
             * <tt>FieldElement</tt> representation.
             */
            private Point( FieldElement x, FieldElement y, FieldElement z ) {
                
                this.x = new FieldElement( x );
                this.y = new FieldElement( y );
                this.z = new FieldElement( z );
                
            }
            
            /**
             * Create a new point, given a point. This is used to create return
             * values for methods like {@link #add}.
             */
            private Point( Point P ) {
                
                this.x = new FieldElement( P.x );
                this.y = new FieldElement( P.y );
                this.z = new FieldElement( P.z );
                
            }
            
            
            
            
            /**
             * Returns the x-coordinate of point <tt>this</tt>
             * in affine representation
             *
             * @return x-coordinate of this
             */
            public FieldElement getX( ) {

                return x.divide( z.multiply( z ) );
                
            }
            
            /**
             * Returns the y-coordinate of point <tt>this</tt>
             * in affine representation
             *
             * @return y-coordinate of this
             */
            public FieldElement getY( ) {
                
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
                
                FieldElement zPow2 = z.multiply( z );
                FieldElement zPow4 = zPow2.multiply( zPow2 );
                FieldElement zPow6 = zPow4.multiply( zPow2 );
                
                FieldElement left  = y.multiply( y );
                FieldElement right = x.multiply( x.multiply( x ) );
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
                
                FieldElement z1Pow2 = this.z.multiply( this.z );
                FieldElement z2Pow2 = operand.z.multiply( operand.z );
                
                FieldElement lambda1 = this.x.multiply( z2Pow2 );
                FieldElement lambda2 = operand.x.multiply( z1Pow2 );
                FieldElement lambda3 = lambda1.subtract( lambda2 );
                FieldElement lambda7 = lambda1.add( lambda2 );
                
                FieldElement lambda4 = this.y.multiply( 
                        z2Pow2.multiply( operand.z ) );
                FieldElement lambda5 = operand.y.multiply( 
                        z1Pow2.multiply( this.z ) );
                FieldElement lambda6 = lambda4.subtract( lambda5 );
                FieldElement lambda8 = lambda4.add( lambda5 );
                
                FieldElement lambda3Pow2 = lambda3.multiply( lambda3 );
                
                FieldElement z3 = this.z.multiply( operand.z ).
                        multiply( lambda3 );
                
                // this was -operand, therfor the result is infinity
                if( z3.equals( ZERO ) )
                    return new Point( );
                
                FieldElement x3 = lambda6.multiply( lambda6 );
                x3 = x3.subtract( lambda7.multiply( lambda3Pow2 ) );
                
                FieldElement lambda9 = lambda7.multiply( lambda3Pow2 );
                lambda9 = lambda9.subtract( x3.add( x3 ) );
                
                FieldElement y3 = lambda9.multiply( lambda6 ).
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
                if( y.equals( ZERO ) )
                    return new Point( );
                
                FieldElement yPow2 = y.multiply( y );
                FieldElement zPow2 = z.multiply( z );
                
                FieldElement lambda1 = x.multiply(x).multiplyByWord( 3 ).
                        add( a.multiply( zPow2.multiply(zPow2) ) );
                FieldElement lambda2 = x.multiply( yPow2 ).multiplyByWord( 4 );
                FieldElement lambda3 = yPow2.multiply( yPow2 ).
                        multiplyByWord( 8 );
                
                FieldElement z3 = y.multiply( z );
                z3 = z3.add( z3 );
                FieldElement x3 = lambda1.multiply( lambda1 ).
                        subtract( lambda2.add( lambda2 ) );
                FieldElement y3 = lambda1.multiply( lambda2.subtract(x3) ).
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
            public Point multiply( FieldElement factor ) {
                
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
            public Point multiply( FieldElement factor, byte width ) {
                
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
            public Point multiply( FieldElement factor, 
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
             * FiniteField.FieldElement,FiniteField.EllipticCurve.Point[],byte) 
             * NAF multiplication algorithm}. This is quite fast if you have to
             * multiply the same point by different factors, since you only
             * have to precompute these points once.
             * <p /> 
             * Once you precomputed the points you just have to compute the NAF
             * representation of your factor and call {@link #multiply(
             * FiniteField.FieldElement,
             * FiniteField.EllipticCurve.Point[],byte)}.
             * This is significantly faster than using {@link #multiply}.
             *
             * @param   width window width to use.
             * @return  array of precomputed points.
             *
             * @see     FiniteField.FieldElement#toNAF
             * @see     #multiply(FiniteField.FieldElement,
             *          FiniteField.EllipticCurve.Point[],byte)
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
                if( (this.infinity && !operand.infinity) || 
                    (!this.infinity && operand.infinity) )
                    return false;
                
                if(    this.x.compareTo( operand.x ) == EQ 
                    && this.y.compareTo( operand.y ) == EQ
                    && this.z.compareTo( operand.z ) == EQ
                    )
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
                
                FieldElement zPow2 = z.multiply( z );
                FieldElement zPow3 = zPow2.multiply( z );

                FieldElement xAffine = x.divide( zPow2 );
                FieldElement yAffine = y.divide( zPow3 );

                return "(" + xAffine.toString() + ", " + 
                        yAffine.toString() + ")";
                
            }
            
        }
        
    }
    
}
