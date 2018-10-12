package de.rub.nds.ec;

import de.rub.nds.ec.math.*;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.custom.sec.SecP128R1Curve;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Example {

    public static void main(String[] args) {
        // curves
        FiniteField sbField = new FiniteField("00fffffffdffffffffffffffffffffffff");
        EllipticCurve sbCurve = new EllipticCurve(sbField, sbField.new FieldElement("00fffffffdfffffffffffffffffffffffc"), sbField.new FieldElement("00e87579c11079f43dd824993c2cee5ed3"));
        System.out.println("SB: Curve: " + sbCurve.toString());

        SecP128R1Curve bcCurve = new SecP128R1Curve();
        System.out.format("BC: Curve: Y^2 = X^3 + %s * X + %s\n", bcCurve.getA(), bcCurve.getB());
        System.out.println();

        // generator
        X9ECParameters bcParameters = SECNamedCurves.getByName("secp128r1");
        System.out.format("BC: Point G: (%s, %s)\n", bcParameters.getG().getAffineXCoord(), bcParameters.getG().getAffineYCoord());

        EllipticCurve.Point G = sbCurve.new Point(sbField.new FieldElement("161ff7528b899b2d0c28607ca52c5b86"), sbField.new FieldElement("cf5ac8395bafeb13c02da292dded7a83"));
        System.out.format("SB: Point G: %s\n", G.toString());
        System.out.println();

        // private key
        ECKeyPairGenerator bcKeyGenerator = new ECKeyPairGenerator();
        SecureRandom secureRandom = new SecureRandom();
        ECDomainParameters bcParams = new ECDomainParameters(bcParameters.getCurve(), bcParameters.getG(), bcParameters.getN(), bcParameters.getH());
        ECKeyGenerationParameters bcKeyGenParam = new ECKeyGenerationParameters(bcParams, secureRandom);
        bcKeyGenerator.init(bcKeyGenParam);
        AsymmetricCipherKeyPair bcKeyPair = bcKeyGenerator.generateKeyPair();
        ECPrivateKeyParameters bcPrivateKey = (ECPrivateKeyParameters)bcKeyPair.getPrivate();
        ECPublicKeyParameters bcPublicKey = (ECPublicKeyParameters)bcKeyPair.getPublic();
        System.out.format("BC: PrivateKey: %s\n", bcPrivateKey.getD().toString(16));
        System.out.format("BC: PublicKey: (%s, %s)\n", bcPublicKey.getQ().getAffineXCoord(), bcPublicKey.getQ().getAffineYCoord());

        FiniteField.FieldElement sbPrivateKey = sbField.new FieldElement(bcPrivateKey.getD().toString(16));
        EllipticCurve.Point sbPublicKey = G.multiply(sbPrivateKey);
        System.out.format("SB: PrivateKey: %s\n", sbPrivateKey.toString());
        System.out.format("SB: PublicKey: %s\n", sbPublicKey.toString());
        System.out.println();

        // ecdsa generate signature
        ECDSA sbEcdsa = new ECDSA(sbField, sbCurve, G);
        FiniteField.FieldElement sbMessage = sbField.new FieldElement();
        Signature sbSignature = sbEcdsa.generateSignature(sbMessage, sbPrivateKey);
        System.out.format("SB: Signature: [%s, %s]\n", sbSignature.getR(), sbSignature.getS());

        BigInteger[] bcSignature = new BigInteger[2];
        org.bouncycastle.math.field.FiniteField bcField = org.bouncycastle.math.field.FiniteFields.getPrimeField(new BigInteger("00fffffffdffffffffffffffffffffffff", 16));
        ECFieldElement bcMessage = bcCurve.fromBigInteger(new BigInteger(sbMessage.toString(), 16));
        ECFieldElement bcK = bcCurve.fromBigInteger(new BigInteger(sbSignature.k.toString(), 16));
        ECPoint bcR = bcParameters.getG().multiply(bcK.toBigInteger()).normalize();
        bcSignature[0] = bcR.getAffineXCoord().toBigInteger();
        ECFieldElement bcTmp = bcCurve.fromBigInteger(bcPrivateKey.getD()).multiply(bcCurve.fromBigInteger(bcSignature[0]));
        bcTmp = bcK.invert().multiply(bcMessage.add(bcTmp));
        bcSignature[1] = bcTmp.toBigInteger();
        System.out.format("BC: Signature: [%s, %s]\n", bcSignature[0].toString(16), bcSignature[1].toString(16));
        System.out.println();

        // verify ecdsa
        boolean valid = sbEcdsa.verifySignature(sbPublicKey, sbMessage, sbSignature);
        System.out.format("SB: Signature: verification successful? %s\n", valid);

        // verify ecdsa bc
        ECFieldElement w = bcCurve.fromBigInteger(bcSignature[1]).invert();
        ECFieldElement u1 = bcMessage.multiply(w);
        ECFieldElement u2 = bcCurve.fromBigInteger(bcSignature[0]).multiply(w);
        ECPoint X = bcParameters.getG().multiply(u1.toBigInteger()).add(bcPublicKey.getQ().multiply(u2.toBigInteger())).normalize();
        System.out.format("BC: Signature: verification successful? %s\n", X.getAffineXCoord().toBigInteger().compareTo(bcSignature[0]) == 0);
        System.out.println();
    }

}
