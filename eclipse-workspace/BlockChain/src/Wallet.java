import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.security.*;

public class Wallet {
	
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public Wallet(){
		generateKeyPair();	
	}
		
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Ű ���ʷ����� �ʱ�ȭ, Ű��� ����
			keyGen.initialize(ecSpec, random);   //256 ����Ʈ�� ����� ���� ���� ����
	        KeyPair keyPair = keyGen.generateKeyPair();
	        	// ������ �����̺�Ű�� �ۺ�Ű�� ������ �Ҵ�
	        privateKey = keyPair.getPrivate();
	        publicKey = keyPair.getPublic();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
