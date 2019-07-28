import java.util.Date;

public class Block {
	
	// 변수명
	public String hash; // 현재 해쉬값 = 이전블록해쉬값 + data
	public String previousHash;  //이전 해쉬값
	private String data; // 임의의 문자열
	private long timeStamp; // Unix 타임. 거래 발생 시간
	private int nonce;
	// 생성자
	public Block(String data, String previousHash) { 
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); 
	}
	// 이전해쉬값 + 거래시간(timestamp)+ 데이터 를 이용한 해쉬값 계산
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