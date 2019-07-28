import java.security.PublicKey;
/*
 Ʈ������� output�� ���� �� ���� �ݾ��� ǥ�õȴ�. 
 �̰��� ���ο� �ŷ��� ������ �� input�� �����ϰ� �Ǹ鼭 ���� �� �ִ� 
 ������ �ִٴ� ���� �����ϰ� �ȴ�
 */
public class TransactionOutput {
	public String id;
	public PublicKey reciepient; //�������� ����Ű
	public float value; //�����ο��� �� ���ξ�
	public String parentTransactionId; //�ش� �ƿ�ǲ�� ������ Ʈ����� id
	
	//������
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
	}
	
	//������ ���������� �� �������� �Ǻ�
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
	
}