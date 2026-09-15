package saga.exceptions;

public class CommentaryNotFoundException extends IllegalArgumentException {
    public CommentaryNotFoundException(String msg){
        super(msg);
    }
}
