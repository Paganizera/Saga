package saga.exceptions;

public class PostNotFoundException extends IllegalArgumentException {
    public PostNotFoundException(String msg){
        super(msg);
    }
}
