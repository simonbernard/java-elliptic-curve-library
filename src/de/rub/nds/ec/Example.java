package de.rub.nds.ec;

import de.rub.nds.ec.math.*;

public class Example {

    public static void main(String[] args) {
        FiniteField field = new FiniteField("00db7c2abf62e35e668076bead208b");
        FiniteField.EllipticCurve curve = field.new EllipticCurve("00db7c2abf62e35e668076bead2088", "659ef8ba043916eede8911702b22");

        FiniteField.EllipticCurve.Point G = curve.new Point("09487239995a5ee76b55f9c2f098", "a89ce5af8724c0a23e0e0ff77500");
        FiniteField.FieldElement privateKey = field.new FieldElement();
        FiniteField.EllipticCurve.Point publicKey = G.multiply(privateKey);


        FiniteField.FieldElement message = field.new FieldElement();
        ECDSA ecdsa = new ECDSA(field, curve, G);
        FiniteField.FieldElement[] signature = ecdsa.generateSignature(message, privateKey);
        boolean valid = ecdsa.verifySignature(publicKey, message, signature);
        System.out.println("Private key: " + privateKey.toString());
        System.out.println("Public key: " + publicKey.toString());
        System.out.format("Signature verification successful? %s", valid);
    }

}
