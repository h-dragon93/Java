public class TransactionInput {
	public String transactionOutputId; //Ʈ����� output�� ����Ͽ� Ʈ����� Input���� ����
	public TransactionOutput UTXO; //������ Output�� UTXO
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
