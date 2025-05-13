package exception;

public class InvalidCepException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidCepException(String cep) {
        super("CEP inválido: " + cep);
    }
    
    public InvalidCepException(String cep, Throwable cause) {
        super("CEP inválido: " + cep, cause);
    }
} 