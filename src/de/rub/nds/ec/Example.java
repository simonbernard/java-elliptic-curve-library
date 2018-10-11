package de.rub.nds.ec;

import de.rub.nds.ec.math.*;

import org.bouncycastle.math.ec.custom.sec.SecP192K1Curve;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class Example {

    public static void main(String[] args) {
        // curves
        FiniteField field = new FiniteField("00fffffffffffffffffffffffffffffffffffffffeffffee37");
        FiniteField.EllipticCurve sbCurve = field.new EllipticCurve("0", "3");
        System.out.println("SB CURVE: " + sbCurve.toString());
        SecP192K1Curve bcCurve = new SecP192K1Curve();
        System.out.format("BC CURVE: Y^2 = X^3 + %s * X + %s;", bcCurve.getA(), bcCurve.getB());

        // generator
        ECKeyPairGenerator bcKeyGenerator = new ECKeyPairGenerator();
        bcKeyGenerator.




        /*
        FiniteField.EllipticCurve.Point G = curve.new Point("09487239995a5ee76b55f9c2f098", "a89ce5af8724c0a23e0e0ff77500");
        FiniteField.FieldElement privateKey = field.new FieldElement();
        FiniteField.EllipticCurve.Point publicKey = G.multiply(privateKey);


        FiniteField.FieldElement message = field.new FieldElement();
        ECDSA ecdsa = new ECDSA(field, curve, G);
        FiniteField.FieldElement[] signature = ecdsa.generateSignature(message, privateKey);
        boolean valid = ecdsa.verifySignature(publicKey, message, signature);
        System.out.println("Private key: " + privateKey.toString());
        System.out.println("Public key: " + publicKey.toString());
        System.out.format("Signature verification successful? %s", valid);*/
    }

}
