import java.security.PublicKey;
/*
 트랜잭션의 output은 전송 된 최종 금액이 표시된다. 
 이것은 새로운 거래를 생성할 때 input이 참조하게 되면서 보낼 수 있는 
 코인이 있다는 것을 증명하게 된다
 */
public class TransactionOutput {
	public String id;
	public PublicKey reciepient; //수신인의 공개키
	public float value; //수신인에게 들어갈 코인양
	public String parentTransactionId; //해당 아웃풋이 생성된 트랙잭션 id
	
	//생성자
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
	}
	
	//코인이 정상적으로 내 소유인지 판별
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
	
}