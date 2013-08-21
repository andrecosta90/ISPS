package parser.exceptions;

public class ParserEmptyException extends Exception {
	
	public ParserEmptyException() {
		super();
	}
	
	public ParserEmptyException(String msg){
		super(msg);
	}

}
