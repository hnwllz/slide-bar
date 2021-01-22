package rest;

public class Result {
	public Result(int code, String message,String data){
		this.setMessage(message);
		this.setCode(code);
		this.setData(data);
	}
	
	public Result(int code, String message){
		this(code, message, "");
	}
	
	/**
     * ���󷵻ظ������ݣ����л����json����
     */
    private String data;

    /**
     * ״̬��
     * �ɹ���200
     * ʧ�ܣ�����
     */
    private int code;

    /**
     * ʧ��״̬������
     * ����ɹ�������
     * ʧ�ܷ���״̬���Ӧ��message��Ϣ
     */
    private String message;
	
	public Result  setData(String data) {
		this.data = data;
		return this;
	}
	
	public String getData(){
		return data;
	}

	public Result setCode(int code) {
		this.code = code;
		return this;
	}
	
	public int getCode(){
		return this.code;
	}

	public Result setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public static Result success(){
		return new Result(200, "");
	}
	
	public static Result error(String message){
		return new Result(-1, message);
	}
	
}