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
			// 키 제너레이터 초기화, 키페어 생성
			keyGen.initialize(ecSpec, random);   //256 바이트의 충분한 보안 공간 생성
	        KeyPair keyPair = keyGen.generateKeyPair();
	        	// 생성한 프라이빗키와 퍼블릭키를 변수에 할당
	        privateKey = keyPair.getPrivate();
	        publicKey = keyPair.getPublic();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
