package blockchain;

import java.security.*;

class Transaction {
	private String text;
	private byte[] signature;
	private long id;
	private PublicKey publicKey;

	public Transaction(String text, byte[] signature, long id, PublicKey publicKey) {
		this.text = text;
		this.signature = signature;
		this.id = id;
		this.publicKey = publicKey;
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	// validates signature
	public boolean hasValidSignature() {
		Signature sign;
		try {
			sign = Signature.getInstance("SHA1withRSA");
			sign.initVerify(publicKey);
			sign.update(text.getBytes());
			return sign.verify(signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e){
			e.printStackTrace();
		}
		return false;
	}

	// get key-pair generator for creating public key and private key for RSA encryption
	public static KeyPairGenerator getKeyPairGenerator(int keyLength) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(keyLength);
		return keyPairGen;
	}

	// generate a signature
	public static byte[] sign(String text, PrivateKey privateKey) throws Exception {
		Signature dsa = Signature.getInstance("SHA1withRSA");
		dsa.initSign(privateKey);
		dsa.update(text.getBytes());
		return dsa.sign();
	}

}

