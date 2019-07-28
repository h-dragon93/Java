import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; // 해시값
	public PublicKey sender; //송신자의 공개키
	public PublicKey reciepient; // 수신자의 공개키
	public float value; //전달할 금액
	public byte[] signature; // 트랜잭션을 보증하는 송신자의 서명
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; // 트랜잭션 발생수
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	//트랜잭션 id를 구하기 위한 메서드
	private String calulateHash() {
		sequence++;
		return StringUtil.applySha256 (
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
	//데이터 서명하기
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		signature = StringUtil.applyECDSASig(privateKey, data);		
	}
	//서명 검증하기
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
}
