public class TransactionInput {
	public String transactionOutputId; //트랜잭션 output을 사용하여 트랜잭션 Input으로 만듬
	public TransactionOutput UTXO; //참조한 Output의 UTXO
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
