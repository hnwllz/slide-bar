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
     * 请求返回附带数据（序列化后的json对象）
     */
    private String data;

    /**
     * 状态码
     * 成功：200
     * 失败：其他
     */
    private int code;

    /**
     * 失败状态码描述
     * 如果成功不返回
     * 失败返回状态码对应的message消息
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