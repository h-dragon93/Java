import java.util.Date;

public class Block {
	
	// ������
	public String hash; // ���� �ؽ��� = ��������ؽ��� + data
	public String previousHash;  //���� �ؽ���
	private String data; // ������ ���ڿ�
	private long timeStamp; // Unix Ÿ��. �ŷ� �߻� �ð�
	private int nonce;
	// ������
	public Block(String data, String previousHash) { 
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); 
	}
	// �����ؽ��� + �ŷ��ð�(timestamp)+ ������ �� �̿��� �ؽ��� ���
	public String calculateHash() { 
		String calculatedhash = StringUtil.applySha256(
				previousHash +
				Long.toString(timeStamp)+
				Integer.toString(nonce) +
				data
				);
		return calculatedhash;
	}
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while(!hash.substring(0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined! : " + hash);
	}
}