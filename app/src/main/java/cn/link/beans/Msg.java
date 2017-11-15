package cn.link.beans;
/**
 * 核心会话交互消息
 * @author hanyu
 *
 */
public class Msg {

	private String head;
	private String body;
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Msg(String head, String body) {
		super();
		this.head = head;
		this.body = body;
	}
	public Msg() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return head+"."+body;
	}
	
	
}
