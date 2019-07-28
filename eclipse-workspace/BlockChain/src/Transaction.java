import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; // �ؽð�
	public PublicKey sender; //�۽����� ����Ű
	public PublicKey reciepient; // �������� ����Ű
	public float value; //������ �ݾ�
	public byte[] signature; // Ʈ������� �����ϴ� �۽����� ����
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; // Ʈ����� �߻���
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	//Ʈ����� id�� ���ϱ� ���� �޼���
	private String calulateHash() {
		sequence++;
		return StringUtil.applySha256 (
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
	//������ �����ϱ�
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		signature = StringUtil.applyECDSASig(privateKey, data);		
	}
	//���� �����ϱ�
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
}
