package de.rub.nds.ec;

import de.rub.nds.ec.math.FiniteField;

public class Signature {

    /**
     *  Part 'r' of the signature.
     *
     *  Definition: https://de.wikipedia.org/wiki/Elliptic_Curve_DSA
     */
    private FiniteField.FieldElement r;

    /**
     *  Part 's' of the signature.
     *
     *  Definition: https://de.wikipedia.org/wiki/Elliptic_Curve_DSA
     */
    private FiniteField.FieldElement s;

    /**
     * Random FieldElement needed for signing as described here: https://de.wikipedia.org/wiki/Elliptic_Curve_DSA
     */
    public final FiniteField.FieldElement k;

    public Signature(FiniteField.FieldElement r, FiniteField.FieldElement s, FiniteField.FieldElement k) {
        this.r = r;
        this.s = s;
        this.k = k;
    }

    public FiniteField.FieldElement getR() {
        return r;
    }

    public FiniteField.FieldElement getS() {
        return s;
    }



}
